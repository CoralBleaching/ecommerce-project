package product;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transactions.TransactionResult;
import utils.Parameter;

// TODO: remove this and consolidate methods for editing products

public class RemoveProductServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idProducString = request.getParameter(Parameter.ProductId.get());

        if (idProducString == null) {
            request.setAttribute("message", "Missing product id selection.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("products.jsp");
            dispatcher.forward(request, response);
        }

        Integer idProduct = Integer.valueOf(idProducString);
        TransactionResult removelResult = ProductDAO.removeProductById(idProduct);

        if (removelResult.wasSuccessful()) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("ProductsEdit");
            dispatcher.forward(request, response);
        }
    }
}
