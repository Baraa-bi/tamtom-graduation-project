package com.example.Controllers;

import com.example.Models.Bus;
import com.example.Models.Driver;
import com.example.Models.LatLng;
import com.example.Models.Line;
import com.example.Repositories.BusRepo;
import com.example.Repositories.DriverRepo;
import com.example.Repositories.LineRepo;
import com.example.Repositories.WayPointsRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by baraa on 2/15/2017.
 */

@Controller
public class Controller1 {

    private BusRepo busRepo;
    private LineRepo lineRepo;
    private DriverRepo driverRepo;
    private WayPointsRepo wayPointsRepo;





    @RequestMapping("/createLine")
    public String createLine(){
        return "newLine";
    }

    @RequestMapping("/createDriver")
    public String createDriver(){
        return "newDriver";
    }
    @RequestMapping(value = "/createDriver",method = RequestMethod.POST)
    public String createDriver1(@ModelAttribute Driver driver){
        return "redirect:/getDriver?id="+driverRepo.save(driver).getDriverId();
    }
    @RequestMapping("/getDriver")
    @ResponseBody
    public Driver createDriver(@RequestParam("id")long id){
        return driverRepo.findOne(id);
    }


    @RequestMapping(value = "/createLine",method = RequestMethod.POST)
    public String createLine1(@ModelAttribute Line line , @RequestParam("wayPoints")String wayPoints)
    {

        lineRepo.save(line);
        String waypoints [] = wayPoints.split(",");
        if(waypoints.length%2!=0)return "not valid way points";
        else
        {
            Line line1 = lineRepo.save(line);
            for (int i = 0; i < waypoints.length; i+=2) {
                wayPointsRepo.save(new LatLng(Double.parseDouble(waypoints[i]),Double.parseDouble(waypoints[i+1]),line1.getLineId()));
            }
        return "redirect:/getLine?id="+line1.getLineId();
        }

    }

    @RequestMapping("/createBus")
    public String createBus(){
        return "newBus";
    }
    @RequestMapping(value = "/createBus",method = RequestMethod.POST)
    public String createBus1(@ModelAttribute Bus bus){
        if(lineRepo.findOne(bus.getLineId())==null||driverRepo.findOne(bus.getDriverId())==null)
        return "oh oh there is no such line or driver with these ids... please try again";
        return "redirect:/getBus?id="+busRepo.save(bus).getBusId();
    }

