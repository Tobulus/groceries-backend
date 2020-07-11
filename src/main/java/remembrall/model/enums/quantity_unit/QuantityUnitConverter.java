package remembrall.model.enums.quantity_unit;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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

        return QuantityUnit.from(dbData);
    }
}
