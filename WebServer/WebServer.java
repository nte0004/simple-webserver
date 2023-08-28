import com.hp.gagawa.java.elements.*;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

public class WebServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext root = server.createContext("/");
        root.setHandler(WebServer::handleRequest);

        HttpContext context = server.createContext("/users");
        context.setHandler(WebServer::handleRequestUser);

        HttpContext product = server.createContext("/products");
        product.setHandler(WebServer::handleRequestOneProduct);

        HttpContext allproducts = server.createContext("/products/all");
        allproducts.setHandler(WebServer::handleRequestAllProducts);

        HttpContext order = server.createContext("/orders");
        order.setHandler(WebServer::handleRequestOrder);

        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Fish Store") );
        head.appendChild( title );

        Body body = new Body();
        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Fish Store Information") );
        body.appendChild( h1 );
        //html.appendChild( body );
        P para = new P();
        para.appendText("Search User Information: localhost:8500/users/&ltusername&gt");
        html.appendChild(para);

        para = new P();
        para.appendText("Search Product Information: localhost:8500/products/&ltproduct_id&gt");
        html.appendChild(para);

        para = new P();
        para.appendText("Search Order Information: localhost:8500/orders/&ltorder_id&gt");
        html.appendChild(para);

        String response = html.write();
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRequestUser(HttpExchange exchange) throws IOException {
        String uri =  exchange.getRequestURI().toString();

        String id = uri.substring(uri.lastIndexOf('/')+1);

        System.out.println(id);


        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text(""+ id));
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text(id + "'s Information") );
        body.appendChild( h1 );

        P para = new P();
        //para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        //body.appendChild(para);

        UserModel user = dao.loadUserID(id);

        if (user != null) {
            para = new P();
            para.appendText("UserID: " + user.userID);
            html.appendChild(para);
            para = new P();
            para.appendText("Username: " + user.username);
            html.appendChild(para);
            para = new P();
            para.appendText("Password: " + user.password);
            html.appendChild(para);
            para = new P();
            para.appendText("Display Name: " + user.displayName);

            String status = "false";
            if (user.isManager) {status = "true";}
            para = new P();
            para.appendText("Manager:" + status);

            html.appendChild(para);
        }
        else {
            para = new P();
            para.appendText("User not found");
            html.appendChild(para);
        }

        String response = html.write();

        System.out.println(response);

        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    private static void handleRequestOrder(HttpExchange exchange) throws IOException {
        String uri =  exchange.getRequestURI().toString();

        int id = Integer.parseInt(uri.substring(uri.lastIndexOf('/')+1));

        System.out.println(id);


        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Order " + id));
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Order " + id + " Information") );
        body.appendChild( h1 );

        P para = new P();
        //para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        //body.appendChild(para);

        OrderModele order = dao.loadOrder(id);

        if (order != null) {
            para = new P();
            para.appendText("Order ID: " + order.orderID);
            html.appendChild(para);
            para = new P();
            para.appendText("Ordered On: " + order.orderDate);
            html.appendChild(para);
            para = new P();
            para.appendText("Customer: " + order.customer);
            html.appendChild(para);
            para = new P();
            para.appendText("Total Cost($): " + order.totalCost);
            html.appendChild(para);
            para = new P();
            para.appendText("Total Tax ($): " + order.totalTax);
            html.appendChild(para);
        }
        else {
            para = new P();
            para.appendText("Order not found");
            html.appendChild(para);
        }

        String response = html.write();

        System.out.println(response);

        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRequestAllProducts(HttpExchange exchange) throws IOException {
//        String response =  "This simple web server is designed with help from ChatGPT!";

        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        List<ProductModel> list = dao.loadAllProducts();

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Fish Products"));
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Available Fish") );
        body.appendChild( h1 );

        //P para = new P();
        //para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        //body.appendChild(para);

        P para = new P();
        para.appendChild( new Text("There are currently " + list.size() + " different types of fish available."));
        body.appendChild(para);

        Table table = new Table();
        Tr row = new Tr();
        Th header = new Th(); header.appendText("ProductID"); row.appendChild(header);
        header = new Th(); header.appendText("Product name"); row.appendChild(header);
        header = new Th(); header.appendText("Price"); row.appendChild(header);
        header = new Th(); header.appendText("Quantity"); row.appendChild(header);
        table.appendChild(row);

        for (ProductModel product : list) {
            row = new Tr();
            Td cell = new Td(); cell.appendText(String.valueOf(product.productID)); row.appendChild(cell);
            cell = new Td(); cell.appendText(product.name); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(product.price)); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(product.quantity)); row.appendChild(cell);
            table.appendChild(row);
        }

        table.setBorder("1");

        html.appendChild(table);
        String response = html.write();

        System.out.println(response);


        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    private static void handleRequestOneProduct(HttpExchange exchange) throws IOException {
        String uri =  exchange.getRequestURI().toString();

        int id = Integer.parseInt(uri.substring(uri.lastIndexOf('/')+1));

        System.out.println(id);


        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Product " + id) );
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Product " + id + " Information") );
        body.appendChild( h1 );

        P para = new P();
        //para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        //body.appendChild(para);

        ProductModel product = dao.loadProduct(id);

        if (product != null) {

            para = new P();
            para.appendText("Product ID: " + product.productID);
            html.appendChild(para);
            para = new P();
            para.appendText("Product name: " + product.name);
            html.appendChild(para);
            para = new P();
            para.appendText("Price ($): " + product.price);
            html.appendChild(para);
            para = new P();
            para.appendText("Quantity: " + product.quantity);
            html.appendChild(para);
        }
        else {
            para = new P();
            para.appendText("Product not found");
            html.appendChild(para);
        }

        String response = html.write();

        System.out.println(response);

        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
