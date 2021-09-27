package se.pehr.assignment.ordermgmt.jsonobjects;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "name",
            "contain_articles"
    })
    public class Product {

        @JsonProperty("name")
        private String name;

        @JsonProperty("available_quantity")
        private int availableQuantity;

        @JsonProperty("contain_articles")
        private List<ProductArticle> containArticles = null;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }
        @JsonProperty("available_quantity")
        public int getAvailableQuantity() {
            return availableQuantity;
        }
        @JsonProperty("available_quantity")
        public void setAvailableQuantity(int availableQuantity) {
            this.availableQuantity = availableQuantity;
        }

        @JsonProperty("contain_articles")
        public List<ProductArticle> getContainArticles() {
            return containArticles;
        }

        @JsonProperty("contain_articles")
        public void setContainArticles(List<ProductArticle> containArticles) {
            this.containArticles = containArticles;
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