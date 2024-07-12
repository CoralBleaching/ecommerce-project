package product;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.User;
import utils.Parameter;
import utils.SessionVariable;
import static utils.ServletUtils.getIntegerParameter;
import static utils.ServletUtils.getBooleanParameter;

public class PictureInfoServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        Boolean isUpdateRequest = getBooleanParameter(request.getParameter(Parameter.IsPictureUpdate.get()));
        Integer pictureId = getIntegerParameter(request.getParameter(Parameter.PictureId.get()));

        User user = (User) session.getAttribute("user");

        if (user == null || !user.isAdmin) {
            request.setAttribute("message", "User not logged in as admin.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
        }

        String base64EncodedData = null;
        byte[] binaryData = null;
        if (pictureId != null) {
            binaryData = ProductDAO.getPictureById(pictureId);
            base64EncodedData = Base64.getEncoder().encodeToString(binaryData);

        }
        session.setAttribute(SessionVariable.PictureData.get(), base64EncodedData);
        session.setAttribute(SessionVariable.PictureId.get(), pictureId);

        if (isUpdateRequest == null) {
            isUpdateRequest = false;
        }
        session.setAttribute(SessionVariable.IsPictureUpdate.get(), isUpdateRequest);

        List<PictureInfo> pictureInfos = ProductDAO.getPictureInfos();
        session.setAttribute(SessionVariable.PictureInfos.get(), pictureInfos);

        RequestDispatcher dispatcher = request.getRequestDispatcher("registerPicture.jsp");
        dispatcher.forward(request, response);
    }
}
