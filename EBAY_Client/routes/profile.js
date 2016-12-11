var ejs = require("ejs");
var mysql = require('./mysql');

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

function getItemsForSale(req, res){
	
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
		var url = baseURL+"/Profile?wsdl";
		var args = {cust_id: cust_id};
		soap.createClient(url,option, function(err, client) {
			client.getItemsForSale(args, function(err, result) {
				console.log("Response from server:"+JSON.stringify(result));
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else{
					var xml = result.getItemsForSaleReturn;
					parseString(xml, function (err, output) {
						var results = output.results.result;
						if (results.length>0){
							json_responses = {"statusCode" : 200};
							res.send(json_responses);
						}
						else{
							json_responses = {"statusCode" : 400};
							res.send(json_responses);
						}
					});
				}
			});
		});

	}
}


function getItemsBought(req, res){
	
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
		var url = baseURL+"/Profile?wsdl";
		var args = {cust_id: cust_id};
		soap.createClient(url,option, function(err, client) {
			client.getItemsBought(args, function(err, result) {
				console.log("Response from server:"+JSON.stringify(result));
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else{
					var xml = result.getItemsBoughtReturn;
					parseString(xml, function (err, output) {
						var results = output.results.result;
						if (results.length>0){
							json_responses = {"statusCode" : 200};
							res.send(json_responses);
						}
						else{
							json_responses = {"statusCode" : 400};
							res.send(json_responses);
						}
					});
				}
			});
		});
	}
}

function getUserInfo(req, res){
	
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
		var url = baseURL+"/Profile?wsdl";
		var args = {cust_id: cust_id};
		soap.createClient(url,option, function(err, client) {
			client.getUserInfo(args, function(err, result) {
				console.log("Response from server:"+JSON.stringify(result));
				if(err){
					json_responses = {"statusCode" : 401};
					res.send(json_responses);
				}
				else{
					var xml = result.getUserInfoReturn;
					parseString(xml, function (err, output) {
						var results = output.results.result;
						if (results.length>0){
							json_responses = {"statusCode" : 200, data : results};
							res.send(json_responses);
						}
						else{
							json_responses = {"statusCode" : 400, data :""};
							res.send(json_responses);
						}
					});
				}
			});
		});
	}
}


function saveProfile(req,res)
{
	var cust_id = req.session.user_id;
	var cust_updated = 0;
	var add_updated = 0;
	var month = req.param("month");
	var year = req.param("year");
	var day = req.param("day");
	var address = req.param("address");
	var city = req.param("city");
	var country = req.param("country");

	var url = baseURL+"/Profile?wsdl";
	var args = {firstname: firstName, lastname : lastName, email: email, password: password};
	var option = {
		ignoredNamespaces : true
	};
	soap.createClient(url,option, function(err, client) {
		client.saveProfile(args, function(err, result) {
			console.log("Response from server:"+JSON.stringify(result));
			if(err){
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
			else if (result.saveProfileReturn)
			{
				logger.event("profile updated", { user: cust_id});
				json_responses = {"statusCode" : 200};
				res.send(json_responses);
			}
			else
			{
				logger.event("profile updated", { user: cust_id});
				json_responses = {"statusCode" : 200};
				res.send(json_responses);
			}
		});
	});

		
}


exports.getItemsForSale = getItemsForSale;
exports.getItemsBought = getItemsBought;
exports.getUserInfo = getUserInfo;
exports.saveProfile = saveProfile;