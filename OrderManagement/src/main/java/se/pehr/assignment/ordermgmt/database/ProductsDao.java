package se.pehr.assignment.ordermgmt.database;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import se.pehr.assignment.ordermgmt.jsonobjects.Article;
import se.pehr.assignment.ordermgmt.jsonobjects.Product;
import se.pehr.assignment.ordermgmt.jsonobjects.ProductArticle;
import se.pehr.assignment.ordermgmt.jsonobjects.Products;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProductsDao {

	private static final String TABLE_NAME =System.getenv("TABLE_PRODUCT_NAME");


	public ProductsDao() {

	}

    //static String tableName = "devHpen";

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion(Regions.EU_NORTH_1)
			.withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
			.build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	Table table = dynamoDB.getTable(TABLE_NAME);

    public void updateProduct(String productName, String art_id, int amount) {
		System.out.println(TABLE_NAME);


		NameMap expressionAttributeNames = new NameMap();
		expressionAttributeNames.put("#AMOUNT", "amount_of");
		//expressionAttributeNames.put("#UPD", "updated");

		ValueMap expressionAttributeValues = new ValueMap()
				.withInt(":valAMT", amount);


		try {
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec()
					.withPrimaryKey(new PrimaryKey("product",productName,"art_id",art_id))
					.withUpdateExpression(" SET #AMOUNT = :valAMT"
							)
					.withNameMap(expressionAttributeNames)
					.withValueMap(expressionAttributeValues)
					.withReturnValues(ReturnValue.UPDATED_NEW);

			UpdateItemOutcome outcome2 =  table.updateItem(updateItemSpec2);
		}
	    catch (Exception e) {
	    	e.printStackTrace();
			throw  new RuntimeException("Product Update failed: "+e.getMessage(), e);

	    }
    }


	public Products getUniqueProducts(){
		Products products = new Products();
		products.setProducts(new ArrayList<>());
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("product = :v_prod")
				.withValueMap(new ValueMap()
						.withString(":v_prod", "PRODUCT"));

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		Item item = null;
		while (iterator.hasNext()) {
			Product product = new Product();
			item = iterator.next();
			System.out.println(item.toJSONPretty());
			product.setName(item.getString("art_id"));

			products.getProducts().add(product);
		}
		return products;
	}

	public List<ProductArticle> getProductArticles(String product_name){
		System.out.println("DB prod name - " + product_name);
		List<ProductArticle> productArticles = new ArrayList<>();
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("product = :v_prod")
				.withValueMap(new ValueMap()
						.withString(":v_prod", product_name));

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		Item item = null;
		while (iterator.hasNext()) {
			ProductArticle productArticle = new ProductArticle();
			item = iterator.next();
			System.out.println(item.toJSONPretty());
			productArticle.setArtId(item.getString("art_id"));
			productArticle.setAmountOf(item.getString("amount_of"));
			productArticle.setAmountInt(Integer.parseInt(item.getString("amount_of")));
			productArticles.add(productArticle);
		}
		return productArticles;
	}
}