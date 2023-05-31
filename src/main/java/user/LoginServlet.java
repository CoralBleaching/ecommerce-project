/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import transactions.TransactionResult;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import transactions.JsonLabel;
import user.UserDAO.UserFetch;

/**
 *
 * @author renat
 */
public class LoginServlet extends HttpServlet {
    private static final String USER_COOKIE_NAME = "ecommerce.user";
    private static final Gson gson = new Gson();
    
    private void setCookie(String value, String cookieName, 
        HttpServletRequest request, HttpServletResponse response) {
        Cookie outCookie = null;
        Cookie[] cookies = request.getCookies();
        
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                outCookie = cookie;
                break;
            }
        }
        
        if (outCookie == null) {
            outCookie = new Cookie(cookieName, value);
        } else {
            outCookie.setValue(value);
        }
        
        outCookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(outCookie);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String username = request.getParameter(Parameter.Username.get());
        String password = request.getParameter(Parameter.Password.get());
        
        response.addHeader("Access-Control-Allow-Origin", 
            "http://localhost:3000");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // String dbpath = getServletContext().getRealPath("database/ieeecommerce-db.db");
        
        TransactionResult validationResult = UserDAO.validateLogin(
            username, password);        
        if (validationResult.wasSuccessful()) {
            HttpSession session = request.getSession(true);
            var fetchUser = UserDAO.getUserByLoginFull(username, password);
            
            session.setAttribute(Parameter.Username.get(),fetchUser.user());
            /*/String userJson = gson.toJson(fetchUser);
            System.out.println("[service] " + fetchUser);
            System.out.println("[service] " + userJson);
            setCookie(fetchUser.user().toString(),
                USER_COOKIE_NAME, 
                request, 
                response);/**/

            String responseJson = buildJsonString(
                fetchUser.resultValue(), JsonLabel.user, fetchUser.user()
            );
            out.println(responseJson);
            out.flush();
            return;
        }
        
        out.println(buildJsonString(
            validationResult, JsonLabel.user, null
        ));
        out.flush();
    }

    private <T> String buildJsonString(TransactionResult result, JsonLabel label, T obj) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(result.name(), result.getMessage());
        jsonObject.addProperty(label.name(), gson.toJson(obj));
        return jsonObject.toString();
    }
}
