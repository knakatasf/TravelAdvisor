package server.servlet;

import hotelapp.contoller.HotelReviewController;
import hotelapp.model.entity.Review;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

public class ManageReviewServlet extends HttpServlet {
    private HotelReviewController modelController;

    public ManageReviewServlet(HotelReviewController modelController) {
        this.modelController = modelController;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || Boolean.FALSE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("Please login first.");
            response.sendRedirect("/login");
        }

        String hotelId = (String) session.getAttribute("hotelId");
        response.sendRedirect("/hotelinfo" + hotelId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || Boolean.FALSE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("Please login first.");
            response.sendRedirect("/login");
        }

        String operationType = request.getParameter("op");
        switch (operationType) {
            case "add":
                addReview(session, request, response);
                break;
            case "edit":
                editReview(session, request, response);
                break;
        }
    }

    private void addReview(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = (String) session.getAttribute("hotelId");
        String userNickname = (String) session.getAttribute("username");

        String format = "yyyy-MM-dd-hh-mm-ss";
        DateFormat formatter = new SimpleDateFormat(format);
        String submissionDateAndTime = formatter.format(Calendar.getInstance().getTime());
        String submissionDate = submissionDateAndTime.substring(0, 10);
        String reviewId = userNickname + submissionDateAndTime;

        String title = request.getParameter("title");
        String reviewText = request.getParameter("reviewText");
        String overallRating = request.getParameter("rating");

        Review.ReviewBuilder builder = new Review.ReviewBuilder();
        Review review = builder.hotelId(hotelId).reviewId(reviewId).title(title).reviewText(reviewText)
                .userNickname(userNickname).submissionDate(submissionDate).overallRating(overallRating)
                .build();

        modelController.addReview(review);

        response.sendRedirect("/hotelinfo/" + hotelId);
    }

    private void editReview(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = (String) session.getAttribute("hotelId");
        String reviewId = request.getParameter("reviewId");

        Optional<Review> maybeReview = modelController.findReviewByValue(hotelId, reviewId);
        if (maybeReview.isPresent()) {
            Review oldReview = maybeReview.get();
            modelController.removeReview(hotelId, reviewId);

            String newTitle = request.getParameter("reviewTitle");
            String newRating = request.getParameter("rating");
            String newReviewText = request.getParameter("reviewText");

            Review.ReviewBuilder builder = new Review.ReviewBuilder();
            Review newReview = builder.hotelId(hotelId).reviewId(reviewId).title(newTitle)
                    .reviewText(newReviewText).userNickname(oldReview.getUserNickname())
                    .submissionDate(oldReview.getSubmissionDate()).overallRating(newRating)
                    .build();

            modelController.addReview(newReview);

            response.sendRedirect("/hotelinfo/" + hotelId);
        }
    }
}
