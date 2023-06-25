<%@page import="category.Subcategory"%>
<%@page import="user.User"%>
<%@page import="product.PictureInfo"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page import="category.Category"%>
<%@page import="product.Product"%>
<%@page import="utils.SessionVariable"%>
<%@page import="utils.Token"%>
<%@page import="utils.Parameter" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<% 
    List<Category> categories = (List<Category>) session.getAttribute(SessionVariable.Categories.get());
    Integer categoryId = (Integer) session.getAttribute(SessionVariable.CategoryId.get());
    
    Category category = null;
    if (categoryId != null) {
        category = categories.stream().filter(cat -> cat.getIdCategory() == categoryId)
                             .findFirst().orElse(null);
    }
    
    User user = (User) session.getAttribute(SessionVariable.User.get());
    
%>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="style.css">
        <title>Categories</title>
    </head>
    <body onLoad="updateSubcategories()">
        
        <%@include file="header.jsp" %>
        
        <% if (user != null && user.isIsAdmin()) { %>
            <h1><%= Token.RegisterCategoryForm.get() %></h1><br><br>
            <h2>Category information</h2>
            <form action="CategoryUpdate" method="post" id="categoryForm"> 
                <script>
                    counter = 0;
                    
                    function addSubcategoryInput() {
                        let input = document.createElement("input");
                        let name = String("<%= Parameter.SubcategoryName.get() %>");
                        input.type = "text";
                        input.name = name + String(counter);
                        input.id = name + String(counter);
                        input.required = true;
                        let label = document.createElement("label");
                        label.for = name + String(counter);
                        label.innerHTML = "Name:";
                        form = document.getElementById("categoryForm");
                        form.appendChild(label);
                        form.appendChild(input);
                    }
                </script>
                
                <input type="hidden" name="<%= Parameter.IsCategoryEditing.get() %>" value="true">
                
                <% if (category != null) { %>
                    <input 
                        type="hidden" 
                        name="<%= Parameter.CategoryId.get() %>" 
                        value="<%= category.getIdCategory() %>">
                <% } %>

                <label for="<%= Parameter.Name.get() %>">Name:</label>
                <input 
                    type="text" 
                    id="<%= Parameter.Name.get() %>" 
                    name="<%= Parameter.Name.get() %>"
                    <% if (category != null) { %>
                        value="<%= category.getName() %>"
                    <% } %>
                    required>
                <br>

                <label for="<%= Parameter.Description.get() %>">Description</label>
                <input 
                    type="text" 
                    id="<%= Parameter.Description.get() %> " 
                    name="<%= Parameter.Description.get() %>" 
                    <% if (category != null) { %>
                        value="<%= category.getDescription() %>"
                    <% } %>
                    required>
                <br>
                
                <% if (category == null) { %>
                <br>
                <button type="button" onClick="addSubcategoryInput()">Add subcategory</button>
                <br><br>
                <% } %>

                <input type="submit" value="<%= ((category == null) ? "Add" : "Update") + " category" %>">
            </form><br><br>
            
            <% if (category != null) { %>
                <h2>Edit subcategories</h2>
                <form action="CategoryUpdate" method="post">
                    <% Integer counter = 0; %>
                    <% for (Subcategory subcategory : category.getSubcategories() ) { %>
                        <input type="hidden" name="<%= String.format("%s%d", Parameter.SubcategoryId.get(), counter) %>"

                        <% String name = String.format("%s%d", Parameter.SubcategoryName.get(), counter); %>
                        <label for="<%= name %>">Name:</label>
                        <input 
                            type="text" 
                            id="<%= name %>" 
                            name="<%= name %>"
                            value="<%= subcategory.getName() %>"
                            required>
                        <br>

                        <% String description = String.format("%s%d", Parameter.SubcategoryDescription.get(), counter); %>
                        <label for="<%= name %>">Description:</label>
                        <input 
                            type="text" 
                            id="<%= description %>" 
                            name="<%= description %>"
                            value="<%= subcategory.getDescription() %>"
                            required>
                        <br>

                        <% String subcategoryCategoryId = String.format("%s%d", Parameter.SubcategoryCategoryId.get(), counter); %>
                        <label for="<%= subcategoryCategoryId %>">Category:</label>
                        <select id="<%= subcategoryCategoryId %>"  
                                name="<%= subcategoryCategoryId %>">
                            <% for (Category categoryOption : categories) { %>
                                <option value="<%= categoryOption.getIdCategory() %>"
                                <%= (categoryOption.getIdCategory() == categoryId) ? "selected" : "" %>>
                                    <%= categoryOption.getName() %>
                                </option>
                            <% } %>
                        </select>
                        <br><br>

                        <% counter++; %>
                    <% } %>
                </form>
            <% } %>
            
            <h2>Select an existing category for editing:</h2>
            <form action="CategoryUpdate" method="post">
                <label for="<%= Parameter.CategoryId.get() %>">Category:</label>
                <select id="<%= Parameter.CategoryId.get() %>"  
                        name="<%= Parameter.CategoryId.get() %>" 
                        onChange="updateSubcategories()">
                    <option value="0">-</option>
                    <% for (Category categoryOption : categories) { %>
                        <option value="<%= categoryOption.getIdCategory() %>">
                            <%= categoryOption.getName() %>
                        </option>
                    <% } %>
                </select>
                <br>
                
                <input type="submit" value="Load selection">
            </form>
            
        <% } else { %>
            User not logged in as admin.
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

  