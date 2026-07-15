import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class SensorReading {
    int readingId;
    int colonyId;
    LocalDateTime readingTime;
    double temp;
    double humidity;
    Scanner ip;

    public SensorReading(int rID, int cID, LocalDateTime rTime, double temp, double humidity, Scanner ip){
        this.readingId = rID;
        this.colonyId = cID;
        this.readingTime = rTime;
        this.temp = temp;
        this.humidity = humidity;
        this.ip = ip;
    }

    // creating many
    public static ArrayList<SensorReading> fromResultSetGroup(ResultSet rs, Scanner ip) throws SQLException {
        ArrayList<SensorReading> readings = new ArrayList<>();

        while (rs.next()) {
            int rID = rs.getInt("reading_id");
            int cID = rs.getInt("colony_id");
            LocalDateTime readingTime = rs.getTimestamp("reading_time").toLocalDateTime();
            double temp = rs.getDouble("temperature");
            double humidity = rs.getDouble("humidity");

            SensorReading s = new SensorReading(rID, cID, readingTime, temp, humidity, ip);
            readings.add(s);

        }
        return readings;
    }



} // class end
}
