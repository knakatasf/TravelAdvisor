package server;

import hotelapp.contoller.HotelReviewController;
import server.TravelServer;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class TravelServerDriver {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        // FILL IN CODE, and add more classes as needed
        HotelReviewController modelController = new HotelReviewController();
        modelController.loadData(args);
        TravelServer server = new TravelServer(modelController);
        server.start();
    }
}
