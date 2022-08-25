<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="https://owasp.org/www-project-csrfguard/Owasp.CsrfGuard.tld" prefix="csrf" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h2>Hi, ${{user.username}}</h2>
<h3>Welcome to your account!</h3>
<p>Balance: ${{user.balance}}</p>
<form action="/accounts" method="POST" autocomplete="off">
    <p>Transfer Money</p>
    <input type="number" name="account" placeholder="accountid">
    <input type="number" name="amount" placeholder="amount">
    <input type="submit" value="send">
    <input type="hidden"
           name="<csrf:tokenname/>"
           value="<csrf:tokenvalue uri="/"/>"
    />
</form>

<a href="/logout">logout</a>
</body>
</html>
