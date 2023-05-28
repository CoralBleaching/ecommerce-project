/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package user;

import com.google.gson.Gson;
import transactions.TransactionResult;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author renat
 */
public class LoginServlet extends HttpServlet {
    private static final String USER_COOKIE_NAME = "ecommerce.user";
    private static final Gson gson = new Gson();
    
    private void setCookie(String value, String cookieName, HttpServletRequest request, HttpServletResponse response) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String username = request.getParameter(Parameter.Username.get());
        String password = request.getParameter(Parameter.Password.get());
        
        TransactionResult validationResult = UserDAO.validateLogin(username, password);
        
        if (validationResult.wasSuccessful()) {
            HttpSession session = request.getSession(true);
            var fetchUser = UserDAO.getUserByLoginFull(username, password);
            
            if (fetchUser.wasSuccessful()) {
                User user = new User(fetchUser.user());
                session.setAttribute(Parameter.Username.get(), 
                user);
                String userJson = gson.toJson(user);
                setCookie(userJson, USER_COOKIE_NAME, request, response);
            } else {
                validationResult = fetchUser.resultValue();
            }
        }
        
        if (!validationResult.wasSuccessful()) {
            
        }
    }

}
