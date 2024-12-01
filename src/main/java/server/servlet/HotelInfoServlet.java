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

    /**
     * Displays Hotel information along with the list of its reviews.
     * When the user clicks "Add Review", "Edit" or "Delete" button, be sent to doPost() method.
     * @param request contains Hotel ID to display its information including reviews.
     * @param response to be displayed for the user.
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
        double averageRating = 0.0;
        Optional<Hotel> maybeHotel = modelController.findHotelByValue(hotelId);
        if (maybeHotel.isPresent()) {
            hotel = maybeHotel.get();
            context.put("hotel", hotel);
            session.setAttribute("hotelId", hotelId);

            List<Review> reviewList = modelController.findReviewsByValue(hotelId);
            if (reviewList != null && !reviewList.isEmpty()) {
                averageRating = getAverageRate(reviewList);
                context.put("reviews", reviewList);
            }
            context.put("averageRating", averageRating);
        }
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    /**
     * When the user clicks "Add Review", "Edit" or "Delete" button, invokes respective method to deal with the user action.
     * "Add Review": addReview() will be invoked and displays add review format.
     * "Edit": editReview() will be invoked and displays edit review format.
     * "Delete": deleteReview() will be invoked.
     * @param request contains what action the user want to take.
     * @param response to be redirected to appropriate service.
     * @throws IOException
     */
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
            default:
                String hotelId = (String)session.getAttribute("hotelId");
                response.sendRedirect("/hotelinfo/" + hotelId);
        }
    }

    /**
     * If the user is not logged in, be redirected to /login
     * @param session contains user login information.
     * @param request
     * @param response to be redirected to /login
     * @throws IOException
     */
    private void getBackToLogin(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (session == null || Boolean.FALSE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("Please login first.");
            response.sendRedirect("/login");
        }
    }

    /**
     * Calculates and returns average satisfactory rate for a hotel.
     * @param reviewList to be calculated the rate.
     * @return average satisfactory rate.
     */
    private double getAverageRate(List<Review> reviewList) {
        double total = 0.0;
        for (Review review : reviewList) {
            total += review.getOverallRating();
        }
        return Math.round((total / reviewList.size()) * 10) / 10.0;
    }

    /**
     * When the user clicks "Add Review" button, this method is invoked through doPost method.
     * Displays add review format, and sends user input to ManageReviewServlet.
     * @param session contains username.
     * @param request
     * @param response to be displayed for the user.
     * @throws IOException
     */
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

    /**
     * When the user clicks "Edit Review" button, this method is invoked through doPost method.
     * Displays edit review format, and sends user input to ManageReviewServlet.
     * @param session contains username.
     * @param request
     * @param response to be displayed for the user.
     * @throws IOException
     */
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

    /**
     * When the user clicks "Delete Review" button, this method is invoked through doPost method. Deletes the review.
     * @param session contains username.
     * @param request
     * @param response to be redirected for the user.
     * @throws IOException
     */
    private void deleteReview(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = (String) session.getAttribute("hotelId");
        String reviewId = request.getParameter("reviewId");

        modelController.removeReview(hotelId, reviewId);
        response.sendRedirect("/hotelinfo/" + hotelId);
    }
}
