package library.network.rpcprotocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import library.model.Book;
import library.model.User;
import library.network.dto.BookReturnedDTO;
import library.network.dto.BookBorrowedDTO;
import library.network.dto.UserBookDTO;
import library.network.dto.UserDTO;
import library.network.json.RequestDeserializer;
import library.services.ILibraryClient;
import library.services.ILibraryServer;
import library.services.LibraryException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by grigo on 12/15/15.
 */
public class LibraryClientRpcWorker implements Runnable, ILibraryClient {
  private ILibraryServer server;
  private Socket connection;

  private BufferedReader input;
  private PrintWriter output;
  private volatile boolean connected;

  public LibraryClientRpcWorker(ILibraryServer server, Socket connection) {
    this.server = server;
    this.connection = connection;
    try{
      output = new PrintWriter(connection.getOutputStream(), true);
      output.flush();
      input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      connected = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    while(connected){
      try {
        String requestJson = input.readLine();
        if (requestJson != null) {
          GsonBuilder gsonBuilder = new GsonBuilder();
          gsonBuilder.registerTypeAdapter(Request.class, new RequestDeserializer());
          Gson gson = gsonBuilder.create();
          Request request = gson.fromJson(requestJson, Request.class);
          System.out.println("Request received " + request);
          Response response = handleRequest(request);
          if (response != null) {
            sendResponse(response);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    try {
      input.close();
      output.close();
      connection.close();
    } catch (IOException e) {
      System.out.println("Error "+e);
    }
  }

  @Override
  public void bookBorrowed(int bookId, int newQuantity, boolean byThisUser) throws LibraryException {
    BookBorrowedDTO bookBorrowedDTO = new BookBorrowedDTO(bookId, newQuantity, byThisUser);
    Response response = new Response.Builder().type(ResponseType.BORROW_BOOK).data(bookBorrowedDTO).build();
    try {
      sendResponse(response);
    } catch (IOException e) {
      throw new LibraryException("Sending error: "+e);
    }
  }

  @Override
  public void bookReturned(int bookId, String author, String title, boolean byThisUser) throws LibraryException {
    BookReturnedDTO bookReturnedDTO = new BookReturnedDTO(bookId, author, title, byThisUser);
    Response response = new Response.Builder().type(ResponseType.RETURN_BOOK).data(bookReturnedDTO).build();
    try {
      sendResponse(response);
    } catch (IOException e) {
      throw new LibraryException("Sending error: "+e);
    }
  }

  private Response handleRequest(Request request){
    Response response = null;
    if (request.type() == RequestType.LOGIN){
      System.out.println("Login request ...");
      UserDTO userDTO=(UserDTO)request.data();
      try {
        User user = server.login(userDTO.getUserName(), userDTO.getPassword(), this);
        return new Response.Builder().type(ResponseType.LOGIN_SUCCESSFULLY).data(user).build();
      } catch (LibraryException e) {
        connected = false;
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.LOGOUT){
      System.out.println("Logout request");
      int userId=(int)request.data();
      try {
        server.logout(userId, this);
        connected = false;
        return new Response.Builder().type(ResponseType.LOGOUT_SUCCESSFULLY).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.GET_AVAILABLE_BOOKS) {
      try {
        List<Book> allBooks = server.getAvailableBooks();
        return new Response.Builder().type(ResponseType.GET_AVAILABLE_BOOKS).data(allBooks).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.GET_USER_BOOKS) {
      try {
        int userId = (int)request.data();
        List<Book> userBooks = server.getUserBooks(userId);
        return new Response.Builder().type(ResponseType.GET_USER_BOOKS).data(userBooks).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.SEARCH_BOOKS) {
      try {
        String key = (String)request.data();
        List<Book> foundBooks = server.searchBooks(key);
        return new Response.Builder().type(ResponseType.SEARCH_BOOKS).data(foundBooks).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.BORROW_BOOK) {
      try {
        UserBookDTO userBookDTO = (UserBookDTO)request.data();
        server.borrowBook(userBookDTO.getUserId(), userBookDTO.getBookId());
        return new Response.Builder().type(ResponseType.OK).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.RETURN_BOOK) {
      try {
        UserBookDTO userBookDTO = (UserBookDTO)request.data();
        server.returnBook(userBookDTO.getUserId(), userBookDTO.getBookId());
        return new Response.Builder().type(ResponseType.OK).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    return response;
  }

  private void sendResponse(Response response) throws IOException{
    Gson gson = new Gson();
    String json = gson.toJson(response);
    System.out.println("Response json "+json);
    output.println(json);
  }
}
