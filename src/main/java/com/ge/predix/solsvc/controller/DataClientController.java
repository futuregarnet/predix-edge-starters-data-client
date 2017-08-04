package com.ge.predix.solsvc.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;

@RestController
@RequestMapping("/api")
public class DataClientController {
    @Autowired
    protected TimeseriesClient timeseriesClient;
    
    @Autowired
    private ObjectMapper jsonMapper;
    
    final static String unsuccessfulQueryError = "Query was unsuccessful.";
    
    @RequestMapping(value = "/latest/{sensorName}/{appName}", method = RequestMethod.GET)
    public String getLatestDatapoints(@PathVariable String sensorName, @PathVariable String appName) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag> tags = new ArrayList<>();
        
        tag.setName(String.join(":", sensorName, appName));
        
        tags.add(tag);
        
        DatapointsLatestQuery datapointsLatestQuery = new DatapointsLatestQuery();
        datapointsLatestQuery.setTags(tags);
        
        List<Header> headers = timeseriesClient.getTimeseriesHeaders();
        try {
            DatapointsResponse datapointsResponse = timeseriesClient.queryForLatestDatapoint(datapointsLatestQuery, headers);
            return jsonMapper.writeValueAsString(datapointsResponse);
        } catch (RuntimeException e) {
            return unsuccessfulQueryError;
        }
        
    }
    
    @RequestMapping(value = "/limit_{limit}/{sensorName}/{appName}", method = RequestMethod.GET)
    public String getLimitedDatapoints(@PathVariable int limit, @PathVariable String sensorName, @PathVariable String appName) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();
        
        tag.setName(String.join(":", sensorName, appName));
        tag.setLimit(limit);
        tag.setOrder("desc");
        
        tags.add(tag);
        
        DatapointsQuery datapointsQuery = new DatapointsQuery();
        datapointsQuery.setStart("1y-ago");
        datapointsQuery.setTags(tags);
        
        List<Header> headers = timeseriesClient.getTimeseriesHeaders();
        try {
            DatapointsResponse datapointsResponse = timeseriesClient.queryForDatapoints(datapointsQuery, headers);
            return jsonMapper.writeValueAsString(datapointsResponse);
        } catch (RuntimeException e) {
            return unsuccessfulQueryError;
        }
    }
    
    @RequestMapping(value = "/aggregation/{aggregationType}/{sensorName}/{appName}", method = RequestMethod.GET)
    public String getSingleAggregateDatapoint(@PathVariable String aggregationType, @PathVariable String sensorName, @PathVariable String appName)
            throws JsonProcessingException {
        final String[] validAggregationType = new String[] {"avg", "count", "dev", "min", "max", "sum"};
        final String typeError = "ERROR: Invalid Aggregation Type. Use one of the following as an aggregation type: " + Arrays.toString(validAggregationType);
        
        if (!Arrays.asList(validAggregationType).contains(aggregationType)) {
            return typeError;
        }
        
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Sampling sampling = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Sampling();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();
        Aggregation aggregation = new Aggregation();
        ArrayList<Aggregation> aggregations = new ArrayList<Aggregation>();
        
        sampling.setDatapoints(1);
        aggregation.setType(aggregationType);
        aggregation.setSampling(sampling);
        aggregations.add(aggregation);
        
        tag.setName(String.join(":", sensorName, appName));
        tag.setAggregations(aggregations);
        tags.add(tag);
        
        DatapointsQuery datapointsQuery = new DatapointsQuery();
        datapointsQuery.setStart(0);
        datapointsQuery.setTags(tags);
        
        List<Header> headers = timeseriesClient.getTimeseriesHeaders();
        try {
            DatapointsResponse datapointsResponse = timeseriesClient.queryForDatapoints(datapointsQuery, headers);
            return jsonMapper.writeValueAsString(datapointsResponse);
        } catch (RuntimeException e) {
            return unsuccessfulQueryError;
        }
    }
    
    @RequestMapping(value = "/day/{day}/{sensorName}/{appName}", method = RequestMethod.GET)
    public String getSingleDay(@PathVariable String day, @PathVariable String sensorName, @PathVariable String appName) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();
        
        tags.add(tag);
        tag.setName(String.join(":", sensorName, appName));
        tag.setOrder("desc");
       
        DatapointsQuery datapointsQuery = new DatapointsQuery();
        
        Calendar date = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date.setTime(dayFormat.parse(day));
            long epoch = date.getTimeInMillis();
            
            datapointsQuery.setStart(epoch);
            datapointsQuery.setEnd(epoch + 86399999); // Stop at last millisecond of the day
            datapointsQuery.setTags(tags);
            
            List<Header> headers = timeseriesClient.getTimeseriesHeaders();
            DatapointsResponse datapointsResponse = timeseriesClient.queryForDatapoints(datapointsQuery, headers);
            
            return jsonMapper.writeValueAsString(datapointsResponse);
        } catch (ParseException e) {
            return "ERROR: Unparsable date. Date must be in yyyy-MM-dd format.";
        } catch (RuntimeException e) {
            return unsuccessfulQueryError;
        }
    }
    
    @RequestMapping(value = "/week/{day}/{sensorName}/{appName}", method = RequestMethod.GET)
    public String getFullWeek(@PathVariable String day, @PathVariable String sensorName, @PathVariable String appName) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();

        tags.add(tag);
        tag.setName(String.join(":", sensorName, appName));
        tag.setOrder("desc");
       
        DatapointsQuery datapointsQuery = new DatapointsQuery();
        
        Calendar date = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date.setTime(dayFormat.parse(day));
            long epoch = date.getTimeInMillis();
            
            datapointsQuery.setStart(epoch - 518400000); // Start at first millisecond 6 days ago
            datapointsQuery.setEnd(epoch + 86399999); // Stop at last millisecond of the day
            datapointsQuery.setTags(tags);
            
            List<Header> headers = timeseriesClient.getTimeseriesHeaders();
            DatapointsResponse datapointsResponse = timeseriesClient.queryForDatapoints(datapointsQuery, headers);
            
            return jsonMapper.writeValueAsString(datapointsResponse);
        } catch (ParseException e) {
            return jsonMapper.writeValueAsString("ERROR: Unparsable date. Date must be in yyyy-MM-dd format.");
        } catch (RuntimeException e) {
            return unsuccessfulQueryError;
        }
    }
}
