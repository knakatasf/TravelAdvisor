package server;

import model.TravelDatabaseModel;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import server.servlet.LoginServlet;
import server.servlet.RegisterServlet;

public class TravelServer {
    public static final int PORT = 8080;

    public static void start() {
        TravelDatabaseModel dbModel = TravelDatabaseModel.getInstance();

        Server server = new Server(PORT);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(RegisterServlet.class, "/register");

        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        handler.setAttribute("templateEngine", velocity);

        server.setHandler(handler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
