package dong.lan.tuyi.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import dong.lan.tuyi.R;
import dong.lan.tuyi.basic.Welcome;

/**
 * Created by Dooze on 2015/8/19.
 */
public class DesktopApp extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName componentName = new ComponentName(context,DesktopApp.class);
        appWidgetManager.updateAppWidget(componentName,getRemoteViews(context));
    }

    public RemoteViews getRemoteViews(Context context)
    {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.desktop_app);
        views.setOnClickPendingIntent(R.id.desk_offline, PendingIntent.getActivity(context, 0, new Intent(context,Welcome.class).putExtra("OFFLINE",true),0));
        views.setOnClickPendingIntent(R.id.desk_add_tuyi, PendingIntent.getActivity(context, 1, new Intent(context,Welcome.class).putExtra("ADD_TUYI",true),0));
        views.setOnClickPendingIntent(R.id.desk_enter, PendingIntent.getActivity(context, 2, new Intent(context, Welcome.class),0));
        return views;
    }
}
