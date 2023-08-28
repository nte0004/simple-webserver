import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ServiceRegistry {

    public static HashMap<Integer, List<ServiceInfoModel>> registry = new HashMap<>();

    public static void main(String[] args) throws Exception {

        //System.out.println("Hello world!");

        Class.forName("org.sqlite.JDBC");

        // server is listening on port 5000
        ServerSocket ss = new ServerSocket(5000);



        // running infinite loop for getting
        // client request

        System.out.println("Starting server program: Service Registry");

        int nClients = 0;

        while (true)
        {
            Socket s = null;
            // socket object to receive incoming client requests
            s = ss.accept();

            nClients++;
            System.out.println("A new client is connected : " + s + " client number: " + nClients);
            serve(s, nClients);

        }
    }

    private static void serve(Socket socket, int clientID) throws IOException {
        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();
        Gson gson = new Gson();
        ServiceMessageModel req = gson.fromJson(msg, ServiceMessageModel.class);
        ServiceMessageModel res = new ServiceMessageModel();

        if (req.code == ServiceMessageModel.SERVICE_PUBLISH_REQUEST) {
            ServiceInfoModel info = gson.fromJson(req.data, ServiceInfoModel.class);

            System.out.println("Service info from client " + clientID + ": " + req.data);

            List<ServiceInfoModel> list = registry.get(info.serviceCode);
            if (list == null) {
                list = new ArrayList<ServiceInfoModel>();
                registry.put(info.serviceCode, list);
            }

            list.add(info);
            //System.out.println("This is what the list says after registering the service:" + list);

            res.code = ServiceMessageModel.SERVICE_PUBLISH_OK;
            res.data = "Successfully added service to registry";
        }

        // need for SERVICE_UNPUBLISH_REQUEST: remove a microservice from the registry!!!
        if (req.code == ServiceMessageModel.SERVICE_UNPUBLISH_REQUEST) {
            ServiceInfoModel info = gson.fromJson(req.data, ServiceInfoModel.class);
            //Get list of these microservices
            List<ServiceInfoModel> list = registry.get(info.serviceCode);
            if(list != null) {
                list.remove(info);
                System.out.println("Removed microservice from Registry.");
            }
            else {
                System.out.println("Failed to remove microservice from Registry.");
            }
        }

        // discovery request!

        if (req.code == ServiceMessageModel.SERVICE_DISCOVER_REQUEST) {
            String[] data = req.data.split(",", 2);
            int serviceCode = Integer.parseInt(data[0]);
            //int serviceSpec = Integer.parseInt(data[1]);
            System.out.println("Client " + clientID + " asks for service " + ": " + serviceCode + " with specification: " + data[1]);

            List<ServiceInfoModel> list = registry.get(serviceCode);

            if (list == null) {
                res.code = ServiceMessageModel.SERVICE_DISCOVER_NOT_FOUND;
                res.data = "Service Not Found!";
            }
            else {
                res.code = ServiceMessageModel.SERVICE_DISCOVER_OK;
                //Picks a service id that is available randomly
                Random rand = new Random();
                int id = rand.nextInt(list.size());
                int code = list.get(id).serviceCode;

                //Connects to the service given host address and port
                String serviceHost = list.get(id).serviceHostAddress;
                int servicePort = list.get(id).serviceHostPort;
                Socket serviceSocket = new Socket(serviceHost, servicePort);

                //Send appropriate info to the service
                DataOutputStream servicePrinter = new DataOutputStream(serviceSocket.getOutputStream());
                if (code == ServiceInfoModel.PRODUCT_INFO_SERVICE) {
                    servicePrinter.writeInt(Integer.parseInt(data[1]));
                }
                else if (code == ServiceInfoModel.ORDER_INFO_SERVICE) {
                    servicePrinter.writeInt(Integer.parseInt(data[1]));
                }
                else if (code == ServiceInfoModel.PRODUCT_PRICE_UPDATE_SERVICE) {
                    servicePrinter.writeUTF(data[1]);
                }
                else if (code == ServiceInfoModel.PRODUCT_QUANTITY_UPDATE_SERVICE) {
                    servicePrinter.writeUTF(data[1]);
                }
                else if (code == ServiceInfoModel.USER_EXIST_SERVICE) {
                    servicePrinter.writeUTF(data[1]);
                }
                else if (code == ServiceInfoModel.CAN_CANCEL_ORDER_SERVICE) {
                    servicePrinter.writeUTF(data[1]);
                }
                servicePrinter.flush();

                //Accept response from service
                DataInputStream serviceReader = new DataInputStream(serviceSocket.getInputStream());
                String serviceMsg = serviceReader.readUTF();
                servicePrinter.close();
                serviceReader.close();
                serviceSocket.close();
                res.data = serviceMsg;
            }

        }

        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        String json = gson.toJson(res);
        System.out.println("Response to client  " + clientID + " json = "  + json);
        printer.writeUTF(json);
        printer.flush();
        printer.close();
        reader.close();
        socket.close();
    }
}
