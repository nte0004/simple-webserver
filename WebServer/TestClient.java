import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Random;
import java.util.ArrayList;

public class TestClient {
    /*
        Test Specification:
            1. updateProductPrice - Updates price of specified product to the newPrice value
            2. updateProductQuantity - Updates quantity of specified product to the newQuantity value
            3. requestProduct - Returns product with value of productID
            4. requestOrder - Returns order with value of orderID
            5. checkUserExist - Returns true if given credentials are valid, false if not
            6. validCancellation - Returns true if login is valid, and the specified order exist where the order Customer
                    matches the User's username. Returns false if not.
     */
    public static void main(String[] args) throws IOException {
        //Info for different service request
        int productID = 3;
        int orderID = 3;
        double newPrice = 13.00;
        double newQuantity = 125;
        String username = "Shopper";
        String password = "password";
        String customer = "admin";
        updateProductPrice(productID, newPrice);
        updateProductQuantity(productID, newQuantity);
        ProductModel product = requestProduct(productID);
        OrderModele order = requestOrder(orderID);
        boolean validLogin = checkUserExist(username, password);
        boolean validCancellation = checkCanCancelOrder(customer, orderID);
        //testOrderSave();
    }

    public static ProductModel requestProduct(int productID) throws IOException {
        System.out.println("Requesting Product");
        // ask for service from Registry
        Socket socket = new Socket("localhost", 5000);
        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = ServiceInfoModel.PRODUCT_INFO_SERVICE + "," + productID;

        Gson gson = new Gson();

        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();
        System.out.println("Discovery request for product - ID: " + productID + " sent.");

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();

        //System.out.println("Message from service registry: " + msg);

        printer.close();
        reader.close();
        socket.close();

        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

        if (res.code == ServiceMessageModel.SERVICE_DISCOVER_OK) {
            System.out.println("Server Reply: \nCode: " + res.code + "\nData: " + res.data + "\nSERVICE COMPLETE");
        }
        else {
            System.out.println("Service not found \nSERVICE COMPLETE");
        }
        return gson.fromJson(res.data, ProductModel.class);
    }

    public static OrderModele requestOrder(int orderID) throws IOException {
        System.out.println("Requesting order");
        // ask for service from Registry
        Socket socket = new Socket("localhost", 5000);
        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = ServiceInfoModel.ORDER_INFO_SERVICE + "," + orderID;

        Gson gson = new Gson();

        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();
        System.out.println("Discovery request for order - ID: " + orderID + " sent.");

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();

        //System.out.println("Message from service registry: " + msg);

        printer.close();
        reader.close();
        socket.close();

        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

        if (res.code == ServiceMessageModel.SERVICE_DISCOVER_OK) {
            System.out.println("Server Reply: \nCode: " + res.code + "\nData: " + res.data);
            System.out.println("SERVICE COMPLETE");
        }
        else {
            System.out.println("Service not found \nSERVICE COMPLETE");
        }
        return gson.fromJson(res.data, OrderModele.class);
    }

    public static void updateProductPrice(int productID, double newPrice) throws IOException {
        System.out.println("Updating price of product");
        // ask for service from Registry
        Socket socket = new Socket("localhost", 5000);
        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = ServiceInfoModel.PRODUCT_PRICE_UPDATE_SERVICE + "," + productID + "," + newPrice;
        Gson gson = new Gson();
        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();
        System.out.println("Discovery request to update price of product " + productID + " to $" + newPrice + " sent.");

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();

        //System.out.println("Message from service registry: " + msg);

        printer.close();
        reader.close();
        socket.close();

        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

        if (res.code == ServiceMessageModel.SERVICE_DISCOVER_OK) {
            System.out.println("Server Reply: \nCode: " + res.code + "\nData: " + res.data);
            System.out.println("SERVICE COMPLETE");
            //requestProduct(productID);
        }
        else {
            System.out.println("Service not found \nSERVICE COMPLETE");
        }
    }

