function getWikiDescriptor()
{
	var name = document.URL.split("#")[1];
	return name;
}

var item = null;

var csApp = angular.module('CsApp', []);

csApp.controller('CsController', ['$scope', '$http', function($scope, $http) {
	$http.get("wiki/chest.json").then(function(obj){
		$scope.item = obj;
		item = $scope.item;
		$scope.error = $scope.item == null;
	});
}]);