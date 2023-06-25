<%@page contentType="text/html" pageEncoding="UTF-8"%>    
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="style.css">
        <title>Ecommerce</title>
    </head>
    <body>
    <%@include file="header.jsp" %> 
    <jsp:useBean id="user" class="user.User" scope="session" />
    <h1>Welcome, <jsp:getProperty name="user" property="name" /></h1>
    <% if (request.getAttribute("message") != null) { %>
        <div class="warning-box">
            <strong>
                <%= request.getAttribute("message") %>
            </strong>
        </div>
    <% } %>
    </body>
</html>
