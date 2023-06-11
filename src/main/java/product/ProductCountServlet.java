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

public class ProductCountServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin",
                DatabaseUtil.WhitelistedDomains.ViteReactTsApp.get());

        String idCategoryString = request.getParameter(Parameter.Category.get());
        String idSubcategoryString = request.getParameter(Parameter.Subcategory.get());
        String searchText = request.getParameter(Parameter.SearchTerms.get());

        Integer idCategory = null, idSubcategory = null;
        if (idCategoryString != null) {
            idCategory = Integer.parseInt(idCategoryString);
        }
        if (idSubcategoryString != null) {
            idSubcategory = Integer.parseInt(idSubcategoryString);
        }

        Integer count = ProductDAO.getProductCount(idCategory, idSubcategory, searchText);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        var json = new JsonObject();
        json.addProperty("count", count);
        out.println(json.toString());
        out.flush();
    }
}
