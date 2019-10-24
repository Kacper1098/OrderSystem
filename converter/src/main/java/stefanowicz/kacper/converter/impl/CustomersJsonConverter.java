package stefanowicz.kacper.converter.impl;

import stefanowicz.kacper.converter.JsonConverter;
import stefanowicz.kacper.model.Customer;

import java.util.Set;

public class CustomersJsonConverter extends JsonConverter<Set<Customer>> {
    public CustomersJsonConverter(String jsonFileName) {
        super(jsonFileName);
    }
}
