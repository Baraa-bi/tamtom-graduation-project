package com.example.Models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by baraa on 2/15/2017.
 */

@Entity
public class Line {

    @Id
    @GeneratedValue
    Long lineId;
    String lineName;
    String lineStart;
    String lineEnd;
    String status;
    String listOfBuses;

    public String getStatus() {
        return status;
    }

    public String getListOfBuses() {
        return listOfBuses;
    }

    public void setListOfBuses(String listOfBuses) {
        this.listOfBuses = listOfBuses;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Line() {
    }

    public Line(String lineStart, String lineEnd, String wayPoints) {
        this.lineId = lineId;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public String getLineStart() {
        return lineStart;
    }

    public void setLineStart(String lineStart) {
        this.lineStart = lineStart;
    }

    public String getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }

}
