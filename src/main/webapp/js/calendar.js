
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

var calApp = angular.module("CalCAREApp", ['ngSanitize']);

calApp.controller("CalendarController", function ($scope, $http, $sce) {
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
        $scope.create = {};
        $scope.create.invitedPeople = [];
        $scope.create.searchedPeople = [];
        setTimeout(function(){ setupUserPage(); }, 10);
        console.log(data);
        //TODO: salvare token
        
        $scope.token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoyNX0.FLQrIKdgdDJwA5Mwar8bsXf1a2-hYVGdosZ7Zht9fXw';
        $http({
            method: 'GET',
            url: "api/users",
            headers: {'Authorization': 'Bearer ' + $scope.token}
        })
        .success(function(data) {
            console.log(data);
            $scope.create.searchedPeople = data;
        })
        .error(function(data) {
            $("#calendarContainer").html(generateNotif('Oh snap!', 'Incorrect login.', 'danger', $sce));
        }); 
    };
    //$scope.login([]);
    
    $scope.logout = function() {
        $scope.loginData = false;
        setTimeout(function(){ positionBG(); $scope.landingNotif = '';}, 10);
        //TODO: cancellare il token
    };

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
});

})();

$(document).ready(function() {
    //load background
    //$("#landing").css('background', 'url(img/background.jpg) no-repeat center');
    positionBG();
    $(window).resize(function(){
        positionBG();
    });
});
