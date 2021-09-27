package se.pehr.assignment.ordermgmt.database;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.FluentHashMap;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.json.Jackson;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import se.pehr.assignment.ordermgmt.jsonobjects.Article;
import se.pehr.assignment.ordermgmt.jsonobjects.Product;
import se.pehr.assignment.ordermgmt.jsonobjects.Products;

import java.util.ArrayList;
import java.util.Iterator;


public class InventoryDao {

	private static final String TABLE_NAME =System.getenv("TABLE_ARTICLE_NAME");


	public InventoryDao() {

	}

    //static String tableName = "devHpen";

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion(Regions.EU_NORTH_1)
			.withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
			.build();
	static DynamoDB dynamoDB = new DynamoDB(client);
	Table table = dynamoDB.getTable(TABLE_NAME);

    public void updateArticle(Article article) {


		NameMap expressionAttributeNames = new NameMap();
		//expressionAttributeNames.put("#ARTID", "art_id");
		expressionAttributeNames.put("#NAME", "name");
		expressionAttributeNames.put("#STOCK", "stock");
		//expressionAttributeNames.put("#UPD", "updated");


//		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		ValueMap expressionAttributeValues = new ValueMap()
				.withString(":valName", article.getName())
				.withInt(":valStock", article.getStock());
		try {
				UpdateItemSpec updateItemSpec2 = new UpdateItemSpec()
				        .withPrimaryKey(new PrimaryKey("art_id",article.getArtId()))
				        .withUpdateExpression(" SET #NAME = :valName, #STOCK = :valStock "
								)
				        .withNameMap(expressionAttributeNames)
				        .withValueMap(expressionAttributeValues)
				        .withReturnValues(ReturnValue.UPDATED_NEW);
			
				UpdateItemOutcome outcome2 =  table.updateItem(updateItemSpec2);

	    }catch (Exception e) {
	    	e.printStackTrace();
			throw new RuntimeException("Database update failed -"+e.getMessage(), e);
	    }
    }


	public Article getArticle(String artId){
		Article article = new Article();

		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("art_id = :v_art")
				.withValueMap(new ValueMap()
						.withString(":v_art", artId));

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		Item item = null;
		while (iterator.hasNext()) {
			Product product = new Product();
			item = iterator.next();
			System.out.println(item.toJSONPretty());
			article.setArtId(item.getString("art_id"));
			article.setName(item.getString("name"));
			article.setStock(item.getInt("stock"));

			System.out.println(item.getString("name"));
		}
		return article;
	}
}