package com.up959875.mobiletraveljournal.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.google.firebase.firestore.Exclude;
//import com.up959875.mobiletraveljournal.BR;
import androidx.annotation.Nullable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;

public class Notification extends BaseObservable implements Serializable {

    private String id;
    @Exclude
    private User userFrom;
    private String idFrom;
    private String idTo;
    private Integer type;
    @ServerTimestamp
    private Timestamp timestamp;

    public Notification() {
    }

    public Notification(String idFrom, String idTo, Integer type) {
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.type = type;
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        //notifyPropertyChanged(BR.id);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            if (obj == null || getClass() != obj.getClass())
                return false;
            Notification n = (Notification) obj;
            return id.equals(n.id)
                    && idFrom.equals(n.idFrom)
                    && idTo.equals(n.idTo)
                    && type.equals(n.type)
                    && timestamp.equals(n.timestamp);
        } catch (Exception ex) {
            return false;
        }
    }

    @Exclude
    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User user) {
        this.userFrom = user;
    }

    @Bindable
    public String getIdTo() {
        return idTo;
    }

    public void setIdTo(String idTo) {
        this.idTo = idTo;
        //notifyPropertyChanged(BR.idTo);
    }

    @Bindable
    public String getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(String idFrom) {
        this.idFrom = idFrom;
        //notifyPropertyChanged(BR.idFrom);
    }



    @Bindable
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Bindable
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
        //notifyPropertyChanged(BR.type);
    }
}
