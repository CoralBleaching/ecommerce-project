package product;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import product.ProductDAO.ProductsFetch;
import utils.DatabaseUtil;

public class ListProductsServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin",
                DatabaseUtil.WhitelistedDomains.ViteReactTsApp.get());

        ProductsFetch fetchProducts = ProductDAO.getAllProducts();

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = gson.toJson(fetchProducts);
        out.println(json);
        out.flush();
    }
}
