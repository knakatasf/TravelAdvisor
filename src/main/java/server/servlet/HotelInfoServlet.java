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
        Template template = ve.getTemplate("static/html/hotelinfo.html");
        VelocityContext context = new VelocityContext();

        String username = (String) session.getAttribute("username");
        if (username != null)
            context.put("username", username);


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
            session.setAttribute("hotelId", hotelId);

            List<Review> reviewList = modelController.findReviewsByValue(hotelId);
            if (reviewList != null && !reviewList.isEmpty()) {
                context.put("averageRating", getAverageRate(reviewList));
                context.put("reviews", reviewList);
            }
        }
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        getBackToLogin(session, request, response);

        String operationType = request.getParameter("operationType");
        switch (operationType) {
            case "add":
                addReview(session, request, response);
                break;
            case "edit":
                editReview(session, request, response);
                break;
            case "delete":
                deleteReview(session, request, response);
                break;
        }
    }

    private void getBackToLogin(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (session == null || Boolean.FALSE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("Please login first.");
            response.sendRedirect("/login");
        }
    }

    private double getAverageRate(List<Review> reviewList) {
        double total = 0.0;
        for (Review review : reviewList) {
            total += review.getOverallRating();
        }
        return Math.round((total / reviewList.size()) * 10) / 10.0;
    }

    private void addReview(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/html/addreview.html");
        VelocityContext context = new VelocityContext();

        String username = (String) session.getAttribute("username");
        if (username != null)
            context.put("username", username);

        String hotelId = (String) session.getAttribute("hotelId");
        if (hotelId != null) {
            Optional<Hotel> maybeHotel = modelController.findHotelByValue(hotelId);
            if (maybeHotel.isPresent()) {
                context.put("hotel", maybeHotel.get());
            }
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    private void editReview(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("static/html/editreview.html");
        VelocityContext context = new VelocityContext();

        String username = (String) session.getAttribute("username");
        if (username != null)
            context.put("username", username);

        String hotelId = (String) session.getAttribute("hotelId");
        if (hotelId != null) {
            Optional<Hotel> maybeHotel = modelController.findHotelByValue(hotelId);
            if (maybeHotel.isPresent()) {
                context.put("hotel", maybeHotel.get());
            }
        }

        String reviewId = request.getParameter("reviewId");

        Optional<Review> maybeReview = modelController.findReviewByValue(hotelId, reviewId);
        if (maybeReview.isPresent()) {
            context.put("review", maybeReview.get());
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    private void deleteReview(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = (String) session.getAttribute("hotelId");
        String reviewId = request.getParameter("reviewId");

        modelController.removeReview(hotelId, reviewId);
        response.sendRedirect("/hotelinfo/" + hotelId);
    }
}
