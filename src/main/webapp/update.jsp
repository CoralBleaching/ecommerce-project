<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="utils.Parameter" %>
<%@page import="user.User" %>

<%
User user = (User) session.getAttribute("user");
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="style.css">
        <title>Update</title>
    </head>
    <body>
        <%@include file="header.jsp" %>
        
        <% if (user != null) { %>
            <h1>Update your information</h1>
            <form action="Update" method="post">
                <label for="<%= Parameter.Name.get() %>">Name:</label>
                <input type="text" id="<%= Parameter.Name.get() %>" name="<%= Parameter.Name.get() %>"
                    value='<%= user.name %>' required><br>


                <label for="<%= Parameter.Username.get() %>">Username:</label>
                <input type="text" id="<%= Parameter.Username.get() %>" name="<%= Parameter.Username.get() %>"
                    value='<%= user.username %>' required><br>


                <label for="<%= Parameter.Email.get() %>">Email:</label>
                <input type="email" id="<%= Parameter.Email.get() %>" name="<%= Parameter.Email.get() %>"
                    value='<%= user.email %>' required><br>


                <label for="<%= Parameter.Password.get() %>">Password:</label>
                <input type="password" id="<%= Parameter.Password.get() %>" name="<%= Parameter.Password.get() %>"
                    value='<%= user.password %>' required><br>

                <input type="submit" value="Submit">
            </form>
        <% } %>
        <% if (request.getAttribute("message") != null) { %>
            <div class="warning-box">
                <strong>
                    <%= request.getAttribute("message") %>
                </strong>
            </div>
        <% } %>
    </body>
</html>

  