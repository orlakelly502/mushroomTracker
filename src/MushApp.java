import com.mysql.cj.protocol.Resultset;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    ArrayList<Colony> colonies = new ArrayList<>();
    ArrayList<MushroomType> types = new ArrayList<>();


    public MushApp(Scanner ip, DBConnect conn) throws SQLException {
        this.ip = ip;
        this.conn = conn;
        this.types = MushroomType.getAllTypes(conn);
        this.client = new MushClient();
    }


    // creates object - adds to colonies array -> calls to colony method -> inserts new colony into DB
    // retrieves auto generated key - updates new colony objects ID to match the auto generated one
    // no 'orphaned' colony objects can be created thanks to this chain
    public void makeNewColony() throws SQLException {
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

    // allows users to select and active colony that sensor data belongs to
    public void setActiveColony(int id){
        Colony col = findColonyById(id);
        if(col != null){
            this.activeColony = col;
        }else{
            System.out.println("No Colony with that ID was found, you can create a new one or try again with another ID");
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
    public void run() throws InterruptedException, IOException {
        boolean running = true;

        // temporarily auto opening dashboard on launch
        OpenDashboard.launchDashboard();
        printMainMenu();

        while(running){
            try{
                // sending the request for data and retrieving it's been handled
                HttpResponse<String> response = client.sendRequest();

                // check Response is valid for GSON parsing - returns valid object if it is
                RawSensorData newReading = checkResponse(response);

                // GSON does not use classes constructor so need to set ID manually - uses reflection
                newReading.setColonyId(1);

                // writing new sensor record to the DB
                newReading.rawSensorReadingToDb(conn.getConnection());

                Thread.sleep(10000);

            }catch(IOException e){
                System.out.println(" Pi's Flask server isn't running or the  network's down");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
