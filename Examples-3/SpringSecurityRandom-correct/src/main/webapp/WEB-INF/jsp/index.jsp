<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello page</title>
</head>
<body>
<h1>Welcome to SmartHome!</h1>
<form method="POST" action="/">
    <input type="text" name="trl" id="name" placeholder="Enter your name">
    <input type="submit" name="submit" value="Start!">
    <input type="hidden" name="csrf_token" value="<%=session.getAttribute("tkn")%>">
</form>
</body>
</html>