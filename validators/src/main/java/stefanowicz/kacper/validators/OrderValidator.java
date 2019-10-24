package stefanowicz.kacper.validators;

import stefanowicz.kacper.validators.generic.AbstractValidator;
import stefanowicz.kacper.model.Order;

import java.time.LocalDate;
import java.util.Map;

public class OrderValidator extends AbstractValidator<Order> {
    @Override
    public Map<String, String> validate(Order order) {

        errors.clear();

        if(order == null){
            errors.put("orderObject", "Order object is not valid, it cannot be null.");
            return errors;
        }

        var customerErrors = getCustomerErrors(order);
        if(!customerErrors.isEmpty()){
            errors.putAll(customerErrors);
        }

        var productErrors = getProductErrors(order);
        if(!productErrors.isEmpty()){
            errors.putAll(productErrors);
        }

        if(!isOrderQuantityValid(order)){
            errors.put("orderQuantity", "Order quantity is not valid, it has to be greater than 0.");
        }

        if(!isOrderDateValid(order)){
            errors.put("orderDate", "Order date is not valid, it has to be today's date or from the future.");
        }

        return errors;
    }

    private Map<String, String> getCustomerErrors(Order order){
        var customerValidator = new CustomerValidator();
        return customerValidator.validate(order.getCustomer());
    }

    private Map<String, String> getProductErrors(Order order){
        var productValidator = new ProductValidator();
        return productValidator.validate(order.getProduct());
    }

    private boolean isOrderQuantityValid(Order order){
        return order.getQuantity() > 0;
    }

    private boolean isOrderDateValid(Order order){
        return order.getOrderDate() != null &&
                (order.getOrderDate().isEqual(LocalDate.now()) || order.getOrderDate().isAfter(LocalDate.now()));
    }
}
