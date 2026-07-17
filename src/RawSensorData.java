public class RawSensorData {
    double temperature;
    double humidity;
    int colonyId;

    public RawSensorData(double temperature, double humidity, int colonyId){
        this.temperature = temperature;
        this.humidity = humidity;
        this.colonyId = colonyId;
    }
}