    @RequestMapping(value = "/getBus",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String createBus1(@RequestParam("id")Long id) throws JSONException {
        Bus bus = busRepo.findOne(id);
        if(bus==null)
            return null;
        else
        {
            JSONObject busObject = new JSONObject(busRepo.findOne(id));
            JSONArray lineWayPoints = new JSONArray();
            JSONObject lineObject = new JSONObject().put("lineId",bus.getLineId());
            Line line = lineRepo.findOne(bus.getLineId());
            String startpoint [] = line.getLineStart().split(",");
            String endpoint [] = line.getLineEnd().split(",");JSONObject startObject = new JSONObject();
            startObject.put("longitude",Double.parseDouble(startpoint[0]));
            startObject.put("latitude",Double.parseDouble(startpoint[1]));
            lineObject.put("startPoint",startObject);
            JSONObject endObject = new JSONObject();
            endObject.put("longitude",Double.parseDouble(endpoint[0]));
            endObject.put("latitude",Double.parseDouble(endpoint[1]));
            lineObject.put("EndPoint",endObject);
            JSONObject driverObject = new JSONObject(driverRepo.findOne(bus.getDriverId()));
            busObject.put("driver",driverObject);
            for(LatLng wayPoints : (wayPointsRepo.findByLineId(bus.getLineId()))) {
                JSONObject object1 = new JSONObject();
                object1.put("longitude",wayPoints.getLongitude());
                object1.put("latitude",wayPoints.getLatitude());
                lineWayPoints.put(object1);
            }
            lineObject.put("LatLng",lineWayPoints);
            busObject.put("line",lineObject);
            busObject.remove("driverId");
            busObject.remove("lineId");
            return busObject.toString();
        }
    }




    @RequestMapping(value = "/getLine",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getLine(@RequestParam("id") long id) throws JSONException {
        Line line = lineRepo.findOne(id);
        JSONArray lineWayPoints = new JSONArray();
        JSONObject lineObject = new JSONObject().put("lineId",line.getLineId());
        String startpoint [] = line.getLineStart().split(",");
        String endpoint [] = line.getLineEnd().split(",");
        JSONObject startObject = new JSONObject();
        startObject.put("longitude",Double.parseDouble(startpoint[0]));
        startObject.put("latitude",Double.parseDouble(startpoint[1]));
        lineObject.put("startPoint",startObject);
        JSONObject endObject = new JSONObject();
        endObject.put("longitude",Double.parseDouble(endpoint[0]));
        endObject.put("latitude",Double.parseDouble(endpoint[1]));
        lineObject.put("EndPoint",endObject);
        for(LatLng wayPoints : (wayPointsRepo.findByLineId(line.getLineId()))) {
            JSONObject object1 = new JSONObject();
            object1.put("longitude",wayPoints.getLongitude());
            object1.put("latitude",wayPoints.getLatitude());
            lineWayPoints.put(object1);
        }
        lineObject.put("LatLng",lineWayPoints);

        return lineObject.toString();
    }

    @RequestMapping(value = "/getAllBuses",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAllBuses() throws JSONException {
        JSONArray allBuses = new JSONArray();
        for (Bus bus : busRepo.findAll())
        {
            JSONObject busObject = new JSONObject(busRepo.findOne(bus.getBusId()));
            JSONArray lineWayPoints = new JSONArray();
            JSONObject lineObject = new JSONObject().put("lineId",bus.getLineId());
            Line line = lineRepo.findOne(bus.getLineId());
            String startpoint [] = line.getLineStart().split(",");
            String endpoint [] = line.getLineEnd().split(",");
            JSONObject startObject = new JSONObject();
            startObject.put("longitude",Double.parseDouble(startpoint[0]));
            startObject.put("latitude",Double.parseDouble(startpoint[1]));
            lineObject.put("startPoint",startObject);
            JSONObject endObject = new JSONObject();
            endObject.put("longitude",Double.parseDouble(endpoint[0]));
            endObject.put("latitude",Double.parseDouble(endpoint[1]));
            lineObject.put("EndPoint",endObject);
            JSONObject driverObject = new JSONObject(driverRepo.findOne(bus.getDriverId()));
            busObject.put("driver",driverObject);
            for(LatLng wayPoints : (wayPointsRepo.findByLineId(bus.getLineId()))) {
                JSONObject object1 = new JSONObject();
                object1.put("longitude",wayPoints.getLongitude());
                object1.put("latitude",wayPoints.getLatitude());
                lineWayPoints.put(object1);
            }
            lineObject.put("LatLng",lineWayPoints);
            busObject.put("line",lineObject);
            busObject.remove("driverId");
            busObject.remove("lineId");
            allBuses.put(busObject);
        }
        return allBuses.toString();
    }


    @RequestMapping(value = "/getAllLines",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAllLines() throws JSONException {
        JSONArray allLines = new JSONArray();
        for (Line line : lineRepo.findAll())
        {
            JSONArray lineWayPoints = new JSONArray();
            JSONObject lineObject = new JSONObject().put("lineId",line.getLineId());
            String startpoint [] = line.getLineStart().split(",");
            String endpoint [] = line.getLineEnd().split(",");JSONObject startObject = new JSONObject();
            startObject.put("longitude",Double.parseDouble(startpoint[0]));
            startObject.put("latitude",Double.parseDouble(startpoint[1]));
            lineObject.put("startPoint",startObject);
            JSONObject endObject = new JSONObject();
            endObject.put("longitude",Double.parseDouble(endpoint[0]));
            endObject.put("latitude",Double.parseDouble(endpoint[1]));
            lineObject.put("EndPoint",endObject);
            for(LatLng wayPoints : (wayPointsRepo.findByLineId(line.getLineId()))) {
                JSONObject object1 = new JSONObject();
                object1.put("longitude",wayPoints.getLongitude());
                object1.put("latitude",wayPoints.getLatitude());
                lineWayPoints.put(object1);
            }
            lineObject.put("LatLng",lineWayPoints);
            allLines.put(lineObject);
        }
        return allLines.toString();
    }

    @RequestMapping("getAllDrivers")
    @ResponseBody
    public Iterable<Driver> getAllDrivers()
    {
        return driverRepo.findAll();
    }

    @Autowired
    public void setBusRepo(BusRepo busRepo) {
        this.busRepo = busRepo;
    }

    @Autowired
    public void setLineRepo(LineRepo lineRepo) {
        this.lineRepo = lineRepo;
    }

    @Autowired
    public void setDriverRepo(DriverRepo driverRepo) {
        this.driverRepo = driverRepo;
    }

    @Autowired
    public void setWayPointsRepo(WayPointsRepo wayPointsRepo) {
        this.wayPointsRepo = wayPointsRepo;
    }
}
