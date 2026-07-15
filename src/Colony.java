import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Colony {
    int colonyId;
    int mushroomTypeId;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    String notes;
    Scanner ip;

    public Colony(int colonyId, int mushroomTypeId, LocalDate startDate, LocalDate endDate, String status, String notes, Scanner ip){
        this.colonyId = colonyId;
        this.mushroomTypeId = mushroomTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.notes = notes;
        this.ip = ip;
    }


    // factory method for creating  several Colony objects from DB data

    public static ArrayList<Colony> fromResultSetGroup(ResultSet rs, Scanner ip) throws SQLException {
        ArrayList<Colony> colonies = new ArrayList<>();

            while (rs.next()) {
                int cId = rs.getInt("colony_id");
                int mID = rs.getInt("mushroom_type_id");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();

                // because endDate can be null  it needs to be checked before attempting to covert to LocalDate
                Date rawEndDate = rs.getDate("end_date");
                LocalDate endDate = (rawEndDate != null) ? rawEndDate.toLocalDate() : null;

                String status = rs.getString("status");
                String notes = rs.getString("notes");

                Colony c = new Colony(cId, mID, startDate, endDate, status, notes, ip);
                colonies.add(c);
            }

        return colonies;
    }


} // class end
