package pe.upn.AppJotDownPlanner.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TaskModel {
    private String uid;
    private String userUid;
    private String name;
    private String desc;
    private String dateTime;
    private boolean isCompleted;

    public TaskModel(){}


    public TaskModel(String userUid, String name, String desc, String dateTime) {
        this.userUid = userUid;
        this.name = name;
        this.desc = desc;
        this.dateTime = dateTime;
    }

    // Helper method to convert LocalDateTime to String (ISO format)
    public static String convertToString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskModel{" +
                "name='" + name + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
