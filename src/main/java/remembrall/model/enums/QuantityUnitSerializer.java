package remembrall.model.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;

public class QuantityUnitSerializer extends JsonSerializer<QuantityUnit> {

    public QuantityUnitSerializer() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public void serialize(QuantityUnit value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getCode() == null ? "" : value.getCode());
    }
}
