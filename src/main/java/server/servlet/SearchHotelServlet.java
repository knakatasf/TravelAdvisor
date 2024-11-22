package server.servlet;

import hotelapp.contoller.HotelReviewController;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchHotelServlet extends HttpServlet {
    private HotelReviewController modelController;

    public SearchHotelServlet(HotelReviewController modelController) {
        this.modelController = modelController;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || Boolean.FALSE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("Please login first.");
            response.sendRedirect("/login");
            return;
        }


        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/search.html");
        VelocityContext context = new VelocityContext();

        String username = (String)session.getAttribute("username");
        if (username != null)
            context.put("username", username);

            try (StringWriter writer = new StringWriter()) {
            template.merge(context, writer);
            out.println(writer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || Boolean.FALSE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("Please login first.");
            response.sendRedirect("/login");
            return;
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/search.html");
        VelocityContext context = new VelocityContext();

        String username = (String)session.getAttribute("username");
        if (username != null)
            context.put("username", username);

        String hotelId = request.getParameter("hotelId");
        List<String> hotels = new ArrayList<>();
        String result = modelController.findHotel(hotelId);
        hotels.add(result);
        context.put("hotels", hotels);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        System.out.println(writer);

        out.println(writer);
    }
}
