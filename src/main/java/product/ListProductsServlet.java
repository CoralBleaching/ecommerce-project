package product;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import product.ProductDAO.ProductsFetch;
import utils.Parameter;

public class ListProductsServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idCategoryString = request.getParameter(Parameter.Category.get());
        String idSubcategoryString = request.getParameter(Parameter.Subcategory.get());
        String orderBy = request.getParameter(Parameter.ProductOrdering.get());
        String searchText = request.getParameter(Parameter.SearchTerms.get());
        String resultsPerPageString = request.getParameter(Parameter.ResultsPerPage.get());
        String pageNumberString = request.getParameter(Parameter.PageNumber.get());

        Integer idCategory = null, idSubcategory = null, resultsPerPage = null, pageNumber = null;
        if (idCategoryString != null) {
            idCategory = Integer.parseInt(idCategoryString);
        }
        if (idSubcategoryString != null) {
            idSubcategory = Integer.parseInt(idSubcategoryString);
        }
        if (resultsPerPageString != null) {
            resultsPerPage = Integer.parseInt(resultsPerPageString);
        }
        if (pageNumberString != null) {
            pageNumber = Integer.parseInt(pageNumberString);
        }

        ProductsFetch fetchProducts = ProductDAO.getProducts(idCategory, idSubcategory,
                orderBy, searchText, resultsPerPage, pageNumber);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = gson.toJson(fetchProducts);
        out.println(json);
        out.flush();
    }
}
