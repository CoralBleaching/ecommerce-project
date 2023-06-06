package product;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import product.ProductDAO.ProductsFetch;
import user.Parameter;
import utils.DatabaseUtil;

public class ListProductsServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin",
                DatabaseUtil.WhitelistedDomains.ViteReactTsApp.get());

        String idCategoryString = request.getParameter(Parameter.Category.get());
        String idSubcategoryString = request.getParameter(Parameter.Subcategory.get());

        Integer idCategory = null, idSubcategory = null;
        if (idCategoryString != null) {
            idCategory = Integer.parseInt(idCategoryString);
        }
        if (idSubcategoryString != null) {
            idSubcategory = Integer.parseInt(idSubcategoryString);
        }

        ProductsFetch fetchProducts = ProductDAO.getProductsByCategoryAndOrSubcategory(idCategory, idSubcategory);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = gson.toJson(fetchProducts);
        out.println(json);
        out.flush();
    }
}
