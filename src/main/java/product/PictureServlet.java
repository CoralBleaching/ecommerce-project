package product;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import utils.DatabaseUtil;
import utils.Parameter;

public class PictureServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin",
                DatabaseUtil.WhitelistedDomains.ViteReactTsApp.get());

        String idPictureString = request.getParameter(Parameter.PictureId.get());
        Integer idPicture = null;
        if (idPictureString != null) {
            idPicture = Integer.parseInt(idPictureString);
        }

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject json = new JsonObject();

        if (idPicture != null) {
            String picture = ProductDAO.getPictureById(idPicture);

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
