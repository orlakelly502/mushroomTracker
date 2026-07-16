import adafruit_dht
import board
import time
import threading
from flask import Flask, jsonify

dht = adafruit_dht.DHT22(board.D4)

latest_reading = {"temperature": None, "humidity": None}

def read_sensor_loop():

        while True:
                try:
                        temp = dht.temperature
                        humidity = dht.humidity
                        print(f"Temp: {temp}C Humidity: {humidity}%")
                except RuntimeError as e:
                        print(f"Read failed, retrying: {e}")
                time.sleep(10)

app = Flask(__name__)

# Flask listens for GET requests by default so no need to declare it in route
@app.route("/reading")
def get_reading():
            return jsonify(latest_reading)

    if __name__ == "__main__":
            sensor_thread = threading.Thread(target=read_sensor_loop, daemon=True)
            sensor_thread.start()
            app.run(host="0.0.0.0", port=5000)