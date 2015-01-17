
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

function invitedPeopleFromEvent(e){
    var invitedPeople = [];
    e.participationCollection.forEach(function(p) {
        if(p.user.email !== e.creator.email){
            invitedPeople.push(p);
            p.owner = p.user;
        };
    });
    return invitedPeople;
}

function setupUserPage(eventsDataStructure){
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
    
    $('#editBeginDatetime').datetimepicker();
    $('#editEndDatetime').datetimepicker();
    $("#editBeginDatetime").on("dp.change",function (e) {
        $('#editEndDatetime').data("DateTimePicker").setMinDate(e.date);
    });
    $("#editEndDatetime").on("dp.change",function (e) {
        $('#editBeginDatetime').data("DateTimePicker").setMaxDate(e.date);
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

            $('#eventListModal').modal('show');
        }
    };
    
    //check for user activation
    if (window.location.hash.replace("#/", "") === 'activated'){
        $scope.landingNotif = generateNotif('Yay!', 'You just activated your account. Now just login and enjoy CalCARE!', 'success', $sce);
        window.location.hash = '#/';
    }
    
    $scope.getCalendar = function(id) {
        $http({
            method: 'GET',
            url: "api/calendars/"+id,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            console.log(data);
            $scope.eventList = {
                events: [],
                day: ""
            };
            $scope.myEvents.time = moment().format("YYYY-MM");
            $scope.myEvents.events = {};
            $scope.myEvents.dbEvents = data;
            $scope.myEvents.events = [];
            
            data.forEach(function(e){
                e.startFormatted = moment(e.start).format('MMMM Do YYYY, h:mm A');
                e.endFormatted = moment(e.end).format('MMMM Do YYYY, h:mm A');
                
                var start = moment(moment(e.start).format("YYYY-MM-DD"));
                var end = moment(moment(e.end).format("YYYY-MM-DD"));
                do {
                    var actualDate = end.format("YYYY-MM-DD");
                    if(!(actualDate in $scope.myEvents.events)){
                        $scope.myEvents.events[end.format("YYYY-MM-DD")] = {
                            "number": 1, 
                            "badgeClass": 
                            "badge-warning", 
                            "url": "#/list/"+actualDate,
                            "dayEvents": [e]
                        };
                    } else {
                        $scope.myEvents.events[actualDate]["number"]++;
                        $scope.myEvents.events[actualDate]["dayEvents"].push(e);
                    }
                    end = end.subtract(1, 'days');
                } while(!end.isBefore(start));
            });
            
            //open the modal for the event if we have a path like #/events/{{id}}
            if (window.location.hash.replace("#/", "").substring(0,7) === 'events/'){
                //this is for the navigation through #/
                var eventId = window.location.hash.replace("#/events/", "");   
                $scope.myEvents.dbEvents.forEach( function(e) {
                    if(String(e.id) === eventId){
                        $scope.openCurrentEventModal(e);
                    }
                });
            };
            
            $(".responsive-calendar").responsiveCalendar('clearAll');
            $(".responsive-calendar").responsiveCalendar('edit', $scope.myEvents.events);
        })
        .error(function(data) {
            //TODO error in case of server error
            console.log(data);
        }); 
    };
           
    $scope.login = function() {
        $scope.loginData = true;
        $scope.landingNotif = '';
        $scope.eventCreate = {};
        $scope.eventCreate.event = {};
        $scope.eventCreate.event.outdoor = false;
        $scope.eventCreate.event.public = false;
        $scope.eventCreate.invitedPeople = [];
        $scope.eventCreate.searchedPeople = [];
        $scope.eventEdit = {};
        $scope.eventEdit.event = {};
        $scope.eventEdit.event.outdoor = false;
        $scope.eventEdit.event.public = false;
        $scope.eventEdit.invitedPeople = [];
        $scope.eventEdit.searchedPeople = [];
        $scope.currentEvent = {};
        $scope.currentEvent.invitedPeople = [];
        $scope.userSearch = {};
        $scope.userSearch.searchedPeople = [];
        $scope.userSearch.searchField = "";
        $scope.notifications = [];
        
        //get the user calendar
        $scope.getCalendar("me");
        
        setTimeout(function(){ setupUserPage($scope.myEvents); }, 10);
        
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
        eventCreate.searchedPeople = [];
        eventCreate.invitedPeople = eventCreate.invitedPeople.map(function(x){return x.id;});
        eventCreate.event.start = $('#createBeginDatetime').data("DateTimePicker").getDate()._d;
        eventCreate.event.end = $('#createEndDatetime').data("DateTimePicker").getDate()._d;
        console.log(eventCreate);
        $scope.eventCreateNotif = $sce.trustAsHtml('<div class="form-group"><div class="col-md-12 text-center"> <span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span></div></div>');
        $http({
            method: 'POST',
            url: "api/events",
            data: eventCreate,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            //TODO: event create success - insert event into events list for calendar
            $('#eventCreateModal').modal('hide');
            eventCreate = {};
            $scope.eventCreateNotif = "";
            $scope.getCalendar("me");
        })
        .error(function(data) {
            //TODO event create error
            eventCreate.invitedPeople = [];
            $scope.eventCreateNotif = generateNotif('Oh snap!', 'There was an error while validating your request. Please retry.', 'danger', $sce);
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
    
    $scope.editEvent = function(e) {
        $scope.eventEdit.event = e;
        $scope.eventEdit.invitedPeople = invitedPeopleFromEvent(e);
        $('#editBeginDatetime').data("DateTimePicker").setDate(moment(e.start).format('MM/DD/YYYY h:mm A'));
        $('#editEndDatetime').data("DateTimePicker").setDate(moment(e.start).format('MM/DD/YYYY h:mm A'));
        $('#eventListModal').modal('hide');
        $('#currentEventModal').modal('hide');
        $('#eventEditModal').modal('show');
    };
    
    $scope.eventEditSubmit = function(eventEdit) {
        delete eventEdit.searchedPeople;
        eventEdit.invitedPeople = eventEdit.invitedPeople.map(function(x){return x.participationPK.calendarsId;});
        eventEdit.event.start = $('#createBeginDatetime').data("DateTimePicker").getDate()._d;
        eventEdit.event.end = $('#createEndDatetime').data("DateTimePicker").getDate()._d;
        $http({
            method: 'PUT',
            url: "api/events/"+eventEdit.event.id,
            data: eventEdit,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            //TODO: event create success - insert event into events list for calendar
            $('#eventEditModal').modal('hide');
            $scope.openCurrentEventModal(eventEdit.event);
        })
        .error(function(data) {
            //TODO event create error
            $scope.eventEditNotif = generateNotif('Oh snap!', 'There was an error while validating your request. Please retry.', 'danger', $sce);
        });
    };
    
    $scope.deleteEvent = function(currentEvent) {
        $http({
            method: 'DELETE',
            url: "api/events/"+currentEvent.id,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            $('#currentEventModal').modal('hide');
            $('#eventListModal').modal('hide');
            $scope.getCalendar("me");
        })
        .error(function(data) {
            //TODO event create error
            $scope.currentEventNotif = generateNotif('Oh snap!', 'There was an error while validating your request. Please retry.', 'danger', $sce);
        });
    };
    
    $scope.openCurrentEventModal = function(e) {
        $scope.currentEvent = e;
        $scope.currentEvent.invitedPeople = invitedPeopleFromEvent(e);
        $('#currentEventModal').modal('show');
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
