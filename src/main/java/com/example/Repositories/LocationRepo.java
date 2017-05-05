package com.example.Repositories;

import com.example.Models.Location;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by baraa on 4/3/2017.
 */

public interface LocationRepo extends CrudRepository<Location,Long>{
    Iterable<Location> findByUserId(Long id);


}
