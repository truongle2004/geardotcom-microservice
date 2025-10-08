<#import "template.ftl" as layout>
<#import "components/link/primary.ftl" as linkPrimary>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
<#if section = "title">
${msg("loginTitle",(realm.displayName!''))}
<#elseif section = "header">
    <link href="https://fonts.googleapis.com/css?family=Muli" rel="stylesheet"/>
    <script>
        function togglePassword() {
            var x = document.getElementById("password");
            var v = document.getElementById("vi");
            if (x.type === "password") {
                x.type = "text";
                v.src = "${url.resourcesPath}/img/eye.png";
            } else {
                x.type = "password";
                v.src = "${url.resourcesPath}/img/eye-off.png";
            }
        }
    </script>
<#elseif section = "form">
<div class="box-container">
    <div>
        <p class="application-name">Welcome to Geardotcom store</p>
    </div>
    <#if realm.password>
        <div>
            <form id="kc-form-login" class="form" onsubmit="return true;" action="${url.loginAction}" method="post">
                <div class="input-group">
                    <input id="username" class="login-field" placeholder="${msg("username")}" type="text" name="username"
                           tabindex="1">
                </div>
                <div class="input-group">
                    <input id="password" class="login-field" placeholder="${msg("password")}" type="password"
                           name="password" tabindex="2">
                    <label class="visibility" id="v" onclick="togglePassword()">
                        <img id="vi" src="${url.resourcesPath}/img/eye-off.png">
                    </label>
                </div>
                <input class="submit" type="submit" value="${msg("doLogIn")}" tabindex="3">
            </form>
        </div>
        <div>
            <@linkPrimary.kw href=url.registrationUrl>
                <button class="register" type="button" tabindex="4">
                    ${msg("doRegister")}
                </button>
            </@linkPrimary.kw>
        </div>
    </#if>
    </#if>
    </@layout.registrationLayout>
