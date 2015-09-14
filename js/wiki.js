function getWikiDescriptor()
{
	var name = document.URL.split("#")[1];
	return name;
}

var csApp = angular.module('CsApp', []);

csApp.controller('CsController', ['$scope', '$http', function($scope, $http) {
	$http.get("wiki/chest.json").then(function(obj){
		$scope.item = obj;
		$scope.error = $scope.item == null;
	});
}]);