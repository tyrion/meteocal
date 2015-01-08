(function() {

var calApp = angular.module("CalCAREApp", []);

calApp.controller("CalendarController", function ($scope, $http, $log) {
    $scope.bodyText = "Debug Text.";
    $scope.loginData = false;
    
    $scope.login = function(data) {
        $scope.loginData = true;
    };
    $scope.logout = function() {
        $scope.loginData = false;
    };

    $scope.signup = function(email, password, givenName, familyName) {
        var data = {
            email: email,
            password: password,
            givenName: givenName,
            familyName: familyName
        };
        
        $http.post("api/users/", data)
        .success(function(data) {
            $scope.login(data);
            //$scope.token = res.data.token;
        })
        .error(function(data) {
            $scope.bodyText = "Error";
        });
    };
});

})();