    public static void updateProductQuantity(int productID, double newQuantity) throws IOException {
        System.out.println("Updating quantity of product");
        // ask for service from Registry
        Socket socket = new Socket("localhost", 5000);
        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = ServiceInfoModel.PRODUCT_QUANTITY_UPDATE_SERVICE + "," + productID + "," + newQuantity;
        Gson gson = new Gson();
        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();
        System.out.println("Discovery request to update quantity of product " + productID + " to " + newQuantity + " sent.");

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();

        //System.out.println("Message from service registry: " + msg);

        printer.close();
        reader.close();
        socket.close();

        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

        if (res.code == ServiceMessageModel.SERVICE_DISCOVER_OK) {
            System.out.println("Server Reply: \nCode: " + res.code + "\nData: " + res.data);
            System.out.println("SERVICE COMPLETE");
            //System.out.println("Loading product post update: \n");
            //requestProduct(productID);
        }
        else {
            System.out.println("Service not found \nSERVICE COMPLETE");
        }
    }

    public static boolean checkUserExist(String username, String password) throws IOException {
        System.out.println("Checking if given credentials exist \nValidating credentials. . .");
        // ask for service from Registry
        Socket socket = new Socket("localhost", 5000);
        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = ServiceInfoModel.USER_EXIST_SERVICE + "," + username + "," + password;
        Gson gson = new Gson();
        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();
        System.out.println("Discovery request for existence of profile with Username: " + username + " and Password: " + password + " sent.");

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();

        //System.out.println("Message from service registry: " + msg);

        printer.close();
        reader.close();
        socket.close();

        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

        if (res.code == ServiceMessageModel.SERVICE_DISCOVER_OK) {
            System.out.println("Server Reply: \nCode: " + res.code + "\nData: " + res.data);
            if (Boolean.parseBoolean(res.data)) {
                System.out.println("LOGIN CREDENTIALS VALID \nSERVICE COMPLETE");
                return true;
            }
            else {
                System.out.println("LOGIN CREDENTIALS INVALID \nSERVICE COMPLETE");
                return false;
            }
            //System.out.println("Loading product post update: \n");
            //requestProduct(productID);
        }
        else {
            System.out.println("Service not found \nSERVICE COMPLETE");
            return false;
        }
    }

    public static boolean checkCanCancelOrder(String username, int orderID) throws IOException {
        System.out.println("Checking if user can cancel order");
        // Simulate being logged into an account
        boolean validLogin = checkUserExist(username, "password");

        //If successfully logged in we can check that an order exist that matches the username of the user.
        if (validLogin) {
            //System.out.println("Connecting to service registry. . .");
            Socket socket = new Socket("localhost", 5000);
            ServiceMessageModel req = new ServiceMessageModel();
            req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
            req.data = ServiceInfoModel.CAN_CANCEL_ORDER_SERVICE + "," + username + "," + orderID;
            Gson gson = new Gson();
            DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
            printer.writeUTF(gson.toJson(req));
            printer.flush();
            System.out.println("Discovery request for cancellation of Order: " + orderID + " from User: " + username + " sent.");

            DataInputStream reader = new DataInputStream(socket.getInputStream());
            String msg = reader.readUTF();

            //System.out.println("Message from service registry: " + msg);

            printer.close();
            reader.close();
            socket.close();
            ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

            if (res.code == ServiceMessageModel.SERVICE_DISCOVER_OK) {
                System.out.println("Server Reply: \nCode: " + res.code + "\nData: " + res.data);
                if (Boolean.parseBoolean(res.data)) {
                    System.out.println("VALID CANCELLATION REQUEST. \nSERVICE COMPLETE.");
                    return true;
                }
                else {
                    System.out.println("INVALID CANCELLATION REQUEST. \nSERVICE COMPLETE.");
                    return false;
                }
                //System.out.println("Loading product post update: \n");
                //requestProduct(productID);
            }
            else {
                System.out.println("Service not found. \nSERVICE COMPLETE.");
                return false;
            }
        }
        else {
            System.out.println("Invalid Login, must have successful login before checking if user can cancel given order. \nSERVICE COMPLETE.");
            return false;
        }
    }
}
