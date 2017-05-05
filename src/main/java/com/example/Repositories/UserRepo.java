package com.example.Repositories;

import com.example.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by baraa on 4/3/2017.
 */
public interface UserRepo extends CrudRepository<User,Long> {

    User findByUserEmail(String emailAddress);


    @Query("select u from users u where u.userId in(select l.userId from Location l where l.userId in(?1) and l.days like %?2% and l.time in(?3))")
    Iterable<User> search(List<Long> userids, String days, String time);

    @Query("select u from users u where u.userId in(select l.userId from Location l where l.userId in(?1) and l.days like %?2%)")
    Iterable<User> search(List<Long> userids, String days);


}
