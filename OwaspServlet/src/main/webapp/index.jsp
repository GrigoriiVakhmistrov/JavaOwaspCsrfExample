<!DOCTYPE html>
<%@ taglib uri="https://owasp.org/www-project-csrfguard/Owasp.CsrfGuard.tld" prefix="csrf" %>

<%response.addHeader("Cache-Control", "no-cache");%>
<%response.addHeader("Expires", "-1");%>
<%response.addHeader("Pragma", "no-cache");%>

<html lang="en">

<head>
    <script type="text/javascript">
        function ajax() {
            const xhr = new XMLHttpRequest();

            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        alert('200: CSRF check passed!');
                    } else {
                        alert('CSRF check FAILED!\nStatus code: ' + xhr.status);
                    }
                }
            }

            xhr.open('POST', 'result.jsp', true);
            xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
            xhr.setRequestHeader('_csrf', document.getElementsByName('_csrf').item(0).value)
            xhr.send();
        }
    </script>
</head>

<body>
<p>
<form method="post" action="result.jsp">
    <input type="hidden"
           name="<csrf:tokenname/>"
           value="<csrf:tokenvalue uri="/"/>"
    />
    <button type="submit">test form</button>
</form>
</p>

<p>
    <button onclick="ajax()">test ajax</button>
</p>
</body>

</html>