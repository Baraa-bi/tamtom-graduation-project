package com.example.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by baraa on 4/3/2017.
 */

@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue
    Long userId;
    @Column(unique = true,nullable = false)
    String userEmail;
    String userPassword;
    String favoriteIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getFavoriteIds() {
        return favoriteIds;
    }

    public void setFavoriteIds(String favoriteIds) {
        this.favoriteIds = favoriteIds;
    }
}
