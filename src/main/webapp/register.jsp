<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="user.Parameter" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Registration</title>
    </head>
    <body>
        <h1>Registration form</h1>
        <form action="SignUp" method="post">
            <label for="<%= Parameter.Name.get() %>">Name:</label>
            <input type="text" id="<%= Parameter.Name.get() %>" name="<%= Parameter.Name.get() %>" required><br>
            
            <label for="<%= Parameter.Username.get() %>">Username:</label>
            <input type="text" id="<%= Parameter.Username.get() %>" name="<%= Parameter.Username.get() %>" required><br>
            
            <label for="<%= Parameter.Email.get() %>">Email:</label>
            <input type="email" id="<%= Parameter.Email.get() %>" name="<%= Parameter.Email.get() %>" required><br>
            
            <label for="<%= Parameter.Password.get() %>">Password:</label>
            <input type="password" id="<%= Parameter.Password.get() %>" name="<%= Parameter.Password.get() %>" required><br>

            <input type="submit" value="Submit">
        </form>
        <% if (request.getAttribute("message") != null) { %>
            <div class="warning-box">
                <strong>
                    <%= request.getAttribute("message") %>
                </strong>
            </div>
        <% } %>
    </body>
</html>

  