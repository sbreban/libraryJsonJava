package library.network.dto;

import java.io.Serializable;

/**
 * Created by Sergiu on 05.04.2016.
 */
public class BookReturnedDTO implements Serializable {
  private int id;
  private String author, title;
  private boolean byThisUser;

  public BookReturnedDTO(int id, String author, String title, boolean byThisUser) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.byThisUser = byThisUser;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isByThisUser() {
    return byThisUser;
  }

  public void setByThisUser(boolean byThisUser) {
    this.byThisUser = byThisUser;
  }
}
