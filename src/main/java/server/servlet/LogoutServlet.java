package server.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    /**
     * Takes care of user logout. Invalidates HttpSession.
     * @param request contains HttpSession.
     * @param response to be redirected for the user.
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session != null)
            session.invalidate();
        response.sendRedirect("/login");
    }
}
