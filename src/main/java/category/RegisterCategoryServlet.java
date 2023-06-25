package category;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import category.CategoryDAO.CategoriesFetch;
import user.User;
import utils.Parameter;
import utils.SessionVariable;
import static utils.ServletUtils.getBooleanParameter;
import static utils.ServletUtils.getIntegerParameter;

public class RegisterCategoryServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        User user = (User) session.getAttribute("user");
        if (user == null || !user.isIsAdmin()) {
            request.setAttribute("message", "User not logged in as admin.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
        }

        Boolean isCategoryEditing = getBooleanParameter(request.getParameter(Parameter.IsCategoryEditing.get()));

        if (isCategoryEditing == null) {
            getInfo(request, response);
        } else {
            registerCategory(request, response);
        }
    }

    private void getInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        Integer categoryId = getIntegerParameter(request.getParameter(Parameter.CategoryId.get()));
        Integer subcategoryId = getIntegerParameter(request.getParameter(Parameter.SubcategoryId.get()));

        CategoriesFetch fetchCategories = CategoryDAO.getAllCategories();

        if (!fetchCategories.wasSuccessful()) {
            request.setAttribute("message", "Problem accessing category information.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
        }

        session.setAttribute(SessionVariable.Categories.get(), fetchCategories.categories());
        if (categoryId != null && categoryId != 0) {
            session.setAttribute(SessionVariable.CategoryId.get(), categoryId);
        } else {
            session.setAttribute(SessionVariable.CategoryId.get(), null);
        }
        if (subcategoryId != null && categoryId != 0) {
            session.setAttribute(SessionVariable.SubcategoryId.get(), subcategoryId);
        } else {
            session.setAttribute(SessionVariable.SubcategoryId.get(), null);

        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("editCategory.jsp");
        dispatcher.forward(request, response);
    }

    private void registerCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer categoryId = getIntegerParameter(request.getParameter(Parameter.CategoryId.get()));
        String categoryName = request.getParameter(Parameter.Name.get());
        String categoryDescription = request.getParameter(Parameter.Description.get());

        if (categoryName != null || categoryDescription != null) {
            if (categoryId != null) {
                CategoryDAO.updateCategory(categoryId, categoryName, categoryDescription);
            } else {
                CategoryDAO.registerCategory(categoryName, categoryDescription);
            }

            request.setAttribute("message", "Added category " + categoryName + ".");
            RequestDispatcher dispatcher = request.getRequestDispatcher("editCategory.jsp");
            dispatcher.forward(request, response);
        }

    }
}
