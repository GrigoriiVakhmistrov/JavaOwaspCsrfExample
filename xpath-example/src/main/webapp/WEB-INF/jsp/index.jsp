<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello page</title>
</head>
<body>
<h1>Upload your xml file</h1>
<form method="POST" action="/" enctype="multipart/form-data">
    <input type="file" name="file" id="file" placeholder="Select your file">
    <input type="text" name="xpath" id="xpath" placeholder="Input xpath">
    <input type="submit" name="submit" value="Load">
</form>
</body>
</html>