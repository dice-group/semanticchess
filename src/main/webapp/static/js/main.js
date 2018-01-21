var appChess = angular.module('appChess',['ngRoute']);

appChess.config(function($routeProvider){
	$routeProvider
	.when("/game/:uri*",{
		templateUrl : "/game.html",
		controller : "appCtrl"
	})
	.when("/",{
		templateUrl : "/search.html",
		controller : "appCtrl"
	});
});

appChess.filter('escape', function() {
	  return window.encodeURIComponent;
});

//controller for the search box
appChess.controller('appCtrl', function($scope, $location, $http){	
	$scope.query = '';
	$scope.result = '';
	$scope.resultCounter = 0;
	$scope.errorFound = false;
	$scope.error = '';
	$scope.loading = false;
	$scope.xml = "";
	
	//sparql queries
	$scope.getSparqlResults = function(){
		$scope.result = "";
		$scope.json = "";
		$scope.xml = "";
		$scope.loading = true;
		
	    $http({
	        'url' : '/sparql/',
	        'method' : 'POST',
	        'headers': {'Content-Type' : 'application/json'},
	        'data' : $scope.query
	    }).then(function(data){	    	
	    	if(!(typeof data == undefined)){
		    	$scope.errorFound = false;
		    	$scope.result = data;
		        $scope.error = '';
		        $scope.resultCounter = $scope.result.data.results.bindings.length;	
	    	}else {
	    		$scope.error = data; 
	    	}      
	    	$scope.loading = false;
	    }).catch(function (err) {
		    	$scope.resultCounter = 0;
		    	$scope.result = "";
		    	$scope.errorFound = true;
		    	$scope.loading = false;
	    });    
	};
	
	//user queries
	$scope.getQueryResults = function(){
		$scope.result = "";
		$scope.json = "";
		$scope.xml = "";
		$scope.loading = true;
	    $http({
	        'url' : '/query/',
	        'method' : 'POST',
	        'headers': {'Content-Type' : 'application/json'},
	        'data' : $scope.query
	    }).then(function(data){	    	
	    	if(!(typeof data == undefined)){
		    	$scope.errorFound = false;
		    	$scope.result = data;
		        $scope.error = '';
		        $scope.resultCounter = $scope.result.data.results.bindings.length;	
	    	}else {
	    		$scope.error = data; 
	    	}  
	    	$scope.loading = false;
	    }).catch(function (err) {
		    	$scope.resultCounter = 0;
		    	$scope.result = "";
		    	$scope.error = err.status;
		    	console.log($scope.error);
		    	$scope.errorFound = true;   
		    	$scope.loading = false;
	    });     
	};
	
	//user queries xml
	$scope.getQueryXMLResults = function(){
		$scope.result = "";
		$scope.json = "";
		$scope.xml = "";
		$scope.loading = true;
	    $http({
	        'url' : '/queryxml/',
	        'method' : 'POST',
	        'headers': {'Content-Type' : 'application/json'},
	        'data' : $scope.query
	    }).then(function(data){	    	
	    	if(!(typeof data == undefined)){
		    	$scope.errorFound = false;
		        $scope.xml = data;
		        $scope.error = '';
		        console.log( $scope.xml);
	    	}else {
	    		$scope.error = data; 
	    	}  
	    	$scope.loading = false;
	    }).catch(function (err) {
		    	$scope.resultCounter = 0;
		    	$scope.xml = "";
		    	$scope.error = err.status;
		    	console.log($scope.error);
		    	$scope.errorFound = true;   
		    	$scope.loading = false;
	    });     
	};
	
	//sparql queries xml
	$scope.getSparqlXMLResults = function(){
		$scope.result = "";
		$scope.json = "";
		$scope.xml = "";
		$scope.loading = true;
	    $http({
	        'url' : '/sparqlxml/',
	        'method' : 'POST',
	        'headers': {'Content-Type' : 'application/json'},
	        'data' : $scope.query
	    }).then(function(data){	    	
	    	if(!(typeof data == undefined)){
		    	$scope.errorFound = false;
		        $scope.xml = data;
		        $scope.error = '';
		        console.log( $scope.xml);
	    	}else {
	    		$scope.error = data; 
	    	}  
	    	$scope.loading = false;
	    }).catch(function (err) {
		    	$scope.resultCounter = 0;
		    	$scope.xml = "";
		    	$scope.error = err.status;
		    	console.log($scope.error);
		    	$scope.errorFound = true;   
		    	$scope.loading = false;
	    });     
	};
	
	//user queries json
	$scope.getQueryJSONResults = function(){
		$scope.result = "";
		$scope.json = "";
		$scope.xml = "";
		$scope.loading = true;
	    $http({
	        'url' : '/query/',
	        'method' : 'POST',
	        'headers': {'Content-Type' : 'application/json'},
	        'data' : $scope.query
	    }).then(function(data){	    	
	    	if(!(typeof data == undefined)){
		    	$scope.errorFound = false;
		        $scope.json = JSON.stringify(data.data, null, 2);
		        $scope.error = '';
		        //console.log($scope.json);
	    	}else {
	    		$scope.error = data; 
	    	}  
	    	$scope.loading = false;
	    }).catch(function (err) {
		    	$scope.resultCounter = 0;
		    	$scope.json = "";
		    	$scope.error = err.status;
		    	console.log($scope.error);
		    	$scope.errorFound = true;   
		    	$scope.loading = false;
	    });     
	};
	
	//sparql queries json
	$scope.getSparqlJSONResults = function(){
		$scope.result = "";
		$scope.json = "";
		$scope.xml = "";
		$scope.loading = true;
	    $http({
	        'url' : '/sparql/',
	        'method' : 'POST',
	        'headers': {'Content-Type' : 'application/json'},
	        'data' : $scope.query
	    }).then(function(data){	    	
	    	if(!(typeof data == undefined)){
		    	$scope.errorFound = false;
		    	$scope.json = JSON.stringify(data.data, null, 2);
		        $scope.error = '';
		        //console.log( $scope.xml);
	    	}else {
	    		$scope.error = data; 
	    	}  
	    	$scope.loading = false;
	    }).catch(function (err) {
		    	$scope.resultCounter = 0;
		    	$scope.json = "";
		    	$scope.error = err.status;
		    	console.log($scope.error);
		    	$scope.errorFound = true;   
		    	$scope.loading = false;
	    });     
	};
	
	//game uri queries
	$scope.getGame = function(uri){
		$scope.result = "";
		$scope.xml = "";
		$scope.loading = true;
	    $http({
	        'url' : '/uri/',
	        'method' : 'POST',
	        'headers': {'Content-Type' : 'application/json'},
	        'data' : uri
	    }).then(function(data){ 
	    	if(!(typeof data == undefined)){
		    	$scope.errorFound = false;
		    	$scope.result = data;
		        $scope.error = '';
		        
	    	}else {
	    		$scope.error = data; 
	    	} 
	    	$scope.loading = false;
	    }).catch(function (err) {
	    	console.log(err);   
		    	$scope.resultCounter = 0;
		    	$scope.result = "";
		    	$scope.errorFound = true; 
		    	$scope.loading = false;
	    });     
	};
});

