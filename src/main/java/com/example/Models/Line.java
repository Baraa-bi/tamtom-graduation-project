package com.example.Models;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baraa on 2/15/2017.
 */

@Entity
public class Line {

    @Id
    @GeneratedValue
    Long lineId;
    String lineStart;
    String lineEnd;


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
