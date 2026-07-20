import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RawSensorData {
    double temperature;
    double humidity;
    int colonyId;

    public RawSensorData(double temperature, double humidity, int colonyId){
        this.temperature = temperature;
        this.humidity = humidity;
        // temporarily hard coding for testing purposes - need to change this later on when i've established my
        // active colony tracking
        this.colonyId = 1;
    }
    public void rawSensorReadingToDb(Connection conn) throws SQLException {
        String insertReading ="INSERT INTO sensor_reading(colony_id, temperature, humidity) VALUES (?, ?, ?)";
        // Need to create an preparedStatement
        try(PreparedStatement addSensorReading = conn.prepareStatement(insertReading)){
            addSensorReading.setInt(1, colonyId);
            addSensorReading.setDouble(2, temperature);
            addSensorReading.setDouble(3, humidity);
            addSensorReading.executeUpdate();
        }
    }

    public int getColonyId() {
        return colonyId;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setColonyId(int colonyId) {
        this.colonyId = colonyId;
    }
}
