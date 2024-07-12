package product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import utils.Parameter;

public class PictureServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idPictureString = request.getParameter(Parameter.PictureId.get());
        Integer idPicture = null;
        if (idPictureString != null) {
            idPicture = Integer.valueOf(idPictureString);
        }

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject json = new JsonObject();

        if (idPicture != null) {
            byte[] binary = ProductDAO.getPictureById(idPicture);
            String picture = Base64.getEncoder().encodeToString(binary);

            json.addProperty("picture", picture);

            out.println(json.toString());
            out.flush();
            return;
        }

        String resp = null;
        json.addProperty("pictures", resp);
        out.println(json.toString());
        out.flush();
    }
}
