package user;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utils.Parameter;
import static utils.ServletUtils.getBooleanParameter;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();

        Boolean isFromStoreFront = getBooleanParameter(request.getParameter(Parameter.IsFromStoreFront.get()));

        if (isFromStoreFront != null && isFromStoreFront) {
            return;
        }

        request.setAttribute("message", "You are signed out.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
    }
}
