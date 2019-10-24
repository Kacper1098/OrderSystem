package stefanowicz.kacper.converter.impl;

import stefanowicz.kacper.converter.JsonConverter;
import stefanowicz.kacper.help.CategoryWithProducts;

import java.util.List;

public class CategoriesWithProductsJsonConverter extends JsonConverter<List<CategoryWithProducts>> {
    public CategoriesWithProductsJsonConverter(String jsonFileName) {
        super(jsonFileName);
    }
}
