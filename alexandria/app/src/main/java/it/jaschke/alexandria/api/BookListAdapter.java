package it.jaschke.alexandria.api;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * Created by saj on 11/01/15.
 */
public class BookListAdapter extends CursorAdapter {

  public BookListAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  @Override public void bindView(View view, Context context, Cursor cursor) {

    ViewHolder viewHolder = (ViewHolder) view.getTag();

    String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
    Glide.with(context)
        .load(imgUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.ic_launcher)
        .placeholder(R.drawable.ic_launcher)
        .into(viewHolder.bookCover);

    String bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
    viewHolder.bookTitle.setText(bookTitle);

    String bookSubTitle =
        cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
    viewHolder.bookSubTitle.setText(bookSubTitle);
  }

  @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

    ViewHolder viewHolder = new ViewHolder(view);
    view.setTag(viewHolder);

    return view;
  }

  public static class ViewHolder {
    public final ImageView bookCover;
    public final TextView bookTitle;
    public final TextView bookSubTitle;

    public ViewHolder(View view) {
      bookCover = (ImageView) view.findViewById(R.id.fullBookCover);
      bookTitle = (TextView) view.findViewById(R.id.listBookTitle);
      bookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
    }
  }
}
