import java.io.Serializable;

public class Book implements Serializable {
    int id;
    String title, author, isbn, genre;
    boolean available = true;
    int count = 0;

    Book(int id, String title, String author, String isbn, String genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
    }
}
