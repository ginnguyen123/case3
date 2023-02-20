package service;


import model.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategorys();

    Category getCategoryById(int id);
}
