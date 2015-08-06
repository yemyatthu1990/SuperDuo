package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;

/**
 * Created by yemyatthu on 8/7/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
  private ArrayList<String> listItemList = new ArrayList<>();
  private Context context = null;
  private int appWidgetId;

  public ListProvider(Context context, Intent intent) {
    this.context = context;
    appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID);
    populateListItem();
  }


  private void populateListItem() {
    for (int i = 0; i < 10; i++) {
      listItemList.add("Data "+i);
    }

  }

  @Override public void onCreate() {
  }

  @Override public void onDataSetChanged() {

  }

  @Override public void onDestroy() {
  }

  @Override
  public int getCount() {
    return listItemList.size();
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
  @Override
  public RemoteViews getViewAt(int position) {
    final RemoteViews remoteView = new RemoteViews(
        context.getPackageName(), android.R.layout.simple_list_item_1);
    String data = listItemList.get(position);
    remoteView.setTextViewText(android.R.id.text1, data);
    return remoteView;
  }

  @Override public RemoteViews getLoadingView() {
    return null;
  }

  @Override public int getViewTypeCount() {
    return 0;
  }
}