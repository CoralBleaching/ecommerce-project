package category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import category.CategoryDAO.CategoriesFetch;
import transactions.TransactionResult;
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
        if (user == null || !user.isAdmin) {
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
            categoryId = (Integer) request.getAttribute(Parameter.CategoryId.get());
            session.setAttribute(SessionVariable.CategoryId.get(), categoryId);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("editCategory.jsp");
        dispatcher.forward(request, response);
    }

    private record OldSubcategory(Integer id, String name, String description, Integer categoryId) {
    }

    private record NewSubcategory(String name, String description) {
    }

    private record GatheredSubcategories(List<OldSubcategory> old, List<NewSubcategory> neww) {
    }

    private GatheredSubcategories gatherAllSubcategoryParameters(
            HttpServletRequest request, HttpServletResponse response) {

        List<OldSubcategory> oldSubcategories = new ArrayList<>();
        List<NewSubcategory> newSubcategories = new ArrayList<>();

        Integer id, categoryId;
        String name, description;
        int counter = 0;
        while (true) {
            String idParameter = String.format("%s%d", Parameter.SubcategoryId.get(), counter);
            String nameParameter = String.format("%s%d", Parameter.SubcategoryName.get(), counter);
            String descriptionParameter = String.format("%s%d", Parameter.SubcategoryDescription.get(), counter);
            String categoryIdParameter = String.format("%s%d", Parameter.SubcategoryCategoryId.get(), counter);

            id = getIntegerParameter(request.getParameter(idParameter));
            if (id == null) {
                break;
            }
            name = request.getParameter(nameParameter);
            description = request.getParameter(descriptionParameter);
            categoryId = getIntegerParameter(request.getParameter(categoryIdParameter));

            var oldSubcategory = new OldSubcategory(id, name, description, categoryId);
            oldSubcategories.add(oldSubcategory);

            counter++;
        }

        counter = 0;
        while (true) {
            String nameParameter = String.format("%s%d", Parameter.NewSubcategoryName.get(), counter);
            String descriptionParameter = String.format("%s%d", Parameter.NewSubcategoryDescription.get(), counter);

            name = request.getParameter(nameParameter);
            description = request.getParameter(descriptionParameter);
            if (name == null) {
                break;
            }
            var newSubcategory = new NewSubcategory(name, description);
            newSubcategories.add(newSubcategory);

            counter++;
        }

        return new GatheredSubcategories(oldSubcategories, newSubcategories);
    }

    private TransactionResult registerSubcategories(Integer categoryId, List<NewSubcategory> newSubcategories) {
        for (NewSubcategory neww : newSubcategories) {
            TransactionResult registerResult = CategoryDAO.registerSubcategory(neww.name(), neww.description(),
                    categoryId);
            if (registerResult != TransactionResult.Successful) {
                return registerResult;
            }
        }
        return TransactionResult.Successful;
    }

    private TransactionResult updateSubcategories(List<OldSubcategory> oldSubcategories) {
        for (OldSubcategory old : oldSubcategories) {
            TransactionResult updateResult = CategoryDAO.updateSubcategory(old.id(), old.name(), old.description(),
                    old.categoryId());
            if (updateResult != TransactionResult.Successful) {
                return updateResult;
            }
        }
        return TransactionResult.Successful;
    }

    private void registerCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer categoryId = getIntegerParameter(request.getParameter(Parameter.CategoryId.get()));
        String categoryName = request.getParameter(Parameter.Name.get());
        String categoryDescription = request.getParameter(Parameter.Description.get());

        if (categoryName != null || categoryDescription != null) {
            GatheredSubcategories subcategoriesInfo = gatherAllSubcategoryParameters(request, response);

            if (categoryId != null) {
                CategoryDAO.updateCategory(categoryId, categoryName, categoryDescription);
                request.setAttribute("message", "Updated category " + categoryName + ".");
            } else {
                var registration = CategoryDAO.registerCategory(categoryName, categoryDescription);
                categoryId = registration.Id();
                request.setAttribute("message", "Added category " + categoryName + ".");
            }
            updateSubcategories(subcategoriesInfo.old());
            registerSubcategories(categoryId, subcategoriesInfo.neww());

            HttpSession session = request.getSession(true);

            CategoriesFetch fetchCategories = CategoryDAO.getAllCategories();
            session.setAttribute(SessionVariable.Categories.get(), fetchCategories.categories());
            RequestDispatcher dispatcher = request.getRequestDispatcher("editCategory.jsp");
            dispatcher.forward(request, response);
        }

    }
}
