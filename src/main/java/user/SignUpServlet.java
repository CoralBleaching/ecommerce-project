package user;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import user.UserDAO.SignUpFetch;
import utils.Parameter;
import static utils.ServletUtils.getBooleanParameter;

public class SignUpServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:5173");

        String name = request.getParameter(Parameter.Name.get());
        String email = request.getParameter(Parameter.Email.get());
        String username = request.getParameter(Parameter.Username.get());
        String password = request.getParameter(Parameter.Password.get());
        Boolean isFromStoreFront = getBooleanParameter(request.getParameter(Parameter.IsFromStoreFront.get()));

        SignUpFetch registrationResult = UserDAO.registerUser(name, username, password, email);

        HttpSession session = request.getSession(true);

        if (isFromStoreFront != null && isFromStoreFront) {
            session.setAttribute(Parameter.User.get(), registrationResult.user());

            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String responseJson = gson.toJson(registrationResult);
            out.println(responseJson);
            out.flush();
            return;
        }

        if (registrationResult.wasSuccessful()) {
            session.setAttribute(Parameter.User.get(), registrationResult.user());

            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
            return;
        }

        request.setAttribute("message", registrationResult.resultValue().getMessage());
        RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
        dispatcher.forward(request, response);

    }
}
