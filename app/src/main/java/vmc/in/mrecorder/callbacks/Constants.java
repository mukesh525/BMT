package vmc.in.mrecorder.callbacks;

/**
 * Created by mukesh on 3/24/2016.
 */
public class Constants {
    public static int position=0;
    public static boolean isRate;
    public static boolean isLogout;
    public static String appVersion;
    public interface ACTION {
        public static String MAIN_ACTION = "vmc.in.mrecorder.action.main";
        public static String PREV_ACTION = "vmc.in.mrecorder.action.prev";
        public static String PLAY_ACTION = "vmc.in.mrecorder.action.play";
        public static String NEXT_ACTION = "vmc.in.mrecorder.action.next";
        public static String STARTFOREGROUND_ACTION = "vmc.in.mrecorder.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "vmc.in.mrecorder.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}


