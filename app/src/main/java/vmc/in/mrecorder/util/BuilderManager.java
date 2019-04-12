package vmc.in.mrecorder.util;

import android.graphics.Color;

import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;

import vmc.in.mrecorder.R;

/**
 * Created by vmc on 10/2/17.
 */

public class BuilderManager {

    private static int[] imageResources = new int[]{
            R.drawable.call,
            R.drawable.msg,
            R.drawable.location,
            R.drawable.share,
            R.drawable.star
    };

    private static int[] titles = {R.string.call,R.string.message,R.string.location,R.string.share,R.string.rate};

    private static int imageResourceIndex = 0;

    public static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    public static SimpleCircleButton.Builder getSimpleCircleButtonBuilder() {
        return new SimpleCircleButton.Builder()
                .normalImageRes(getImageResource());
    }

    public static TextInsideCircleButton.Builder getTextInsideCircleButtonBuilder(int i) {
        return new TextInsideCircleButton.Builder()
                .normalImageRes(imageResources[i])
                .normalTextRes(titles[i]);
    }

    public static TextInsideCircleButton.Builder getTextInsideCircleButtonBuilderWithDifferentPieceColor(int i) {
        return new TextInsideCircleButton.Builder()
                .normalImageRes(imageResources[i])
                .normalTextRes(titles[i])
                .pieceColor(Color.WHITE);
    }

    public static TextOutsideCircleButton.Builder getTextOutsideCircleButtonBuilder(int i) {
        return new TextOutsideCircleButton.Builder()
                .normalImageRes(imageResources[i])
                .normalTextRes(titles[i]);
    }

    public static TextOutsideCircleButton.Builder getTextOutsideCircleButtonBuilderWithDifferentPieceColor() {
        return new TextOutsideCircleButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.app_name)
                .pieceColor(Color.WHITE);
    }

//    static HamButton.Builder getHamButtonBuilder() {
//        return new HamButton.Builder()
//                .normalImageRes(getImageResource())
//                .normalTextRes(R.string.text_ham_button_text_normal)
//                .subNormalTextRes(R.string.text_ham_button_sub_text_normal);
//    }

//    static HamButton.Builder getHamButtonBuilderWithDifferentPieceColor() {
//        return new HamButton.Builder()
//                .normalImageRes(getImageResource())
//                .normalTextRes(R.string.text_ham_button_text_normal)
//                .subNormalTextRes(R.string.text_ham_button_sub_text_normal)
//                .pieceColor(Color.WHITE);
//    }

    private static BuilderManager ourInstance = new BuilderManager();

    public static BuilderManager getInstance() {
        return ourInstance;
    }

    private BuilderManager() {
    }
}
