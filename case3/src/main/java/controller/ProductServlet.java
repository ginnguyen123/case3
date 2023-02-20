package controller;

import model.Category;
import model.Product;
import service.ICategoryService;
import service.IProductService;
import service.mysql.MSCategoryService;
import service.mysql.MSProductService;
import utils.ValidateUtils;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ProductServlet", urlPatterns = {"/product"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 50, // 50MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class ProductServlet extends HttpServlet{
    private MSProductService productService;
    private ICategoryService categoryService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                showCreateProducts(req, resp);
                break;
            case "delete":
                showDeleteProducts(req, resp);
                break;
            case "edit":
                showEditProducts(req, resp);
                break;
            default:
                showProducts(req, resp);
        }
    }

    private void showProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        String search = "";
        int numberOfPage = 4;

        List<Product> productList = productService.getAllProduct();

        if (req.getParameter("search") != null ){
            search = req.getParameter("search");
        }

        int idCategorie = -1;
        if (req.getParameter("idCategorie")!=null && !req.getParameter("idCategorie").equals("")){
            idCategorie = Integer.parseInt(req.getParameter("idCategorie"));
        }

        int page = 1;
        if (req.getParameter("page") != null) {
            page = Integer.parseInt(req.getParameter("page"));
        }

//        List<Product> products = productService.getAllProductSearch(search,idCategorie);
//        List<Product> products = productService.getAllProduct();
        List<Product> productsSearch = productService.getAllProductsPagging(search, (page - 1) * numberOfPage,numberOfPage);
        List<Category> categories = categoryService.getAllCategorys();

        req.setAttribute("search",search);
        req.setAttribute("products",productsSearch);
//        req.setAttribute("products",products);
        int noOfRecords = productService.getNoOfRecords();
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / numberOfPage);
        req.setAttribute("noOfPages", noOfPages);
        req.setAttribute("currentPage", page);
