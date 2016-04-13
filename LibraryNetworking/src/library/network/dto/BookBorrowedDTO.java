package library.network.dto;

import java.io.Serializable;

/**
 * Created by Sergiu on 04.04.2016.
 */
public class BookBorrowedDTO implements Serializable {
  private int bookId;
  private int newQuantity;
  private boolean byThisUser;

  public BookBorrowedDTO(int bookId, int newQuantity, boolean byThisUser) {
    this.bookId = bookId;
    this.newQuantity = newQuantity;
    this.byThisUser = byThisUser;
  }

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
  }

  public int getNewQuantity() {
    return newQuantity;
  }

  public void setNewQuantity(int newQuantity) {
    this.newQuantity = newQuantity;
  }

  public boolean isByThisUser() {
    return byThisUser;
  }

  public void setByThisUser(boolean byThisUser) {
    this.byThisUser = byThisUser;
  }
}
