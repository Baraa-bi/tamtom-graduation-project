package com.example.Controllers;

import com.example.Models.LatLng;
import com.example.Models.Line;
import com.example.Models.User;
import com.example.Repositories.BusRepo;
import com.example.Repositories.LineRepo;
import com.example.Repositories.UserRepo;
import com.example.Repositories.WayPointsRepo;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by baraa on 4/3/2017.
 */

@RestController
@Transactional
public class LineController {

    LineRepo lineRepo;
    BusRepo busRepo;
    UserRepo userRepo;
    WayPointsRepo wayPointsRepo;

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


    @RequestMapping(value = "/createLine")
    public void createLine(HttpServletResponse response,@ModelAttribute Line line ,
                              @RequestParam("wayPoints")String wayPoints,
                              @RequestParam(value = "stopWayPoints",required = false)String stopWayPoints) throws IOException {
        String waypoints[] = wayPoints.split(",");
        if (waypoints.length % 2 != 0) {
            response.sendError(400, "invalid way points");
            return;
        }

        Line line1 = lineRepo.save(line);
        for (int i = 0; i < waypoints.length; i += 2) {
            wayPointsRepo.save(new LatLng(Double.parseDouble(waypoints[i]), Double.parseDouble(waypoints[i + 1]), line1.getLineId(), false));
        }

        if (stopWayPoints != null) {
            String stopwayPoints[] = stopWayPoints.split(",");
            if(stopwayPoints.length%2!=0)
            {
                response.sendError(400,"invalid stop way points");
                return;
            }
            for (int i = 0; i < stopwayPoints.length; i += 2) {
                wayPointsRepo.save(new LatLng(Double.parseDouble(waypoints[i]), Double.parseDouble(waypoints[i + 1]), line1.getLineId(), true));
            }
        }
        response.sendRedirect("/getLine?lineId=" + line1.getLineId());
    }


    @RequestMapping(value = "/getLine",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void getLine(@RequestParam("lineId") long id,HttpServletRequest request,HttpServletResponse response) throws JSONException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        Line line = lineRepo.findOne(id);
        if(line==null)
        {
            response.sendError(400,"lineId Not Exist");
            return;
        }
        JSONObject lineObject = new JSONObject().put("lineId",line.getLineId());
        lineObject.put("lineName",line.getLineName()==null?"":line.getLineName());
        lineObject.put("lineStatus",line.getStatus()==null?"":line.getStatus());
        if(line.getLineStart()!=null) {
            String startpoint[] = line.getLineStart().split(",");
            JSONObject startObject = new JSONObject();
            startObject.put("latitude", Double.parseDouble(startpoint[0]));
            startObject.put("longitude", Double.parseDouble(startpoint[1]));
            lineObject.put("startPoint", startObject);
        }
        if(line.getLineEnd()!=null) {
            String endpoint[] = line.getLineEnd().split(",");
            JSONObject endObject = new JSONObject();
            endObject.put("latitude", Double.parseDouble(endpoint[0]));
            endObject.put("longitude", Double.parseDouble(endpoint[1]));
            lineObject.put("EndPoint", endObject);
        }
        JSONArray listOfBuses = new JSONArray();
        if(line.getListOfBuses()!=null) {
            for (String busId : new HashSet<String>(Arrays.asList(line.getListOfBuses().split(",")))) {
                listOfBuses.put(new JSONObject(busRepo.findOne(Long.parseLong(busId))));
            }
            lineObject.put("listOfBuses", listOfBuses);
            lineObject.put("numberOfBuses", listOfBuses.length());
        }
        JSONArray lineWayPoints = new JSONArray();
        JSONArray lineWayStopPoints = new JSONArray();
        if(wayPointsRepo.findByLineId(line.getLineId())!=null) {
            for (LatLng wayPoints : (wayPointsRepo.findByLineId(line.getLineId()))) {
                JSONObject object1 = new JSONObject();
                object1.put("longitude", wayPoints.getLongitude());
                object1.put("latitude", wayPoints.getLatitude());
                if (wayPoints.isStop())
                    lineWayStopPoints.put(object1);
                else
                    lineWayPoints.put(object1);
            }
        }
        lineObject.put("LatLng",lineWayPoints);
        lineObject.put("LatLngStops",lineWayStopPoints);
        response.getWriter().write(lineObject.toString());
    }



