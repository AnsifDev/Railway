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

class Ticket {
    String From, To;
    int TripID, Platform;
}

class SerializableArrayList<T> extends ArrayList<T> implements Serializable {}

class User implements Serializable {
    public String Name, username;
    private String password;
    SerializableArrayList<Ticket> tickets = new SerializableArrayList<>();

    User(String username, String Name, String password) {
        this.Name = Name;
        this.password = password;
        this.username = username;
    }

    boolean authenticate(String pass) {
        return pass.equals(password);
    }
}

class DataBase implements Serializable {
    SerializableArrayList<User> users = new SerializableArrayList<>();
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
                if (usr.authenticate(password)) {
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
        User usr = new User(username, Name, password);
        db.users.add(usr);
        System.out.println("Registered Login Again");
    }
}