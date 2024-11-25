package server.servlet;

import hotelapp.contoller.HotelReviewController;
import hotelapp.model.entity.Hotel;
import hotelapp.model.entity.Review;
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
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

public class HotelInfoServlet extends HttpServlet {
    private HotelReviewController modelController;

    public HotelInfoServlet(HotelReviewController modelController) {
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
        Template template = ve.getTemplate("static/hotelinfo.html");
        VelocityContext context = new VelocityContext();

        String hotelId = request.getPathInfo().substring(1);
        if (hotelId == null || hotelId.isEmpty()) {
            System.out.println("Hotel ID was not provided.");
            response.sendRedirect("/search");
            return;
        }

        Hotel hotel = null;
        Optional<Hotel> maybeHotel = modelController.findHotelByValue(hotelId);
        if (maybeHotel.isPresent()) {
            hotel = maybeHotel.get();
            context.put("hotel", hotel);

            List<Review> reviewList = modelController.findReviewsByValue(hotelId);
            if (reviewList != null && !reviewList.isEmpty()) {
                context.put("reviews", reviewList);
            }
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
