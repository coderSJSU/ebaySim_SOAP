var ejs = require("ejs");
var winston = require('winston');
var soap = require('soap');
var baseURL = "http://localhost:8080/Ebay-SOAP/services";
var myCustomLevels = {
	    levels: {
	      event: 0,
	      bid: 1
	    }
	  };

var logger = new (winston.Logger)({
    level: 'event', 
    levels: myCustomLevels.levels,
    transports: [new winston.transports.File({filename: 'F:/lib/event.log'})]
  }); 

var bidLogger = new (winston.Logger)({
    level: 'bid', 
    levels: myCustomLevels.levels,
    transports: [new winston.transports.File({filename: 'F:/lib/bidding.log'})]
  });

function addBid(req, res){
	
	// check user already exists
	var amount = req.param("amount");
	var prodId = req.param("prodId");
	var user_id = req.session.user_id;
	var json_responses;
	var option = {
		ignoredNamespaces : true
	};
	var url = baseURL+"/Trans?wsdl";
	var args = {bid_amount: amount, product_id : prodId, customer_id: user_id};
	soap.createClient(url,option, function(err, client) {
		client.addBid(args, function(err, result) {
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else if (result.addBidReturn)
			{
				bidLogger.bid("bid submitted",{ user: user_id, product_id: prodId, amount: amount});
				json_responses = {"statusCode" : 200};
				res.send(json_responses);
			}
			else
			{
				json_responses = {"statusCode" : 400};
				res.send(json_responses);
			}
		});
	});


}

function addToCart(req, res){
	
	// check user already exists
	var data = req.param("data");
	var finalData = JSON.parse(data);
	var user_id = req.session.user_id;
	var json_responses;
	if(user_id == undefined){
		json_responses = {"statusCode" : 405};
		res.send(json_responses);
	}
	else{
		var option = {
			ignoredNamespaces : true
		};
		var url = baseURL+"/Trans?wsdl";
		var args = {product_id:finalData.id, user_id: user_id, quantity: finalData.quantity};
		soap.createClient(url,option, function(err, client) {
			client.addToCart(args, function(err, result) {
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else if (result.addToCartReturn)
				{
					logger.event("added to cart", { user: user_id, product: finalData.id});
					json_responses = {"statusCode" : 200, "id":""};
					res.send(json_responses);
				}
				else
				{
					json_responses = {"statusCode" : 400};
					res.send(json_responses);
				}
			});
		});
	}

}
function getCart(req, res){
	
	var cust_id = req.session.user_id;
	var json_responses;

	if(cust_id == undefined){
		json_responses = {"statusCode" : 405};
		res.send(json_responses);
	}
	else{
		var option = {
			ignoredNamespaces : true
		};
		var url = baseURL+"/Trans?wsdl";
		var args = {prod_id: prod_id, customer_id : customer_id};
		soap.createClient(url,option, function(err, client) {
			client.getCart(args, function(err, result) {
				console.log("Response from server:"+JSON.stringify(result));
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else
				{
					var xml = result.getCartReturn;
					parseString(xml, function (err, output) {
						var results = output.results.result;
						json_responses = {"statusCode" : 200, "data": results};
						res.send(json_responses);
					});
				}
			});
		});

	}
}


function getCartAmount(req, res){
	
	var cust_id = req.session.user_id;
	var json_responses;
	if(cust_id == undefined){
		json_responses = {"statusCode" : 405};
		res.send(json_responses);
	}
	else{
		var option = {
			ignoredNamespaces : true
		};
		var url = baseURL+"/Trans?wsdl";
		var args = {cust_id : cust_id};
		soap.createClient(url,option, function(err, client) {
			client.getCartAmount(args, function(err, result) {
				console.log("Response from server:"+JSON.stringify(result));
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else
				{
					var xml = result.getCartAmountReturn;
					parseString(xml, function (err, output) {
						var results = output.results.result;
						json_responses = {"statusCode" : 200, "data": results};
						res.send(json_responses);
					});
				}
			});
		});
	}
}


