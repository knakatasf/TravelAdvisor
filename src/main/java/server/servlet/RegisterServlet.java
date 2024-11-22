package server.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TravelDatabaseModel;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/register.html");
        VelocityContext context = new VelocityContext();

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (isValidPassword(password) && isValidPassword(password)) {
            TravelDatabaseModel dbModel = TravelDatabaseModel.getInstance();
            dbModel.registerUser(username, password);
            response.sendRedirect("/login");
        } else {
            System.out.println("Username or password is invalid.");
            response.sendRedirect("/register");
        }
    }

    private boolean isValidUsername(String username) {
        if (username.length() < 4)
            return false;

        Pattern pattern = Pattern.compile("(?=.*[A-Za-z])");
        Matcher matcher = pattern.matcher(username);
        return matcher.find();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8)
            return false;

        Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[A-Za-z])(?=.*[@#$%])");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

}
