package se.pehr.assignment.ordermgmt.jsonobjects;
    import static org.junit.Assert.assertEquals;
    import static org.junit.Assert.assertNotNull;
    import static org.junit.Assert.assertTrue;
    import org.junit.Test;
public class ProductArticleTest {

    @Test
    public void correctCalculationOfAvailableInventory() {
        /**App app = new App();
         APIGatewayProxyResponseEvent result = app.handleRequest(null, null);
         assertEquals(result.getStatusCode().intValue(), 200);
         assertEquals(result.getHeaders().get("Content-Type"), "application/json");
         String content = result.getBody();
         assertNotNull(content);
         assertTrue(content.contains("\"message\""));
         assertTrue(content.contains("\"hello world\""));
         assertTrue(content.contains("\"location\""));
         **/

        ProductArticle productArticle = new ProductArticle();
        productArticle.setStock(18);
        productArticle.setAmountInt(4);
        // Available is calculated from (Inventory / AmountInt)

        assertTrue(productArticle.getAvailable()==4);

        productArticle.setStock(20);
        assertTrue(productArticle.getAvailable()==5);

        productArticle.setStock(3);
        System.out.println(productArticle.getAvailable());
        assertTrue(productArticle.getAvailable()==0);
    }
}
