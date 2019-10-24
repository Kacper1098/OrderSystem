package stefanowicz.kacper.help;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stefanowicz.kacper.model.enums.Category;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryWithProducts {
    private Category category;
    private List<String> products;
}
