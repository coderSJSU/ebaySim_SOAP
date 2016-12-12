var ejs = require("ejs");
var parseString = require('xml2js').parseString;
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

function productDetails(req, res){
	
	var prod_id = req.query.prod_id;
	var customer_id = req.query.user_id;
	var option = {
		ignoredNamespaces : true
	};
	var url = baseURL+"/Product?wsdl";
	var args = {prod_id: prod_id, customer_id : customer_id};
	soap.createClient(url,option, function(err, client) {
		client.productDetails(args, function(err, result) {
			console.log("Response from server:"+JSON.stringify(result));
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else
			{
				var xml = result.productDetailsReturn;
				parseString(xml, function (err, output) {
					var results = output.results.result;
					json_responses = {"statusCode" : 400};
					res.send(json_responses);
				});
			}
		});
	});

}



function showProducts(req, res){
	
	//var cust_id = req.session.user_id;

	var cust_id = req.query.user_id;
	//var cust_id = req.session.user_id;
	var cat_id = req.query.cat;
	var json_responses;
	var option = {
		ignoredNamespaces : true
	};
	var url = baseURL+"/Product?wsdl";
	var args = {cust_id: cust_id, category_id : cat_id};
	soap.createClient(url,option, function(err, client) {
		client.showProducts(args, function(err, result) {
			console.log("Response from server:"+JSON.stringify(result));
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else
			{
				var xml = result.showProductsReturn;
				parseString(xml, function (err, output) {
					var results = output.results.result;

					ejs.renderFile('./views/products.ejs', {  username:req.session.first_nm, data:"", title:'EBay' },function(err, result) {
						if (!err) {
							res.end(result);
						}
						else {
							res.end('An error occurred');
							console.log(err);
						}
					});
				});
				ejs.renderFile('./views/products.ejs', {  username:req.session.first_nm, data:"", title:'EBay' },function(err, result) {
					if (!err) {
						res.end(result);
					}
					else {
						res.end('An error occurred');
						console.log(err);
					}
				});
			}
		});
	});
	

}

function prodDescription(req, res){
	
	var cust_id = req.session.user_id;
	var type = req.query.type;
	
	var json_responses ;
	var json_responses;
	var option = {
		ignoredNamespaces : true
	};
	var url = baseURL+"/Product?wsdl";
	var args = {cust_id: cust_id, category_id : cat_id};
	soap.createClient(url,option, function(err, client) {
		client.prodDescription(args, function(err, result) {
			console.log("Response from server:"+JSON.stringify(result));
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else
			{
				var xml = result.prodDescriptionReturn;
				parseString(xml, function (err, output) {
					var results = output.results.result;

					ejs.renderFile('./views/sellProduct.ejs', {  username:req.session.first_nm },function(err, result) {
						if (!err) {
							res.end(result);
						}
						else {
							res.end('An error occurred');
							console.log(err);
						}
					});
				});
				ejs.renderFile('./views/sellProduct.ejs', {  username:req.session.first_nm},function(err, result) {
					if (!err) {
						res.end(result);
					}
					else {
						res.end('An error occurred');
						console.log(err);
					}
				});
			}
		});
	});

}


function addProduct(req,res)
{
	var json_responses;
	var cust_id = req.session.user_id;
	if(cust_id == undefined){
	json_responses = {"statusCode" : 405};
	res.send(json_responses);
	}
	else{
		
	//var cust_id = req.session.cust_id;
	var label = req.param("label");
	var desc = req.param("desc");
	var quantity = req.param("quantity");
	var brand = req.param("brand");
	var price = req.param("price");
	var condition = req.param("condition");
	var is_auction = req.param("is_auction");
	var is_fixed = req.param("is_fixed");
	var freeShip = req.param("freeShip");
	var ship_price = '5';
	if(freeShip)
		ship_price = '0';
	
	if(is_auction == '1'){
		price = '0';
		quantity = '1';
	}
	var startingPrice = req.param("startingPrice");
	if(is_auction == '0')
		startingPrice = '0';
	var category_id = req.session.cat_id;

		var option = {
			ignoredNamespaces : true
		};
		var url = baseURL+"/Product?wsdl";
		var args = {label: label, description : desc, brand: brand, quantity: quantity};
		soap.createClient(url,option, function(err, client) {
			client.addProduct(args, function(err, result) {
				console.log("Response from server:"+JSON.stringify(result));
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else if (result.addProductReturn)
				{
					logger.event("product for sale", { user_id: cust_id, label:label, description:desc, brand:brand});
					json_responses = {"statusCode" : 200};
					res.send(json_responses);
				}
				else
				{
					logger.event("product for sale", { user_id: cust_id, label:label, description:desc, brand:brand});
					json_responses = {"statusCode" : 200};
					res.send(json_responses);
				}
			});
		});

	}
}


function paymentNow(req, res){
	
	var prod_id = req.query.prod_id;
	var customer_id = req.session.user_id;
	var json_responses;
	
	if(customer_id == undefined){
		res.render('signin',function(err, result) {
			// render on success
			if (!err) {
			res.end(result);
			}
			// render or error
			else {
			res.end('An error occurred');
			console.log(err);
			}
			});
	}
	else{
	
	ejs.renderFile('./views/checkout.ejs', {  username:req.session.first_nm, prod_id:prod_id },function(err, result) {
		if (!err) {
			req.session.prod_id = prod_id;
			req.session.is_urgent = 1;	
			res.end(result);
			}
			else {
			res.end('An error occurred');
			console.log(err);
			}
			});
	
	}
}

exports.paymentNow= paymentNow;
exports.addProduct = addProduct;
exports.prodDescription = prodDescription;
exports.showProducts = showProducts;
exports.productDetails = productDetails;