package library.network.json;

import com.google.gson.*;
import library.model.Book;

import java.lang.reflect.Type;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class BookDeserializer implements JsonDeserializer<Book> {
  @Override
  public Book deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    int bookId = Integer.parseInt(jsonObject.get("id").getAsString());
    String author = jsonObject.get("author").getAsString();
    String title = jsonObject.get("title").getAsString();
    int available = Integer.parseInt(jsonObject.get("available").getAsString());

    return new Book(bookId, author, title, available);
  }
}
