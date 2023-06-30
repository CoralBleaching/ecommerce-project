<%@page import="user.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>    
<% User user = (User) session.getAttribute("user"); %>
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
    <h1>Welcome, <%= user.name %></h1>
    <% if (request.getAttribute("message") != null) { %>
        <div class="warning-box">
            <strong>
                <%= request.getAttribute("message") %>
            </strong>
        </div>
    <% } %>
    </body>
</html>
