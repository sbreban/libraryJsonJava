package library.network.json;

import com.google.gson.*;
import library.network.dto.UserBookDTO;
import library.network.dto.UserDTO;
import library.network.rpcprotocol.Request;
import library.network.rpcprotocol.RequestType;

import java.lang.reflect.Type;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class RequestDeserializer implements JsonDeserializer<Request> {
  @Override
  public Request deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    Request request = null;

    JsonObject jsonObject = jsonElement.getAsJsonObject();

    String requestType = jsonObject.get("type").getAsString();

    if (requestType.equals(RequestType.LOGIN.name())) {
      UserDTO userDTO = jsonDeserializationContext.deserialize(jsonObject.get("data"), UserDTO.class);
      request = new Request.Builder().type(RequestType.LOGIN).data(userDTO).build();
    } else if (requestType.equals(RequestType.LOGOUT.name())) {
      int userId = Integer.parseInt(jsonObject.get("data").getAsString());
      request = new Request.Builder().type(RequestType.LOGOUT).data(userId).build();
    } else if (requestType.equals(RequestType.GET_AVAILABLE_BOOKS.name())) {
      request = new Request.Builder().type(RequestType.GET_AVAILABLE_BOOKS).build();
    } else if (requestType.equals(RequestType.GET_USER_BOOKS.name())) {
      int userId = Integer.parseInt(jsonObject.get("data").getAsString());
      request = new Request.Builder().type(RequestType.GET_USER_BOOKS).data(userId).build();
    } else if (requestType.equals(RequestType.SEARCH_BOOKS.name())) {
      String searchKey = jsonObject.get("data").getAsString();
      request = new Request.Builder().type(RequestType.SEARCH_BOOKS).data(searchKey).build();
    } else if (requestType.equals(RequestType.BORROW_BOOK.name())) {
      UserBookDTO userBookDTO = jsonDeserializationContext.deserialize(jsonObject.get("data"), UserBookDTO.class);
      request = new Request.Builder().type(RequestType.BORROW_BOOK).data(userBookDTO).build();
    } else if (requestType.equals(RequestType.RETURN_BOOK.name())) {
      UserBookDTO userBookDTO = jsonDeserializationContext.deserialize(jsonObject.get("data"), UserBookDTO.class);
      request = new Request.Builder().type(RequestType.RETURN_BOOK).data(userBookDTO).build();
    }

    return request;
  }
}
