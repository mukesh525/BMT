package vmc.in.mrecorder.callbacks;

import java.util.Date;

/**
 * Created by gousebabjan on 4/3/16.
 */
public interface TAG {
    int MY_PERMISSIONS_CALL = 0;
    int SHARE_CALL = 14;
    String UNKNOWN = "Unknown";
    String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    String PROJECT_NUMBER = "596968407103";
    String MISSED = "Missed";
    String DEFAULT = "n/a";
    String INCOMING = "Inbound";
    String OUTGOING = "Outbound";
    String Outbound = "outbound";
    String Inbound = "inbound";
    String missed = "missed";
    String SHOWN = "shown";
    String NUMBER = "Number";
    String MOBILE_NUMBER = "number";
    String FILEPATH = "FilePath";
    String TIME = "Time";
    String ID = "_id";
    String UPLOADEDFILE = "uploadedfile";
    String PASSWORD = "password";
    String CODE = "code";
    String MESSAGE = "msg";
    String OTP = "otp";
    String DEVICE_ID = "deviceid";
    String GCM_KEY = "gcmkey";
    String DEVICE = "model";
    String CONTACTNAME = "name";
    String DURATION = "duration";
    int NOTIFICATION_ID = 0;
    String APP_VERSION = "version";

    ///NEw parametre
    String DATA = "data";
    String DEBUG = "mtdebug";
    String BID = "bid";
    String THEME = "theme";
    String EID = "eid";
    String CALLTO = "callto";
    String STARTTIME = "starttime";
    String ENDTIME = "endtime";
    String SEEN = "seen";
    String REVIEW = "review";
    String PULSE = "pulse";
    String CALLTYPEE = "calltype";
    String CALLTYPE = "CallType";
    String FILENAME = "filename";
    String LOCATION = "location";
    String NAME = "name";
    String BUSINESS = "business";
    String ADDRESS = "address";
    String EMAIL = "email";
    String USERTYPE = "usertype";
    String REMARK = "remark";

    String KEYWORD = "keyword";
    String ASSIGNTO = "assignto";
    String LEADID = "leadid";
    String TKTID = "tktid";
    String SOURCE = "source";
    String LASTMODIFIED = "last_modified";
    String EMPNAME = "empname";
    String EMPLOYEE = "employee";

    String AUTHKEY = "authkey";
    String SESSION_ID = "sessionid";
    String TAG = "TEST_LOG";
    String OFFSET = "offset";
    String LIMIT = "limit";
    String TYPE = "type";
    String DEVICEID = "deviceid";
    String FIRST_TYME = "firstime";
    String CALLSTATUS="callStatus";

    String TYPE_MISSED = "0";
    String TYPE_INCOMING = "1";
    String TYPE_OUTGOING = "2";
    String TYPE_ALL = "all";
    String FEEDBACK = "feedback";
    String SIM = "sim";
    String RATING_TITLE = "rating_title";
    String COMMENT = "comment";
    String DATE = "date";
    String SHOW = "shown";

    String BASE_URL = "https://mcube.vmctechnologies.com/mtappv3/";
    String GET_CALL_LIST = BASE_URL + "getList";
    String GET_FEED_BACK_URL = BASE_URL + "feedback_mtrack";
    String STREAM_TRACKER = "https://mcube.vmctechnologies.com/sounds/";
    String UPLOAD_URL = BASE_URL + "insert_calldetail";
    String GET_OTP = BASE_URL + "login_mtrack";
    String LOGIN_URL = BASE_URL + "check_auth";
    String EMPREPORT_URL = BASE_URL + "reportByEmp";
    String TYPEREPORT_URL = BASE_URL + "reportBycallType";
    String SET_RATE_URL = BASE_URL + "insert_rating";
    String SET_SEEN_URL = BASE_URL + "recording_status";
    String GET_RATE_URL = BASE_URL + "getList_rating";
    String ERROR_URL = BASE_URL + "errorlog";
    String UPDATE_RECORDING_FILE=BASE_URL+"insertFile";
    // String ERROR_URL = "http://192.168.1.131/mcnew/mtappv3/errorlog";

    //TEMP
    String CALLID = "callid";
    String LISTEN = "recordlisten";
    String DATETIME = "datetime";
    String CALLEREMAIL = "caller_email";
    String CALLFROM = "callfrom";
    String DATAID = "dataid";
    String CALLERNAME = "callername";
    String GROUPNAME = "groupname";
    String CALLTIMESTRING = "calltime";
    String STATUS = "status";
    String RECORDS = "records";
    String REPORTTYPE = "reporttype";
    String GROUPS = "groups";
    String VAL = "val";
    String KEY = "key";
    String RECORDING = "record";
    String MCUBECALLS = "mcubecalls";
    String WORKHOUR = "workhour";
    String FIELDS = "fields";
    String COUNT = "count";
    String RATING_COUNT = "ratingcount";
    String DROPDOWN = "dropdown";
    String CHECKBOX = "checkbox";
    String RADIO = "radio";
    String OPTIONS = "options";
    String TITLE = "title";
    String DESCRIPTION = "description";
    String RATING = "rating";
    String RATING_LIST = "ratinglist";



}
