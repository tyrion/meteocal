(function() {

var calApp = angular.module("CalCAREApp", ['ngSanitize']);

calApp.controller("CalendarController", function ($scope, $http, $log) {
    $scope.errorText = "";
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
            $scope.errorText = '<div class="alert alert-danger" role="alert">There was an error while validating your request. Please retry.</div>';
        });
    };
});

})();

$(document).ready(function() {
    //load background
    $("#landing").css('background', 'url(img/background.jpg) no-repeat center');

    function positionBG(){
        if(($(document).width())/($(document).height()) >= 3648/2048){
            $("#landing").css('background-size', '100% auto');
        }
        else{
            $("#landing").css('background-size', 'auto 100%');
        }
    }
    positionBG();
    $(window).resize(function(){
        positionBG();
    });
});
