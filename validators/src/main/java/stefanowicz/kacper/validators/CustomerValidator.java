package stefanowicz.kacper.validators;

import stefanowicz.kacper.validators.generic.AbstractValidator;
import stefanowicz.kacper.model.Customer;

import javax.mail.internet.InternetAddress;
import java.util.Map;

public class CustomerValidator extends AbstractValidator<Customer> {
    @Override
    public Map<String, String> validate(Customer customer){
        errors.clear();

        if(customer == null){
            errors.put("customerObject", "Customer object is not valid, it cannot be null,.");
            return errors;
        }

        if(!isCustomerNameValid(customer)){
            errors.put("customerName", "Customer name is not valid, it has to consists of  letters and whitespaces only.");
        }

        if(!isCustomerSurnameValid(customer)){
            errors.put("customerSurname", "Customer surname is not valid, it has to consists of  letters and whitespaces only.");
        }

        if(!isCustomerAgeValid(customer)){
            errors.put("customerAge", "Customer age is not valid, it has to be greater than or equal to 18.");
        }

       try{
           InternetAddress emailAdress = new InternetAddress(customer.getEmail());
           emailAdress.validate();
       }
       catch ( Exception e){
            errors.put("customerEmail", "Customer email is not valid");
       }
        return errors;
    }

    private boolean isCustomerNameValid(Customer customer){
        return customer.getName() != null && customer.getName().matches("([A-Z]+\\s)?[A-Za-z]+");
    }

    private boolean isCustomerSurnameValid(Customer customer){
        return customer.getSurname() != null && customer.getSurname().matches("([A-Z]+\\s)?[A-Za-z]+");
    }

    private boolean isCustomerAgeValid(Customer customer){
        return customer.getAge() >= 18;
    }

}
