package category;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import category.CategoryDAO.CategoriesFetch;
import utils.DatabaseUtil;

public class CategoryServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin",
                DatabaseUtil.WhitelistedDomains.ViteReactTsApp.get());
        CategoriesFetch fetchCategories = CategoryDAO.getAllCategories();

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = gson.toJson(fetchCategories);
        out.println(json);
        out.flush();
    }
}
