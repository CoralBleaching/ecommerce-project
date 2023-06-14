package product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import utils.DatabaseUtil;

public class PicturesServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin",
                DatabaseUtil.WhitelistedDomains.ViteReactTsApp.get());

        BufferedReader reader = request.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        Gson gson = new Gson();
        Integer[] arrayData = gson.fromJson(requestBody.toString(), Integer[].class);

        String test = "[PicturesServlet]";
        for (int i : arrayData) {
            test += Integer.toString(i) + " ";
        }
        System.out.println(test);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject json = new JsonObject();

        if (arrayData != null) {
            List<String> pictures = ProductDAO.getPicturesById(arrayData);

            String pictureData = gson.toJson(pictures);
            json.addProperty("pictures", pictureData);

            out.println(json.toString());
            out.flush();
            return;
        }

        String[] resp = { "errror" };
        String respD = gson.toJson(resp);
        json.addProperty("pictures", respD);
        out.println(json.toString());
        out.flush();
    }
}
