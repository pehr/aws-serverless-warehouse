package se.pehr.assignment.ordermgmt.lambdas;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.pehr.assignment.ordermgmt.database.InventoryDao;
import se.pehr.assignment.ordermgmt.jsonobjects.Article;
import se.pehr.assignment.ordermgmt.jsonobjects.Inventory;
import se.pehr.assignment.ordermgmt.jsonobjects.Products;
import se.pehr.assignment.ordermgmt.warehouse.InventoryManager;
import se.pehr.assignment.ordermgmt.warehouse.ProductsManager;


public class FileUpdateHandler implements RequestHandler<S3Event, String>{

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();


    private static final String TABLE_NAME =System.getenv("TABLE_NAME");


    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_NORTH_1).build();
    static DynamoDB dynamoDB = new DynamoDB(client);



    // Test purpose only.
    FileUpdateHandler(AmazonS3 s3) {
        this.s3 = s3;
    }

    public FileUpdateHandler() {}

    @Override
    public String handleRequest(S3Event event, Context context) {
        LambdaLogger logger = context.getLogger();
        String awsReqId = context.getAwsRequestId();

        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
            // Get the object from the event
            InputStream is= readS3File(bucket,key);

            if(key.contains("inventory")){
                InventoryManager  inventoryManager = new InventoryManager();
                inventoryManager.updateInventory(is);
            }else if (key.contains("product")){
                ProductsManager productsManager = new ProductsManager();
                productsManager.updateProducts(is);
            }else{
                throw new RuntimeException("Update file does not appear to be an Inventory or a Products file: "+key +". From bucket:"+bucket);
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR reading update file - "+key +". From bucket:"+bucket, e);

        }
        return "ok";
    }

    /**
     * Gets the object from S3 and returns an Inputstream of it
     *
      * @param bucket
     * @param key
     * @return
     */
    protected InputStream readS3File(String bucket, String key){
        System.out.println("bucket: " + bucket);
        System.out.println("key: " + key);
        S3Object obj = s3.getObject(
                new GetObjectRequest(bucket , key));

        InputStream objectData = obj.getObjectContent();
        return objectData;
    }



}
