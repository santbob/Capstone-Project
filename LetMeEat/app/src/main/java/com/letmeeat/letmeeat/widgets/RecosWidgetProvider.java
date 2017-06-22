package com.letmeeat.letmeeat.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.letmeeat.letmeeat.MainActivity;
import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.RecoDetailsActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecosWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, getRemoteViews(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, getRemoteViews(context));
        }
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static RemoteViews getRemoteViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.let_me_eat_widget);

        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

        // Set up the collection
        views.setRemoteAdapter(R.id.widget_list, new Intent(context, WidgetRemoteViewsService.class));

        Intent appIntent = new Intent(context, RecoDetailsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list, appPendingIntent);

//        Intent clickIntentTemplate = new Intent(context, MainActivity.class); // may be build a details screen and replace MainActivity with details activity for a particular match.
//
//        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
//                .addNextIntentWithParentStack(clickIntentTemplate)
//                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
        views.setEmptyView(R.id.widget_list, R.id.widget_empty);
        return views;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

