package se.pehr.assignment.ordermgmt.warehouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.pehr.assignment.ordermgmt.database.InventoryDao;
import se.pehr.assignment.ordermgmt.database.ProductsDao;
import se.pehr.assignment.ordermgmt.jsonobjects.*;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class ProductsManager {

    ObjectMapper objectMapper = new ObjectMapper();
    ProductsDao productsDao = new ProductsDao();

    /**
     * Parses  the inputstream to a Products object.
     * Sends the individual products to  be updated/created in the database.
     *
     * @param inputStream
     * @throws Exception
     */
    public void updateProducts(InputStream inputStream) throws Exception {
        Products products = null;

        products = objectMapper.readValue(inputStream, Products.class);

        for (Product product:
                products.getProducts()) {
            updateProductArticles(product);
        }
    }

    /**
     * Adds the  product and all its articles to the database.
     *
     * TODO if an article in the product changes. Is removed and/or changes article_id then the old article is not removed at the moment. Fix later so they are removed.
     * @param product
     */
    public void updateProductArticles(Product product){
        for (ProductArticle article :
                product.getContainArticles()) {
            productsDao.updateProduct(product.getName(), article.getArtId(), Integer.parseInt(article.getAmountOf()));

        }
        //For  quick access to unique product names, create a row with "PRODUCT" as product id and the  name as article Id.
        //This can be solved more neatly with secondary indexes, but to save time this will have to do.
        productsDao.updateProduct("PRODUCT", product.getName(), -1);
    }

    public Products getProducts(boolean onlyAvailable){
        ProductsDao dao = new ProductsDao();
        Products products = dao.getUniqueProducts();

        Iterator<Product> product = products.getProducts().iterator();
        while (product.hasNext()) {
            Product prod = product.next(); // must be called before you can call i.remove()

            prod.setContainArticles(dao.getProductArticles(prod.getName()));
            getProductArticlesInventory(prod.getContainArticles());

            getAvailableProductCount(prod);
            if(prod.getAvailableQuantity()==0 && onlyAvailable){
                product.remove();
            }


        }

        return products;
    }

    public Product getProduct(String product_name){
        ProductsDao dao = new ProductsDao();

        Product product = new Product();
        product.setContainArticles(dao.getProductArticles(product_name));
        getProductArticlesInventory(product.getContainArticles());

        getAvailableProductCount(product);

        return product;
    }

    InventoryManager inventoryManager  = new InventoryManager();
    protected void getProductArticlesInventory(List<ProductArticle> productArticles){
        for (ProductArticle productArticle :
                productArticles) {
            Article article = inventoryManager.getArticle(productArticle.getArtId());
            productArticle.setStock(article.getStock());
        }

    }

    protected void getAvailableProductCount(Product product){
        //Init leastavailable with an arbitrary high value.
        int leastAvailable= 1000000;
        for (ProductArticle productArticle :
                product.getContainArticles()) {
            System.out.println(productArticle.getArtId());
            System.out.println(productArticle.getAvailable());
            int tempAvailable = productArticle.getAvailable();
                if(tempAvailable<leastAvailable){
                    leastAvailable=tempAvailable;
                }
        }
        product.setAvailableQuantity(leastAvailable);
    }

}
