# MushroomTracker
A mixed language project that collects live sensor data, writing it to a MySQL database & visualising it in a browser using Grafana. Allowing the easy tracking of key environmental metrics which can impact the yield & growth speed of mushrooms.
The main goal of creating this was to experiment with creating a connected system.
![Demo of MushroomTracker](demo.gif)
## How it Works
The sensor data is collected via a DHT sensor connected to Raspberry Pi. The Pi runs a Python script which collects the sensor data & translates it into primitive values using the Adafruit library.
A Flask route listens for GET requests, responding to them with a JSONified version of the latest sensor reading.
Meanwhile a Java application utilising the HTTPClient library sends GET requests to the Pi & validates the returned data before writing it to the MySQL database - this happens automatically once an Active Colony has been created & set.
The Java application has a simple console based menu from which the user can SET an active colony (all sensor readings are associated with the current Active Colony via a FK link), CREATE a new colony, open the Grafana dashboard or exit the program.
The Grafana visualisations are based on the current Active Colony only - changing the colour of humidity/temperature displays (red representing too high, blue too low & green healthy) to show whether they are in a healthy range.
Alerts have also been set up to fire via a Discord webhook if temperature or humidity fall out of a healthy range (with a restriction in frequency) so you don't have to be currently viewing the dashboard to be made aware of an environmental issue that could affect the overall health and growth of the colony.
## Limitations
- Currently only works with a single active colony & sensor.
- Grafana has manual thresholds and does not work from the min/max ranges located in the mushroomTypes table (these differ depending on the mushroom type being grown) - requires new different dashboards for each different type which is a bit unwieldy.
- Loading of pre-existing colonies into a collection of hydrated objects is unnecessary & doesn't scale well as the dataset grows over time.
- Many of the lookup methods work unnecessarily with hydrated objects instead of directly utilising queries, adding a 'middle man' with no benefit - a hangover from prior experience working with flat files instead of DBs; a more conservative approach to ORM would have simplified things.
- Single point of failure - I did not create a connection pool, the DB interactions all work from a single connection with no back up.

## In Progress
- Still to add in flush creation & update methods so users can add in flush information for their current colony when they have a harvest. Flush size (weight) coupled with the environmental health data would be helpful in identifying the impact of environment on outcomes.

## What I Learned
- Creating a functioning system that connects multiple devices (Pi, PC, DB & Grafana).
- Multi-threading, managing concurrency (use of volatile).
- Sending HTTP requests & responding to them, processing responses.
- Connecting to a DB using JDBC, use of PreparedStatements & hydrating objects from result sets.
- ORM not necessary where data is not being manipulated - DBs are incredibly efficient at filtering and sorting data; hydrating collections of objects & storing them in application memory for these purposes creates unnecessary overhead.
