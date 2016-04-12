package library.network.rpcprotocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import library.model.Book;
import library.model.User;
import library.network.dto.BookDTO;
import library.network.dto.BookQuantityDTO;
import library.network.dto.UserBookDTO;
import library.network.dto.UserDTO;
import library.network.json.ResponseDeserializer;
import library.services.ILibraryClient;
import library.services.ILibraryServer;
import library.services.LibraryException;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LibraryServerRpcProxy implements ILibraryServer {
  private String host;
  private int port;

  private ILibraryClient client;

  private BufferedReader input;
  private PrintWriter output;
  private Socket connection;

  private BlockingQueue<Response> responseBlockingQueue;
  private volatile boolean finished;

  public LibraryServerRpcProxy(String host, int port) {
    this.host = host;
    this.port = port;
    this.responseBlockingQueue = new LinkedBlockingQueue<>();
  }

  public User login(String userName, String password, ILibraryClient client) throws LibraryException {
    initializeConnection();
    UserDTO userDTO = new UserDTO(userName, password);
    Request request = new Request.Builder().type(RequestType.LOGIN).data(userDTO).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    User user = (User)response.data();
    if (user != null && response.type() == ResponseType.LOGIN_SUCCESSFULLY) {
      this.client=client;
    }
    return user;
  }

  public void logout(int userId, ILibraryClient client) throws LibraryException {
    Request request = new Request.Builder().type(RequestType.LOGOUT).data(userId).build();
    sendRequest(request);
    Response response = readResponse();
    closeConnection();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      throw new LibraryException(errorMessage);
    }
  }

  @Override
  public List<Book> getAvailableBooks() throws LibraryException {
    Request request = new Request.Builder().type(RequestType.GET_AVAILABLE_BOOKS).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type()==ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    List<Book> allBooks = (List<Book>)response.data();
    return allBooks;
  }

  @Override
  public List<Book> getUserBooks(int userId) throws LibraryException {
    Request request = new Request.Builder().type(RequestType.GET_USER_BOOKS).data(userId).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    return (List<Book>)response.data();
  }

  @Override
  public List<Book> searchBooks(String key) throws LibraryException {
    Request request = new Request.Builder().type(RequestType.SEARCH_BOOKS).data(key).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    return (List<Book>)response.data();
  }

  @Override
  public void borrowBook(int userId, int bookId) throws LibraryException {
    UserBookDTO userBookDTO = new UserBookDTO(userId, bookId);
    Request request = new Request.Builder().type(RequestType.BORROW_BOOK).data(userBookDTO).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      throw new LibraryException(errorMessage);
    }
  }

  @Override
  public void returnBook(int userId, int bookId) throws LibraryException {
    UserBookDTO userBookDTO = new UserBookDTO(userId, bookId);
    Request request = new Request.Builder().type(RequestType.RETURN_BOOK).data(userBookDTO).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      throw new LibraryException(errorMessage);
    }
  }

  private void closeConnection() {
    finished=true;
    try {
      input.close();
      output.close();
      connection.close();
      client=null;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void sendRequest(Request request)throws LibraryException {
      Gson gson = new Gson();
      String json = gson.toJson(request);
      System.out.println("Request json " + json);
      output.println(json);
  }

  private Response readResponse() throws LibraryException {
    Response response=null;
    try{
      response = responseBlockingQueue.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return response;
  }
  private void initializeConnection() throws LibraryException {
    try {
      connection=new Socket(host,port);
      output = new PrintWriter(connection.getOutputStream(), true);
      output.flush();
      input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      finished = false;
      startReader();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void startReader() {
    Thread readerThread = new Thread(new ReaderThread());
    readerThread.start();
  }


  private void handleUpdate(Response response){
    if (response.type() == ResponseType.BORROW_BOOK) {
      try {
        BookQuantityDTO bookQuantityDTO = (BookQuantityDTO)response.data();
        client.bookUpdated(bookQuantityDTO.getBookId(), bookQuantityDTO.getNewQuantity());
      } catch (LibraryException exception) {

      }
    }
    if (response.type() == ResponseType.RETURN_BOOK) {
      try {
        BookDTO bookDTO = (BookDTO)response.data();
        client.bookReturned(bookDTO.getId(), bookDTO.getAuthor(), bookDTO.getTitle());
      } catch (LibraryException exception) {

      }
    }
  }

  private boolean isUpdate(Response response){
    return response.type() == ResponseType.BORROW_BOOK || response.type() == ResponseType.RETURN_BOOK;
  }

  private class ReaderThread implements Runnable {
    public void run() {
      while(!finished){
        try {
          String responseJson = input.readLine();
          if (responseJson != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Response.class, new ResponseDeserializer());
            Gson gson = gsonBuilder.create();
            Response response = gson.fromJson(responseJson, Response.class);
            System.out.println("Response received " + response);
            if (isUpdate(response)) {
              handleUpdate(response);
            } else {
              try {
                responseBlockingQueue.put(response);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        } catch (IOException e) {
          System.out.println("Reading error "+e);
        }
      }
    }
  }
}
