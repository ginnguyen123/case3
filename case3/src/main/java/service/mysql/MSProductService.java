package service.mysql;

import model.Product;
import service.IProductService;
import utils.DateUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MSProductService extends DBContext implements IProductService {
    private static final String SELLECT_ALL_PRODUCT = "SELECT * FROM product;";
    private static final String FIND_PRODUCT_BY_ID = "SELECT * FROM product where `id` = ?;";
    //private static final String EDIT_PRODUCT = "UPDATE `product` SET `name` = ?, `price` = ?, `description` = ?, `image` = ?, `create_at` = ?, `category_id` = ? WHERE (`id` = ?);";
    private static final String DELETE_PRODUCT_BY_ID = "delete from product where `id` = ?;";
    private static final String INSERT_PRODUCT = "insert into product(`name` ,`price`, `description`,`image`,`category_id`) value(?,?,?, ?,?); ";
    private static final String CHECK_IMAGE_EXIST = "SELECT * FROM product where image = ?;";
    public static final String EDIT_PRODUCT = "UPDATE product SET `name` = ?, `price` = ?, `description` = ?, `image` =?, `category_id` = ? WHERE (`id` = ?);";
    public static final String SELECT_PRODUCT_BY_KW_IDCATEGORY = "select * from product where idCategory= ? and name like ?;";
    public static final String SEARCH_ALL_PRODUCT = "SELECT * from product where `name` like ?;";
    public static final String SEARCH_PRODUCT_BY_CATALOGY = "SELECT * from product where `name` like ? and `category_id` = ?;";
    public static final String SELLECT_ALL_PRODUCT_PAGGING = "SELECT SQL_CALC_FOUND_ROWS * FROM product where `name` like ? limit ?,?;";

    private int noOfRecords;

    public int getNoOfRecords() {
        return noOfRecords;
    }

    public void setNoOfRecords(int noOfRecords) {
        this.noOfRecords = noOfRecords;
    }

    @Override
    public List<Product> getAllProduct() {
        List<Product> products = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_ALL_PRODUCT);
            ResultSet rs = preparedStatement.executeQuery();
            while  (rs.next()){
                Product product = getProductFromRs(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    @Override
    public List<Product> getAllProductsPagging(String keyWord, int page, int numberOfPage) {
        List<Product> products = new ArrayList<>();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SELLECT_ALL_PRODUCT_PAGGING);
            //SELECT SQL_CALC_FOUND_ROWS * FROM product where `name` like ? limit ?,?
            preparedStatement.setString(1, "%" + keyWord + "%");
            preparedStatement.setInt(2,page);
            preparedStatement.setInt(3,numberOfPage);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Product product = getProductFromRs(resultSet);
                products.add(product);
            }

            resultSet = preparedStatement.executeQuery("SELECT FOUND_ROWS()");
            while (resultSet.next()) {
                noOfRecords = resultSet.getInt(1);
            }
            connection.close();
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

        return products;
    }

    @Override
    public List<Product> getAllProductSearch(String keyWord, int idCategory) {
        List<Product> products = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = null;
            if(idCategory == -1){
                preparedStatement = connection.prepareStatement(SEARCH_ALL_PRODUCT);
                preparedStatement.setString(1,"%" + keyWord + "%");

            }else {
                preparedStatement = connection.prepareStatement(SEARCH_PRODUCT_BY_CATALOGY);
                preparedStatement.setString(1, "%" + keyWord + "%");
                preparedStatement.setInt(2, idCategory);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Product product = getProductFromRs(resultSet);
                products.add(product);
            }
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return products;
    }

    private Product getProductFromRs(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        String image = rs.getString("image");
        int idCategory = rs.getInt("category_id");

        //long id, String name, double price, String description, String image, Date createAt, int idCategory
        Product product = new Product(id, name, price, description, image,  idCategory);
        return product;
    }

    @Override
    public Product findProductById(Long id) {
        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_PRODUCT_BY_ID);
            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Product product = getProductFromRs(rs);
                return product;
            }

            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    @Override
    public void editProduct(Product product) {
        Connection connection = getConnection();
        try {
            //UPDATE product SET `name` = ?, `price` = ?, `description` = ?, `image` =?,
            //   WHERE (`id` = ?);"

            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_PRODUCT);
            preparedStatement.setString(1,product.getName());
            preparedStatement.setDouble(2,product.getPrice());
            //description` = ?, `image` =?, `create_at` = ?, `category_id` = ?
            preparedStatement.setString(3,product.getDescription());
            preparedStatement.setString(4, product.getImage());
            preparedStatement.setInt(5,product.getIdCategory());
            preparedStatement.setLong(6,product.getId());
            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

    }

    @Override
    public void deleteProductById(Long id) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCT_BY_ID);
            preparedStatement.setLong(1,id);
            preparedStatement.executeUpdate();
            System.out.println("deleteProductById" + preparedStatement);
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    @Override
    public void createProduct(Product product)  {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT);
            //"insert into product(`name` ,`price`, `description`,`image`,`category_id`) value(?,?,?, ?,?); ";
            preparedStatement.setString(1,product.getName());
            preparedStatement.setLong(2, (long)product.getPrice());
            preparedStatement.setString(3,product.getDescription());
            preparedStatement.setString(4,product.getImage());
            preparedStatement.setInt(5,product.getIdCategory());
            preparedStatement.executeUpdate();
            System.out.println("preparedStatement()");
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    @Override
    public boolean checkImageExists(String image) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CHECK_IMAGE_EXIST);
            preparedStatement.setString(1,image);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return true;
            }
            connection.close();

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

        return false;
    }
}
