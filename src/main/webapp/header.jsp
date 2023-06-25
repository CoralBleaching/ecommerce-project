<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% if (session.getAttribute("user") != null) { %>
    <div class="header-menu">
        <a href="Categories">Categories</a>
        <a href="ProductsEdit">Products</a>
        <a href="PictureInfo">Pictures</a>
        <a href="SignOut">Sign out</a>
        <a href="update.jsp">Update your information</a>
        <a href="Delete">Delete account</a>
    </div>
<% } else { %>
    <h1>You are not signed in.</h1>
    <a href="index.jsp">Start page</a>
<% } %>
        