<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main page</title>
</head>

<body>
<h2>Hello, <%=session.getAttribute("nm")%>! Welcome to the main control center of your smart home</h2>
<h3>Your token is <%=session.getAttribute("tkn")%></h3>
<h3>Your cookie is ${cookie["CSRF-Token"].getValue()}</h3>
<button id="shbut" onclick="show()" type="button">Show secret key</button><br><br>
<div id="secret-key" style="visibility:hidden">
    Your secret key is <%=session.getAttribute("resKey")%>
    <form style="display: inline;" method="POST" action="">
        <input type="text" name="newKey" placeholder="Enter new key">
        <input type="submit" name="submit" value="submit">
    </form>
</div><br>
<a href="/logout">Logout</a>
</body>

<script>
    document.getElementById("shbut").onclick=function show(){
        var cons = document.getElementById("secret-key").style.visibility;
        if (cons === 'hidden'){
            document.getElementById("shbut").innerHTML = "Hide secret key"
            document.getElementById("secret-key").style.visibility='visible';
        }
        else if (cons === 'visible'){
            document.getElementById("shbut").innerHTML = "Show secret key"
            document.getElementById("secret-key").style.visibility='hidden';

        }
    };
</script>

</html>