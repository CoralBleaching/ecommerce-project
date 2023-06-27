package category;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import transactions.TransactionResult;
import utils.Parameter;
import static utils.ServletUtils.getIntegerParameter;

public class RemoveSubcategoryServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer subcategoryId = getIntegerParameter(request.getParameter(Parameter.SubcategoryId.get()));

        TransactionResult removeResult = CategoryDAO.removeSubcategoryById(subcategoryId);

        HttpSession session = request.getSession(true);
        Integer categoryId = (Integer) session.getAttribute(Parameter.CategoryId.get());
        RequestDispatcher dispatcher = request.getRequestDispatcher("CategoryUpdate");
        request.setAttribute(Parameter.CategoryId.get(), categoryId);
        if (removeResult.wasSuccessful()) {
            request.setAttribute("message", "Successfuly removed subcategory.");
            dispatcher.forward(request, response);
            return;
        }

        request.setAttribute("message", removeResult.getMessage());
        dispatcher.forward(request, response);

    }
}
