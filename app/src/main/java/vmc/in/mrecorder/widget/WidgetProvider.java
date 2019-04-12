/***
 * Copyright (c) 2008-2012 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * <p>
 * From _The Busy Coder's Guide to Advanced Android Development_
 * http://commonsware.com/AdvAndroid
 */


package vmc.in.mrecorder.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.callbacks.CallList;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.syncadapter.SyncAdapter;

public class WidgetProvider extends AppWidgetProvider implements CallList {
    public static String AUDIO_LINK = "Audio";
    private static CallList mListener;

    private AppWidgetManager appWidgetManager;
    int[] appWidgetIds;
    private  static final String PLAY_CLICKED = "playButtonClick";
    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent svcIntent = new Intent(ctxt, WidgetService.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
                    R.layout.widget);

            widget.setRemoteAdapter(appWidgetIds[i], R.id.words,
                    svcIntent);

            Intent clickIntent = new Intent(ctxt, Home.class);
            PendingIntent clickPI = PendingIntent
                    .getActivity(ctxt, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.words, clickPI);


           // widget.setOnClickPendingIntent(R.id.play, getPendingSelfIntent(ctxt, PLAY_CLICKED)); need to use onRecive()

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);

        }
         SyncAdapter.bindListener(this);

        super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
    }

    public static void bindListener(CallList listener) {
        mListener = listener;
    }

    @Override
    public void allCalls(ArrayList<CallData> cd) {
        if (mListener != null) {
            mListener.allCalls(cd);
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.words);
    }



    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}