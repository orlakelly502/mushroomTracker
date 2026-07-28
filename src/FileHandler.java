import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileHandler {
    public FileHandler() {

    }
    //no need to use the same stream for operations since they are small and streams are quick to open
    // don't have the same over overhead a DBConnction has


    // write active colonyID to a file - overwrites old active id
    public static void writeActiveCol(int id) {
        // FileWriter will be closed automatically here
        try (FileWriter myWriter = new FileWriter("activeColony.txt")) {
            myWriter.write(String.valueOf(id));
            System.out.println("Active Colony ID has been saved successfully");
        } catch (IOException e) {
            System.out.println("An error occurred. Failed to save.");
            e.printStackTrace();
        }
    }

    // read active colonyid from a file & return it
    public static int readActiveCol() {
        File idTxt = new File("activeColony.txt");

        // try-with-resources: Scanner will be closed automatically
        try (Scanner myReader = new Scanner(idTxt)) {
                if(myReader.hasNextLine()) {
                    int id = myReader.nextInt();
                    return id;
                }
        } catch (FileNotFoundException e) {
            // flag for failure - ids can never be negative numbers
            return -1;
        }
        return -1;
    }
}


