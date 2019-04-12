package vmc.in.mrecorder.entity;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by gousebabjan on 4/3/16.
 */
public class Model implements Comparable<Model> {

    private String phoneNumber;
    private String time;
    private String location;
    private String duration;
    private String filePath;
    private String callType;
    private File file;
    private String seen;

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public String getId() {
        return id;

    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }


    @Override
    public int compareTo(Model another) {
        return new Date(Long.parseLong(getTime())).compareTo(new Date(Long.parseLong(another.getTime())));
    }
}
