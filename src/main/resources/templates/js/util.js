function blank(s){
    return !s || s.length === 0;
}

function logout(ev){
    Lockr.rm("User");
    window.location.replace("/logout"); //Remove session cookie
}

function showError(err){
    if (err.response ){
        if (err.response.status === 401){
            if (Lockr.get("SignUp") || Lockr.get("User")){
                window.location.replace("/login");
            }else {
                logout();
            }
        }else if (err.response.status === 413){
             showErrorMessage("Size Uploaded Too Big");
        }else if (err.response.data && err.response.data.SWFHttpResponse.Error) {
            showErrorMessage(err.response.data.SWFHttpResponse.Error)
        }else {
            showErrorMessage(err.response.toString());
        }
    }else {
        showErrorMessage(err.toString());
    }
}

var errorTimeOut = undefined;
function showErrorMessage(msg,duration){
    let time = duration || 1500;
    $("#msg").removeClass("invisible");
    $("#msg").html(msg);
    if (errorTimeOut){
        clearTimeout(errorTimeOut);
        errorTimeOut = undefined;
    }
    return new Promise(function(resolve,reject){
        errorTimeOut = setTimeout(function(){
            $("#msg").addClass("invisible");
            resolve();
        },time);
    });

}

function sendSubscriptionToServer(subscription){
    let device = Lockr.get("device");
    let user = Lockr.get("user");
    if (subscription &&  user && user.Id) {
        api().url("/devices/save").parameters({ 'Device': { 'DeviceId': subscription, 'UserId': user.Id } }).post()
        .then(function (response) {
            if (response.Devices && response.Devices.length > 0){
                Lockr.set("device",response.Devices[0]);
                if (device && device.Id && device.Id * 1.0 > 0 && device.Id !== Lockr.get("device").Id) {
                    api().url("/devices/destroy/"+device.Id).get().then(function(response) {
                        console.log("old subscription removed");
                    }).catch(function (err){
                        console.log("old token not deleted on the server");
                    });
                }
                console.log("Subscription made on the server");
            }
        });
    }else {
        console.log("Subscription not set on server");
    }
}

function showSpinner(){
    let a = typeof Android == "undefined" ? undefined : Android ;
    if (a){
        a.showSpinner();
    }else {
        let p = $("#spinner") ;
        p.removeClass("invisible");
    }
}
function hideSpinner(){
    let a = typeof Android == "undefined" ? undefined : Android ;
    if (a){
        a.hideSpinner();
    }else{
        let p = $("#spinner") ;
        p.addClass("invisible");
    }
}

function isMobile(){
    return isAndroidApp() || isMobileBrowser();
}


function isMobileBrowser(){
    var hasTouchScreen = false;
    if ("maxTouchPoints" in navigator) {
        hasTouchScreen = navigator.maxTouchPoints > 0;
    } else if ("msMaxTouchPoints" in navigator) {
        hasTouchScreen = navigator.msMaxTouchPoints > 0;
    } else {
        var mQ = window.matchMedia && matchMedia("(pointer:coarse)");
        if (mQ && mQ.media === "(pointer:coarse)") {
            hasTouchScreen = !!mQ.matches;
        } else if ('orientation' in window) {
            hasTouchScreen = true; // deprecated, but good fallback
        } else {
            // Only as a last resort, fall back to user agent sniffing
            var UA = navigator.userAgent;
            hasTouchScreen = (
                /\b(BlackBerry|webOS|iPhone|IEMobile)\b/i.test(UA) ||
                /\b(Android|Windows Phone|iPad|iPod)\b/i.test(UA) ||
                /\b(Mobile)\b/i.test(UA)
            );
        }
    }

    return hasTouchScreen;
}

function copyAddress(from , to){
    to.LongName = from.LongName; 
    to.AddressLine1 = from.AddressLine1; 
    to.AddressLine2 = from.AddressLine2; 
    to.AddressLine3 = from.AddressLine3; 
    to.AddressLine4 = from.AddressLine4; 
    to.City = from.City; 
    to.PinCode = from.PinCode; 
    to.PhoneNumber = from.PhoneNumber; 
    to.Email = from.Email; 
    to.Lat = from.Lat; 
    to.Lng = from.Lng; 
}
