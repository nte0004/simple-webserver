import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProductQuantityUpdateService {
    public static void main(String[] args) throws Exception {

        // server is listening on port 5050

        int port = 5050;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket ss = new ServerSocket(port);

        // running infinite loop for getting
        // client request

        System.out.println("Starting ProductInfo service at port = " + port);

        boolean result = register("localhost", 5000, "localhost", port); // Register with the Registry, so the client know how to find!

        if (!result) {
            System.out.println("Error: Registered unsuccessfully");
            ss.close();
            return;
        }

        int nClients = 0;

        while (true)
        {
            Socket s = null;
            // socket object to receive incoming client requests
            s = ss.accept();
            nClients++;
            System.out.println("A new client is connected : " + s + " client number: " + nClients);
            deregister("localhost", 5000, "localhost", port);
            serve(s, nClients);

        }
    }

    private static void serve(Socket socket, int clientID) throws Exception {

        DataInputStream reader = new DataInputStream(socket.getInputStream());

        //String msg = reader.readUTF();

        String json = reader.readUTF();
        String[] data = json.split(",", 2);
        int productID = Integer.parseInt(data[0]);
        double newQuantity = Double.parseDouble(data[1]);
        System.out.println("Information from client " + clientID + ": " + "Update quantity of Product " + productID + " to " + newQuantity);

        //Connect to database and update info
        //Class.forName("org.sqlite.JDBC");
        DataAccess adapter = new SQLiteDataAdapter();
        adapter.connect("jdbc:sqlite:store.db");
        ProductModel product = adapter.loadProduct(productID);
        product.quantity = newQuantity;
        adapter = new SQLiteDataAdapter();
        adapter.connect("jdbc:sqlite:store.db");
        adapter.saveProduct(product);

        String ans = "Product successfully updated!";
        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(ans);
        printer.flush();
        printer.close();
        reader.close();
        socket.close();
        System.out.println("MICROSERVICE COMPLETE");
        boolean result = register("localhost", 5000, "localhost", socket.getLocalPort());
        if (result) {
            System.out.println("RE-REGISTRATION SUCCESS");
        }
        else {
            System.out.println("FAILED TO RE-REGISTER");
        }
    }
    /*
        Register this service to the Registry!
     */
    private static boolean register(String regHost, int regPort, String myHost, int myPort) throws IOException {
        System.out.println("Requesting registration to ServiceRegistry");
        ServiceInfoModel info = new ServiceInfoModel();
        info.serviceCode = ServiceInfoModel.PRODUCT_QUANTITY_UPDATE_SERVICE;
        info.serviceHostAddress = myHost;
        info.serviceHostPort = myPort;

        Gson gson = new Gson();

        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_PUBLISH_REQUEST;
        req.data = gson.toJson(info);

        Socket socket = new Socket(regHost, regPort);

        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();
        printer.close();
        reader.close();
        socket.close();


        //System.out.println("Message from server: " + msg);
        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);
        System.out.println("Server Response: \nCode: " + res.code + "\nData: " + res.data);

        if (res.code == ServiceMessageModel.SERVICE_PUBLISH_OK)
            return true;
        else
            return false;
    }

    private static void deregister(String regHost, int regPort, String myHost, int myPort) throws IOException {
        ServiceInfoModel info = new ServiceInfoModel();
        info.serviceCode = ServiceInfoModel.PRODUCT_INFO_SERVICE;
        info.serviceHostAddress = myHost;
        info.serviceHostPort = myPort;

        Gson gson = new Gson();

        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_UNPUBLISH_REQUEST;
        req.data = gson.toJson(info);
        Socket socket = new Socket(regHost, regPort);

        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();

        printer.close();
        socket.close();
        System.out.println("Requested de-registration from ServiceRegistry");
    }
}