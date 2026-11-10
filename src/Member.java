import java.io.Serializable;
import java.util.ArrayList;

public class Member implements Serializable {
    int id;
    String name, email, password;
    boolean dueAlert = true, newAlert = true;
    ArrayList<String> inbox = new ArrayList<>();
    ArrayList<String> searches = new ArrayList<>();

    Member(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
