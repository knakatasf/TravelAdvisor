package server.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import database.TravelDatabaseHandler;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.CONST;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterServlet extends HttpServlet {
    private static final Map<String, String> ERROR_MAP = Map.of(
            "connectionError" ,"Database connection failed..",
            "usernameInvalid", "Username is invalid..",
            "passwordInvalid", "Password is invalid..",
            "usernameAndPasswordInvalid", "Username and password are invalid.."
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/register.html");
        VelocityContext context = new VelocityContext();

        String errorMessage = request.getParameter("error");
        if (errorMessage != null)
            context.put("errorMessage", errorMessage);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (isValidUsername(username) && isValidPassword(password)) {
            TravelDatabaseHandler dbHandler = TravelDatabaseHandler.getInstance();
            try {
                dbHandler.registerUser(username, password);
                response.sendRedirect("/login");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                response.sendRedirect("/register?error=connection");
            }
        } else {
            System.out.println("Username or password is invalid.");
            response.sendRedirect("/register?error=invalid");
        }
    }

    private boolean isValidUsername(String username) {
        if (username.length() < CONST.USERNAME_MIN_LENGTH)
            return false;

        Pattern pattern = Pattern.compile("(?=.*[A-Za-z])");
        Matcher matcher = pattern.matcher(username);
        return matcher.find();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < CONST.PASSWORD_MIN_LENGTH)
            return false;

        Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[A-Za-z])(?=.*[@#$%])");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

}
