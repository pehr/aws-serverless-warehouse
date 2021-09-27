package se.pehr.assignment.ordermgmt.jsonobjects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "order_id",
        "product_cart"
})
public class OrderRequest {

    @JsonProperty("order_id")
    private String order_id;
    @JsonProperty("product_cart")
    private List<OrderProduct> orderProducts = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("order_id")
    public String getOrder_id() {
        return order_id;
    }
    @JsonProperty("order_id")
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
    @JsonProperty("product_cart")
    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }
    @JsonProperty("product_cart")
    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}