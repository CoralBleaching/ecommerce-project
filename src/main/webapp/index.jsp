<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="utils.Parameter" %>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="style.css">
    </head>
    <body>
        <h1>Super Store</h1></br>
        <h2>Login</h2></br>
        <form action="Login" method="post">
            <div>
                <label for="<%= Parameter.Username.get() %>">Username</label>
                <input type="text" id="<%= Parameter.Username.get() %>" name="<%= Parameter.Username.get() %>"
                placeholder="Type your username">
            </div>
            <div>
                <label for="<%= Parameter.Password.get() %>">Password</label>
                <input type="<%= Parameter.Password.get() %>" id="<%= Parameter.Password.get() %>" name="<%= Parameter.Password.get() %>"
            placeholder="Type your password">
            </div>
            <button type="submit">Sign in</button>
        </form>
        <% if (request.getAttribute("message") != null) { %>
            <div class="warning-box">
                <strong>
                    <%= request.getAttribute("message") %>
                </strong>
            </div>
        <% } %>
        <a href="register.jsp">Sign Up</a></br>
    </body>
</html>
