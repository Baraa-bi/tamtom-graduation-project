package com.example.Models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by baraa on 2/15/2017.
 */

@Entity
public class Bus {

    @Id
    @GeneratedValue
    Long busId;
    @Column(unique = true,nullable = false)
    String plateNo;
    Long lineId;
    Long driverId;
    String status;



    public Bus(String plateNo, Long lineId, Long driverId, String status) {
        this.plateNo = plateNo;
        this.lineId = lineId;
        this.driverId = driverId;
        this.status = status;
    }
    public Bus(){}

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }


    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }
}
