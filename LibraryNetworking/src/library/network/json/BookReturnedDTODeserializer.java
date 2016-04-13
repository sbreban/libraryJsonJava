package library.network.json;

import com.google.gson.*;
import library.network.dto.BookReturnedDTO;

import java.lang.reflect.Type;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class BookReturnedDTODeserializer implements JsonDeserializer<BookReturnedDTO> {
  @Override
  public BookReturnedDTO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    int bookId = Integer.parseInt(jsonObject.get("id").getAsString());
    String author = jsonObject.get("author").getAsString();
    String title = jsonObject.get("title").getAsString();
    boolean byThisUser = jsonObject.get("byThisUser").getAsBoolean();

    return new BookReturnedDTO(bookId, author, title, byThisUser);
  }
}
