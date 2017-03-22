package com.example.Models;


import javax.persistence.*;
import java.util.List;

/**
 * Created by baraa on 2/15/2017.
 */

@Entity
public class Bus {

    @Id
    @GeneratedValue
    Long busId;
    String image;
    String status;
    String currentLocation;
    long lineId;
    long driverId;

public Bus(){}
    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }
}
