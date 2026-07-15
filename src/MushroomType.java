import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MushroomType {
    int mushroomTypeId;
    String commonName;
    String scientificName;
    double tempMin;
    double tempMax;
    double humidityMin;
    double humidityMax;
    String description;


    public MushroomType(int mID, String cName, String sName, double tempMin, double tempMax, double humidityMin,
                        double humidityMax, String desc){
        this.mushroomTypeId = mID;
        this.commonName = cName;
        this.scientificName = sName;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidityMin = humidityMin;
        this.humidityMax = humidityMax;
        this.description = desc;
    }

    // creating many
    public static ArrayList<MushroomType> fromResultSetGroup(ResultSet rs) throws SQLException {
        ArrayList<MushroomType> types = new ArrayList<>();

        while (rs.next()) {
            int mID = rs.getInt("mushroom_type_id");
            String cName = rs.getString("common_name");
            String sName = rs.getString("scientific_name");
            double tempMin = rs.getDouble("temp_min");
            double tempMax = rs.getDouble("temp_max");
            double humidityMin = rs.getDouble("humidity_min");
            double humidityMax = rs.getDouble("humidity_max");
            String desc = rs.getString("description");

            MushroomType m = new MushroomType(mID, cName, sName, tempMin, tempMax, humidityMin, humidityMax, desc);
            types.add(m);

        }
        return types;
    }


    public static ArrayList<MushroomType> getAllTypes(DBConnect conn) throws SQLException {
        String sql = "SELECT * FROM mushroom_type";

        try(PreparedStatement ps = conn.getConnection().prepareStatement(sql)){
            ResultSet rs = ps.executeQuery(sql);
            ArrayList<MushroomType> allShrooms = fromResultSetGroup(rs);
            return allShrooms;
        }
    }

    public String getCommonName() {
        return commonName;
    }

    public int getMushroomTypeId() {
        return mushroomTypeId;
    }

    public String getScientificName() {
        return scientificName;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public double getHumidityMin() {
        return humidityMin;
    }

    public double getHumidityMax() {
        return humidityMax;
    }

    public String getDescription() {
        return description;
    }
} // class end
