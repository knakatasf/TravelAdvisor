package server.servlet;

import hotelapp.contoller.HotelReviewController;
import hotelapp.model.entity.Hotel;
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
import java.util.Optional;

public class SearchHotelServlet extends HttpServlet {
    private HotelReviewController modelController;

    public SearchHotelServlet(HotelReviewController modelController) {
        this.modelController = modelController;
    }

    /**
     * Displays Hotel Search format and sends the user input (Hotel ID or keyword) to doPost() method.
     * @param request contains HttpSession to check if the user is logged in or not
     * @param response to be sent to doPost() method.
     * @throws IOException
     */
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
        Template template = ve.getTemplate("static/html/search.html");
        VelocityContext context = new VelocityContext();

        String username = (String)session.getAttribute("username");
        if (username != null)
            context.put("username", username);

        List<Hotel> hotelList = modelController.loadAllHotelsByValue();
        context.put("hotelList", hotelList);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    /**
     * Handles user input (Hotel ID or keyword) and displays hotels that match the search criteria.
     * When the user clicks of the hotels displayed, be sent to HotelInfoServlet with Hotel ID.
     * @param request contains Hotel ID or keyword.
     * @param response contains Hotel ID to be sent to HotelInfoServlet
     * @throws IOException
     */
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
        Template template = ve.getTemplate("static/html/search.html");
        VelocityContext context = new VelocityContext();

        String username = (String)session.getAttribute("username");
        if (username != null)
            context.put("username", username);

        String hotelId = request.getParameter("hotelId");
        String keyword = request.getParameter("keyword");

        List<Hotel> hotelList = new ArrayList<>();
        if (hotelId != null && !hotelId.isEmpty()) {
            Optional<Hotel> maybeHotel = modelController.findHotelByValue(hotelId);
            if (maybeHotel.isPresent()) {
                hotelList.add(maybeHotel.get());
            }
        }

        if (keyword != null && !keyword.isEmpty()) {
            List<Hotel> maybeHotels = modelController.findHotelsByKeyword(keyword);
            if (!maybeHotels.isEmpty()) {
                hotelList.addAll(maybeHotels);
            }
        }

        if (!hotelList.isEmpty()) {
            context.put("hotelList", hotelList);
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
