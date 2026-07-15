import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Date;

public class Flush {
    int flushId;
    int colonyId;
    int flushNumber;
    LocalDate harvestDate;
    double weight;
    Scanner ip;

    public Flush(int fID, int cID, int flushNum, LocalDate harvestDate, double weight, Scanner ip){
        this.flushId = fID;
        this.colonyId = cID;
        this.flushNumber = flushNum;
        this.harvestDate = harvestDate;
        this.weight = weight;
        this.ip = ip;
    }


    public static ArrayList<Flush> fromResultSetGroup(ResultSet rs, Scanner ip) throws SQLException {
        ArrayList<Flush> flushes = new ArrayList<>();

        while (rs.next()) {
            int flushId = rs.getInt("flush_id");
            int colonyId = rs.getInt("colony_id");
            int flushNumber = rs.getInt("flush_number");
            Date rawHarvestDate = rs.getDate("harvest_date");
            LocalDate harvestDate = (rawHarvestDate != null) ? rawHarvestDate.toLocalDate() : null;
            double weight = rs.getDouble("weight");

            Flush f = new Flush(flushId, colonyId, flushNumber, harvestDate, weight, ip);
            flushes.add(f);

        }
        return flushes;
    }



} // class end
