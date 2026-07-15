import com.mysql.cj.protocol.Resultset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

// Collection Manager kinda?

public class MushApp {
    Scanner ip;
    DBConnect conn;
    ArrayList<Colony> colonies = new ArrayList<>();
    ArrayList<MushroomType> types = new ArrayList<>();

    public MushApp(Scanner ip, DBConnect conn) throws SQLException {
        this.ip = ip;
        this.conn = conn;
        this.types = MushroomType.getAllTypes(conn);
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
}
