package stefanowicz.kacper.help;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stefanowicz.kacper.model.Customer;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerWithMoney {
    private Customer customer;
    private BigDecimal money;
}
