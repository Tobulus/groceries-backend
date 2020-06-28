package remembrall.model.enums.quantity_unit;

public enum QuantityUnit {
    PIECE("P"), LITER("L"), MILLILITER("ML"), GRAM("G"), KILOGRAM("KG");

    private String code;

    QuantityUnit(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
