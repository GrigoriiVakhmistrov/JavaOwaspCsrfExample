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
    <input type="hidden" required name="CSRF-Token" value="${requestScope.CSRF}">
    <input type="text" name="trl" id="name" placeholder="Enter your name">
    <input type="password" required name="pswd" id="password" placeholder="Enter password">
    <input type="submit" name="submit" value="Start!">
</form>
</body>
</html>