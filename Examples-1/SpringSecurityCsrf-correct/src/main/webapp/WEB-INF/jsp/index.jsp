<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}"/>
    <input type="submit" value="send">
</form>

<form action="/logout" method="post">
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}"/>
    <button>logout</button>
</form>
</body>
</html>
