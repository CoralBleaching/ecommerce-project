<%@page import="utils.Token"%>
<%@page import="utils.SessionVariable"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="category.Subcategory"%>
<%@page import="category.Category"%>
<%@page import="product.Product"%>
<%@page import="java.util.List"%>
<%@page import="utils.Parameter"%>
<%@page import="user.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% User user = (User) session.getAttribute("user"); %>
<% List<Product> products = (List<Product>) session.getAttribute(SessionVariable.Products.get()); %>
<% List<Category> categories = (List<Category>) session.getAttribute(SessionVariable.Categories.get()); %>

<!DOCTYPE html>
<html>
    
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="style.css">
    <title><%= Token.RegisterProductTitle.get() %></title> <%-- TODO: new token --%>
</head>

<body onLoad="updateSubcategories()">
    <%@include file="header.jsp" %>
    <% if (user != null && user.isIsAdmin()) { %>
        <h1><%= Token.RegisterProductTitle.get() %></h1>
        
        <a href="ProductUpdate">Register new product</a><br><br>    
        
        <form action="ProductsEdit" method="GET" id="filterForm">
            <%-- Hidden input field to preserve the existing parameters --%>
            <input type="hidden" name=<%= Parameter.ResultsPerPage.get() %> value="100">
            <input type="hidden" name=<%= Parameter.PageNumber.get() %> value="1">

            <input type="text" id="<%= Parameter.SearchTerms.get() %>" name="<%= Parameter.SearchTerms.get() %>" placeholder="Find keywords...">

            <% String categoriesJson = new Gson().toJson(categories); %>

            <script>
                var categories = <%= categoriesJson %>;

                function updateSubcategories() {
                    var categorySelect = document.getElementById("<%= Parameter.Category.get() %>")
                    var categoryId = categorySelect.value;
                    var selectedCategory = categories.find(category =>
                        category.idCategory === parseInt(categoryId)
                    );

                    var subcategories = selectedCategory.subcategories;

                    var subcategorySelect = document.getElementById("<%= Parameter.Subcategory.get() %>");
                    subcategorySelect.innerHTML = "";

                    subcategories.forEach(subcategory => {
                        var option = document.createElement("option");
                        option.value = String(subcategory.idSubcategory);
                        option.text = subcategory.name;
                        subcategorySelect.appendChild(option);
                    });

                }
            </script>

            <label for="<%= Parameter.Category.get() %>">Category:</label>
            <select id="<%= Parameter.Category.get() %>"  name="<%= Parameter.Category.get() %>" onChange="updateSubcategories()">
                <% for (Category category : categories) { %>
                    <option 
                        value=<%= category.getIdCategory() %>
                        <%= (category.getIdCategory() == categories.get(0).getIdCategory()) ?
                            "selected" : "" %>
                    >
                        <%= category.getName() %>
                    </option>
                <% } %>
            </select>


            <label for="<%= Parameter.Subcategory.get() %>">Subcategory:</label>
            <select id="<%= Parameter.Subcategory.get() %>" name="<%= Parameter.Subcategory.get() %>"></select>

            <input type="submit" value="Apply">
        </form>

        <div>
        <table>
           <thead>
              <tr>
                 <th>ID</th>
                 <th>Name</th>
                 <th>Price</th>
                 <th>Stock</th>
                 <th>Hotness</th>
                 <th>Description</th>
                 <th>Category</th>
                 <th>Subcategory</th>
                 <th></th>
              </tr>
           </thead>
           <tbody>
              <% for (Product product : products) { %>
                <tr>
                   <td><%= product.getIdProduct() %></td>
                   <td><%= product.getName() %></td>
                   <td>$<%= String.format("%.2f", product.getPrice()) %></td>
                   <td><%= product.getStock() %></td>
                   <td><%= product.getHotness() %></td>
                   <td><%= product.getDescription() %></td>
                   <td><%= product.getCategory() %></td>
                   <td><%= product.getSubcategory() %></td>
                   <td>   
                        <form action="ProductUpdate" method="GET">
                            <input type="hidden" name="<%= Parameter.ProductId.get() %>" value="<%= product.getIdProduct() %>">
                            <input type="hidden" name="<%= Parameter.PictureId.get() %>" value="<%= product.getIdPicture() %>">
                            <button type="submit">Edit</button>
                        </form>
                        <form action="ProductRemove">
                            <input type="hidden" name="<%= Parameter.ProductId.get() %>" value="<%= product.getIdProduct() %>">
                            <button type="submit">Remove</button>
                        </form>
                   </td>

                </tr>
              <% } %>
           </tbody>
        </table>
        </div>

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