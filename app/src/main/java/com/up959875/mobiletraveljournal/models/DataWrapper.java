package com.up959875.mobiletraveljournal.models;
import com.up959875.mobiletraveljournal.other.Status;

public class DataWrapper<T> {
    private T data;
    private Status error;
    private String message;
    private boolean isAdded = false;
    private boolean isAuthenticated = false;
    private boolean isVerified = false;


    public DataWrapper(T data, Status error, String message, boolean isAuthenticated, boolean isAdded, boolean isVerified) {
        this.data = data;
        this.error = error;
        this.message = message;
        this.isAuthenticated = isAuthenticated;
        this.isAdded = isAdded;
        this.isVerified = isVerified;
    }

    public DataWrapper(T data, Status error, String message) {
        this.data = data;
        this.error = error;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public Status getError() {
        return error;
    }

    public void setStatus(Status status) {
        this.error = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public boolean isVerified() {
        return isVerified;
    }
}