//controller to show the specified game
appChess.controller('gameCtrl', function($scope, $location, $http, $routeParams){
	$scope.uri = $routeParams.uri;
	$scope.game = "";
	$scope.loading = true;
	
	//get pgn from game
    $http({
        'url' : '/uri/',
        'method' : 'POST',
        'headers': {'Content-Type' : 'application/json'},
        'data' : $scope.uri
    }).then(function(data){   
    	if(!(typeof data == undefined)){
	    	$scope.errorFound = false;
	    	$scope.result = data;
	    	$scope.game = $scope.result.data.results.bindings[0].answer.value.replace(/\'/g,"\"");
	        $scope.error = '';
	        
	        var pgn = $scope.game;
	        var cfg = { position: '', pgn: pgn, locale: 'en', pieceStyle: 'merida' };
	        var board = pgnView('board', cfg);
	        
	        $scope.loading = false;
    	}else {
    		$scope.error = data; 
    	}    
    }).catch(function (err) {
    	console.log(err);   
	    	$scope.resultCounter = 0;
	    	$scope.result = "";
	    	$scope.errorFound = true;   
	    	$scope.loading = false;
    });	
});

//resizes the textarea if more space is useful (e.g sparql queries)
// source: https://codepen.io/vsync/pen/czgrf
var textarea = document.querySelector('textarea');

try{
textarea.addEventListener('keydown', autosize);
}catch(err){}

function autosize(){
  var el = this;
  setTimeout(function(){
    el.style.cssText = 'height:auto';
    // for box-sizing other than "content-box" use:
    // el.style.cssText = '-moz-box-sizing:content-box';
    el.style.cssText = 'height:' + el.scrollHeight + 'px';
  },0);
}




