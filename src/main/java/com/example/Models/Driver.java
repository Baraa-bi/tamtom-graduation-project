package com.example.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
    String driverPassword;
    @Column(unique = true,nullable = false)
    String driverEmail;
    Long busId;
    String status;
    String statusReason;

    public Long getBusId() {

        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getDriverPassword() {
        return driverPassword;
    }

    public void setDriverPassword(String driverPassword) {
        this.driverPassword = driverPassword;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }
}
