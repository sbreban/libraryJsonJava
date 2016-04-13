package library.network.json;

import com.google.gson.*;
import library.model.Book;
import library.model.User;
import library.network.dto.BookReturnedDTO;
import library.network.dto.BookBorrowedDTO;
import library.network.rpcprotocol.Response;
import library.network.rpcprotocol.ResponseType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergiu on 11.04.2016.
 */
public class ResponseDeserializer implements JsonDeserializer<Response> {
  @Override
  public Response deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    Response response = null;

    JsonObject jsonObject = jsonElement.getAsJsonObject();

    String responseType = jsonObject.get("type").getAsString();

    if (responseType.equals(ResponseType.LOGIN_SUCCESSFULLY.name())) {
      User user = jsonDeserializationContext.deserialize(jsonObject.get("data"), User.class);
      response = new Response.Builder().type(ResponseType.LOGIN_SUCCESSFULLY).data(user).build();
    } else if (responseType.equals(ResponseType.LOGOUT_SUCCESSFULLY.name())) {
      response = new Response.Builder().type(ResponseType.LOGOUT_SUCCESSFULLY).build();
    } else if (responseType.equals(ResponseType.GET_AVAILABLE_BOOKS.name())) {
      JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
      List<Book> availableBooks = new ArrayList<>();
      for (int i = 0; i < jsonArray.size(); i++) {
        availableBooks.add(jsonDeserializationContext.deserialize(jsonArray.get(i), Book.class));
      }
      response = new Response.Builder().type(ResponseType.GET_AVAILABLE_BOOKS).data(availableBooks).build();
    } else if (responseType.equals(ResponseType.GET_USER_BOOKS.name())) {
      JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
      List<Book> userBooks = new ArrayList<>();
      for (int i = 0; i < jsonArray.size(); i++) {
        userBooks.add(jsonDeserializationContext.deserialize(jsonArray.get(i), Book.class));
      }
      response = new Response.Builder().type(ResponseType.GET_USER_BOOKS).data(userBooks).build();
    } else if (responseType.equals(ResponseType.SEARCH_BOOKS.name())) {
      JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
      List<Book> foundBooks = new ArrayList<>();
      for (int i = 0; i < jsonArray.size(); i++) {
        foundBooks.add(jsonDeserializationContext.deserialize(jsonArray.get(i), Book.class));
      }
      response = new Response.Builder().type(ResponseType.SEARCH_BOOKS).data(foundBooks).build();
    } else if (responseType.equals(ResponseType.OK.name())) {
      response = new Response.Builder().type(ResponseType.OK).build();
    } else if (responseType.equals(ResponseType.ERROR.name())) {
      response = new Response.Builder().type(ResponseType.ERROR).build();
    } else if (responseType.equals(ResponseType.RETURN_BOOK.name())) {
      BookReturnedDTO bookReturnedDTO = jsonDeserializationContext.deserialize(jsonObject.get("data"), BookReturnedDTO.class);
      response = new Response.Builder().type(ResponseType.RETURN_BOOK).data(bookReturnedDTO).build();
    } else if (responseType.equals(ResponseType.BORROW_BOOK.name())) {
      BookBorrowedDTO bookBorrowedDTO = jsonDeserializationContext.deserialize(jsonObject.get("data"), BookBorrowedDTO.class);
      response = new Response.Builder().type(ResponseType.BORROW_BOOK).data(bookBorrowedDTO).build();
    }

    return response;
  }
}
