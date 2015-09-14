function getWikiDescriptor()
{
	var name = document.URL.split("#")[1];
	return name;
}

var item = null;

var csApp = angular.module('CsApp', []);

csApp.controller('CsController', ['$scope', '$http', function($scope, $http) {
	$http.get("wiki/chest.json").then(function(obj){
		if(obj.status == 200)
		{
			$scope.item = obj.data;
			item = $scope.item;
			$scope.error = $scope.item == null;
		}
		else
		{
			$scope.error = true;
		}
	});
}]);