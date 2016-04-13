package library.services;

public interface ILibraryClient {
  void bookBorrowed(int bookId, int newQuantity, boolean byThisUser) throws LibraryException;
  void bookReturned(int bookId, String author, String title, boolean byThisUser) throws LibraryException;
}
