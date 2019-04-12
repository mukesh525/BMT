package vmc.in.mrecorder.entity;

import vmc.in.mrecorder.callbacks.TAG;

/**
 * Created by gousebabjan on 29/6/16.
 */
public class LoginData {

    private String code;
    private String authcode;
    private String message;
    private String usertype;
    private String username;
    private String recording;
    private String mcuberecording;
    private String workhour;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecording() {
        return recording;
    }

    public void setRecording(String recording) {
        this.recording = recording;
    }

    public String getMcuberecording() {
        return mcuberecording;
    }

    public void setMcuberecording(String mcuberecording) {
        this.mcuberecording = mcuberecording;
    }

    public String getWorkhour() {
        return workhour;
    }

    public void setWorkhour(String workhour) {
        this.workhour = workhour;
    }


}
