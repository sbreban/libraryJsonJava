package library.network.json;

import com.google.gson.*;
import library.network.dto.UserBookDTO;

import java.lang.reflect.Type;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class UserBookDTODeserializer implements JsonDeserializer<UserBookDTO> {
  @Override
  public UserBookDTO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    int userId = Integer.parseInt(jsonObject.get("userId").getAsString());
    int bookId = Integer.parseInt(jsonObject.get("bookId").getAsString());

    return new UserBookDTO(userId, bookId);
  }
}
