package remembrall.model.enums.quantity_unit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import remembrall.config.i18n.I18n;

import java.io.IOException;

public class QuantityUnitSerializer extends JsonSerializer<QuantityUnit> {

    @Autowired
    private I18n i18n;

    public QuantityUnitSerializer() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public void serialize(QuantityUnit value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(i18n.getMessage("QuantityUnit." + value.getCode()));
    }
}
