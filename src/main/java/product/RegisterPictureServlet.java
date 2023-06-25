package product;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import utils.Parameter;
import static utils.ServletUtils.getIntegerParameter;

@MultipartConfig
public class RegisterPictureServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Part pictureIdPart = request.getPart(Parameter.PictureId.get());
        Part namePart = request.getPart(Parameter.Name.get());
        Part picturePart = request.getPart(Parameter.PictureFile.get());

        Integer pictureId = null;
        if (pictureIdPart != null) {
            InputStream pictureIdInputStream = pictureIdPart.getInputStream();
            String pictureIdString = new String(pictureIdInputStream.readAllBytes(), StandardCharsets.UTF_8);
            pictureId = Integer.parseInt(pictureIdString);
        }

        String name = null;
        if (namePart != null) {
            InputStream nameInputStream = namePart.getInputStream();
            name = new String(nameInputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        System.out.println("[RegisterPictureServlet]");
        System.out.println(String.format("pictureId: %d\nname: %s", pictureId, name));
        System.out.println(picturePart);

        if (picturePart != null && picturePart.getSize() > 0) {
            InputStream pictureStream = picturePart.getInputStream();
            byte[] pictureBytes = pictureStream.readAllBytes();
            String pictureBase64 = Base64.getEncoder().encodeToString(pictureBytes);

            if (pictureId == null) {
                ProductDAO.registerPicture(name, pictureBase64);
            } else {
                ProductDAO.updatePicture(pictureId, name, pictureBase64);
            }

        } else {
            System.out.println("[RegisterPictureServlet] else");
            ProductDAO.updatePicture(pictureId, name, null);
        }
        response.sendRedirect("PictureInfo"); // TODO: error-checking
    }
}