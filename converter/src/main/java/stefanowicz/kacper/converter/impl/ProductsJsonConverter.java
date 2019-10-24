package stefanowicz.kacper.converter.impl;

import stefanowicz.kacper.converter.JsonConverter;
import stefanowicz.kacper.model.Product;

import java.util.Set;

public class ProductsJsonConverter extends JsonConverter<Set<Product>> {
    public ProductsJsonConverter(String jsonFileName) {
        super(jsonFileName);
    }
}
