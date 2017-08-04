# Predix Edge Starters Data Client
This client serves as a basic Predix data client that can be used to query the Predix Timeseries service. The repo only includes minimal classes. The main dependency is the timeseries-bootstrap package which uses the Predix instances variables set in config/application.properties.

This code can be used for any Timeseries instance, regardless of whether you're using Edge Starters or Grove Sensors.

## Getting Started
If you're working through my Predix Edge Starters series, this client is the 2nd step in creating a full Predix app that ingests and displays asset data. Be sure to go back to any previous tutorials that you have not yet completed.

1. [Predix Edge Starters Machine](https://github.com/futuregarnet/predix-edge-starters-machine.git)
2. **[Predix Edge Starters Data Client](https://github.com/futuregarnet/predix-edge-starters-data-client.git)**
3. [Predix Edge Starters Seed](https://github.com/futuregarnet/predix-edge-starters-seed.git)


### Get the source code
Make a directory for your project.  Clone or download and extract the client in that directory.

```
git clone https://github.com/futuregarnet/predix-edge-starters-data-client.git
cd predix-edge-starters-data-client
```

### Install Tools
If you don't have them already, you'll need Maven on your machine.  

1. Install [maven](https://maven.apache.org/download.cgi). Installation instructions can be found [here](https://maven.apache.org/install.html).

## Local Configuration
The app needs a Timeseries instance, which also requires a UAA instance.

To learn how to create a Predix UAA instance, follow [this guide](https://www.predix.io/resources/tutorials/tutorial-details.html?tutorial_id=1544).

To learn how to create a Predix Timeseries instance, follow [this guide](https://www.predix.io/resources/tutorials/tutorial-details.html?tutorial_id=1549).

### Install Dependancies
This data client requires a customized version of the Predix Timeseries SDK data model which modified parts of [dpqueryrequest.xsd](https://github.com/PredixDev/ext-interface/blob/master/ext-model/src/main/resources/META-INF/schemas/predix/entity/timeseries/dpqueryrequest.xsd) to handle more aggregation types the require a **sampling** aggregator with a datapoint specification. Use the following command to install the required dependency.

```
mvn install:install-file -Dfile=config/ext-model-2.3.2.jar -DgroupId=com.ge.predix.solsvc -DartifactId=ext-model -Dversion=2.3.2 -Dpackaging=jar

```

### Configuring Timeseries Bootstrap

You will need to copy the application properties template to a file the app can locate. Because this file contains sensitive information about your UAA instance, application.properties been added to the .gitignore file and will not be pushed to GitHub during commits.

```
cp config/application-template.properties config/application.properties
```

Edit the config/application.properties file and add the following details:

- **predix.oauth.issuerId.url**: Your UAA Issuer ID (include the /oauth/token endpoint)
- **predix.oauth.clientId**: Your UAA Client ID and UAA Client Secret, separated by a colon and Base64 encoded.
- **predix.timeseries.queryUrl**: Your TimeSeries Query URL
  - Most common: https://time-series-store-predix.run.aws-usw02-pr.ice.predix.io/v1/datapoints
- **predix.timeseries.zoneid**: Your Time Series Zone ID
- **predix.timeseries.websocket.uri**: Your Time Series Web Socket URL
  - Most common: wss://gateway-predix-data-services.run.aws-usw02-pr.ice.predix.io/v1/stream/messages

For the colon separated client credential you'll need to use a Base64 encoder. UNIX/Linux based terminal can use the base64 built in tool.
  
```
echo -n example-client:example-secret | base64
```
Copy the output provided (e.g. ZXhhbXBsZS1jbGllbnQ6ZXhhbXBsZS1zZWNyZXQ=)

## Running the app locally
You can import the app into Spring Tool Suite to run a Spring Boot App or use the following command in the terminal:
```
mvn spring-boot:run
```

then navigate to **http://<i></i>localhost:8080/api/latest/&lt;sensor-name&gt;/&lt;asset-name&gt;/&lt;asset-id&gt;**.

## Deploying to Predix Cloud
You will need to build a distribution version of the app, and deploy it to the Predix.

### Create a distribution version
Use maven to create a distribution version of your app in the target directory. You will need to run this command every time you deploy to the Predix.
```
mvn clean install
```

### Steps
You will also need to copy the manifest template to a file `cf push` can locate. Because this file contains sensitive information about your UAA instance, **manifest.yml** been added to the .gitignore file and will not be pushed to GitHub during commits.

1. Copy the manifest template.

    `cp manifest-template.yml manifest.yml`

2. Edit the manifest.yml file and add the following details:

    - **name**: Replace <your-app-name> with the name you want to use for the Predix app
    - **services**: Replace <your-uaa-instance> and <your-timeseries-instance> with the name of your UAA and Time Series instances, respectively
    - **predix_uaa_name**: Replace <your-uaa-instance> with the name of your UAA instance
    - **predix_timeseries_name**: Replace <your-timeseries-instance> with the name of your Time Series instance
    - **predix_oauth_clientId**: Replace <uaa-client-id>:<uaa-client-secret> with your UAA Client ID and UAA Client Secret, separated by a colon and Base64 encoded.

3. Push to the cloud.

    ```
    cf push
    ```

4. Access the cloud deployment of your Seed application
  Access your application by loading the **API Endpoint** above in your web browser.

## TODO List
- [x] Create initial working release (1.0.0)
- [ ] Integrate Java-based testing framework
- [ ] Integrate server side logging conventions
- [x] Expand documention on README.md