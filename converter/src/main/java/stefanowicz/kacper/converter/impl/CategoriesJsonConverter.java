package stefanowicz.kacper.converter.impl;

import stefanowicz.kacper.converter.JsonConverter;
import stefanowicz.kacper.model.enums.Category;

import java.util.List;

public class CategoriesJsonConverter extends JsonConverter<List<Category>> {
    public CategoriesJsonConverter(String jsonFileName) {
        super(jsonFileName);
    }
}
