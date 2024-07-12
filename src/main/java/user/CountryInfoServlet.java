package user;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import user.AddressDAO.CountriesFetch;

public class CountryInfoServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CountriesFetch fetchCountries = AddressDAO.getCountryStateCityInfo();

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseJson = gson.toJson(fetchCountries);
        out.println(responseJson);
        out.flush();
        return;
    }
}
