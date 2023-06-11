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

public class UpdateServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter(Parameter.Name.get());
        String email = request.getParameter(Parameter.Email.get());
        String new_username = request.getParameter(Parameter.Username.get());
        String password = request.getParameter(Parameter.Password.get());

        HttpSession session = request.getSession(true);
        User oldUser = (User) session.getAttribute(Parameter.User.get());
        String oldUsername = oldUser.getUsername();

        User user = new User(-1, name, new_username, password, email, null, false);

        TransactionResult updateResult = UserDAO.updateUser(oldUsername, user, null);

        if (updateResult.wasSuccessful()) {
            session.setAttribute(Parameter.User.get(), user);

            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
            return;
        }

        request.setAttribute("message", updateResult.getMessage());
        RequestDispatcher dispatcher = request.getRequestDispatcher("update.jsp");
        dispatcher.forward(request, response);

    }
}
