package product;

import category.CategoryDAO;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import product.ProductDAO.ProductFetch;
import transactions.TransactionResult;
import utils.Parameter;
import utils.SessionVariable;
import static utils.ServletUtils.getIntegerParameter;
import static utils.ServletUtils.getFloatParameter;
import static utils.ServletUtils.getBooleanParameter;

// TODO: do only service and use enum to sort which subroutine to call

public class UpdateProductServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Boolean isProductEditing = getBooleanParameter(request.getParameter(Parameter.IsProductEditing.get()));
        if (isProductEditing == null) {
            getInfo(request, response);
        } else {
            registerOrUpdate(request, response);
        }
    }

    private void getInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String idProducString = request.getParameter(Parameter.ProductId.get());

        CategoryDAO.CategoriesFetch fetchCategories = CategoryDAO.getAllCategories();
        List<PictureInfo> pictureInfos = ProductDAO.getPictureInfos();

        if (idProducString == null || idProducString.isEmpty()) {
            session.setAttribute(SessionVariable.IsProductUpdate.get(), false);
            session.setAttribute(SessionVariable.PictureInfos.get(), pictureInfos);
            session.setAttribute(SessionVariable.Categories.get(), fetchCategories.categories());
            response.sendRedirect("editProduct.jsp");
            return;
        }

        Integer idProduct = Integer.valueOf(idProducString);
        ProductFetch fetchProduct = ProductDAO.getProduct(idProduct);
        String imgData = ProductDAO.getPictureById(fetchProduct.product().getIdPicture());

        if (fetchProduct.wasSuccessful() && fetchCategories.wasSuccessful()) {
            session.setAttribute(SessionVariable.Product.get(), fetchProduct.product());
            session.setAttribute(SessionVariable.PictureInfos.get(), pictureInfos);
            session.setAttribute(SessionVariable.PictureData.get(), imgData);
            session.setAttribute(SessionVariable.Categories.get(), fetchCategories.categories());
            session.setAttribute(SessionVariable.IsProductUpdate.get(), true);

            RequestDispatcher dispatcher = request.getRequestDispatcher("editProduct.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void registerOrUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer productId = getIntegerParameter(request.getParameter(Parameter.ProductId.get()));
        String name = request.getParameter(Parameter.Name.get());
        String description = request.getParameter(Parameter.Description.get());
        Integer category = getIntegerParameter(request.getParameter(Parameter.Category.get()));
        Integer subcategory = getIntegerParameter(request.getParameter(Parameter.Subcategory.get()));
        Integer pictureId = getIntegerParameter(request.getParameter(Parameter.PictureId.get()));
        Integer stock = getIntegerParameter(request.getParameter(Parameter.Stock.get()));
        Integer hotness = getIntegerParameter(request.getParameter(Parameter.Hotness.get()));
        Float price = getFloatParameter(request.getParameter(Parameter.Price.get()));

        TransactionResult updateResult;
        if (productId == null) {
            updateResult = ProductDAO.registerProduct(name, description, category, subcategory, pictureId, stock,
                    hotness, price);
        } else {
            updateResult = ProductDAO.updateProduct(productId, pictureId, name, description, category,
                    subcategory, stock, hotness, price);
        }

        if (updateResult.wasSuccessful()) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("ProductsEdit");
            dispatcher.forward(request, response);
            return;
        }

        request.setAttribute("message", updateResult.getMessage());
        RequestDispatcher dispatcher = request.getRequestDispatcher("editProduct.jsp");
        dispatcher.forward(request, response);

    }

}
