package com.example.Controllers;

import com.example.Models.Driver;
import com.example.Repositories.BusRepo;
import com.example.Repositories.DriverRepo;
import com.example.Repositories.LineRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by baraa on 4/3/2017.
 */
@RestController
@Transactional
public class DriverController {


    DriverRepo driverRepo;
    BusRepo busRepo;
    LineRepo lineRepo;

    @Autowired
    public void setBusRepo(BusRepo busRepo) {
        this.busRepo = busRepo;
    }

    @Autowired
    public void setDriverRepo(DriverRepo driverRepo) {
        this.driverRepo = driverRepo;
    }

    @Autowired
    public void setLineRepo(LineRepo lineRepo) {
        this.lineRepo = lineRepo;
    }

    @RequestMapping("/createDriver")
    public void createUser(HttpServletResponse response, @ModelAttribute Driver driver) throws IOException {
        if (driver.getDriverEmail() == null) {
            response.sendError(400, "Email not Exist");
            return;
        }
        if (driverRepo.findByDriverEmail(driver.getDriverEmail()) != null) {
            response.sendError(400, "Driver Already Exists");
            return;
        } else {

            if (driver.getBusId() != null && busRepo.findOne(driver.getBusId()) == null) {
                response.sendError(400, "BusId Not Exist");
                return;
            }
            Long id = driverRepo.save(driver).getDriverId();
            if (driver.getBusId() != null) {
                busRepo.setBusDriver(id, driver.getBusId());
            }
            response.sendRedirect("/getDriver?driverId=" + id);
        }
    }

    @RequestMapping(value = "/getDriver", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void getDriver(HttpServletResponse response, @RequestParam("driverId") long id) throws JSONException, IOException {
        Driver driver = driverRepo.findOne(id);
        if (driver == null) {
            response.sendError(400, "Driver Not Exist");
        } else {
            JSONObject driver1 = new JSONObject(driver);
            if (driver.getBusId() != null) {
                JSONObject bus = new JSONObject(busRepo.findOne(driver.getBusId()));
                driver1.put("bus", bus);
                driver1.remove("busId");
                if (!bus.get("lineId").toString().equals("null")) {
                    JSONObject line = new JSONObject(lineRepo.findOne((Long) bus.get("lineId")));
                    bus.put("line", line);
                }
            } else {
                Object obj = null;
                driver1.put("bus", obj);
                driver1.remove("busId");

            }
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter wr = response.getWriter();
            wr.write(driver1.toString());
        }
    }

    @RequestMapping(value = "/getAllDrivers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllDrivers(HttpServletRequest request) throws MalformedURLException, JSONException {
        JSONArray driversArray = new JSONArray();
        for (Driver driver : driverRepo.findAll()) {
            RestTemplate template = new RestTemplate();
            driversArray.put(new JSONObject(template.getForObject(getHost(request) + "/getDriver?driverId=" + driver.getDriverId(), String.class)));
        }
        return driversArray.toString();
    }

    @RequestMapping("/driverLogin")
    public void driverLogin(HttpServletResponse response, @RequestParam("driverEmail") String driverEmail,
                            @RequestParam("driverPassword") String driverPassword) throws IOException {
        Driver driver = driverRepo.findByDriverEmail(driverEmail);
        if (driver == null) {
            response.sendError(400, "driver Not Exist");
        }
        if (driver.getDriverPassword().equals(driverPassword))
            response.sendRedirect("/getDriver?driverId=" + driver.getDriverId());
        else
            response.sendError(400, "Invalid Driver Password");

    }

    @RequestMapping("/removeDriver")
    public void removeBus(@RequestParam Long driverId, HttpServletResponse response) throws IOException {
        Driver driver = driverRepo.findOne(driverId);
        if (driver == null)
            response.sendError(400, "driverId not Exist");
        else {
            if (driver.getBusId() != null)
                busRepo.setBusDriver(null, driver.getBusId());
            driverRepo.delete(driverId);
            response.getWriter().write("Driver Has Been Removed Successfully");
        }
    }


    @RequestMapping("/updateDriver")
    public void updateDriver(HttpServletResponse response, @ModelAttribute Driver driver) throws IOException {
        if (driver.getDriverId() == null) {
            response.sendError(400, "driverId Not Exist");
            return;
        }
        Driver dbDriver = driverRepo.findOne(driver.getDriverId());
        if (dbDriver == null) {
            response.sendError(400, "driverId Not Exist");
            return;
        } else {
            if (driver.getBusId() != null && busRepo.findOne(driver.getBusId()) == null) {
                response.sendError(400, "busId Not Exist");
                return;
            }

            dbDriver.setBusId(driver.getBusId() != null ? driver.getBusId() : dbDriver.getBusId());
            busRepo.setBusDriver(driver.getBusId() != null ? driver.getBusId() : dbDriver.getBusId(), driver.getBusId());

            dbDriver.setDriverPassword(driver.getDriverPassword() != null ? driver.getDriverPassword() : dbDriver.getDriverPassword());
            dbDriver.setDriverName(driver.getDriverName() != null ? driver.getDriverName() : dbDriver.getDriverName());
            dbDriver.setStatus(driver.getStatus() != null ? driver.getStatus() : dbDriver.getStatus());
            dbDriver.setStatusReason(driver.getStatusReason() != null ? driver.getStatusReason() : dbDriver.getStatusReason());

            driverRepo.save(dbDriver);
            response.sendRedirect("/getDriver?driverId=" + dbDriver.getDriverId());
        }
    }


    public String getHost(HttpServletRequest request) throws MalformedURLException {
        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        String host = requestURL.getProtocol() + "://" + requestURL.getHost() + port;
        return host;
    }

}
