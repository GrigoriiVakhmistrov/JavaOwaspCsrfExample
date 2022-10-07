<!DOCTYPE html>

<%response.addHeader("Cache-Control", "no-cache");%>
<%response.addHeader("Expires", "-1");%>
<%response.addHeader("Pragma", "no-cache");%>

<html lang="en">

<body>
<form method="post" autocomplete="off">
    <label>
        <input type='text' name='username' placeholder='username'/>
    </label>
    <label>
        <input type='password' name='password' placeholder='password'/>
    </label>
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}"/>
    <input type="submit" value='login'>
</form>
</body>

</html>