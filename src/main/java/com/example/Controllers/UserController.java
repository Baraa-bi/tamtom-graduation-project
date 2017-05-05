package com.example.Controllers;

import com.example.Models.Location;
import com.example.Models.User;
import com.example.Repositories.LineRepo;
import com.example.Repositories.LocationRepo;
import com.example.Repositories.UserRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

/**
 * Created by baraa on 4/3/2017.
 */
@RestController
public class UserController {

    UserRepo userRepo;
    LocationRepo locationRepo;
    LineRepo lineRepo;

    @Autowired
    public void setLineRepo(LineRepo lineRepo) {
        this.lineRepo = lineRepo;
    }

    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @RequestMapping("/createUser")
    public void createUser(HttpServletResponse response, @ModelAttribute User user) throws IOException {
        if (userRepo.findByUserEmail(user.getUserEmail()) != null)
            response.sendError(400, "Already Exists");
        else
            response.sendRedirect("/getUser?userId=" + userRepo.save(user).getUserId());
    }


    @RequestMapping("/createUserLocation")
    public void createLocation(@ModelAttribute Location location, HttpServletResponse response) throws IOException {
        if (userRepo.findOne(location.getUserId()) == null)
            response.sendError(400, "User Not Exist");
        else
            response.sendRedirect("/getUser?userId=" + locationRepo.save(location).getUserId());
    }

    @RequestMapping("/removeUserLocation")
    public void removeLocation(@RequestParam Long userId,
                               @RequestParam Long locationId,
                               HttpServletResponse response) throws IOException {
        Location location = locationRepo.findOne(locationId);
        if (location == null) {
            response.sendError(400, "Location Not Exist");
            return;
        }
        if (location.getUserId() == userId) {
            response.sendError(400, "User Not Match This Location Id");
            return;
        } else {
            locationRepo.delete(locationId);
            response.sendRedirect("/getUser?userId=" + locationRepo.save(location).getUserId());
        }
    }


