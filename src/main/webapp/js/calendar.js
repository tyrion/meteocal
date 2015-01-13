
function positionBG(){
    if(($(document).width())/($(document).height()) >= 3648/2048){
        $("#landing").css('background-size', '100% auto');
    }
    else{
        $("#landing").css('background-size', 'auto 100%');
    }
}

function addLeadingZero(num) {
    if (num < 10) {
        return "0" + num;
    } else {
        return "" + num;
    }
}

function generateNotif(title, body, type, $sce){
    return $sce.trustAsHtml('<div class="alert alert-' + type + ' alert-dismissible fade in" role="alert">' +
        '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' + 
        '<span aria-hidden="true">&times;</span></button> <b>' + title + '</b> ' + body +'</div>');
}

function setupUserPage(eventsDataStructure){
    var eventsDataStructure2 = {
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
    };
    $(".responsive-calendar").responsiveCalendar(eventsDataStructure);

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
    $("#importCalendarField").attr("class","filestyle").attr("data-buttonName", "btn-primary");
};

(function() {

var calApp = angular.module("CalCAREApp", ['ngSanitize', 'ngStorage']);

calApp.controller("CalendarController", function ($scope, $http, $sce, $localStorage) {
    $scope.landingNotif = '';
    $scope.loginData = false;
    $scope.myEvents = {
        onActiveDayClick: function(events) {
            var thisDayEvent, key;
            key = $(this).data('year')+'-'+addLeadingZero( $(this).data('month') )+'-'+addLeadingZero( $(this).data('day') );
            thisDayEvent = events[key];

            $scope.eventList.day = moment(key).format("MMMM Do");
            $scope.eventList.events = thisDayEvent.dayEvents;
            console.log($scope.eventList.day);

            $('#eventListModal').modal('show');
        }
    };
    
    //check for user activation
    if (window.location.hash.replace("#/", "") === 'activated'){
        $scope.landingNotif = generateNotif('Yay!', 'You just activated your account. Now just login and enjoy CalCARE!', 'success', $sce);
        window.location.hash = '#/';
    }
           
    $scope.login = function() {
        $scope.loginData = true;
        $scope.landingNotif = '';
        $scope.eventCreate = {};
        $scope.eventCreate.event = {};
        $scope.eventCreate.event.outdoor = false;
        $scope.eventCreate.event.public = false;
        $scope.eventCreate.invitedPeople = [];
        $scope.eventCreate.searchedPeople = [];
        $scope.userSearch = {};
        $scope.userSearch.searchedPeople = [];
        $scope.userSearch.searchField = "";
        $scope.notifications = [];
        $scope.eventList = {
            events: [],
            day: ""
        };
        $scope.myEvents.time = moment().format("YYYY-MM");
        $scope.myEvents.events = {};
        
        setTimeout(function(){ setupUserPage($scope.myEvents); }, 10);
        
        //get the user calendar
        $http({
            method: 'GET',
            url: "api/calendars/me",
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            $scope.myEvents.owner = data.owner;
            data.participations.forEach(function(p){
                var start = moment(moment(p.event.start).format("YYYY-MM-DD"));
                var end = moment(moment(p.event.end).format("YYYY-MM-DD"));
                do {
                    var actualDate = end.format("YYYY-MM-DD");
                    if(!(actualDate in $scope.myEvents.events)){
                        $scope.myEvents.events[end.format("YYYY-MM-DD")] = {
                            "number": 1, 
                            "badgeClass": 
                            "badge-warning", 
                            "url": "#/list/"+actualDate,
                            "ng-click":"robba()",
                            "dayEvents": [p.event]
                        };
                    } else {
                        $scope.myEvents.events[actualDate]["number"]++;
                        $scope.myEvents.events[actualDate]["dayEvents"].push(p.event);
                    }
                    end = end.subtract(1, 'days');
                } while(!end.isBefore(start));
            });
            
            console.log($scope.myEvents);
        })
        .error(function(data) {
            //TODO error in case of server error
            console.log(data);
        }); 
        
        //get the user notifications
        $http({
            method: 'GET',
            url: "api/notifications",
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            console.log(data);
            $scope.notifications = data;
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
        $scope.login();
    }

    $scope.loginSubmit = function(loginInfo) {
        $http({
            method: 'POST',
            url: "api/auth/login",
            data: $.param({email: loginInfo.email, password: loginInfo.password}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $localStorage.token = data.token;
            $scope.login();
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
        eventCreate.event.start = $('#createBeginDatetime').data("DateTimePicker").getDate()._d;
        eventCreate.event.end = $('#createEndDatetime').data("DateTimePicker").getDate()._d;
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
    
    $scope.searchUsers = function(searchObject) {
        $http({
            method: 'GET',
            url: "api/calendars/?search="+searchObject.searchField,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            searchObject.searchedPeople = data;
            if(searchObject.invitedPeople){
                data.forEach(function(cal) {
                    searchObject.invitedPeople.forEach(function(inv) {
                        if(cal.owner.email === inv.owner.email){
                            searchObject.searchedPeople.pop(cal);
                        };
                    });
                });
            };
        })
        .error(function(data) {
            //TODO error in case of server error
            console.log(data);
            searchObject.searchedPeople = [];
        }); 
    };
    $(window).bind( 'hashchange', function(e) {
        if (window.location.hash.replace("#/", "").substring(0,7) === 'events/'){
            //this is for the navigation through #/
            var eventId = window.location.hash.replace("#/events/", "");   
            console.log(eventId);
        };
    });
    
    $scope.getCalendar = function(id) {
        //TODO load others calendar
        console.log(id);
    };
    
    $scope.importCalendar = function(files) {
        var fd = new FormData();
        //Take the first selected file
        fd.append("file", files[0]);

        $http.post("api/calendars/import", fd, {
            withCredentials: true,
            headers: {'Content-Type': undefined, 'Authorization': 'Bearer ' + $localStorage.token },
            transformRequest: angular.identity
        })
        .success(function(data) {
            //TODO: calendar import success
        })
        .error(function(data) {
            //TODO calendar import failure
        });
    };
    
    $scope.exportCalendar = function(){
        $http({
            method: 'GET',
            url: "api/calendars/export",
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            console.log(data);
            //TODO: serve file
        })
        .error(function(data) {
            console.log(data);
            $scope.editSettingsNotif = generateNotif('Oh snap!', 'There was an error while validating your request. Please retry.', 'danger', $sce);
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
