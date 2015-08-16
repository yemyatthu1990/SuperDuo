package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresDBHelper;
import barqsoft.footballscores.WidgetProvider;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yemyatthu on 8/7/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
  private Cursor listItemList;
  private Context context = null;
  private int appWidgetId;

  public ListProvider(Context context, Intent intent) {
    this.context = context;
    Log.d("at least","here");
    appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID);
    populateListItem();
  }


  private void populateListItem() {
    Date date = new Date();
    SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
    String today = mformat.format(date);
    SQLiteDatabase db = new ScoresDBHelper(context).getReadableDatabase();
    Log.d("String today",today);
    listItemList = db.query(DatabaseContract.SCORES_TABLE,null,null,null,null,null,"date");
    Log.d("count",listItemList.getCount()+"");
  }

  @Override public void onCreate() {
  }

  @Override public void onDataSetChanged() {
    populateListItem();
  }

  @Override public void onDestroy() {
  }

  @Override
  public int getCount() {
    return listItemList.getCount();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override public boolean hasStableIds() {
    return false;
  }

  /*
  *Similar to getView of Adapter where instead of View
  *we return RemoteViews
  *
  */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) @Override
  public RemoteViews getViewAt(int position) {
    final RemoteViews remoteView = new RemoteViews(
        context.getPackageName(),R.layout.widget_item_layout);
    int padding = (int) context.getResources().getDimension(R.dimen.spacing_minor);
    remoteView.setViewPadding(R.id.widget_container,padding,padding,padding,padding);
    listItemList.moveToPosition(position);
    String date = listItemList.getString(listItemList.getColumnIndexOrThrow("date"));
    String home = listItemList.getString(listItemList.getColumnIndexOrThrow("home"));
    String away = listItemList.getString(listItemList.getColumnIndexOrThrow("away"));
    String home_goals = listItemList.getString(listItemList.getColumnIndexOrThrow("home_goals"));
    String away_goals = listItemList.getString(listItemList.getColumnIndexOrThrow("away_goals"));
    if(Integer.valueOf(home_goals)==-1){
      home_goals = "";
    }
    if (Integer.valueOf(away_goals)==-1){
      away_goals = "";
    }
    remoteView.setTextViewText(R.id.data_textview, date);
    remoteView.setTextViewText(R.id.home_name,home);
    remoteView.setTextViewText(R.id.away_name, away);
    remoteView.setTextViewText(R.id.score_textview, home_goals + " : " + away_goals);
    Intent fillInIntent = new Intent();
    fillInIntent.putExtra(WidgetProvider.EXTRA_LIST_VIEW_ROW_NUMBER, position);
    remoteView.setOnClickFillInIntent(R.id.widget_container, fillInIntent);
    return remoteView;
  }

  @Override public RemoteViews getLoadingView() {
    return null;
  }

  @Override public int getViewTypeCount() {
    return 1;
  }
}