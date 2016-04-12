package library.network.json;

import com.google.gson.*;
import library.network.dto.BookDTO;

import java.lang.reflect.Type;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class BookDTODeserializer implements JsonDeserializer<BookDTO> {
  @Override
  public BookDTO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    int bookId = Integer.parseInt(jsonObject.get("id").getAsString());
    String author = jsonObject.get("author").getAsString();
    String title = jsonObject.get("title").getAsString();

    return new BookDTO(bookId, author, title);
  }
}
