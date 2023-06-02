<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% if (session.getAttribute("user") != null) { %>
    <!DOCTYPE html>
    <jsp:useBean id="user" class="user.User" scope="session" />
    <h1>Welcome, <jsp:getProperty name="user" property="name" /></h1>
    <a href="SignOut">Sign out</a><br>
    <a href="update.jsp">Update your information</a><br>
    <a href="Delete">Delete account</a><br>
<% } else { %>
    <h1>You are not signed in.</h1>
    <a href="index.jsp">Start page</a>
<% } %>
<% if (request.getAttribute("message") != null) { %>
    <div class="warning-box">
        <strong>
            <%= request.getAttribute("message") %>
        </strong>
    </div>
<% } %>
