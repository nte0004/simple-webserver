import org.sqlite.SQLiteException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDataAdapter implements DataAccess {
    Connection conn = null;

    @Override
    public void connect(String url) {
        try {
            // db parameters
            // create a connection to the database
            Class.forName("org.sqlite.JDBC");

            conn = DriverManager.getConnection(url);
            if (conn == null)
                System.out.println("Cannot make the connection!!!");
            else
                System.out.println("The connection object is " + conn);

            System.out.println("Connection to SQLite has been established.");

            /* Test data!!!
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product");

            while (rs.next())
                System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
            */

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void saveProduct(ProductModel product) {
        try {
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate("UPDATE Product SET "
                    + "productID = " + product.productID + ","
                    + "name = " + '\'' + product.name + '\'' + ","
                    + "price = " + product.price + ","
                    + "quantity = " + product.quantity +
                    " WHERE productID = " + product.productID
                );
            this.conn.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public boolean doUsernamePasswordExist(String username, String password) {
        boolean result = false;
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM User WHERE UserName = ? AND Password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result = true;
            }
            this.conn.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean doOrdererMatchUser(String username, int orderID) {
        boolean result = false;
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM \"Order\" WHERE OrderID = ? AND Customer = ?");
            statement.setString(2, username);
            statement.setInt(1, orderID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result = true;
            }
            else {
                OrderModele order = loadOrder(orderID);
                System.out.println("Expected Customer: " + username + " Found Customer: " + order.customer);
            }
            this.conn.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    @Override
    public ProductModel loadProduct(int productID) {
        ProductModel product = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + productID);
            if (rs.next()) {
                product = new ProductModel();
                product.productID = rs.getInt(1);
                product.name = rs.getString(2);
                product.price = rs.getDouble(3);
                product.quantity = rs.getDouble(4);
            }
            //stmt.close();
            this.conn.close();
            //conn.close();
            //System.out.println("Connection to SQLite closed");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return product;
    }

    @Override
    public List<ProductModel> loadAllProducts() {
        List<ProductModel> list = new ArrayList<>();
        ProductModel product = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product ");
            while (rs.next()) {
                product = new ProductModel();
                product.productID = rs.getInt(1);
                product.name = rs.getString(2);
                product.price = rs.getDouble(3);
                product.quantity = rs.getDouble(4);
                list.add(product);
            }
            this.conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    @Override
    public UserModel loadUserID(String userID) {
        UserModel user = null;
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM User WHERE UserName = ?");
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                user = new UserModel();
                user.userID = rs.getInt(1);
                user.username = rs.getString(2);
                user.password = rs.getString(3);
                user.displayName = rs.getString(4);
                user.isManager = rs.getBoolean(5);
            }
            //statement.close();
            this.conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }
    @Override
    public OrderModele loadOrder(int orderID) {
        OrderModele order = null;
        try {
            Statement stmt = conn.createStatement();
            //stmt.closeOnCompletion();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"Order\" WHERE OrderID = " + orderID);
            if (rs.next()) {
                order = new OrderModele();
                order.orderID = rs.getInt(1);
                order.orderDate = rs.getDate(2);
                order.customer = rs.getString(3);
                order.totalCost = rs.getDouble(4);
                order.totalTax = rs.getDouble(5);
            }
            //stmt.close();
            this.conn.close();
            //System.out.println("Connection to SQLite closed");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return order;
    }
}
