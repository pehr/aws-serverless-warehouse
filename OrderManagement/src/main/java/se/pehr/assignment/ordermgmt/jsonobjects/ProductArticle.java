package se.pehr.assignment.ordermgmt.jsonobjects;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;





@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "art_id",
        "amount_of"
})
public class ProductArticle {

    @JsonProperty("art_id")
    private String artId;
    @JsonProperty("amount_of")
    private String amountOf;

    //Used for easier calculations, not to be exported to JSON
    @JsonIgnore
    private int amountInt;
    //Used for easier calculations, not to exported to JSON. Imported from inventory
    @JsonIgnore
    private int stock;
    //Calculated noOfStock /  amountInt = available. not to exported to JSON.
    @JsonIgnore
    private int available;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("art_id")
    public String getArtId() {
        return artId;
    }

    @JsonProperty("art_id")
    public void setArtId(String artId) {
        this.artId = artId;
    }

    @JsonProperty("amount_of")
    public String getAmountOf() {
        return amountOf;
    }

    @JsonProperty("amount_of")
    public void setAmountOf(String amountOf) {
        this.amountOf = amountOf;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public int getAmountInt() {
        return amountInt;
    }

    public void setAmountInt(int amountInt) {
        this.amountInt = amountInt;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    //Calculated from stock and amountInt values
    public int getAvailable() {
        return (stock /amountInt);
    }
}