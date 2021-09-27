package se.pehr.assignment.ordermgmt.lambdas;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.pehr.assignment.ordermgmt.database.InventoryDao;
import se.pehr.assignment.ordermgmt.database.ProductsDao;
import se.pehr.assignment.ordermgmt.jsonobjects.Product;
import se.pehr.assignment.ordermgmt.jsonobjects.Products;
import se.pehr.assignment.ordermgmt.warehouse.ProductsManager;

import java.util.Collections;


public class ProductsApiHandler {

    ObjectMapper objectMapper = new ObjectMapper();
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();


        try {
            System.out.println(objectMapper.writeValueAsString(apiGatewayProxyRequestEvent));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        switch (apiGatewayProxyRequestEvent.getHttpMethod()){
            case "GET":
                Products products = getProducts();
                generateOKResponse(apiGatewayProxyResponseEvent, products);

                break;
            default:
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


    protected Products getProducts(){
        System.out.println("GET PRODUCTS");
        ProductsManager productsManager= new ProductsManager();

        Products products = productsManager.getProducts();
        try {
            System.out.println(objectMapper.writeValueAsString(products));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return products;
    }
}
