import java.util.List;

public interface DataAccess {
    void connect(String str);

    void saveProduct(ProductModel product);

    boolean doUsernamePasswordExist(String username, String password);

    boolean doOrdererMatchUser(String username, int orderID);

    ProductModel loadProduct(int productID);

    List<ProductModel> loadAllProducts();

    UserModel loadUserID(String userID);

    OrderModele loadOrder(int orderID);
}
