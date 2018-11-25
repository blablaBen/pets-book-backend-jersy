<%--
  Created by IntelliJ IDEA.
  User: Joan
  Date: 11/24/18
  Time: 7:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>


<body>

<fb:login-button
        scope="public_profile,email"
        onlogin="checkLoginState();">
</fb:login-button>
</body>
<script>
    window.fbAsyncInit = function () {
        FB.init({
            appId: '1591324134347015',
            cookie: true,
            xfbml: true,
            version: 'v3.2'
        });

        FB.AppEvents.logPageView();

    };

    (function (d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) {
            return;
        }
        js = d.createElement(s);
        js.id = id;
        js.src = "https://connect.facebook.net/en_US/sdk.js";
        fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));


    FB.getLoginStatus(function (response) {
        statusChangeCallback(response);
    });


    function checkLoginState() {
        FB.getLoginStatus(function (response) {
            statusChangeCallback(response);
        });
    }
</script>
</html>
