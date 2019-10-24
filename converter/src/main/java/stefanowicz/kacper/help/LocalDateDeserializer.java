package stefanowicz.kacper.help;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import stefanowicz.kacper.exception.AppException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try{
            return LocalDate.parse(json.getAsString(), formatter);
        }
        catch (Exception e){
            throw new AppException("LocalDateDeserializer exception - " + e.getMessage());
        }
    }
}
