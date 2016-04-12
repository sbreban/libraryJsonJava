package library.network.json;

import com.google.gson.*;
import library.network.dto.BookQuantityDTO;

import java.lang.reflect.Type;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class BookQuantityDTODeserializer implements JsonDeserializer<BookQuantityDTO> {
  @Override
  public BookQuantityDTO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    int bookId = Integer.parseInt(jsonObject.get("bookId").getAsString());
    int newQuantity = Integer.parseInt(jsonObject.get("newQuantity").getAsString());

    return new BookQuantityDTO(bookId, newQuantity);
  }
}
