
function positionBG(){
    if(($(document).width())/($(document).height()) >= 3648/2048){
        $("#landing").css('background-size', '100% auto');
    }
    else{
        $("#landing").css('background-size', 'auto 100%');
    }
}

function generateNotif(title, body, type, $sce){
    return $sce.trustAsHtml('<div class="alert alert-' + type + ' alert-dismissible fade in" role="alert">' +
        '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' + 
        '<span aria-hidden="true">&times;</span></button> <b>' + title + '</b> ' + body +'</div>');
}

function setupUserPage(){
    $(".responsive-calendar").responsiveCalendar({
        time: '2013-05',
        events: {
            "2013-04-30": {"number": 5, "badgeClass": "badge-warning", "url": "http://w3widgets.com/responsive-calendar"},
            "2013-04-26": {"number": 1, "badgeClass": "badge-warning", "url": "http://w3widgets.com"}, 
            "2013-05-03": {"number": 1, "badgeClass": "badge-error"}, 
            "2013-06-12": {"class": "active special"},
            "2013-06-23": {},
            "2013-05-30": {
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

    $('#createBeginDatetime').datetimepicker();
    $('#createEndDatetime').datetimepicker();
    $("#createBeginDatetime").on("dp.change",function (e) {
        $('#createEndDatetime').data("DateTimePicker").setMinDate(e.date);
    });
    $("#createEndDatetime").on("dp.change",function (e) {
        $('#createBeginDatetime').data("DateTimePicker").setMaxDate(e.date);
    });
};

(function() {

var calApp = angular.module("CalCAREApp", ['ngSanitize', 'ngStorage']);

calApp.controller("CalendarController", function ($scope, $http, $sce, $localStorage) {
    $scope.landingNotif = '';
    $scope.loginData = false;
    
    //check for user activation
    if (window.location.hash.replace("#/", "") === 'activated'){
        $scope.landingNotif = generateNotif('Yay!', 'You just activated your account. Now just login and enjoy CalCARE!', 'success', $sce);
        window.location.hash = '#/';
    }
           
    $scope.login = function(data) {
        $scope.loginData = true;
        $scope.landingNotif = '';
        $scope.eventCreate = {};
        $scope.eventCreate.outdoor = false;
        $scope.eventCreate.public = false;
        $scope.eventCreate.invitedPeople = [];
        $scope.eventCreate.searchedPeople = [];
        setTimeout(function(){ setupUserPage(); }, 10);
        console.log(data);
        
        $localStorage.token = data.token;
        $http({
            method: 'GET',
            url: "api/users",
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            console.log(data);
            $scope.eventCreate.searchedPeople = data;
        })
        .error(function(data) {
            //TODO error in case of server error
            console.log(data);
        }); 
    };
    
    $scope.logout = function() {
        $scope.loginData = false;
        setTimeout(function(){ positionBG(); $scope.landingNotif = '';}, 10);
        delete $localStorage.token;
    };
    
    //check for auth token
    if (typeof $localStorage.token !== 'undefined'){
        $scope.loginData = true;
        $scope.login($localStorage);
    }

    $scope.loginSubmit = function(loginInfo) {
        $http({
            method: 'POST',
            url: "api/auth/login",
            data: $.param({email: loginInfo.email, password: loginInfo.password}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.login(data);
        })
        .error(function(data) {
            $scope.landingNotif = generateNotif('Oh snap!', 'Incorrect login.', 'danger', $sce);
        });
    };
    
    $scope.signupSubmit = function(signupData) {
        $http.post("api/users/", signupData)
        .success(function(data) {
            $scope.landingNotif = generateNotif('Welcome aboard!', 'We have sent you an email with a confirmation link, please click on it, and your account will be activated.', 'success', $sce);
        })
        .error(function(data) {
            $scope.landingNotif = generateNotif('Oh snap!', 'There was an error while validating your request. Please retry.', 'danger', $sce);
        });
    };
    
    $scope.eventCreateSubmit = function(eventCreate) {
        delete eventCreate.searchedPeople;
        eventCreate.invitedPeople = eventCreate.invitedPeople.map(function(x){return x.id;});
        eventCreate.start = $('#createBeginDatetime').data("DateTimePicker").getDate()._d;
        eventCreate.end = $('#createEndDatetime').data("DateTimePicker").getDate()._d;
        console.log(eventCreate);
        $http({
            method: 'POST',
            url: "api/events",
            data: eventCreate,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            //TODO: event create success - insert event into events list for calendar
        })
        .error(function(data) {
            //TODO event create error
        });
    };
});

})();

$(document).ready(function() {
    positionBG();
    $(window).resize(function(){
        positionBG();
    });
});
