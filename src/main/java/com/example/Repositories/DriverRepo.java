package com.example.Repositories;

import com.example.Models.Driver;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by baraa on 2/28/2017.
 */
public interface DriverRepo extends CrudRepository<Driver,Long> {

    Driver findByDriverEmail (String driverEmail);

    @Modifying
    @Query("update Driver d set d.busId = ?1 where d.busId = ?2")
    void setBusLine(Long busId, Long busId2);
}
