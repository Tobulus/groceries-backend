package remembrall.model.enums.quantity_unit;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter
public class QuantityUnitConverter implements AttributeConverter<QuantityUnit, String> {

    @Override
    public String convertToDatabaseColumn(QuantityUnit attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getCode();
    }

    @Override
    public QuantityUnit convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Arrays.stream(QuantityUnit.values()).filter(el -> el.getCode().equals(dbData)).findFirst()
                     .orElseThrow(IllegalAccessError::new);
    }
}
