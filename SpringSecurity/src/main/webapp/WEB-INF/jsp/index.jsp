<!DOCTYPE html>
<%@ taglib uri="https://owasp.org/www-project-csrfguard/Owasp.CsrfGuard.tld" prefix="csrf" %>

<%response.addHeader("Cache-Control", "no-cache");%>
<%response.addHeader("Expires", "-1");%>
<%response.addHeader("Pragma", "no-cache");%>

<html lang="en">

<body>
<form method="post">
    <input type="hidden"
           name="<csrf:tokenname/>"
           value="<csrf:tokenvalue uri="/"/>"
    />
    <input type="submit">
</form>
</body>

</html>