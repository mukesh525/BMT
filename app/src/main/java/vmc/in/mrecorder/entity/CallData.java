package vmc.in.mrecorder.entity;

import java.util.Date;

/**
 * Created by mukesh on 3/24/2016.
 */
public class CallData{
    private String callid;
    private String bid;
    private String eid;
    private String callfrom;
    private String callto;
    private String starttime;
    private String endtime;
    private String pulse;
    private String calltype;
    private String filename;
    private String name;
    private String business;
    private String address;
    private String email;
    private String remark;
    private String status;
    private String keyword;
    private String assignto;
    private String leadid;
    private String tktid;
    private String source;
    private String last_modified;
    private String empname;
    private String location;
    private String seen;
    private String review;
    private Date StartTime;
    private Date EndTime;
    private int duration;//for Status

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

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

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    public String getCallid() {

        return callid;
    }

    public void setCallid(String callid) {
        this.callid = callid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getCallfrom() {
        return callfrom;
    }

    public void setCallfrom(String callfrom) {
        this.callfrom = callfrom;
    }

    public String getCallto() {
        return callto;
    }

    public void setCallto(String callto) {
        this.callto = callto;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getAssignto() {
        return assignto;
    }

    public void setAssignto(String assignto) {
        this.assignto = assignto;
    }

    public String getLeadid() {
        return leadid;
    }

    public void setLeadid(String leadid) {
        this.leadid = leadid;
    }

    public String getTktid() {
        return tktid;
    }

    public void setTktid(String tktid) {
        this.tktid = tktid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }
}
