package category;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transactions.TransactionResult;
import utils.Parameter;
import static utils.ServletUtils.getIntegerParameter;

public class RemoveCategoryServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer categoryId = getIntegerParameter(request.getParameter(Parameter.CategoryId.get()));

        TransactionResult removeResult = CategoryDAO.removeCategoryById(categoryId);

        RequestDispatcher dispatcher = request.getRequestDispatcher("CategoryUpdate");
        if (removeResult.wasSuccessful()) {
            request.setAttribute("message", "Successfuly removed category.");
            dispatcher.forward(request, response);
            return;
        }

        request.setAttribute("message", removeResult.getMessage());
        dispatcher.forward(request, response);

    }
}
