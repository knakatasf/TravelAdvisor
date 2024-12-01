package server.servlet;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static server.CONST.ERROR_MAP;

public class RegisterServlet extends HttpServlet {
    /**
     * Displays a register format and sends the user inputs to doPost() method.
     * @param request sent by a user
     * @param response to be sent to the user
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    /**
     * Takes the user inputs (username and password) and inserts the new user into the database.
     * @param request contains username and password as parameters
     * @param response to be redirected to /login or /register again
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TravelDatabaseManager dbManager = TravelDatabaseManager.getInstance();

        String username = request.getParameter("username");
        username = StringEscapeUtils.escapeHtml(username);

        try {
            if (dbManager.isDuplicatedUsername(username)) {
                response.sendRedirect("/register?error=userTaken");
                return;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.sendRedirect("/register?error=conn");
        }

        String password = request.getParameter("password");
        password = StringEscapeUtils.escapeHtml(password);

        if (isValidUsername(username) && isValidPassword(password)) {
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

    /**
     * Checks the username is valid.
     * @param username passed by the user
     * @return true if the username is valid, false otherwise.
     */
    private boolean isValidUsername(String username) {
        if (username.length() < CONST.USERNAME_MIN_LENGTH)
            return false;

        Pattern pattern = Pattern.compile("(?=.*[A-Za-z])");
        Matcher matcher = pattern.matcher(username);
        return matcher.find();
    }

    /**
     * Checks the password is valid.
     * @param password passed by the user
     * @return true if the password is valid, false otherwise.
     */
    private boolean isValidPassword(String password) {
        if (password.length() < CONST.PASSWORD_MIN_LENGTH)
            return false;

        Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[A-Za-z])(?=.*[!@#$%^&*?])");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

}
