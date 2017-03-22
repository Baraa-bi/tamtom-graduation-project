package com.example.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by baraa on 2/28/2017.
 */
@Entity
public class Driver {

    @Id
    @GeneratedValue
    Long driverId;
    String driverName;
    String driverEmail;

    public Long getDriverId() {
        return driverId;
    }

    public Driver(){}
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }
}
