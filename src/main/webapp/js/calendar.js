
function positionBG(){
    if(($(document).width())/($(document).height()) >= 3648/2048){
        $("#landing").css('background-size', '100% auto');
    }
    else{
        $("#landing").css('background-size', 'auto 100%');
    }
}

(function() {

var calApp = angular.module("CalCAREApp", ['ngSanitize']);

calApp.controller("CalendarController", function ($scope, $http, $log) {
    $scope.errorText = '<div class="alert alert-danger" role="alert">There was an error while validating your request. Please retry.</div>';
    $scope.loginData = true;
    
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
    //$("#landing").css('background', 'url(img/background.jpg) no-repeat center');
    positionBG();
    $(window).resize(function(){
        positionBG();
    });
    
    $(".responsive-calendar").responsiveCalendar({
        time: '2013-05',
        events: {
            "2013-04-30": {"number": 5, "badgeClass": "badge-warning", "url": "http://w3widgets.com/responsive-calendar"},
            "2013-04-26": {"number": 1, "badgeClass": "badge-warning", "url": "http://w3widgets.com"}, 
            "2013-05-03": {"number": 1, "badgeClass": "badge-error"}, 
            "2013-06-12": {"class": "active special"},
            "2013-06-23": {},
            "2013-04-30": {
                "number": 2, 
                "badgeClass": 
                "badge-warning", 
                "url": "http://w3widgets.com/responsive-calendar",
                "dayEvents": [
                    {
                      "name": "Important meeting",
                      "hour": "17:30" 
                    },
                    {
                      "name": "Morning meeting at coffee house",
                      "hour": "08:15" 
                    }
                ]
            }
        }
    });
    
    $("#nextMonthButton").click(function(){
        $(".responsive-calendar").responsiveCalendar('next');
    });
    $("#prevMonthButton").click(function(){
        $(".responsive-calendar").responsiveCalendar('prev');
    });
});
