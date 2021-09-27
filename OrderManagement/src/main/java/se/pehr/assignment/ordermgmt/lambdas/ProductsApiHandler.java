package se.pehr.assignment.ordermgmt.lambdas;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.pehr.assignment.ordermgmt.database.InventoryDao;
import se.pehr.assignment.ordermgmt.database.ProductsDao;
import se.pehr.assignment.ordermgmt.jsonobjects.*;
import se.pehr.assignment.ordermgmt.warehouse.InventoryManager;
import se.pehr.assignment.ordermgmt.warehouse.ProductsManager;

import java.io.IOException;
import java.util.Collections;


public class ProductsApiHandler {

    ObjectMapper objectMapper = new ObjectMapper();
    ProductsManager productsManager= new ProductsManager();
    InventoryManager inventoryManager = new InventoryManager();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        try {
            System.out.println(objectMapper.writeValueAsString(apiGatewayProxyRequestEvent));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();

        switch (apiGatewayProxyRequestEvent.getHttpMethod()){
            case "GET":
                Products products = getProducts();
                generateOKResponse(apiGatewayProxyResponseEvent, products);

                break;
            case "POST":
                try {
                    OrderRequest order = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), OrderRequest.class);
                    System.out.println(objectMapper.writeValueAsString(order));
                    String  is_order_ok = checkOrder(order);
                    switch (is_order_ok) {
                        case "OK":
                            System.out.println("OK - ");
                            updateInventory(order);
                            generateOKResponse(apiGatewayProxyResponseEvent, "Inventory updated");
                            break;
                        default:
                            System.out.println("Error - "  +  is_order_ok);
                            generateErrorResponse(apiGatewayProxyResponseEvent,"Bad request, part of order is not available "+is_order_ok, 400);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                generateErrorResponse(apiGatewayProxyResponseEvent,"HTTP Method "+apiGatewayProxyRequestEvent.getHttpMethod() + " not implemented", 501);
        }


        //generateResponse(apiGatewayProxyResponseEvent, responseMessage);
        return apiGatewayProxyResponseEvent;
    }


    private void generateOKResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, Object object ) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        try {
            apiGatewayProxyResponseEvent.setBody(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    private void generateOKResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String msg ) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);

        apiGatewayProxyResponseEvent.setBody(msg);

    }
    private void generateErrorResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String msg, int statuscode ) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(statuscode);

        apiGatewayProxyResponseEvent.setBody(msg);
    }


    protected Products getProducts(){
        System.out.println("GET PRODUCTS");
        ProductsManager productsManager= new ProductsManager();

        Products products = productsManager.getProducts(true);

        return products;
    }
    protected Product getProduct(String product_name){
        System.out.println("GET PRODUCT  - "  +product_name);

        Product product = productsManager.getProduct(product_name);
        try {
            System.out.println(objectMapper.writeValueAsString(product));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return product;
    }


    protected String checkOrder(OrderRequest order){
        try {
            System.out.println(objectMapper.writeValueAsString(order));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for (OrderProduct orderproduct :
                order.getOrderProducts()) {
            System.out.println("check orderproduct - " +orderproduct.getName());
            Product prod =  getProduct(orderproduct.getName());

            System.out.println("order: " +orderproduct.getQuantity() + " - avail:" + prod.getAvailableQuantity());

            //If request quantity of order is larger than available then return product name for error messsage.
            if(orderproduct.getQuantity()>prod.getAvailableQuantity()){
                return orderproduct.getName();
            }
        }

        return "OK";
    }
    protected String updateInventory(OrderRequest order){
        for (OrderProduct orderproduct :
                order.getOrderProducts()) {
            int quantity = orderproduct.getQuantity();
            Product prod =  getProduct(orderproduct.getName());

            for (ProductArticle prodArtticle :
                    prod.getContainArticles()) {
                int orderedQuanttity  = (prodArtticle.getAmountInt()*quantity*-1);
                System.out.println("Orderd qty: " + orderedQuanttity);
                inventoryManager.updateArticleStock(prodArtticle.getArtId(),orderedQuanttity);
            }

        }

        return "OK";
    }

}
