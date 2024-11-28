package server.servlet;

import hotelapp.contoller.HotelReviewController;
import hotelapp.model.entity.Review;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SubmitReviewServlet extends HttpServlet {
    private HotelReviewController modelController;

    public SubmitReviewServlet(HotelReviewController modelController) {
        this.modelController = modelController;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || Boolean.FALSE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("Please login first.");
            response.sendRedirect("/login");
        }

        String hotelId = (String) session.getAttribute("hotelId");
        String userNickname = (String) session.getAttribute("username");

        String format = "yyyy-MM-dd hh:mm:ss";
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

}
