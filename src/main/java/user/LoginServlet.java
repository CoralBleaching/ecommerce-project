package user;

import com.google.gson.Gson;
import utils.Parameter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
// import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static utils.ServletUtils.getBooleanParameter;
// import transactions.JsonLabel;
// import user.UserDAO.UserFetch;

public class LoginServlet extends HttpServlet {
	// private static final String USER_COOKIE_NAME = "ecommerce.user";
	private static final Gson gson = new Gson();

	// private void setCookie(String value, String cookieName,
	// HttpServletRequest request, HttpServletResponse response) {
	// Cookie outCookie = null;
	// Cookie[] cookies = request.getCookies();

	// for (Cookie cookie : cookies) {
	// if (cookie.getName().equals(cookieName)) {
	// outCookie = cookie;
	// break;
	// }
	// }

	// if (outCookie == null) {
	// outCookie = new Cookie(cookieName, value);
	// } else {
	// outCookie.setValue(value);
	// }

	// outCookie.setMaxAge(Integer.MAX_VALUE);
	// response.addCookie(outCookie);
	// }

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "http://localhost:5173");

		String username = request.getParameter(Parameter.Username.get());
		String password = request.getParameter(Parameter.Password.get());
		Boolean isFromStoreFront = getBooleanParameter(request.getParameter(Parameter.IsFromStoreFront.get()));

		var fetchUser = UserDAO.getUserByLogin(username, password);

		if (isFromStoreFront != null && isFromStoreFront) {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			String responseJson = gson.toJson(fetchUser);
			out.println(responseJson);
			out.flush();
			return;
		}

		if (fetchUser.resultValue().wasSuccessful()) {
			HttpSession session = request.getSession(true);

			session.setAttribute(Parameter.User.get(), fetchUser.user());

			// String userJson = gson.toJson(fetchUser);
			// System.out.println("[service] " + fetchUser);
			// System.out.println("[service] " + userJson);
			// setCookie(fetchUser.user().toString(),
			// USER_COOKIE_NAME,
			// request,
			// response);

			RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
			dispatcher.forward(request, response);
			return;
		}

		request.setAttribute("message", fetchUser.resultValue().getMessage());
		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
		dispatcher.forward(request, response);
	}

	// private <T> String buildJsonString(TransactionResult result, JsonLabel label,
	// T obj) {
	// JsonObject jsonObject = new JsonObject();
	// jsonObject.addProperty(result.name(), result.getMessage());
	// jsonObject.addProperty(label.name(), gson.toJson(obj));
	// return jsonObject.toString();
	// }
}
