package it.jaschke.alexandria;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;

public class BookDetail extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

  public static final String EAN_KEY = "EAN";
  private final int LOADER_ID = 10;
  private String ean;
  private String bookTitle;
  private ShareActionProvider shareActionProvider;
  private Button deleteButton;
  private Intent shareIntent;
  private Toolbar toolbar;
  private ImageView fullBookCover;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_full_book);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ean = getIntent().getStringExtra(BookDetail.EAN_KEY);
    getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    deleteButton = (Button) findViewById(R.id.delete_button);
    fullBookCover = (ImageView) findViewById(R.id.fullBookCover);
    deleteButton.setOnClickListener(this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.book_detail, menu);
    MenuItem menuItem = menu.findItem(R.id.action_share);
    shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    shareActionProvider.setShareIntent(shareIntent);
    return true;
  }

  @Override public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(this,
        AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)), null, null, null, null);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
  public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
    if (!data.moveToFirst()) {
      return;
    }

    bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
    ((TextView) findViewById(R.id.fullBookTitle)).setText(bookTitle);

    shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
    // TODO: 7/20/15 FIX NULL POINTER EXCEPTION

    String bookSubTitle =
        data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
    ((TextView) findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

    String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
    ((TextView) findViewById(R.id.fullBookDesc)).setText(desc);

    String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
    String[] authorsArr = authors.split(",");
    ((TextView) findViewById(R.id.authors)).setLines(authorsArr.length);
    ((TextView) findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
    String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
    if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
      Glide.with(this).load(imgUrl).into(fullBookCover);
    } else {
      fullBookCover.setVisibility(View.GONE);
    }

    String categories =
        data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
    ((TextView) findViewById(R.id.categories)).setText(categories);
  }

  @Override public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

  }

  @Override public void onClick(View view) {
    if (view.equals(deleteButton)) {
      Intent bookIntent = new Intent(this, BookService.class);
      bookIntent.putExtra(BookService.EAN, ean);
      bookIntent.setAction(BookService.DELETE_BOOK);
      startService(bookIntent);
      Intent i = new Intent(this, MainActivity.class);
      startActivity(i);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}