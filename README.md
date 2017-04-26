# Predix Data Client
This code is to serve as a basic Predix data client that can be used to query the Predix Timeseries service. The repo only includes minimal classes. The main dependency is the timeseries-bootstrap package which uses the Predix instances variables set in config/application.properties.

## Configuration
```
git clone https://github.com/futuregarnet/predix-data-client.git
cd predix-data-client
mvn clean install
```

Copy the config/application-template.properties to config/application.properties and insert your Predix services information.

Copy the manifest-template.yml to manifest.yml and insert your Predix services information.
**NOTE**: For the predix_oauth_clientId env varible, you will need to Base64 encode your colon separated client-id:client-password.

## TODO List
- [x] Create initial working release
- [ ] Integrate Java-based testing framework
- [ ] Integrate server side logging conventions
- [ ] Expand documention on README.md