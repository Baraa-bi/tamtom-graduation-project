package com.example.Repositories;

import com.example.Models.Bus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by baraa on 2/15/2017.
 */
public interface BusRepo extends CrudRepository<Bus,Long> {


    @Modifying
    @Query("update Bus b set b.driverId = ?1 where b.id = ?2")
    void setBusDriver(Long driverId, Long busId);


    @Modifying
    @Query("update Bus b set b.lineId = ?1 where b.lineId = ?2")
    void setBusLine(Long lineId, Long lineId2);


    @Modifying
    @Query("update Bus b set b.status = ?1 where b.id = ?2")
    void setBusStatus(String status, Long busId);


}
