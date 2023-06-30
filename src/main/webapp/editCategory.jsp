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
    <body>
        
        <%@include file="header.jsp" %>

        
        <% if (user != null && user.isAdmin) { %>
            <h1><%= Token.RegisterCategoryForm.get() %></h1><br><br>
                                    
            <% if (request.getAttribute("message") != null) { %>
                <div class="warning-box">
                    <strong>
                        <%= request.getAttribute("message") %>
                    </strong>
                </div>
            <% } %>
            
            <h2>Category information</h2>
            <form action="CategoryUpdate" method="post" id="categoryForm"> 
                <script>
                    var counter = 0;
                    
                    function addSubcategoryInput(formId, submitButtonId, submitButtonText, elementsToRemove) {
                        let titleSpan = document.createElement("span");
                        titleSpan.innerText = "New subcategory " + String(counter + 1);
                        
                        let nameInput = document.createElement("input");
                        const name = String("<%= Parameter.NewSubcategoryName.get() %>");
                        nameInput.type = "text";
                        nameInput.name = name + String(counter);
                        nameInput.id = name + String(counter);
                        nameInput.required = true;
                        
                        let nameLabel = document.createElement("label");
                        nameLabel.for = name + String(counter);
                        nameLabel.innerHTML = "Name:";
                  
                        let descriptionInput = document.createElement("input");
                        const description = String("<%= Parameter.NewSubcategoryDescription.get() %>");
                        descriptionInput.type = "text";
                        descriptionInput.name = description + String(counter);
                        descriptionInput.id = description + String(counter);
                        descriptionInput.required = true;
                        
                        let descriptionlabel = document.createElement("label");
                        descriptionlabel.for = description + String(counter);
                        descriptionlabel.innerHTML = "Description";
                        
                        const submitButton = document.getElementById(submitButtonId);
                        submitButton.value = submitButtonText;
                        
                        let form = document.getElementById(formId);
                        form.removeChild(submitButton);
                        form.appendChild(titleSpan);
                        form.appendChild(document.createElement("br"));
                        form.appendChild(nameLabel);
                        form.appendChild(nameInput);
                        form.appendChild(document.createElement("br"));
                        form.appendChild(descriptionlabel);
                        form.appendChild(descriptionInput);
                        form.appendChild(document.createElement("br"));
                        form.appendChild(document.createElement("br"));
                        form.appendChild(submitButton);
                        
                        elementsToRemove.forEach(elementInfo => {
                            parent = document.getElementById(elementInfo.parent);
                            child = document.getElementById(elementInfo.child);
                            if (parent !== null && child !== null) {
                                parent.removeChild(child);
                            }
                        });
                        
                        counter++;
                    }
                    
                    function removeSubcategory(event, subcategoryId) {
                        event.preventDefault();
                        
                        var nestedForm = document.createElement('form');
                        nestedForm.action = 'SubcategoryRemove';
                        nestedForm.method = 'post';

                        var paramInput = document.createElement('input');
                        paramInput.type = 'hidden';
                        paramInput.name = '<%= Parameter.SubcategoryId.get() %>';
                        paramInput.value = subcategoryId;

                        nestedForm.appendChild(paramInput);
                        document.body.appendChild(nestedForm);
                        nestedForm.submit();
                    }
                    
                    function saveCategoryAndSubcategories(event) {
                        event.preventDefault();
                        
                        let subcategoryForm = document.getElementById("subcategoryForm");
                        let categoryNameInput = document.getElementById("<%= Parameter.Name.get() %>");
                        let categoryDescriptionInput = document.getElementById("<%= Parameter.Description.get() %>");
                        
                        subcategoryForm.appendChild(categoryNameInput);
                        subcategoryForm.appendChild(categoryDescriptionInput);
                        subcategoryForm.submit();
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
                    id="<%= Parameter.Description.get() %>" 
                    name="<%= Parameter.Description.get() %>" 
                    <% if (category != null) { %>
                        value="<%= category.getDescription() %>"
                    <% } %>
                    required>
                <br>
                
                <% if (category == null) { %>
                <br>
                <button type="button" 
                        onClick="addSubcategoryInput(
                                    'categoryForm', 
                                    'categorySubmitButton',
                                    'Register category and subcategories',
                                    [])">
                    Add subcategory
                </button>
                <br>
                <% } %>
                
                <br>
                <input type="submit" 
                       value="<%= ((category == null) ? "Register" : "Update") + " category" %>"
                       id="categorySubmitButton">
            </form>
                      
            
            <% if (category != null) { %>
            
                <br><br>
                
                <form action="CategoryRemove" method="post">
                    <input type="hidden" 
                           name="<%= Parameter.CategoryId.get() %>" 
                           value="<%= category.getIdCategory() %>">
                    <input type="submit" value="Delete category">
                </form>
            
                <h2>Edit subcategories</h2>
                <form action="CategoryUpdate" method="post" id="subcategoryForm">
                    <input type="hidden" name="<%= Parameter.IsCategoryEditing.get() %>" value="true">
                    <input type="hidden" name="<%= Parameter.CategoryId.get() %>" value="<%= category.getIdCategory() %>">
                    
                    <% Integer counter = 0; %>
                    <% for (Subcategory subcategory : category.getSubcategories() ) { %>
                        <input type="hidden" 
                               name="<%= String.format("%s%d", Parameter.SubcategoryId.get(), counter) %>" 
                               value="<%= subcategory.getIdSubcategory() %>">
                        
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
                        <label for="<%= subcategoryCategoryId %>">Belongs to category:</label>
                        <select id="<%= subcategoryCategoryId %>"  
                                name="<%= subcategoryCategoryId %>">
                            <% for (Category categoryOption : categories) { %>
                                <option value="<%= categoryOption.getIdCategory() %>"
                                <%= (categoryOption.getIdCategory() == categoryId) ? "selected" : "" %>>
                                    <%= categoryOption.getName() %>
                                </option>
                            <% } %>
                        </select>
                        <br>
                        <button type="button" 
                                onClick="removeSubcategory(event, '<%= subcategory.getIdSubcategory() %>')">
                                Remove
                        </button>
                        <br><br>
                        
                        <% counter++; %>
                    <% } %>
                    
                    <br>
                    <button type="button" 
                            onClick="addSubcategoryInput(
                                        'subcategoryForm', 
                                        'subcategorySubmitButton',
                                        'Save edits to category and subcategories',
                                        [{'parent': 'categoryForm',
                                          'child':'categorySubmitButton'}])">
                        Add subcategory
                    </button>

                    <br>
                    <input type="button" 
                           value="Save"
                           id="subcategorySubmitButton"
                           onClick="saveCategoryAndSubcategories(event)">
                    <br><br>
                </form>
            <% } %>
            
            <h2>Select an existing category for editing:</h2>
            <form action="CategoryUpdate" method="post">
                <label for="<%= Parameter.CategoryId.get() %>">Category:</label>
                <select id="<%= Parameter.CategoryId.get() %>"  
                        name="<%= Parameter.CategoryId.get() %>">
                    <option value="0">-</option>
                    <% for (Category categoryOption : categories) { %>
                        <option value="<%= categoryOption.getIdCategory() %>"
                                <% if (category != null && 
                                    category.getIdCategory() == categoryOption.getIdCategory()) { %>
                                    selected 
                                <% } %>
                        >
                            <%= categoryOption.getName() %>
                        </option>
                    <% } %>
                </select>
                <br>
                
                <input type="submit" value="Load selection">
            </form>
                <form action="CategoryUpdate" method="post">
                    <input type="submit" value="Clear selection">
                </form>
            
        <% } else { %>
            User not logged in as admin.
        <% } %>

    </body>
</html>

  