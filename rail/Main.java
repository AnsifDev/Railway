package rail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        DataBase db;

        try (ObjectInputStream objIn = new ObjectInputStream(new FileInputStream("db.bin"))) {
            db = (DataBase) objIn.readObject();
            if (db == null) db = new DataBase();
        } catch (ClassNotFoundException | IOException e) {
            db = new DataBase();
            System.out.println("No file");
        }

        RailSystem r = new RailSystem(db);
        db = r.start();

        try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream("db.bin"))) {
            objOut.writeObject(db);
        }
    }
}

class DataBase implements Serializable {
    HashMap<String, HashMap<String, String>> Users, SuperUsers;
    String[][] Trips, Tickets;
}

class RailSystem {
    HashMap<String, HashMap<String, String>> Users, SuperUsers;
    ArrayList<HashMap<String, String>> Trips, Tickets;
    
    static String stations[] = {"A", "B", "C", "D", "E"};
    String MainMenu = "----Main Menu----\n  1. Login\n  2. Register\n  3. Exit\n>> ";
    Scanner sc = new Scanner(System.in);

    public RailSystem(DataBase db) {
        if (db.Users == null) Users = new HashMap<>();
        else Users = db.Users;
        if (db.SuperUsers == null) SuperUsers = new HashMap<>();
        else SuperUsers = db.SuperUsers;

        if (db.Trips == null) Trips = new ArrayList<>();
        else Trips = deParseHashMapArray(db.Trips);

        if (db.Tickets == null) Tickets = new ArrayList<>();
        else Tickets = deParseHashMapArray(db.Tickets);
    }

    public DataBase start() {
        while (true) {
            print(MainMenu);
            int input = Integer.parseInt(sc.nextLine());

            switch(input) {
                case 3: return parseData();
                case 2: register(); break;
                case 1: login(); break;
                default: print("Invalid Input!!!\n");
            }
        }
    }

    private DataBase parseData() {
        DataBase db = new DataBase();

        db.SuperUsers = SuperUsers;
        db.Users = Users;
        
        if (this.Trips.size() != 0) {
            db.Trips = parseHashMapArray(this.Trips);
        }
        
        if (this.Tickets.size() != 0) {
            db.Tickets = parseHashMapArray(this.Tickets);
        }

        return db;
    }

    private String[][] parseHashMapArray(ArrayList<HashMap<String, String>> array) {
        if (array.size() != 0) {
            String strArr[][];
            strArr = new String[array.size()+1][];
            Set<String> keys = this.Trips.get(0).keySet();

            for (int i = 0; i <= array.size(); i++) {
                String[] hashmapArray = new String[keys.size()];
                strArr[i] = hashmapArray;
                if (i == 0) {
                    int j = 0;
                    for (String key: keys) {
                        hashmapArray[j++] = key;
                    }
                } else {
                    HashMap<String, String> map = array.get(i-1);
                    for (int j = 0; j < strArr[0].length; j++) {
                        hashmapArray[j] = map.get(strArr[0][j]);
                    }
                }
            }
            return strArr;
        } else return null;
    }

    private ArrayList<HashMap<String, String>> deParseHashMapArray(String[][] arr) {
        if (arr != null) {
            ArrayList<HashMap<String, String>> rtList = new ArrayList<>();
            String[] keys = arr[0];
            for (int i = 1; i < arr.length; i++) {
                String[] values = arr[i];
                HashMap<String, String> map = new HashMap<>();
                for (int j = 0; j < keys.length; j++) {
                    map.put(keys[j], values[j]);
                }
                rtList.add(map);
            }
            return rtList;
        } else return null;
    }

    private void register() {
        print("Register as Station Master (y/n): ");
        char opt = sc.nextLine().charAt(0);
        boolean regSuper = opt == 'Y' || opt == 'y';

        print("Name: ");
        String Name = sc.nextLine();
        print("Username: ");
        String username = sc.nextLine();
        print("Password: ");
        String password = sc.nextLine();

        HashMap<String, String> map = new HashMap<>();
        map.put("Name", Name);
        map.put("Username", username);
        map.put("Password", password);

        if (Users.containsKey(username) || SuperUsers.containsKey(username)) print("User already exists\n");
        else {
            if (regSuper) SuperUsers.put(username, map);
            else Users.put(username, map);
            print("Registration sucessfull\n");
        }
    }

    private void login() {
        print("Username: ");
        String username = sc.nextLine();
        print("Password: ");
        String password = sc.nextLine();

        if (Users.containsKey(username)) {
            HashMap<String, String> user = Users.get(username);
            if (user.get("Password").equals(password)) {
                print("Login Sucessfull\n");
                postLogin(user, false);
                print("Hey "+user.get("Name")+"\n");
            } else print("Wrong password\n");
            return;
        }

        if (SuperUsers.containsKey(username)) {
            HashMap<String, String> user = SuperUsers.get(username);
            if (user.get("Password").equals(password)) {
                print("Login Sucessfull\n");
                postLogin(user, true);
                print("Hey "+user.get("Name")+"\nYou are logged in as a super user\n");
            } else print("Wrong password\n");
            return;
        }

        print("No such username found\n");
    }

