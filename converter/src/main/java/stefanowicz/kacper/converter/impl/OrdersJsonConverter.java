package stefanowicz.kacper.converter.impl;

import stefanowicz.kacper.converter.JsonConverter;
import stefanowicz.kacper.model.Order;

import java.util.List;

public class OrdersJsonConverter extends JsonConverter<List<Order>> {
    public OrdersJsonConverter(String jsonFileName) {
        super(jsonFileName);
    }
}
