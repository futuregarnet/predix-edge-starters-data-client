package com.ge.predix.solsvc.controller;

import java.util.ArrayList;
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
    
    @RequestMapping(value = "/latest/{sensorName}/{assetType}/{id}", method = RequestMethod.GET)
    public String getLatestDatapoints(@PathVariable String sensorName, @PathVariable String assetType, @PathVariable String id) throws JsonProcessingException {
        com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag();
        tag.setName(String.join(":", assetType, id, sensorName));
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag> tags = new ArrayList<>();
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
        tag.setName(String.join(":", assetType, id, sensorName));
        tag.setLimit(limit);
        tag.setOrder("desc");
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<>();
        tags.add(tag);
        
        DatapointsQuery datapointsQuery = new DatapointsQuery();
        datapointsQuery.setStart("1y-ago");
        datapointsQuery.setTags(tags);
        
        List<Header> headers = timeseriesClient.getTimeseriesHeaders();
        DatapointsResponse datapointsResponse = timeseriesClient.queryForDatapoints(datapointsQuery, headers);
        
        return jsonMapper.writeValueAsString(datapointsResponse);
    }
}