    private void postLogin(HashMap<String, String> user, boolean superUser) {
        String SkelMenu = "----User Menu----\n  1. Book Tickets\n  2. View My Tickets\n  3. Logout\n";
        String menu = SkelMenu+(superUser?"  4. Trip Manager\n>> ":">> ");

        while (true) {
            print(menu);
            int input = Integer.parseInt(sc.nextLine());

            switch (input) {
                case 3: return;
                case 4: if (superUser) TripManager(); break;
                case 1: book(user); break;
                case 2: manageTickets(user); break;
                default: print("Invalid Input!!!\n");
            }
        }
    }

    private void manageTickets(HashMap<String, String> user) {
        ArrayList<HashMap<String, String>> myTickets = new ArrayList<>();

        print("----Booked Tickets----\n");
        int i = 1;
        for (HashMap<String, String> ticket: Tickets) {
            if (ticket.get("Username").equals(user.get("Username"))) {
                myTickets.add(ticket);
                print(""+i+".\tFrom: "+ticket.get("From")+"\n\tTo: "+ticket.get("To")+"\n\tTrip ID: "+ticket.get("Id")+"\n");
            }
        }

        print("----Ticket Manager----\n1.  Cancel A Ticket\n2.  Back\n");
        while(true) {
            print(">> ");
            int input = Integer.parseInt(sc.nextLine());
            switch(input) {
                case 2: return;
                case 1: 
                    print("Enter the ticket index number: ");
                    int index = Integer.parseInt(sc.nextLine());
                    try {
                        HashMap<String, String> ticket = myTickets.remove(index-1);
                        Tickets.remove(ticket);
                    } catch (IndexOutOfBoundsException e) {
                        print("Invalid index\n");
                    }
                    break;
                default: print("Invalid Input!!!\n");
            }
        }
    }

    private void book(HashMap<String, String> user) {
        print("From: ");
        String from = sc.nextLine();
        print("To: ");
        String to = sc.nextLine();

        int fromIndex = getIndex(from);
        int toIndex = getIndex(to);

        boolean forward = fromIndex<toIndex;
        ArrayList<HashMap<String, String>> selected = new ArrayList<>();
        for (HashMap<String, String> trip: Trips) {
            if (forward && getIndex(trip.get("From")) <= fromIndex && getIndex(trip.get("To")) >= toIndex) selected.add(trip);
            if (!forward && getIndex(trip.get("From")) >= fromIndex && getIndex(trip.get("To")) <= toIndex) selected.add(trip);
        }

        HashMap<String, String> tripOk;
        if (selected.size() == 0) {
            print("No trips available\n");
            return;
        } else {
            print("Trips Found: \n");
            for (HashMap<String, String> trip: selected) print("Trip id: "+trip.get("Id")+"\tFrom: "+trip.get("From")+"\tTo: "+trip.get("To")+"\n");

            if (selected.size() == 1) {
                tripOk = selected.get(0);
            } else {
                print("Select a trip: ");
                int tripIndex = Integer.parseInt(sc.nextLine());
                tripOk = selected.get(tripIndex);
            }
            
            tripOk.put("From", from);
            tripOk.put("To", to);
            tripOk.put("Username", user.get("Username"));
            Tickets.add(tripOk);
            print("Booking OK\n");
        }
    }

    private int getIndex(String to) {
        for (int i = 0; i < stations.length; i++) {
            if (stations[i].equals(to)) return i;
        }
        return -1;
    }

    private void TripManager() {
        String menu = "----Trip Management----\n  1. Add Trip\n  2. View Trips\n  3. Remove Trip\n  4. Back\n>> ";

        while (true) {
            print(menu);
            int input = Integer.parseInt(sc.nextLine());

            switch (input) {
                case 4: return;
                case 1: addTrip(); break;
                case 2: for(HashMap<String, String> trip: Trips) {
                    print("Trip id: "+trip.get("Id")+"\tFrom: "+trip.get("From")+"\tTo: "+trip.get("To")+"\n");
                } break;
                case 3: removeTrip(); break;
                default: print("Invalid Input!!!\n");
            }
        }
    }

    private void removeTrip() {
        print("Trip ID: ");
        String id = sc.nextLine();

        for (int i = 0; i < Trips.size(); i++)
            if (Trips.get(i).get("Id").equals(id)) {
                Trips.remove(i);
                print("Removed\n");
                return;
            }
        
        print("No trip with this id found\n");
    }

    private void addTrip() {
        print("Stations: ");
        for (String station: stations) print("\t"+station);
        print("\n");
        
        print("Trip ID: ");
        String id = sc.nextLine();
        print("From: ");
        String from = sc.nextLine();
        print("To: ");
        String to = sc.nextLine();
        
        if (getIndex(from) == -1) print(from+" is not a station\n");
        else if (getIndex(to) == -1) print(to+" is not a station\n");
        else {
            HashMap<String, String> map = new HashMap<>();
            map.put("Id", id);
            map.put("From", from);
            map.put("To", to);
            Trips.add(map);
        }
        
    }

    private void print(String str) {System.out.print(str);}
}