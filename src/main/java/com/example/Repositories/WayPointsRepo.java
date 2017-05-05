package com.example.Repositories;

import com.example.Models.LatLng;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by baraa on 2/28/2017.
 */
public interface WayPointsRepo extends CrudRepository<LatLng,Long> {
    List<LatLng> findByLineId(Long lineId);
    List<LatLng> deleteByLineIdAndIsStop(Long lineId,boolean isStop);

}
