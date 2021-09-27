package se.pehr.assignment.ordermgmt.warehouse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.pehr.assignment.ordermgmt.database.InventoryDao;
import se.pehr.assignment.ordermgmt.jsonobjects.Article;
import se.pehr.assignment.ordermgmt.jsonobjects.Inventory;

import java.io.IOException;
import java.io.InputStream;

public class InventoryManager {

    ObjectMapper objectMapper = new ObjectMapper();
    InventoryDao inventoryDao = new InventoryDao();
    public void updateInventory(InputStream inputStream) throws Exception {
        Inventory inventory = null;

        inventory = objectMapper.readValue(inputStream, Inventory.class);

        for (Article article:
                inventory.getInventory()) {
            inventoryDao.updateArticle(article);
        }
    }

    /**
     * returns the article with requested id from the database.
     * @param artId
     * @return
     */
    public Article getArticle(String artId){
        return inventoryDao.getArticle(artId);
    }

    /**
     * Sends a request to update the article with the requested quantity.</br>
     * @param artId
     * @param changedStockQuantity  can be both a positive and negative value.  If negative the quantity is removed from the stock.
     */
    public void updateArticleStock(String artId, int changedStockQuantity){
        inventoryDao.updateArticleStock(artId,changedStockQuantity);
    }
}
