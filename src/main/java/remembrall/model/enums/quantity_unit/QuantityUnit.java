package remembrall.model.enums.quantity_unit;

import java.util.Arrays;

public enum QuantityUnit {
    UNDEFINED("UD"), PIECE("P"), LITER("L"), MILLILITER("ML"), GRAM("G"), KILOGRAM("KG");

    private final String code;

    QuantityUnit(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static QuantityUnit from(String code) {
        return Arrays.stream(values()).filter(quantityUnit -> quantityUnit.code.equals(code)).findFirst()
                     .orElseThrow(IllegalArgumentException::new);
    }
}
