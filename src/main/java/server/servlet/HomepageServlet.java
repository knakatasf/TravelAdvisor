package server.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class HomepageServlet extends HttpServlet {
    /**
     * When the user access "/", if logged in, be sent to "/search", otherwise "login"
     * @param request contains HttpSession.
     * @param response to be redirected for the user.
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

        response.sendRedirect("/search");
    }
}
