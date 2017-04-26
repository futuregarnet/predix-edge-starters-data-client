package com.ge.predix.solsvc.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Filters;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.util.map.Map;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;

@RestController
@RequestMapping("/api")
public class DataClientController {
    @Autowired
    protected TimeseriesClient timeseriesClient;
    
    @Autowired
    private ObjectMapper jsonMapper;
    
    @RequestMapping(value = "/latest/{sensorName}/{assetType}/{id}", method = RequestMethod.GET)
    public String getLatestDatapoints(@PathVariable String sensorName, @PathVariable String assetType, @PathVariable String id) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag> tags = new ArrayList<>();
        
        tag.setName(String.join(":", assetType, id, sensorName));
        
        tags.add(tag);
        
        DatapointsLatestQuery datapointsLatestQuery = new DatapointsLatestQuery();
        datapointsLatestQuery.setTags(tags);
        
        List<Header> headers = timeseriesClient.getTimeseriesHeaders();
        DatapointsResponse datapointsResponse = timeseriesClient.queryForLatestDatapoint(datapointsLatestQuery, headers);

        return jsonMapper.writeValueAsString(datapointsResponse);
    }
    
    @RequestMapping(value = "/limit_{limit}/{sensorName}/{assetType}/{id}", method = RequestMethod.GET)
    public String getLimitedDatapoints(@PathVariable int limit, @PathVariable String sensorName, @PathVariable String assetType, @PathVariable String id) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();
        
        tag.setName(String.join(":", assetType, id, sensorName));
        tag.setLimit(limit);
        tag.setOrder("desc");
        
        tags.add(tag);
        
        DatapointsQuery datapointsQuery = new DatapointsQuery();
        datapointsQuery.setStart("1y-ago");
        datapointsQuery.setTags(tags);
        
        List<Header> headers = timeseriesClient.getTimeseriesHeaders();
        DatapointsResponse datapointsResponse = timeseriesClient.queryForDatapoints(datapointsQuery, headers);
        
        return jsonMapper.writeValueAsString(datapointsResponse);
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/day/{day}/{sensorName}/{assetType}/{id}", method = RequestMethod.GET)
    public String getSingleDay(@PathVariable String day, @PathVariable String sensorName, @PathVariable String assetType, @PathVariable String id) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();

        Map attribute = new Map();
        attribute.put("datatype", "FLOAT");
        Filters attrFilters = new Filters();
        attrFilters.setAttributes(attribute);
        tag.setFilters(attrFilters);
        
        tags.add(tag);
        tag.setName(String.join(":", assetType, id, sensorName));
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
            return jsonMapper.writeValueAsString("ERROR: Unparsable date. Date must be in yyyy-MM-dd format.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/week/{day}/{sensorName}/{assetType}/{id}", method = RequestMethod.GET)
    public String getFullWeek(@PathVariable String day, @PathVariable String sensorName, @PathVariable String assetType, @PathVariable String id) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();

        Map attribute = new Map();
        attribute.put("datatype", "FLOAT");
        Filters attrFilters = new Filters();
        attrFilters.setAttributes(attribute);
        tag.setFilters(attrFilters);
        
        tags.add(tag);
        tag.setName(String.join(":", assetType, id, sensorName));
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
        }
    }
}
