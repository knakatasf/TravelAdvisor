package server.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.TravelDatabaseModel;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        Boolean isLoggedIn = (Boolean)(session.getAttribute("isLoggedIn"));

        if (isLoggedIn == null || !isLoggedIn) {
            VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
            Template template = ve.getTemplate("static/login.html");
            VelocityContext context = new VelocityContext();

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            out.println(writer);
        } else {
            System.out.println("You stay logged in.");
            response.sendRedirect("/search");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(180);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        TravelDatabaseModel dbModel = TravelDatabaseModel.getInstance();

        Boolean flag = dbModel.authenticateUser(username, password);
        if (flag) {
            System.out.println("Login success!");
            session.setAttribute("username", username);
            session.setAttribute("isLoggedIn ", true );

            response.sendRedirect("/search");
        } else {
            System.out.println("Login failed...");
            response.sendRedirect("/login");
        }
    }
}