function removeFromCart(req, res){
	var user_id = req.session.user_id;
	var json_responses;
	var prod_id = req.param("prod_id");
	if(user_id == undefined){
		json_responses = {"statusCode" : 405};
		res.send(json_responses);
	}
	else{
		var option = {
			ignoredNamespaces : true
		};
		var url = baseURL+"/Trans?wsdl";
		var args = {user_id: user_id, prod_id : prod_id};

		soap.createClient(url,option, function(err, client) {
			client.removeFromCart(args, function(err, result) {
				console.log("Response from server:"+JSON.stringify(result));
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else if (result.removeFromCartReturn)
				{
					logger.event("removed from cart", { user: user_id, product: prod_id});
					json_responses = {"statusCode" : 200};
					res.send(json_responses);
				}
				else
				{
					json_responses = {"statusCode" : 400};
					res.send(json_responses);
				}
			});
		});
	}
}

function payment1(req, res){
	
	var cust_id = req.session.user_id;
	var json_responses;
	var total = req.param("total");
	req.session.total = total;
	
	if(cust_id == undefined){
		json_responses = {"statusCode" : 405};
		res.send(json_responses);
	}
	else {
		json_responses = {"statusCode" : 202};
		res.send(json_responses);
}
}

function emptyCart(req, res){
	var user_id = req.session.user_id;
	var json_responses;

	var option = {
		ignoredNamespaces : true
	};
	var url = baseURL+"/Trans?wsdl";
	var args = {user_id: user_id};

	soap.createClient(url,option, function(err, client) {
		client.emptyCart(args, function(err, result) {
			console.log("Response from server:"+JSON.stringify(result));
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else if (result.emptyCartReturn)
			{
				logger.event("empty cart", { user: user_id});
				json_responses = {"statusCode" : 200};
				res.send(json_responses);
			}
			else
			{
				json_responses = {"statusCode" : 400};
				res.send(json_responses);
			}
		});
	});

}

exports.getProductQuanity = function(req, res){
	var data = req.param("data");
	var finalData = JSON.parse(data);
	var json_responses;
	var option = {
		ignoredNamespaces : true
	};
	var url = baseURL+"/Trans?wsdl";
	var args = {prod_id: finalData.prod_id};
	soap.createClient(url,option, function(err, client) {
		client.getProductQuanity(args, function(err, result) {
			console.log("Response from server:"+JSON.stringify(result));
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else
			{
				var xml = result.getProductQuanityReturn;
				parseString(xml, function (err, output) {
					var results = output.results.result;
					json_responses = {"statusCode" : 200, "data": results};
					res.send(json_responses);
				});
			}
		});
	});
};


function getAmount(req, res){
	
	prod_id = req.session.prod_id;
	is_urgent = req.session.is_urgent;
	//delete req.session['prod_id'];
	delete req.session['is_urgent'];
	
	var cust_id = req.session.user_id;
	var json_responses;

	var queryString = 'select max(bid_amount) as max'+ 
	  ' from bid where product_id =' + prod_id+ '';
		
		if(cust_id == undefined){
			json_responses = {"statusCode" : 405};
			res.send(json_responses);
		}
		else{

			var url = baseURL+"/Trans?wsdl";
			var args = {prod_id: finalData.prod_id};
			soap.createClient(url,option, function(err, client) {
				client.getAmount(args, function(err, result) {
					console.log("Response from server:"+JSON.stringify(result));
					if(err){
						json_responses = {"statusCode" : 401};
						res.send(json_responses);
					}
					else
					{
						var xml = result.getAmountReturn;
						parseString(xml, function (err, output) {
							var results = output.results.result;
							json_responses = {"statusCode" : 200, "bid": results};
							res.send(json_responses);
						});
					}
				});
			});
		}
}


function sold(req, res){
	var user_id = req.session.user_id;
	prod_id = req.session.prod_id;
	delete req.session['prod_id'];
	
	var json_responses;

	var option = {
		ignoredNamespaces : true
	};
	var url = baseURL+"/Trans?wsdl";
	var args = {customer_id: user_id, product_id : prod_id, quantity: "1"};
	soap.createClient(url,option, function(err, client) {
		client.sold(args, function(err, result) {
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else if (result.soldReturn)
			{
				logger.event("product sold", { user: user_id, product: prod_id, quantity: "1"});
				json_responses = {"statusCode" : 200, };
				res.send(json_responses);
			}
			else
			{
				json_responses = {"statusCode" : 400};
				res.send(json_responses);
			}
		});
	});
}	

exports.emptyCart = emptyCart
exports.removeFromCart = removeFromCart
exports.addBid = addBid;
exports.addToCart = addToCart;
exports.getCart = getCart;
exports.payment1 = payment1;
exports.getCartAmount = getCartAmount;
exports.getAmount = getAmount;
exports.sold = sold;