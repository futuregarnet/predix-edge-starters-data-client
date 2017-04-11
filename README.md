# Predix Data Client
This code is to serve as a basic Predix data client that can be used to query the Predix Timeseries service. The repo only includes minimal classes. The main dependency is the timeseries-bootstrap package which uses the Predix instances variables set in config/application.properties.

## Configuration
```
git clone https://github.com/futuregarnet/predix-data-client.git
cd predix-data-client
mvn clean install
```

Copy the config/application-template.properties to config/application.properties and insert your Predix services information.
