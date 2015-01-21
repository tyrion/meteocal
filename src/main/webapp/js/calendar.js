
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

function generateLoading($sce){
    return $sce.trustAsHtml('<div class="form-group"><div class="col-md-12 text-center"> <span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span></div></div>');
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
    
    //check if we have a reset token
    if (window.location.hash.replace("#/", "").substring(0,12) === 'reset?token='){
        $scope.newPasswordStruct = {resetToken: window.location.hash.replace("#/reset?token=", "")};
        
        $(document).ready(function() {
            $('#newPasswordForm').toggle();
        });
    };
    
    $scope.newPasswordSubmit = function(newPassword, resetToken){
        $http({
            method: 'POST',
            url: "api/auth/reset/confirm/?token="+resetToken,
            data: $.param({password: newPassword}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.landingNotif = generateNotif('Yay!', 'You have a new password! Now simply log in :)', 'success', $sce);
        })
        .error(function(data) {
            $scope.landingNotif = generateNotif('Oh snap!', 'Something went wrong while setting your new password.', 'danger', $sce);
        });
    }
    
    $scope.passwordReset = function(){
        $('#resetForm').toggle();
    };
    
    $scope.resetSubmit = function(reset){
        $scope.landingNotif = generateLoading($sce);
        $http({
            method: 'POST',
            url: "api/auth/reset/request",
            data: $.param({email: reset.email}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.landingNotif = generateNotif('Yay!', 'You have a mail with your password reset link :)', 'success', $sce);
        })
        .error(function(data) {
            $scope.landingNotif = generateNotif('Oh snap!', 'Something went wrong while resetting your password.', 'danger', $sce);
        });
    };
    
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
        
        //calendar info
        $http({
            method: 'GET',
            url: "api/calendars/mine",
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            console.log(data);
            $scope.myCal = data;
            $scope.editSettings = data.owner;
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
        $scope.eventCreateNotif = generateLoading($sce);
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
    
    $scope.updatePublicCalFlag = function(){
        $http({
            method: 'PUT',
            url: "api/calendars/me",
            data: $scope.myCal,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            $scope.settingsNotif = "";
        })
        .error(function(data) {
            $scope.settingsNotif = generateNotif('Oh snap!', 'There was an error while validating your request. Please retry.', 'danger', $sce);
        });
    };
    
    $scope.updateUser = function(user){
        $scope.settingsNotif = generateLoading($sce);
        console.log(user);
        $http({
            method: 'PUT',
            url: "api/users/me",
            data: user,
            headers: {'Authorization': 'Bearer ' + $localStorage.token}
        })
        .success(function(data) {
            $scope.settingsNotif = "";
        })
        .error(function(data) {
            console.log(data);
            $scope.settingsNotif = generateNotif('Oh snap!', 'There was an error while validating your request. Please retry.', 'danger', $sce);
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
            $scope.getCalendar("me");
            
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
            headers: {'Content-Type': 'text/plain', 'Authorization': 'Bearer ' + $localStorage.token },
            transformRequest: angular.identity
        })
        .success(function(data) {
            //TODO: calendar import success
        })
        .error(function(data) {
            //TODO calendar import failure
            console.log(data);
        });
    };
    
    // Based on an implementation here: web.student.tuwien.ac.at/~e0427417/jsdownload.html
    $scope.exportCalendar = function() {
        // Use an arraybuffer
        $http({
            method: 'GET',
            url: "api/calendars/export",
            headers: {'Authorization': 'Bearer ' + $localStorage.token},
            responseType: 'arraybuffer'
        })
        .success( function(data, status, headers) {

            var octetStreamMime = 'application/octet-stream';
            var success = false;

            // Get the headers
            headers = headers();

            // Get the filename from the x-filename header or default to "download.bin"
            var filename = headers['x-filename'] || 'myCalendar.meteocal';

            // Determine the content type from the header or default to "application/octet-stream"
            var contentType = headers['content-type'] || octetStreamMime;

            try
            {
                // Try using msSaveBlob if supported
                console.log("Trying saveBlob method ...");
                var blob = new Blob([data], { type: contentType });
                if(navigator.msSaveBlob)
                    navigator.msSaveBlob(blob, filename);
                else {
                    // Try using other saveBlob implementations, if available
                    var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
                    if(saveBlob === undefined) throw "Not supported";
                    saveBlob(blob, filename);
                }
                console.log("saveBlob succeeded");
                success = true;
            } catch(ex)
            {
                console.log("saveBlob method failed with the following exception:");
                console.log(ex);
            }

            if(!success)
            {
                // Get the blob url creator
                var urlCreator = window.URL || window.webkitURL || window.mozURL || window.msURL;
                if(urlCreator)
                {
                    // Try to use a download link
                    var link = document.createElement('a');
                    if('download' in link)
                    {
                        // Try to simulate a click
                        try
                        {
                            // Prepare a blob URL
                            console.log("Trying download link method with simulated click ...");
                            var blob = new Blob([data], { type: contentType });
                            var url = urlCreator.createObjectURL(blob);
                            link.setAttribute('href', url);

                            // Set the download attribute (Supported in Chrome 14+ / Firefox 20+)
                            link.setAttribute("download", filename);

                            // Simulate clicking the download link
                            var event = document.createEvent('MouseEvents');
                            event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
                            link.dispatchEvent(event);
                            console.log("Download link method with simulated click succeeded");
                            success = true;

                        } catch(ex) {
                            console.log("Download link method with simulated click failed with the following exception:");
                            console.log(ex);
                        }
                    }

                    if(!success)
                    {
                        // Fallback to window.location method
                        try
                        {
                            // Prepare a blob URL
                            // Use application/octet-stream when using window.location to force download
                            console.log("Trying download link method with window.location ...");
                            var blob = new Blob([data], { type: octetStreamMime });
                            var url = urlCreator.createObjectURL(blob);
                            window.location = url;
                            console.log("Download link method with window.location succeeded");
                            success = true;
                        } catch(ex) {
                            console.log("Download link method with window.location failed with the following exception:");
                            console.log(ex);
                        }
                    }

                }
            }

            if(!success)
            {
                // Fallback to window.open method
                console.log("No methods worked for saving the arraybuffer, using last resort window.open");
                window.open(httpPath, '_blank', '');
            }
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
