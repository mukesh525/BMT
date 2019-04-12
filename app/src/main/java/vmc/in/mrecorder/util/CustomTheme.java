package vmc.in.mrecorder.util;

import android.app.Activity;
import android.content.Intent;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;

/**
 * Created by gousebabjan on 15/4/16.
 */
public class CustomTheme implements TAG {
    public static int sTheme;
    public final static int THEME_BLUE = 0;
    public final static int THEME_RED = 1;
    public final static int THEME_GREEN = 2;
    public final static int THEME_PURPLE = 3;
    public final static int THEME_INDIGO = 4;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme) {
        // sTheme = theme;
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        int id = Integer.parseInt(Utils.getFromPrefs(activity, THEME, "5"));
        ;
        switch (id){

            case THEME_BLUE:
                activity.setTheme(R.style.Bluetheme);
                break;
            case THEME_RED:
                activity.setTheme(R.style.redtheme);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.greentheme);
                break;
            case THEME_PURPLE:
                activity.setTheme(R.style.deeppurple);
                break;
            case THEME_INDIGO:
                activity.setTheme(R.style.indigo);
                break;
            default:
                activity.setTheme(R.style.MyMaterialTheme);
                break;
        }
    }
}
