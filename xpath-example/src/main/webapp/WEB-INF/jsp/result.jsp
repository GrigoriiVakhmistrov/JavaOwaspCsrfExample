<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Result</title>
</head>
<body>
<h1>Xml file has been uploaded successfully</h1>
<h2>Search result:</h2>
<br/>
<p><%=request.getAttribute("result")%></p>
<br/>
<a href="/">return to previous page</a>
</body>
</html>