package com.example.Controllers;

import com.example.Models.Bus;
import com.example.Models.Driver;
import com.example.Repositories.BusRepo;
import com.example.Repositories.DriverRepo;
import com.example.Repositories.LineRepo;
import com.example.Repositories.WayPointsRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by baraa on 4/3/2017.
 */

@RestController
public class BusController {

    BusRepo busRepo;
    LineRepo lineRepo;
    WayPointsRepo wayPointsRepo;
    DriverRepo driverRepo;



    @Autowired
    public void setBusRepo(BusRepo busRepo) {
        this.busRepo = busRepo;
    }

    @Autowired
    public void setLineRepo(LineRepo lineRepo) {
        this.lineRepo = lineRepo;
    }

    @Autowired
    public void setWayPointsRepo(WayPointsRepo wayPointsRepo) {
        this.wayPointsRepo = wayPointsRepo;
    }

    @Autowired
    public void setDriverRepo(DriverRepo driverRepo) {
        this.driverRepo = driverRepo;
    }



    @RequestMapping("/createBus")
    public void createBus (@ModelAttribute Bus bus, HttpServletRequest request,HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        if(bus.getPlateNo()==null)
            response.sendError(400,"PlateNo Required");
        else
        {
            if(bus.getLineId()!=null)
            {
                RestTemplate template = new RestTemplate();
                template.getForObject(getHost(request)+"/addBusToLine?lineId="+bus.getLineId()+"&busId="+bus.getBusId(),String.class);
            }
            response.sendRedirect("/getBus?busId="+busRepo.save(bus).getBusId());
        }
    }


    @RequestMapping(value = "/getAllBuses",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAllBuses(HttpServletRequest request) throws JSONException, MalformedURLException {
        JSONArray usersArray = new JSONArray();
        for (Bus bus : busRepo.findAll()) {
            RestTemplate template = new RestTemplate();
            usersArray.put(new JSONObject(template.getForObject(getHost(request)+"/getBus?busId="+bus.getBusId(),String.class)));
        }
        return usersArray.toString();
    }


    @RequestMapping(value = "/getBus",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void createBus1(HttpServletRequest request,HttpServletResponse response,@RequestParam("busId")Long id) throws JSONException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        Bus bus = busRepo.findOne(id);
        if(bus==null)
        {
            response.sendError(400,"Bus Not Exist");
            return;
        }
        else {
            JSONObject busObject = new JSONObject(bus);
            JSONObject lineObject = new JSONObject();
            if (bus.getLineId() != null)
            {
                RestTemplate template = new RestTemplate();
                lineObject = new JSONObject(template.getForObject(getHost(request)+"/getLine?lineId="+bus.getLineId(),String.class));
                lineObject.remove("listOfBuses");
                busObject.put("line",lineObject);
            }
            if(bus.getDriverId()!=null) {
                Driver driver = driverRepo.findOne(bus.getDriverId());
                if (driver!= null) {
                    JSONObject driverObject = new JSONObject(driver);
                    driverObject.remove("busId");
                    busObject.put("driver", driverObject);
                }
            }
            busObject.remove("driverId");
            busObject.remove("lineId");
            response.getWriter().write(busObject.toString());
        }
    }

    @RequestMapping("removeBus")
    public void removeBus(@RequestParam Long busId,HttpServletRequest request,HttpServletResponse response) throws IOException {
        if (busRepo.findOne(busId)==null)
            response.sendError(400,"busId not Exist");
        else
        {
            if(busRepo.findOne(busId).getLineId()!=null) {
                RestTemplate template = new RestTemplate();
                template.getForObject(getHost(request) + "/removeBusFromLine?busId=" + busId + "&lineId=" + busRepo.findOne(busId).getLineId(), String.class);
            }busRepo.delete(busId);
            response.getWriter().write("Bus Has Been Removed Successfully");
        }
    }


    @RequestMapping("/updateBus")
    public void updateDriver (HttpServletResponse response,HttpServletRequest request,@ModelAttribute Bus bus) throws IOException
    {
        if(bus.getBusId()==null)
        {
            response.sendError(400,"busId Not Exist");
            return;
        }
        Bus dbBus = busRepo.findOne(bus.getBusId());
        if (dbBus==null)
        {
            response.sendError(400,"busId Not Exist");
            return;
        }
        else{
            dbBus.setStatus(bus.getStatus()!=null?bus.getStatus():dbBus.getStatus());
            if(bus.getDriverId()!=null&&driverRepo.findOne(bus.getDriverId())==null)
            {
                response.sendError(400,"driverId Not Exist");
                return;
            }
            dbBus.setDriverId(bus.getDriverId()!=null?bus.getDriverId():dbBus.getDriverId());
            if(bus.getLineId()!=null&&lineRepo.findOne(bus.getLineId())==null)
            {
                response.sendError(400,"lineId Not Exist");
                return;
            }
            if(bus.getLineId()!=null)
            {
                RestTemplate template = new RestTemplate();
                template.getForObject(getHost(request)+"/addBusToLine?lineId="+bus.getLineId()+"&busId="+bus.getBusId(),String.class);
            }
            dbBus.setLineId(bus.getLineId()!=null?bus.getLineId():dbBus.getLineId());
            dbBus.setPlateNo(bus.getPlateNo()!=null?bus.getPlateNo():dbBus.getPlateNo());
            busRepo.save(dbBus);
            response.sendRedirect("/getBus?busId="+dbBus.getBusId());
        }
    }







    public String getHost(HttpServletRequest request) throws MalformedURLException {
        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        String host = requestURL.getProtocol() + "://" + requestURL.getHost() + port;
        return host;
    }


}
