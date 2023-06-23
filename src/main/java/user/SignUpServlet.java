package user;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import transactions.TransactionResult;
import utils.Parameter;

public class SignUpServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter(Parameter.Name.get());
        String email = request.getParameter(Parameter.Email.get());
        String username = request.getParameter(Parameter.Username.get());
        String password = request.getParameter(Parameter.Password.get());

        User user = new User(-1, name, username, password, email, false);

        TransactionResult registrationResult = UserDAO.registerUser(user, null);

        if (registrationResult.wasSuccessful()) {
            HttpSession session = request.getSession(true);
            session.setAttribute(Parameter.User.get(), user);

            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
            return;
        }

        request.setAttribute("message", registrationResult.getMessage());
        RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
        dispatcher.forward(request, response);

    }
}
