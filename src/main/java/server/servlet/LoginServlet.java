package server.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import database.TravelDatabaseManager;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LoginServlet extends HttpServlet {
    private final int SESSION_TIMEOUT = 600;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("You stay logged in.");
            response.sendRedirect("/search");
            return;
        }

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/html/login.html");
        VelocityContext context = new VelocityContext();

        String errorMessage = request.getParameter("error");
        if (errorMessage != null)
            context.put("errorMessage", errorMessage);

        try (StringWriter writer = new StringWriter()) {
            template.merge(context, writer);
            out.println(writer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(SESSION_TIMEOUT);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        TravelDatabaseManager dbManager = TravelDatabaseManager.getInstance();
        if (dbManager.authenticateUser(username, password)) {
            System.out.println("Login success!");
            session.setAttribute("username", username);
            session.setAttribute("isLoggedIn ", Boolean.TRUE );
            response.sendRedirect("/search");
        } else {
            System.out.println("Login failed..");
            response.sendRedirect("/login?error=invalid");
        }
    }
}
