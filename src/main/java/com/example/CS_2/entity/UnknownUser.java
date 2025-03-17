package com.example.CS_2.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "unknown_users")
public class UnknownUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "logtime")
    private LocalDateTime logtime;

    public UnknownUser() {}

    public UnknownUser(String username, LocalDateTime logtime) {
        this.username = username;
        this.logtime = logtime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public LocalDateTime getLogtime() {
        return logtime;
    }

    public void setLogtime(LocalDateTime logtime) {
        this.logtime = logtime;
    }

    @Override
    public String toString() {
        return "UnknownUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", logtime=" + logtime +
                '}';
    }
}
