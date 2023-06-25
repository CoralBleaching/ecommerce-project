package product;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import category.CategoryDAO;
import category.CategoryDAO.CategoriesFetch;
import product.ProductDAO.ProductsFetch;
import user.User;
import utils.Parameter;
import utils.SessionVariable;

public class ListProductsAdminServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        User user = (User) session.getAttribute("user");
        if (!user.isIsAdmin()) {
            request.setAttribute("message", "User not logged in as admin.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
        }

        String idCategoryString = request.getParameter(Parameter.Category.get());
        String idSubcategoryString = request.getParameter(Parameter.Subcategory.get());
        String orderBy = request.getParameter(Parameter.ProductOrdering.get());
        String searchText = request.getParameter(Parameter.SearchTerms.get());
        String resultsPerPageString = request.getParameter(Parameter.ResultsPerPage.get());
        String pageNumberString = request.getParameter(Parameter.PageNumber.get());

        Integer idCategory = null, idSubcategory = null, resultsPerPage = null, pageNumber = null;
        if (idCategoryString != null) {
            idCategory = Integer.valueOf(idCategoryString);
        }
        if (idSubcategoryString != null && !idSubcategoryString.equals("undefined")) {
            idSubcategory = Integer.valueOf(idSubcategoryString);
        }
        if (resultsPerPageString != null) {
            resultsPerPage = Integer.valueOf(resultsPerPageString);
        }
        if (pageNumberString != null) {
            pageNumber = Integer.valueOf(pageNumberString);
        }

        ProductsFetch fetchProducts = ProductDAO.getProducts(idCategory, idSubcategory,
                orderBy, searchText, resultsPerPage, pageNumber);

        CategoriesFetch fetchCategories = CategoryDAO.getAllCategories();

        if (fetchProducts.wasSuccessful() && fetchCategories.wasSuccessful()) {
            session.setAttribute(SessionVariable.Products.get(), fetchProducts.products());
            session.setAttribute(SessionVariable.Categories.get(), fetchCategories.categories());
            RequestDispatcher dispatcher = request.getRequestDispatcher("products.jsp");
            dispatcher.forward(request, response);
            return;
        }

        request.setAttribute("message", fetchProducts.resultValue().getMessage());
        RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
        dispatcher.forward(request, response);
    }
}
