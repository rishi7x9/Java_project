import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    int id, bookId, memberId, fine;
    LocalDate borrowDate, dueDate, returnDate;

    Transaction(int id, int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }
}
