package service;


import model.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProduct();
    Product findProductById(Long id);
    void editProduct(Product Product);
    void deleteProductById(Long id);
    void createProduct(Product Product);
    boolean checkImageExists(String image);
    List<Product> getAllProductSearch(String keyWord, int idCategory);

    List<Product> getAllProductsPagging(String keyWord, int page, int numberOfPage);

}
