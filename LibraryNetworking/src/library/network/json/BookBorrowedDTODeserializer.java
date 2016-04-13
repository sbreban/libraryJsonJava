package library.network.json;

import com.google.gson.*;
import library.network.dto.BookBorrowedDTO;

import java.lang.reflect.Type;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class BookBorrowedDTODeserializer implements JsonDeserializer<BookBorrowedDTO> {
  @Override
  public BookBorrowedDTO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    int bookId = Integer.parseInt(jsonObject.get("bookId").getAsString());
    int newQuantity = Integer.parseInt(jsonObject.get("newQuantity").getAsString());
    boolean byThisUser = jsonObject.get("byThisUser").getAsBoolean();

    return new BookBorrowedDTO(bookId, newQuantity, byThisUser);
  }
}
