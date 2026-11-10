import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<Book> books = new ArrayList<>();
    static ArrayList<Member> members = new ArrayList<>();
    static ArrayList<Transaction> txs = new ArrayList<>();
    static int nextBook = 1, nextMember = 1, nextTx = 1;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        load();
        while (true) {
            System.out.println("\n--- LIBRARY ---");
            System.out.println("1. Librarian login");
            System.out.println("2. Member login");
            System.out.println("3. Register");
            System.out.println("0. Exit");
            int c = askInt("Choice: ");
            if (c == 1) librarian();
            else if (c == 2) member();
            else if (c == 3) addMember();
            else if (c == 0) break;
            else System.out.println("Wrong choice");
            save();
        }
        System.out.println("Bye");
    }

    static void librarian() {
        String u = ask("Login ID: ");
        String p = ask("Password: ");
        if (!u.equals("admin") || !p.equals("admin123")) {
            System.out.println("Wrong login");
            return;
        }
        while (true) {
            System.out.println("\n--- LIBRARIAN ---");
            System.out.println("1. Add book");
            System.out.println("2. Edit book");
            System.out.println("3. Delete book");
            System.out.println("4. Show books");
            System.out.println("5. Add member");
            System.out.println("6. Edit member");
            System.out.println("7. Delete member");
            System.out.println("8. Show members");
            System.out.println("9. Issue book");
            System.out.println("10. Return book");
            System.out.println("11. Show transactions");
            System.out.println("12. Send notification");
            System.out.println("13. Report");
            System.out.println("0. Logout");
            int c = askInt("Choice: ");
            if (c == 1) addBook();
            else if (c == 2) editBook();
            else if (c == 3) deleteBook();
            else if (c == 4) showBooks(books);
            else if (c == 5) addMember();
            else if (c == 6) editMember();
            else if (c == 7) deleteMember();
            else if (c == 8) showMembers();
            else if (c == 9) borrow(askInt("Member ID: "), askInt("Book ID: "));
            else if (c == 10) giveBack(askInt("Member ID: "), askInt("Book ID: "));
            else if (c == 11) showTx(txs);
            else if (c == 12) sendNote();
            else if (c == 13) report();
            else if (c == 0) return;
            else System.out.println("Wrong choice");
            save();
        }
    }

    static void member() {
        String e = ask("Email: ");
        String p = ask("Password: ");
        Member m = null;
        for (Member x : members) {
            if (x.email.equalsIgnoreCase(e) && x.password.equals(p)) m = x;
        }
        if (m == null) {
            System.out.println("Wrong login");
            return;
        }
        System.out.println("Welcome " + m.name + " (ID " + m.id + ")");
        alerts(m);
        if (m.inbox.size() > 0) System.out.println("You have " + m.inbox.size() + " new notifications (option 8)");
        while (true) {
            System.out.println("\n--- MEMBER ---");
            System.out.println("1. Search books");
            System.out.println("2. Borrow book");
            System.out.println("3. Return book");
            System.out.println("4. My history");
            System.out.println("5. My search history");
            System.out.println("6. Update profile");
            System.out.println("7. Notification settings");
            System.out.println("8. Read notifications");
            System.out.println("9. Show all books");
            System.out.println("0. Logout");
            int c = askInt("Choice: ");
            if (c == 1) search(m);
            else if (c == 2) {
                ArrayList<Book> free = new ArrayList<>();
                for (Book b : books) if (b.available) free.add(b);
                showBooks(free);
                borrow(m.id, askInt("Book ID to borrow: "));
            }
            else if (c == 3) giveBack(m.id, askInt("Book ID to return: "));
            else if (c == 4) {
                ArrayList<Transaction> mine = new ArrayList<>();
                for (Transaction t : txs) if (t.memberId == m.id) mine.add(t);
                showTx(mine);
            }
            else if (c == 5) {
                if (m.searches.size() == 0) System.out.println("No searches yet");
                for (String s : m.searches) System.out.println("- " + s);
            }
            else if (c == 6) profile(m);
            else if (c == 7) {
                m.dueAlert = askYes("Due date alerts? (y/n): ");
                m.newAlert = askYes("New arrival alerts? (y/n): ");
                System.out.println("Saved");
            }
            else if (c == 8) {
                if (m.inbox.size() == 0) System.out.println("No notifications");
                for (String s : m.inbox) System.out.println("* " + s);
                m.inbox.clear();
            }
            else if (c == 9) showBooks(books);
            else if (c == 0) return;
            else System.out.println("Wrong choice");
            save();
        }
    }

    static void addBook() {
        String t = need("Title: ");
        String a = need("Author: ");
        String i = need("ISBN: ");
        String g = need("Genre: ");
        books.add(new Book(nextBook, t, a, i, g));
        System.out.println("Book added. ID = " + nextBook);
        nextBook++;
    }

    static void editBook() {
        Book b = findBook(askInt("Book ID: "));
        if (b == null) {
            System.out.println("Book not found");
            return;
        }
        System.out.println("Leave blank to keep old value");
        String t = ask("Title (" + b.title + "): ");
        String a = ask("Author (" + b.author + "): ");
        String i = ask("ISBN (" + b.isbn + "): ");
        String g = ask("Genre (" + b.genre + "): ");
        if (!t.isEmpty()) b.title = t;
        if (!a.isEmpty()) b.author = a;
        if (!i.isEmpty()) b.isbn = i;
        if (!g.isEmpty()) b.genre = g;
        System.out.println("Book updated");
    }

    static void deleteBook() {
        Book b = findBook(askInt("Book ID: "));
        if (b == null) {
            System.out.println("Book not found");
        } else if (!b.available) {
            System.out.println("Book is issued, cannot delete");
        } else {
            books.remove(b);
            System.out.println("Book deleted");
        }
    }

    static void showBooks(ArrayList<Book> list) {
        if (list.size() == 0) {
            System.out.println("No books");
            return;
        }
        System.out.printf("%-4s %-28s %-20s %-14s %-12s %s%n", "ID", "Title", "Author", "ISBN", "Genre", "Status");
        for (Book b : list) {
            System.out.printf("%-4d %-28s %-20s %-14s %-12s %s%n", b.id, b.title, b.author, b.isbn, b.genre, b.available ? "Available" : "Issued");
        }
    }

    static void search(Member m) {
        System.out.println("1. Title  2. Author  3. Genre");
        int f = askInt("Search by: ");
        if (f < 1 || f > 3) {
            System.out.println("Wrong choice");
            return;
        }
        String k = need("Keyword: ").toLowerCase();
        String[] names = {"", "title", "author", "genre"};
        m.searches.add(names[f] + ": " + k);
        ArrayList<Book> found = new ArrayList<>();
        for (Book b : books) {
            String s = b.genre;
            if (f == 1) s = b.title;
            if (f == 2) s = b.author;
            if (s.toLowerCase().contains(k)) found.add(b);
        }
        showBooks(found);
    }

    static void addMember() {
        String n = need("Name: ");
        String e = need("Email: ");
        String p = need("Password (min 4): ");
        if (!validEmail(e, 0)) return;
        if (p.length() < 4) {
            System.out.println("Password too short");
            return;
        }
        members.add(new Member(nextMember, n, e, p));
        System.out.println("Member added. ID = " + nextMember);
        nextMember++;
    }

    static void editMember() {
        Member m = findMember(askInt("Member ID: "));
        if (m == null) {
            System.out.println("Member not found");
            return;
        }
        profile(m);
    }

    static void profile(Member m) {
        System.out.println("Leave blank to keep old value");
        String n = ask("Name (" + m.name + "): ");
        String e = ask("Email (" + m.email + "): ");
        String p = ask("Password: ");
        if (!e.isEmpty() && !validEmail(e, m.id)) return;
        if (!p.isEmpty() && p.length() < 4) {
            System.out.println("Password too short");
            return;
        }
        if (!n.isEmpty()) m.name = n;
        if (!e.isEmpty()) m.email = e;
        if (!p.isEmpty()) m.password = p;
        System.out.println("Profile updated");
    }

    static void deleteMember() {
        Member m = findMember(askInt("Member ID: "));
        if (m == null) {
            System.out.println("Member not found");
        } else if (activeLoans(m.id) > 0) {
            System.out.println("Member has books, cannot delete");
        } else {
            members.remove(m);
            System.out.println("Member deleted");
        }
    }

    static void showMembers() {
        if (members.size() == 0) {
            System.out.println("No members");
            return;
        }
        System.out.printf("%-4s %-20s %-28s %s%n", "ID", "Name", "Email", "Books");
        for (Member m : members) {
            System.out.printf("%-4d %-20s %-28s %d%n", m.id, m.name, m.email, activeLoans(m.id));
        }
    }

    static boolean validEmail(String e, int ownId) {
        if (!e.contains("@") || !e.contains(".") || e.contains(" ")) {
            System.out.println("Email is not valid");
            return false;
        }
        for (Member m : members) {
            if (m.email.equalsIgnoreCase(e) && m.id != ownId) {
                System.out.println("Email already used");
                return false;
            }
        }
        return true;
    }

    static void borrow(int mid, int bid) {
        Member m = findMember(mid);
        Book b = findBook(bid);
        if (m == null) {
            System.out.println("Member not found");
        } else if (b == null) {
            System.out.println("Book not found");
        } else if (!b.available) {
            System.out.println("Book is already issued");
        } else if (activeLoans(mid) >= 3) {
            System.out.println("Limit reached, max 3 books");
        } else {
            LocalDate today = LocalDate.now();
            txs.add(new Transaction(nextTx, bid, mid, today, today.plusDays(14)));
            nextTx++;
            b.available = false;
            b.count++;
            System.out.println("Book issued. Due date: " + today.plusDays(14));
        }
    }

    static void giveBack(int mid, int bid) {
        Transaction t = null;
        for (Transaction x : txs) {
            if (x.memberId == mid && x.bookId == bid && x.returnDate == null) t = x;
        }
        if (t == null) {
            System.out.println("This member has not borrowed that book");
            return;
        }
        t.returnDate = LocalDate.now();
        long late = ChronoUnit.DAYS.between(t.dueDate, t.returnDate);
        if (late > 0) t.fine = (int) late * 2;
        findBook(bid).available = true;
        System.out.println("Book returned");
        if (t.fine > 0) System.out.println("Late. Fine = Rs " + t.fine);
        else System.out.println("No fine");
    }

    static int activeLoans(int mid) {
        int n = 0;
        for (Transaction t : txs) {
            if (t.memberId == mid && t.returnDate == null) n++;
        }
        return n;
    }

    static void showTx(ArrayList<Transaction> list) {
        if (list.size() == 0) {
            System.out.println("No transactions");
            return;
        }
        System.out.printf("%-5s %-7s %-7s %-12s %-12s %-13s %s%n", "TxID", "BookID", "Member", "Borrowed", "Due", "Returned", "Fine");
        for (Transaction t : list) {
            System.out.printf("%-5d %-7d %-7d %-12s %-12s %-13s %d%n", t.id, t.bookId, t.memberId, t.borrowDate, t.dueDate, t.returnDate == null ? "Not returned" : t.returnDate.toString(), t.fine);
        }
    }

    static void sendNote() {
        System.out.println("1. Due date reminder  2. New arrival");
        int type = askInt("Type: ");
        if (type != 1 && type != 2) {
            System.out.println("Wrong type");
            return;
        }
        String msg = need("Message: ");
        int id = askInt("Member ID (0 = all): ");
        if (id != 0 && findMember(id) == null) {
            System.out.println("Member not found");
            return;
        }
        int sent = 0, skipped = 0;
        for (Member m : members) {
            if (id != 0 && m.id != id) continue;
            boolean wants = type == 1 ? m.dueAlert : m.newAlert;
            if (wants) {
                m.inbox.add((type == 1 ? "[Due] " : "[New] ") + msg);
                sent++;
            } else {
                skipped++;
            }
        }
        System.out.println("Sent to " + sent + ", skipped " + skipped + " (alerts off)");
    }

    static void alerts(Member m) {
        if (!m.dueAlert) return;
        LocalDate today = LocalDate.now();
        for (Transaction t : txs) {
            if (t.memberId != m.id || t.returnDate != null) continue;
            long left = ChronoUnit.DAYS.between(today, t.dueDate);
            String title = findBook(t.bookId).title;
            if (left < 0) System.out.println("! OVERDUE by " + (-left) + " days: " + title);
            else if (left <= 2) System.out.println("! Due in " + left + " days: " + title);
        }
    }

    static void report() {
        int free = 0, overdue = 0, fines = 0;
        for (Book b : books) if (b.available) free++;
        for (Transaction t : txs) {
            fines += t.fine;
            if (t.returnDate == null && t.dueDate.isBefore(LocalDate.now())) overdue++;
        }
        System.out.println("Total books   : " + books.size());
        System.out.println("Available     : " + free);
        System.out.println("Issued        : " + (books.size() - free));
        System.out.println("Overdue       : " + overdue);
        System.out.println("Members       : " + members.size());
        System.out.println("Fines (Rs)    : " + fines);
        System.out.println("Borrow trend:");
        for (Book b : books) {
            String bar = "";
            for (int i = 0; i < b.count; i++) bar += "#";
            System.out.printf("%-28s %s %d%n", b.title, bar, b.count);
        }
    }

    static Book findBook(int id) {
        for (Book b : books) if (b.id == id) return b;
        return null;
    }

    static Member findMember(int id) {
        for (Member m : members) if (m.id == id) return m;
        return null;
    }

    static void save() {
        try {
            new File("data").mkdirs();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data/library.dat"));
            out.writeObject(books);
            out.writeObject(members);
            out.writeObject(txs);
            out.writeInt(nextBook);
            out.writeInt(nextMember);
            out.writeInt(nextTx);
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    static void load() {
        File f = new File("data/library.dat");
        if (f.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                books = (ArrayList<Book>) in.readObject();
                members = (ArrayList<Member>) in.readObject();
                txs = (ArrayList<Transaction>) in.readObject();
                nextBook = in.readInt();
                nextMember = in.readInt();
                nextTx = in.readInt();
                in.close();
                return;
            } catch (Exception e) {
                System.out.println("Could not read saved data, starting fresh");
            }
        }
        books.add(new Book(nextBook++, "Head First Java", "Kathy Sierra", "9780596009205", "Programming"));
        books.add(new Book(nextBook++, "Clean Code", "Robert Martin", "9780132350884", "Programming"));
        books.add(new Book(nextBook++, "Wings of Fire", "APJ Abdul Kalam", "9788173711466", "Biography"));
        books.add(new Book(nextBook++, "The Alchemist", "Paulo Coelho", "9780061122415", "Fiction"));
        members.add(new Member(nextMember++, "Demo Member", "member@test.com", "1234"));
    }

    static String ask(String text) {
        System.out.print(text);
        try {
            return sc.nextLine().trim();
        } catch (Exception e) {
            save();
            System.exit(0);
            return "";
        }
    }

    static String need(String text) {
        String s = ask(text);
        while (s.isEmpty()) {
            System.out.println("Cannot be empty");
            s = ask(text);
        }
        return s;
    }

    static int askInt(String text) {
        while (true) {
            try {
                return Integer.parseInt(ask(text));
            } catch (NumberFormatException e) {
                System.out.println("Enter a number");
            }
        }
    }

    static boolean askYes(String text) {
        while (true) {
            String s = ask(text).toLowerCase();
            if (s.equals("y")) return true;
            if (s.equals("n")) return false;
            System.out.println("Type y or n");
        }
    }
}
