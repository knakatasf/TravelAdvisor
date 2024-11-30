package server.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import database.TravelDatabaseManager;
import org.apache.commons.lang.StringEscapeUtils;
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

import static server.CONST.ERROR_MAP;


public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/html/register.html");
        VelocityContext context = new VelocityContext();

        String errorCode = request.getParameter("error");
        if (errorCode != null)
            context.put("errorMessage", ERROR_MAP.get(errorCode));

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        username = StringEscapeUtils.escapeHtml(username);
        String password = request.getParameter("password");
        password = StringEscapeUtils.escapeHtml(password);

        if (isValidUsername(username) && isValidPassword(password)) {
            TravelDatabaseManager dbManager = TravelDatabaseManager.getInstance();
            try {
                dbManager.registerUser(username, password);
                response.sendRedirect("/login");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                response.sendRedirect("/register?error=conn");
            }
        } else if (isValidUsername(username)) {
            response.sendRedirect("/register?error=pass");
        } else if (isValidPassword(password)) {
            response.sendRedirect("/register?error=user");
        } else {
            response.sendRedirect("/register?error=userAndPass");
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

        Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[A-Za-z])(?=.*[!@#$%^&*?])");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

}
