package server;

import hotelapp.contoller.HotelReviewController;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import server.servlet.*;

public class TravelServer {
    private static final int PORT = 8080;
    private Server travelServer;
    private HotelReviewController modelController;

    public TravelServer(HotelReviewController modelController) {
        travelServer = new Server(PORT);
        this.modelController = modelController;
    }

    public void start() {
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        handler.addServlet(HomepageServlet.class, "/");
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(RegisterServlet.class, "/register");
        handler.addServlet(LogoutServlet.class, "/logout");
        handler.addServlet(new ServletHolder(new SearchHotelServlet(modelController)), "/search");
        handler.addServlet(new ServletHolder(new HotelInfoServlet(modelController)), "/hotelinfo/*");
        handler.addServlet(new ServletHolder(new ManageReviewServlet(modelController)), "/manage-review/*");

        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        handler.setAttribute("templateEngine", velocity);

        travelServer.setHandler(handler);

        try {
            travelServer.start();
            travelServer.join();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
