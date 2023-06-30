<%@page import="user.User"%>
<%@page import="utils.SessionVariable"%>
<%@page import="product.PictureInfo"%>
<%@page import="java.util.List"%>
<%@page import="utils.Parameter"%>
<%@page import="utils.Token"%>
<!DOCTYPE html>
<%
    List<PictureInfo> pictureInfos = (List<PictureInfo>) session.getAttribute(SessionVariable.PictureInfos.get());
    String imgData = (String) session.getAttribute(SessionVariable.PictureData.get());
    Boolean isUpdateRequest = (Boolean) session.getAttribute(SessionVariable.IsPictureUpdate.get());
    Integer pictureId = (Integer) session.getAttribute(SessionVariable.PictureId.get());
    User user = (User) session.getAttribute(SessionVariable.User.get());
    
    PictureInfo pinfo = null;
    if (pictureId != null) {
        pinfo = pictureInfos.stream().filter(info -> info.getIdPicture() == pictureId).findFirst().orElse(null);
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title><%= Token.EditPicturesTitle.get() %></title>
        <link rel="stylesheet" href="style.css">
    </head>
    <body>
        <%@include file="header.jsp" %>
        <% if (user != null && user.isAdmin) { %>
            <h1><%= Token.RegisterPictureTitle.get() %></h1>
            <form action="RegisterPicture" method="post" enctype="multipart/form-data">            
                <% if (isUpdateRequest) { %>
                    <input type="hidden" name="<%= Parameter.PictureId.get() %>" value="<%= pictureId %>">
                    Edit <%= String.format("[%d] ", pictureId) + pinfo.getName() %>'s data.<br><br>
                <% } else { %>
                Upload and register a new picture or select a picture below for editing.<br><br>
                <% } %>

                <label for="name">Name:</label>
                <input type="text" 
                       id="<%= Parameter.Name.get() %>" 
                       name="<%= Parameter.Name.get() %>" 
                       value="<%= (isUpdateRequest) ? pinfo.getName() : "" %>"
                       required><br><br>

                <label for="picture">File:</label>
                <input type="file" 
                       id="<%= Parameter.PictureFile.get() %>" 
                       name="<%= Parameter.PictureFile.get() %>"
                       ><br><br>

                <input type="submit" value=<%= (isUpdateRequest) ? "Update" : "Upload" %>>
            </form><br><br>

            <form action="PictureInfo" method="post">
                <input type="hidden" name="<%= Parameter.IsPictureUpdate.get() %>" value="true">
                Leave blank below if you wish to register a new picture.<br><br>
                <label for="<%= Parameter.PictureId.get() %>">Select picture to edit: </label>
                <select id="<%= Parameter.PictureId.get() %>"
                        name="<%= Parameter.PictureId.get() %>"
                        onChage="onUpdatePictureSelection()">
                    <% for (PictureInfo info : pictureInfos) { %>
                        <option value="<%= info.getIdPicture() %>"
                                 <% if (isUpdateRequest && pinfo != null && info.getIdPicture() == pinfo.getIdPicture()) { %>
                                    selected
                                 <% } %>
                        >
                            <%= Integer.toString(info.getIdPicture()) + " " + info.getName() %>
                        </option>
                    <% } %>
                </select><br><br>
                <input type="submit" value="Load picture">
            </form>

            <form action="PictureInfo" method="post">
                <input type="hidden" name="<%= Parameter.IsPictureUpdate.get() %>" value="false">
                <input type="submit" value="Clear selection">
            </form>

        <% if (imgData != null) { %>
            Current image:<br>
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
