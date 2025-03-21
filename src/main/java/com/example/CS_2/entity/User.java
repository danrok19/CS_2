package com.example.CS_2.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // identyfikator

    @Column(name = "username")
    private String username; // nazwa uzytkownika

    @Column(name = "password")
    private String password; // haslo

    private boolean locked = false; // czy konto ma temp zablokowane logowanie

    private int failedAttempts = 0; // liczba ostatnich nieudanych logowan z kolei

    private LocalDateTime lockTime = LocalDateTime.now(); // czas do kiedy jest zablokowane logowanie uzytkownika

    private LocalDateTime lastSuccessfulLogin; // ostatnie udane logowanie

    private LocalDateTime lastFailedLogin; // ostatnie nieudane logowanie

    private boolean lockAccount = false; // czy uzytkownik wybral system blokowania

    private int allowedLoginAttempts = 5; // liczba prob logowan bez blokowania konta

    public User(){}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    public LocalDateTime getLastSuccessfulLogin() {
        return lastSuccessfulLogin;
    }

    public void setLastSuccessfulLogin(LocalDateTime lastSuccessfulLogin) {
        this.lastSuccessfulLogin = lastSuccessfulLogin;
    }

    public LocalDateTime getLastFailedLogin() {
        return lastFailedLogin;
    }

    public void setLastFailedLogin(LocalDateTime lastFailedLogin) {
        this.lastFailedLogin = lastFailedLogin;
    }

    public boolean isLockAccount() {
        return lockAccount;
    }

    public void setLockAccount(boolean lockAccount) {
        this.lockAccount = lockAccount;
    }

    public int getAllowedLoginAttempts() {
        return allowedLoginAttempts;
    }

    public void setAllowedLoginAttempts(int allowedLoginAttempts) {
        this.allowedLoginAttempts = allowedLoginAttempts;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", locked=" + locked +
                ", failedAttempts=" + failedAttempts +
                ", lockTime=" + lockTime +
                ", lastSuccessfulLogin=" + lastSuccessfulLogin +
                '}';
    }
}
