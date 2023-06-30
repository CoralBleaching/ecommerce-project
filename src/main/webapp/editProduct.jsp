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
    boolean isProductUpdate = (boolean) session.getAttribute(SessionVariable.IsProductUpdate.get());
    Product product = (Product) session.getAttribute(SessionVariable.Product.get()); 
    List<Category> categories = (List<Category>) session.getAttribute(SessionVariable.Categories.get());
    List<PictureInfo> pictureInfos = (List<PictureInfo>) session.getAttribute(SessionVariable.PictureInfos.get());
    String imgData = (String) session.getAttribute(SessionVariable.PictureData.get());
    User user = (User) session.getAttribute(SessionVariable.User.get());
  
    String pageTitle;
    if (isProductUpdate) {
        pageTitle = Token.UpdateProductTitle.get();
    } else { 
        pageTitle = Token.RegisterProductTitle.get();
    }
    
    String categoriesJson = new Gson().toJson(categories);
%>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="style.css">
        <title><%= pageTitle %></title>
    </head>
    <body onLoad="updateSubcategories()">
        
        <%@include file="header.jsp" %>
        
        <% if (user != null && user.isAdmin) { %>
            <h1><%= Token.RegisterProductForm.get()  %></h1>

            <form action="ProductUpdate" method="post">

                <script>
                    var categories = <%= categoriesJson %>;

                    function updateSubcategories() {
                        var categorySelect = document.getElementById("<%= Parameter.Category.get() %>");
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
                            if (subcategory.name === "<%= (product != null) ? product.getSubcategory() : "" %>") {
                                option.selected = true;
                            }
                            subcategorySelect.appendChild(option);
                        });

                    }
                </script>
                
                <input type="hidden" name="<%= Parameter.IsProductEditing.get() %>" value="true">
                
                <% if (product != null) { %>
                    <input 
                        type="hidden" 
                        name="<%= Parameter.ProductId.get() %>" 
                        value="<%= product.getIdProduct() %>">
                <% } %>

                <label for="<%= Parameter.Name.get() %>">Name:</label>
                <input 
                    type="text" 
                    id="<%= Parameter.Name.get() %>" 
                    name="<%= Parameter.Name.get() %>"
                    <% if (product != null) { %>
                        value="<%= product.getName() %>"
                    <% } %>
                    required>
                <br>

                <label for="<%= Parameter.Description.get() %>">Description</label>
                <input 
                    type="text" 
                    id="<%= Parameter.Description.get() %>" 
                    name="<%= Parameter.Description.get() %>" 
                    <% if (product != null) { %>
                        value="<%= product.getDescription() %>"
                    <% } %>
                    required>
                <br>

                <label for="<%= Parameter.Category.get() %>">Category:</label>
                <select id="<%= Parameter.Category.get() %>"  
                        name="<%= Parameter.Category.get() %>" 
                        onChange="updateSubcategories()">
                    <% for (Category category : categories) { %>
                        <option value="<%= category.getIdCategory() %>" 
                                <% if (product != null) { %>
                                    <%= (product.getCategory().equals(category.getName())) ? "selected" : "" %>
                                <% } else { %>
                                    <% if (categories.indexOf(category) == 0) { %> selected <% } %> 
                                <% } %>
                        >
                            <%= category.getName() %>
                        </option>
                    <% } %>
                </select>
                <br>

                <label for="<%= Parameter.Subcategory.get() %>">Subcategory:</label>
                <select id="<%= Parameter.Subcategory.get() %>" name="<%= Parameter.Subcategory.get() %>"></select>
                <br>

                <label for="<%= Parameter.Stock.get() %>">Stock:</label>
                <input 
                    type="number" 
                    min=0
                    id="<%= Parameter.Stock.get() %>" 
                    name="<%= Parameter.Stock.get() %>" 
                    <% if (product != null) { %>
                        value="<%= product.getStock() %>"
                    <% } %>
                    required>
                <br>

                <label for="<%= Parameter.Hotness.get() %>">Hotness:</label>
                <input 
                    type="number" 
                    max=5
                    id="<%= Parameter.Hotness.get() %>" 
                    name="<%= Parameter.Hotness.get() %>" 
                    <% if (product != null) { %>
                        value="<%= product.getHotness() %>"
                    <% } %>
                    required>
                <br>

                <label for="<%= Parameter.Price.get() %>">Price:</label>
                <input 
                    type="number" 
                    step="0.01" 
                    id="<%= Parameter.Price.get() %>" 
                    name="<%= Parameter.Price.get() %>" 
                    <% if (product != null) { %>
                        value="<%= String.format("%.2f", product.getPrice()) %>"
                    <% } %>
                    required>
                <br>

                <label for="<%= Parameter.PictureId.get() %>">Picture:</label>
                <select id="<%= Parameter.PictureId.get() %>"
                        name="<%= Parameter.PictureId.get() %>">
                    <% for (PictureInfo info : pictureInfos) { %>
                        <option value="<%= info.getIdPicture() %>"
                                <% if (product != null) { %>
                                    <%= (info.getIdPicture() == product.getIdPicture()) ? "selected" : "" %>
                                <% } %>>
                            <%= Integer.toString(info.getIdPicture()) + " " + info.getName() %>
                        </option>
                    <% } %>
                </select><br>

                <input type="submit" value="Submit">
            </form><br><br>
            
            <% if (imgData != null) { %>
                Current picture: <br>
                <img src=data:image/jpeg;base64,<%= imgData %> alt="" />
            <% } %>
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

  