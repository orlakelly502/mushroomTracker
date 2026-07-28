import java.io.IOException;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import com.google.gson.Gson;

// Collection Manager kinda?

public class MushApp {
    Scanner ip;
    DBConnect conn;
    MushClient client;
    Gson gson = new Gson();
    Colony activeColony;
    volatile boolean running;

    ArrayList<Colony> colonies = new ArrayList<>();
    ArrayList<MushroomType> types = new ArrayList<>();


    public MushApp(Scanner ip, DBConnect conn) throws SQLException {
        this.ip = ip;
        this.conn = conn;
        this.types = MushroomType.getAllTypes(conn);
        this.client = new MushClient();
        this. running = true;
    }


    // creates object - adds to colonies array -> calls to colony method -> inserts new colony into DB
    // retrieves auto generated key - updates new colony objects ID to match the auto generated one
    // no 'orphaned' colony objects can be created thanks to this chain
    public void makeNewColony(){
        boolean makingSelection = true;

        while (makingSelection) {
            // check for potential no types on return
            if (types.isEmpty()) {
                System.out.println("Error no mushroom type data available - exiting application");
                return;
            } else {
                System.out.println("Please Select a Mushroom Type for your new Colony: ");
                displayAvailableTypes();
                int usersChoice = ip.nextInt();
                ip.nextLine();


                // passing mushroom id's to the colony creation method
                if(usersChoice -1 < 0 || usersChoice -1 > types.size() -1){
                    System.out.println("Invalid menu selection, please try again.");
                }else{
                    Colony newCol = Colony.collectColonyDetails(ip, types.get(usersChoice -1).getMushroomTypeId());
                    colonies.add(newCol);
                    newCol.insertIntoDB(conn);

                    makingSelection = false;
                }

            }
        }
    }

    public void displayAvailableTypes(){
        int i = 1;

        for(MushroomType mushroom : types){
            System.out.println(i + ". " + mushroom.getCommonName());
            i++;
        }
    }

    // restoring active colony from file
    public void restoreActiveColony(){
        int restoredId = FileHandler.readActiveCol();

        // if the return is < 0 file does not exist or is empty which is expected on first launch.
        if(restoredId < 0){
            return;
        }
        // otherwise a valid id has been found!
        setActiveColony(restoredId);
    }

    // allows users to select and active colony that sensor data belongs to - returns false if invalid id is entered
    public boolean setActiveColony(int id){
        Colony col = findColonyById(id);
        if(col != null){
            this.activeColony = col;
            return true;
        }else{
            System.out.println("No Colony with that ID was found, you can create a new one or try again with another ID");
            return false;
        }
    }

    // search hydrated colonies Array for existing colony that matches passed id
    public Colony findColonyById(int id){
        for(Colony colony : colonies){
            if(colony.getColonyId() == id){
                return colony;
            }
        }
        return null;
    }

    // checks client request response - checks the body for error codes to prevent exceptions before 'translating' it
    // into an object using GSON - maybe to many responsibilities in this function could do with splitting later
    public RawSensorData checkResponse(HttpResponse<String> response){
        String responseBody = response.body();
        System.out.println(responseBody);
        if(responseBody.equals("503")){
            System.out.println("No Reading available");
            return null;
        }else{
            RawSensorData reading = gson.fromJson(response.body(), RawSensorData.class);
            return reading;
        }
    }

    public Colony getActiveColony() {
        return activeColony;
    }

    // main loop for requesting and writing sensor data - needs it's own thread so it runs constantly in the background
    public void pollAndPersistReadings() {
        // on launch check if there is a saved active colony from a previous session & restore it
        restoreActiveColony();

        // makes use of instance variable so it's status can be changed externally in main menu
        while(running){

                try {
                    if(getActiveColony() == null){
                        System.out.println("No Active Colony");
                        Thread.sleep(10000);
                        continue;

                    }
                    // sending the request for data and retrieving it's been handled
                    HttpResponse<String> response = client.sendRequest();

                    // check Response is valid for GSON parsing - returns valid object if it is
                    RawSensorData newReading = checkResponse(response);

                    // GSON does not use classes constructor so need to set ID manually - uses reflection
                    newReading.setColonyId(1);

                    // writing new sensor record to the DB
                    newReading.rawSensorReadingToDb(conn.getConnection());
                    System.out.println("Adding new Reading");

                    Thread.sleep(10000);

                } catch (IOException e) {
                    System.out.println(" Pi's Flask server isn't running or the  network's down");
                } catch (SQLException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    public void printMainMenu(){
        System.out.println("""
                Please make a selection from the menu below:
                1.Create New Colony
                2.Set active Colony (NOTE: All incoming sensor readings are automatically associated with active colony).
                3.View Dashboard
                4.Create a new Flush
                """);
    }


    // main application loop
    public void navigateMenu() {
        while (running) {
            printMainMenu();

            int usersChoice = ip.nextInt();
            // flush buffer without waiting for more input
            ip.nextLine();

            switch (usersChoice) {
                case 1 -> {
                    makeNewColony();
                }
                case 2 -> {
                    while (true) {
                        System.out.println("Please enter the ID of the Colony you wish to make Active");
                        int newId = ip.nextInt();
                        ip.nextLine();
                        if (setActiveColony(newId)) {
                            break;
                        }
                    }
                }
                case 3 -> {
                    try {
                        OpenDashboard.launchDashboard();
                    } catch (IOException e) {
                        System.out.println("Couldn't open the dashboard: " + e.getMessage());
                    }
                }

                case 4 -> {
                    // Create new Flush
                }

                case 5 -> {
                    System.out.println("Exiting Application");
                    if (activeColony != null) {
                        FileHandler.writeActiveCol(getActiveColony().getColonyId());
                    }
                    running = false;
                }
            }
        }
    }
}
