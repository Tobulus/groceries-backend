package remembrall.model.enums;

import java.util.Arrays;
import java.util.Objects;

public enum QuantityUnit {
    UNDEFINED(null), PIECE("P"), LITER("L"), MILLILITER("ML"), GRAM("G"), KILOGRAM("KG");

    private final String code;

    QuantityUnit(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static QuantityUnit from(String code) {
        return Arrays.stream(values()).filter(quantityUnit -> Objects.equals(quantityUnit.code, code)).findFirst()
                     .orElse(UNDEFINED);
    }
}