    @RequestMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("userId") long id) throws JSONException, IOException {
        User user = userRepo.findOne(id);
        if (user == null) {
            response.sendError(400, "user not Exisit");
            return "";
        }
        JSONArray locations = new JSONArray();
        JSONObject object = new JSONObject(user);
        if (user.getFavoriteIds() != null) {
            JSONArray favIds = new JSONArray();
            for (String lineIds : user.getFavoriteIds().split(",")) {
                Long lineId = Long.parseLong(lineIds);
                RestTemplate template = new RestTemplate();
                favIds.put(new JSONObject(template.getForObject(getHost(request) + "/getLine?lineId=" + lineId, String.class)));
            }
            object.remove("favoriteIds");
            object.put("favoriteLines", favIds);
            object.put("favoriteLinesSize", favIds.length());
        }
        for (Location location : (locationRepo.findByUserId(id))) {
            JSONObject object1 = new JSONObject();
            JSONArray latLng = new JSONArray();
            JSONObject points = new JSONObject();
            points.put("latitude", location.getLatitude());
            points.put("longitude", location.getLongitude());
            latLng.put(points);
            JSONArray days = new JSONArray(location.getDays().split(","));
            object1.put("time", location.getTime());
            object1.put("days", days);
            object1.put("LatLng", latLng);
            locations.put(object1);
        }
        object.put("locations", locations);
        object.put("locationsSize", locations.length());
        return object.toString();
    }

    @RequestMapping(value = "/getAllUsers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllUsers(HttpServletRequest request) throws JSONException, MalformedURLException {
        JSONArray usersArray = new JSONArray();
        for (User user : userRepo.findAll()) {
            RestTemplate template = new RestTemplate();
            usersArray.put(new JSONObject(template.getForObject(getHost(request) + "/getUser?userId=" + user.getUserId(), String.class)));
        }
        return usersArray.toString();
    }

    @RequestMapping(value = "/userLogin")
    public void driverLogin(HttpServletResponse response, @RequestParam("userEmail") String userEmail,
                            @RequestParam("userPassword") String userPassword) throws IOException {
        User user = userRepo.findByUserEmail(userEmail);
        if (user == null) {
            response.sendError(400, "User Not Exist");
            return;
        }
        if (user.getUserPassword().equals(userPassword)) {
            response.sendRedirect("/getUser?userId=" + user.getUserId());
            return;
        } else
            response.sendError(400, "Invalid User Password");
    }


    @RequestMapping("/addUserFavoriteLine")
    public void addBusToLine(HttpServletResponse response,
                             @RequestParam("lineId") Long lineId,
                             @RequestParam("userId") Long userId) throws IOException {
        if (lineRepo.findOne(lineId) != null) {
            User user = userRepo.findOne(userId);
            if (user.getFavoriteIds() == null)
                user.setFavoriteIds(lineId + ",");
            else
                user.setFavoriteIds(user.getFavoriteIds() + lineId + ",");
            userRepo.save(user);
            response.sendRedirect("/getUser?userId=" + userId);
        } else
            response.sendError(400, "Line Not Exist");
    }

    @RequestMapping("/removeUserFavoriteLine")
    public void removeBusToLine(HttpServletResponse response,
                                @RequestParam("lineId") Long lineId,
                                @RequestParam("userId") Long userId) throws IOException {
        if (lineRepo.findOne(lineId) != null) {
            User user = userRepo.findOne(userId);
            if (user.getFavoriteIds().split(",").length == 1)
                user.setFavoriteIds(null);
            else {
                String[] ar = user.getFavoriteIds().split(",");
                String favorite = null;
                for (int i = 0; i < ar.length; i++) {
                    if (String.valueOf(lineId).equals(ar[i]))
                        continue;
                    else {
                        favorite += ar[i] + ",";
                    }
                }
                user.setFavoriteIds(favorite);
            }

            userRepo.save(user);
            response.sendRedirect("/getUser?userId=" + userId);
        } else
            response.sendError(400, "Line Not Exist");
    }

    @RequestMapping("removeUser")
    public void removeUser(@RequestParam Long userId, HttpServletResponse response) throws IOException {
        if (userRepo.findOne(userId) == null)
            response.sendError(400, "userId not Exist");
        else {
            userRepo.delete(userId);
            response.getWriter().write("User Has Been Removed Successfully");
        }
    }

    @RequestMapping("/updateUser")
    public void updateUser(HttpServletResponse response, @ModelAttribute User user) throws IOException {
        if (user.getUserId() == null) {
            response.sendError(400, "userId Not Exist");
            return;
        }
        User dbUser = userRepo.findOne(user.getUserId());
        if (dbUser == null) {
            response.sendError(400, "userId Not Exist");
            return;
        } else {
            dbUser.setUserPassword(user.getUserPassword() != null ? user.getUserPassword() : dbUser.getUserPassword());
            dbUser.setFavoriteIds(user.getFavoriteIds() != null ? user.getFavoriteIds() : dbUser.getFavoriteIds());
            dbUser.setUserEmail(user.getUserEmail() != null ? user.getUserEmail() : dbUser.getUserEmail());
            userRepo.save(dbUser);
            response.sendRedirect("/getUser?userId=" + dbUser.getUserId());
        }
    }


    @RequestMapping(value = "/searchUsers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String searchUsers(HttpServletRequest request, @RequestParam ArrayList<Long> usersIds, @RequestParam String days, @RequestParam(required = false) String time) throws MalformedURLException, JSONException {
        JSONArray usersArray = new JSONArray();
        if (time == null)
            for (User user : userRepo.search(usersIds, days)) {
                RestTemplate template = new RestTemplate();
                usersArray.put(new JSONObject(template.getForObject(getHost(request) + "/getUser?userId=" + user.getUserId(), String.class)));
            }
        else
            for (User user : userRepo.search(usersIds, days, time)) {
                RestTemplate template = new RestTemplate();
                usersArray.put(new JSONObject(template.getForObject(getHost(request) + "/getUser?userId=" + user.getUserId(), String.class)));
            }

        return usersArray.toString();
    }


    public String getHost(HttpServletRequest request) throws MalformedURLException {
        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        String host = requestURL.getProtocol() + "://" + requestURL.getHost() + port;
        return host;
    }
}
