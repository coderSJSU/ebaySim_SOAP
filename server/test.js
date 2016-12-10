var assert = require('assert');
var c = require('chai');
var e = require('expect');
var request = require('request');
var 	http = require('chai-http');
c.use(http);

describe('/POST checkUser', function() {
	var req = "http://localhost:3000"
	it("sign in", function(done){
		c.request(req)
		.post('/checkUser')
		.send({"email":"test1@test2.com", "password":"1234"})
		.end(function(err,res){
			assert.equal(res.statusCode, 200);
			done();
		})
	})	
});

describe('/POST cart', function() {
	var req = "http://localhost:3000"
	it("post cart", function(done){
		c.request(req)
		.post('/getCart')
		.end(function(err,res){
			assert.equal(res.statusCode, 200);
			done();
		})
	})	
});


describe('/GET getItemsBought', function() {
	var req = "http://localhost:3000"
	it("get bought items", function(done){
		c.request(req)
		.get('/getItemsBought')
		.end(function(err,res){
			assert.equal(res.statusCode, 200);
			done();
		})
	})	
});


describe('/GET getItemsForSale', function() {
	var req = "http://localhost:3000"
	it("get items for sale", function(done){
		c.request(req)
		.post('/getItemsForSale')
		.end(function(err,res){
			assert.equal(res.statusCode, 200);
			done();
		})
	})	
});


describe('/GET getUserInfo', function() {
	var req = "http://localhost:3000"
	it("get user information", function(done){
		c.request(req)
		.get('/getUserInfo')
		.end(function(err,res){
			assert.equal(res.statusCode, 200);
			done();
		})
	})	
});
		
	