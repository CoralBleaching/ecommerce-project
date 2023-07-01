package user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import utils.Parameter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PingSessionServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:5173");

        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute(Parameter.User.get());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", Boolean.toString(user != null));
        jsonObject.addProperty("user", gson.toJson(user));

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.println(jsonObject.toString());
        out.flush();

    }

}
