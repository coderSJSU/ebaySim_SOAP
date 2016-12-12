var ejs = require("ejs");
var crypto = require('crypto');
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

var bidLogger = new (winston.Logger)({
    level: 'bid', 
    levels: myCustomLevels.levels,
    transports: [new winston.transports.File({filename: 'F:/lib/bidding.log'})]
  }); 

/*
 * GET users listing.
 */

exports.list = function(req, res){
  res.send("respond with a resource");
};

exports.signOut = function(req, res){
	logger.event("user logged out", { user_id: req.session.user_id});	
	req.session.destroy();
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
};

exports.loggedIn = function(req, res){
	if(req.session.user_id){
		res.header('Cache-Control', 'no-cache, private, no-store, must-revalidate, max-stale=0, post-check=0, pre-check=0');
	
	res.render('index', { title: 'EBay', username:req.session.first_nm, last_ts: req.session.last_ts },function(err, result) {
	if (!err) {
		res.end(result);
		}
		else {
		res.end('An error occurred');
		console.log(err);
		}
		});
	}
	else{
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
	};

function register(req,res)
{
	var firstName = req.param("firstname");
	var lastName = req.param("lastname");
	var email = req.param("email");
	var password = req.param("password");
	logger.event("new user registration", { email: email, first_name: firstName});
	
	password = encrypt(password);
	var json_responses;
	var tel = req.param("tel");
	
	var option = {
			ignoredNamespaces : true	
		};
	 var url = baseURL+"/User?wsdl";
	  var args = {firstname: firstName, lastname : lastName, email: email, password: password};
	  console.log("Request:"+firstName);
	soap.createClient(url,option, function(err, client) {
	      client.register(args, function(err, result) {
	    	  console.log("Response from server:"+JSON.stringify(result));
	    	  if(err){
	    		  json_responses = {"statusCode" : 401};
	    			res.send(json_responses);
	    	  }
	    	  else if (result.registerReturn)
	    	  {
	    		  json_responses = {"statusCode" : 200};
				  req.session.first_name = firstName;
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



function checkUser(req, res){
	var email_id = req.param("email");
	var password = req.param("password");
	
	password = encrypt(password);
	var option = {
		ignoredNamespaces : true
	};
	var json_responses;
	var url = baseURL+"/User?wsdl";
	var args = {email: email_id, password: password};
	try {
		soap.createClient(url, option, function (err, client) {
			client.isUser(args, function (err, result) {
				console.log("Response from server:" + JSON.stringify(result));
				if (err) {
					json_responses = {"statusCode": 401};
					res.send(json_responses);
				}
				else {
					var xml = result.isUserReturn;
					parseString(xml, function (err, output) {
						var results = output.results.result;
						if (results.length > 0) {
							req.session.user_id = results[0].$.cust_id;
							req.session.first_nm = results[0].$.first_nm;
							req.session.last_ts = results[0].$.date;
							logger.event("user logged in", {user_id: req.session.user_id});
							json_responses = {"statusCode": 200};
							res.send(json_responses);
						}
						else {
							json_responses = {"statusCode": 400};
							res.send(json_responses);
						}
					});
				}

			});
		});
	}
	catch(err){
		json_responses = {"statusCode": 500};
		res.send(json_responses);
	}
}

function fetchData(callback,sqlQuery,key){

	}

function encrypt(text){
	var algorithm = 'aes-256-ctr';
	var password = 'd6F3Efeq';
	
	var cipher = crypto.createCipher(algorithm,password)
	  var crypted = cipher.update(text,'utf8','hex')
	  crypted += cipher.final('hex');
	return crypted;
}

function decrypt(text){
		var algorithm = 'aes-256-ctr';
		var password = 'd6F3Efeq';
		
	  var decipher = crypto.createDecipher(algorithm,password)
	  var dec = decipher.update(text,'hex','utf8')
	  dec += decipher.final('utf8');
	  return dec;
	}


exports.checkUser = checkUser;
exports.register=register;