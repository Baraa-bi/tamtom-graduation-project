package com.example.Repositories;

import com.example.Models.Line;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by baraa on 2/15/2017.
 */
public interface LineRepo extends CrudRepository<Line,Long> {

    @Query("select l from Line l where l.lineId  in(?1)")
    Iterable<Line> search (List<Long>lineIds);



}