    @RequestMapping(value = "/getAllLines",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllLines(HttpServletRequest request ,HttpServletResponse response, @RequestParam(required = false) Long userId) throws JSONException, IOException {
        JSONArray linesArray = new JSONArray();
        if(userId==null)
        for (Line line : lineRepo.findAll()) {
            RestTemplate template = new RestTemplate();
            linesArray.put(new JSONObject(template.getForObject(getHost(request)+"/getLine?lineId="+line.getLineId(),String.class)));
        }
        else {
            User user =userRepo.findOne(userId);
            if(user==null){response.sendError(400,"user not exist");return "";}
            for (Line line : lineRepo.findAll()) {
                RestTemplate template = new RestTemplate();
                linesArray.put(new JSONObject(template.getForObject(getHost(request)+"/getLine?lineId="+line.getLineId(),String.class))
                        .put("isFavorite",(user.getFavoriteIds()==null?"false":user.getFavoriteIds().contains(""+line.getLineId())?"true":"false")));
            }
        }
        return linesArray.toString();
    }

    @RequestMapping("/removeBusFromLine")
    public void removeBusFromLine(HttpServletResponse response,@RequestParam("lineId")Long lineId,@RequestParam("busId")Long busId) throws IOException {
        if(busRepo.findOne(busId)!=null)
        {
            Line line = lineRepo.findOne(lineId);
            if(line==null)
            {
                response.sendError(400,"lineId not Exist");
                return;
            }
            ArrayList<String> busList = new ArrayList(Arrays.asList(line.getListOfBuses().split(",")));
            busList.remove(busId);
            line.setListOfBuses(String.join(",",busList));
            lineRepo.save(line);
            response.sendRedirect("/getLine?lineId="+lineId);
        }
        else
            response.sendError(400,"busId not Exist");
    }

    @RequestMapping("/addBusToLine")
    public void addBusToLine(HttpServletResponse response,@RequestParam("lineId")Long lineId,@RequestParam("busId")Long busId) throws IOException {
        if(busRepo.findOne(busId)!=null)
        {
            Line line = lineRepo.findOne(lineId);
            if(line==null)
            {
                response.sendError(400,"lineId not Exist");
                return;
            }
            if(line.getListOfBuses()==null)
            {
                line.setListOfBuses(""+busId+",");
            }
            else
            line.setListOfBuses(line.getListOfBuses()+","+busId+",");
            line.setListOfBuses(line.getListOfBuses().substring(0,line.getListOfBuses().length()-1));
            lineRepo.save(line);
            response.sendRedirect("/getLine?lineId="+lineId);
        }
        else
            response.sendError(400,"busId not Exist");
    }

    @RequestMapping("/removeLine")
    public void removeLine(@RequestParam Long lineId,HttpServletResponse response) throws IOException {
        Line line=lineRepo.findOne(lineId);
        if (line==null)
            response.sendError(400,"lineId not Exist");
        else
        {
            busRepo.setBusLine(null,lineId);
            lineRepo.delete(lineId);
            response.getWriter().write("Line Has Been Removed Successfully");
        }
    }

    @RequestMapping("/updateLine")
    public void updateLine(@ModelAttribute Line line,HttpServletResponse response,
                           @RequestParam(value = "wayPoints", required = false)String wayPoints,
                           @RequestParam(value = "stopWayPoints",required = false)String stopWayPoints) throws IOException {
        if(line.getLineId()==null)
        {
            response.sendError(400,"lineId Not Exist");
            return;
        }

        Line dbLine = lineRepo.findOne(line.getLineId());

        if (dbLine==null)
        {
            response.sendError(400,"lineId Not Exist");
            return;
        }
        else{

            if(line.getLineStart()!=null)
            {
                if(line.getLineStart().split(",").length!=2) {
                    response.sendError(400, "Invalid Start Point");
                    return;
                }
                else
                    dbLine.setLineStart(line.getLineStart()!=null?line.getLineStart():dbLine.getLineStart());
            }

            if(line.getLineEnd()!=null)
            {
                if(line.getLineEnd().split(",").length!=2) {
                    response.sendError(400, "Invalid End Point");
                    return;
                }
                else
                    dbLine.setLineEnd(line.getLineEnd()!=null?line.getLineEnd():dbLine.getLineEnd());
            }

            dbLine.setLineName(line.getLineName()!=null?line.getLineName():dbLine.getLineName());
            dbLine.setStatus(line.getStatus()!=null?line.getStatus():dbLine.getStatus());
            dbLine.setListOfBuses(line.getListOfBuses()!=null?line.getListOfBuses():dbLine.getListOfBuses());

            if(wayPoints!=null)
            {
                if(wayPoints.split(",").length%2!=0) {
                    response.sendError(400, "invalid way points");
                    return;
                }
                else
                {
                    String waypoints [] = wayPoints.split(",");
                    wayPointsRepo.deleteByLineIdAndIsStop(line.getLineId(),false);
                    for (int i = 0; i < waypoints.length; i+=2) {
                        wayPointsRepo.save(new LatLng(Double.parseDouble(waypoints[i]),Double.parseDouble(waypoints[i+1]),dbLine.getLineId(),false));
                    }
                }
            }

            if(stopWayPoints!=null)
            {
                if(stopWayPoints.split(",").length%2!=0) {
                    response.sendError(400, "invalid stop way points");
                    return;
                }
                else
                {
                    String stopwayPoints [] = stopWayPoints.split(",");
                    wayPointsRepo.deleteByLineIdAndIsStop(line.getLineId(),true);
                    for (int i = 0; i < stopwayPoints.length; i+=2) {
                        wayPointsRepo.save(new LatLng(Double.parseDouble(stopwayPoints[i]),Double.parseDouble(stopwayPoints[i+1]),dbLine.getLineId(),true));
                    }
                }
            }

            lineRepo.save(dbLine);
            response.sendRedirect("/getLine?lineId="+dbLine.getLineId());
        }
    }

    public String getHost(HttpServletRequest request) throws MalformedURLException {
        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        String host = requestURL.getProtocol() + "://" + requestURL.getHost() + port;
        return host;
    }

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