//        req.setAttribute("search",search);
        req.setAttribute("categories",categories);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/product.jsp");
        requestDispatcher.forward(req,resp);
    }

    private void showEditProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        List<Category> categories = categoryService.getAllCategorys();
        long idProduct = Long.parseLong(req.getParameter("id"));
        Product product = productService.findProductById(idProduct);
        if (product==null){
            resp.sendRedirect("/product?message=edit");
        }else {
            req.setAttribute("product", product);
            req.setAttribute("categories",categories);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/edit-product.jsp");
            requestDispatcher.forward(req,resp);
        }


    }

    private void showDeleteProducts(HttpServletRequest req, HttpServletResponse resp)throws ServletException,IOException {
        long id = Long.parseLong(req.getParameter("idDelete"));
        Product product = productService.findProductById(id);
        req.setAttribute("product",product);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/product.jsp");
        requestDispatcher.forward(req,resp);

    }

    private void showCreateProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        List<Category> categories = categoryService.getAllCategorys();
        req.setAttribute("categories",categories);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/create-product.jsp");
        requestDispatcher.forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null){
            action = "";
        }
        switch (action){
            case "create":
                createProducts(req,resp);
                break;
            case "delete":
                deletePRoduct(req,resp);
                break;
            case "edit":
                editProducts(req,resp);
                break;
            default:
                showProducts(req,resp);
        }

    }

    private void editProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException{
        List<String> errors = new ArrayList<>();

        Product product = new Product();
        isValidName(req,product,errors);
//        product.setName(req.getParameter("name"));
        isvalidCategories(req,product,errors);
        long idProduct = Long.parseLong(req.getParameter("id"));

        product.setId(idProduct);
        //(long id, String name, double price, String description, String image, Date createAt, int idCategory)
        product.setDescription(req.getParameter("description"));
        product.setPrice(Double.parseDouble(req.getParameter("price")));
        product.setImage(req.getParameter("image"));
        product.setIdCategory(Integer.parseInt(req.getParameter("categories")));
        Part part = isValidImage(req, product, errors);
        List<Category> categories = categoryService.getAllCategorys();
        req.setAttribute("categories",categories);

        if (errors.size()==0){
            Product productDB = productService.findProductById(product.getId());
            if ( product.getImage()!=null) {
                if(productDB.getImage() != null && !productDB.getImage().equals(product.getImage()) && part != null){
                    handleEditImageUploadAdvanced(part);
                }
            }
            req.setAttribute("message", "Them thanh cong");
            productService.editProduct(product);
            resp.sendRedirect("/product");

        }else {
            req.setAttribute("errors", errors);
            req.setAttribute("product", product);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/edit-product.jsp");
            requestDispatcher.forward(req, resp);
        }

    }

    private void  handleEditImageUploadAdvanced(Part part) throws IOException {
        String fileName = extractFileName(part);
        String appRealPath = getServletContext().getRealPath("/") + "images";
        File file = new File(appRealPath);
        if (!file.exists()) {
            file.mkdir();
        }
        String nameFileServer = appRealPath + File.separator + fileName;
        System.out.println("Name file server: " + nameFileServer);
        part.write(nameFileServer);
        String nameFileProject = "C:\\Users\\Gin\\OneDrive\\Máy tính\\CaseM3\\case3\\src\\main\\webapp\\images" + File.separator + fileName;
        System.out.println("Name file project: " + nameFileProject);
        part.write(nameFileProject);
    }

    private Part isValidImage(HttpServletRequest request, Product product,List<String> erros) throws ServletException,IOException{
        for (Part part : request.getParts()){
            String fileName = extractFileName(part);
            product.setImage(fileName);
            if (!fileName.equals("")){
                boolean check = productService.checkImageExists(fileName);
                if (check == true) {
                    return null;
                }else {
                    return part;
                }
            }
        }

        return null;
    }

    private void createProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> errors = new ArrayList<>();
        Product product = new Product();
        //(long id, String name, double price, String description, String image, Date createAt, int idCategory)
        isValidName(req, product, errors);
        isvalidCategories(req,product, errors);

        double price = Double.parseDouble(req.getParameter("price"));

        product.setPrice(price);
        String description = req.getParameter("description");
        product.setDescription(description);
        List<Category> categories = categoryService.getAllCategorys();
        req.setAttribute("categories",categories);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/create-product.jsp");
        if (errors.size()==0){
            System.out.println("Cập nhật thành công.");
            handleImageUpload(req,product,errors);
            req.setAttribute("message","Cập nhật thành công.");
            productService.createProduct(product);
            requestDispatcher.forward(req,resp);
        }else {
            req.setAttribute("errors", errors);
            req.setAttribute("product", product);
            requestDispatcher.forward(req, resp);
        }
    }

    private void isvalidCategories(HttpServletRequest req, Product product, List<String> errors)  {
        try
        {
            int id = Integer.parseInt(req.getParameter("categories"));
            if (categoryService.getCategoryById(id)!=null){
                product.setIdCategory(id);
            }else {
                errors.add("Loại sản phẩm không hợp lệ!");
            }
        }catch (NumberFormatException numberFormatException){
            errors.add("Định dạng loại sản phẩm không hợp lệ.");
        }


    }

    private void handleImageUpload(HttpServletRequest req, Product product, List<String> errors) throws ServletException, IOException {
        for (Part part : req.getParts()){
            String filename = extractFileName(part); // extractFileName xử lý link khi lớp part bắt được link hình
            if (!filename.equals("")){
                String appRealPath = getServletContext().getRealPath("/") + "images";
                File file = new File(appRealPath);
                if (!file.exists()){
                    file.mkdir();
                }
                String nameFileServer = appRealPath + File.separator + filename;
                System.out.println("Name file server: " + nameFileServer);
                part.write(nameFileServer);


                String nameFileProject = "C:\\Users\\Gin\\OneDrive\\Máy tính\\CaseM3\\case3\\src\\main\\webapp\\images" + File.separator + filename;
                System.out.println("Name file project: " + nameFileProject);
                part.write(nameFileProject);
                product.setImage(filename);
            }
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }

    private void isValidName(HttpServletRequest request, Product product, List<String> errors){
        String name = request.getParameter("name");
        if (!ValidateUtils.isNameValid(name)){
            errors.add("Tên không hợp lệ. Viết hoa chữ cái đầu tiên. Ví dụ: Nguyen Van A.");
        }
        product.setName(name);
    }

    private void deletePRoduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("idDelete"));
        productService.deleteProductById(id);
        resp.sendRedirect("/product?message=delete");
    }


    @Override
    public void init() throws ServletException {
        productService = new MSProductService();
        categoryService = new MSCategoryService();
    }
}