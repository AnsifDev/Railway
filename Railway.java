import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

class DataBaseFile {
    private final String filename;

    public DataBaseFile(String filename) {
        this.filename = filename;
    }

    void write(DataBase db) throws IOException {
        ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(filename));
        o.writeObject(db);
        o.close();
        System.out.println("Write ok");
    }


    Object read() throws IOException, ClassNotFoundException {
        ObjectInputStream i = new ObjectInputStream(new FileInputStream(filename));
        Object rt = i.readObject();
        i.close();
        return rt;
    }
}

class Ticket implements Serializable {
    String From, To;
    int TripID, Platform;

    Ticket(String From, String To, int TripID, int Platform) {
        this.From = From;
        this.To = To;
        this.TripID = TripID;
        this.Platform = Platform;
    }
}

class Trip implements Serializable {

    String From, To, TrainID;
    int TripID;
}

class SerializableArrayList<T> extends ArrayList<T> implements Serializable {}

class User implements Serializable {
    public String Name, username;
    private String password;
    DataBase db;
    SerializableArrayList<Ticket> tickets = new SerializableArrayList<>();
    Scanner sc = new Scanner(System.in);
    protected String menu = "----User Menu----\n  1. Logout\n  2. Book\n  3. View Tickets\n  4. Cancel Ticket";

    User(String username, String Name, String password, DataBase db) {
        this.Name = Name;
        this.password = password;
        this.username = username;
        this.db = db;
    }

    boolean login(String pass) {
        Scanner sc = new Scanner(System.in);
        System.out.println(menu);
        if (!pass.equals(password)) return false;
        while(true) {
            System.out.print(">> ");
            int choice = Integer.parseInt(sc.nextLine());
            if (choice == 1) break;
            handle(choice);
        }
        return true;
    }

    void handle(int choice) {
        switch (choice) {
            case 2: book(); break;
            case 3: viewTicket(); break;
            case 4: cancelTicket(); break;
        }
    }

    private void cancelTicket() {
    }

    private void viewTicket() {
        for (Ticket ticket: tickets) System.out.println(ticket);
    }

    private void book() {
        String from, to;
        System.out.print("From: ");
        from = sc.nextLine();
        System.out.print("To: ");
        to = sc.nextLine();
        Ticket ticket = new Ticket(from, to, 0, 1);
        tickets.add(ticket);
    }
}

class DataBase implements Serializable {
    SerializableArrayList<User> users = new SerializableArrayList<>();
    SerializableArrayList<Trip> trips = new SerializableArrayList<>();
    static final String STATIONS[] = {
        "A", "B", "C", "D", "E"
    };
}

class Railway {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String menu = "----Menu----\n  1. Login\n  2.Register\n  3.Exit";
        System.out.println(menu);
        DataBaseFile dbFile = new DataBaseFile("db.bin");

        DataBase db;
        try {
            db = (DataBase) dbFile.read();
            if (db == null) db = new DataBase();
        } catch (FileNotFoundException e) {
            db = new DataBase();
            System.out.println("No file");
        }

        while (true) {
            System.out.print(">> ");
            int input = Integer.parseInt(sc.nextLine());

            switch (input) {
                case 1: login(db); break;
                case 2: register(db); break;
                case 3: dbFile.write(db); return;
            }
        }
    }

    private static void login(DataBase db) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the Phone number as username: ");
        String username = sc.nextLine();
        System.out.print("Enter your password: ");
        String password = sc.nextLine();
        for (User usr: db.users) {
            if (usr.username.equals(username)) {
                if (usr.login(password)) {
                    System.out.println("OK");
                } else System.out.println("Not OK");
                return;
            }
        }
        System.out.println("Login Failed");
    }

    private static void register(DataBase db) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String Name = sc.nextLine();
        System.out.print("Enter the Phone number as username: ");
        String username = sc.nextLine();
        System.out.print("Enter your password: ");
        String password = sc.nextLine();
        User usr = new User(username, Name, password, db);
        db.users.add(usr);
        System.out.println("Registered Login Again");
    }
}