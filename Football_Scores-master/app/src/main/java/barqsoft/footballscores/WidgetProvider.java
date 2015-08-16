package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import barqsoft.footballscores.service.WidgetService;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by yemyatthu on 8/7/15.
 */
public class WidgetProvider extends AppWidgetProvider {
  public static final String DATA_FETCHED = "barqsoft.footballscores.WidgetProvider.DATA_FETCHED";
  public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "barqsoft.footballscores.WidgetProvider.EXTRA_LIST_VIEW_ROW_NUMBER";

  /**
   * this method is called every 30 mins as specified on widgetinfo.xml
   * this method is also called on every phone reboot
   **/

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
  public void onUpdate(Context context, AppWidgetManager
      appWidgetManager,int[] appWidgetIds) {

/*int[] appWidgetIds holds ids of multiple instance
 * of your widget
 * meaning you are placing more than one widgets on
 * your homescreen*/
    final int N = appWidgetIds.length;
    for (int i = 0; i < N; ++i) {
      Intent serviceIntent = new Intent(context, myFetchService.class);
      serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
      context.startService(serviceIntent);
      RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
      Intent configIntent = new Intent(context, MainActivity.class);
      PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
      remoteViews.setOnClickPendingIntent(R.id.main_widget_container, configPendingIntent);
      PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setPendingIntentTemplate(R.id.listViewWidget, startActivityPendingIntent);
      appWidgetManager.updateAppWidget(appWidgetIds[i],remoteViews);
    }
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private RemoteViews updateWidgetListView(Context context,
      int appWidgetId) {

    //which layout to show on widget
    RemoteViews remoteViews = new RemoteViews(
        context.getPackageName(),R.layout.widget_layout);

    //RemoteViews Service needed to provide adapter for ListView
    Intent svcIntent = new Intent(context, WidgetService.class);
    //passing app widget id to that RemoteViews Service
    svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    //setting a unique Uri to the intent
    //don't know its purpose to me right now
    svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
    //setting adapter to listview of the widget
    remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget, svcIntent);
    //setting an empty view in case of no data
    remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
    return remoteViews;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    if (intent.getAction().equals(DATA_FETCHED)) {
      int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
          AppWidgetManager.INVALID_APPWIDGET_ID);
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      final ComponentName cn = new ComponentName(context,
          WidgetProvider.class);
      appWidgetManager.notifyAppWidgetViewDataChanged(
          appWidgetManager.getAppWidgetIds(cn),
          R.id.listViewWidget);
    }
  }

}
