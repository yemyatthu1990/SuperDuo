package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorRecyclerAdapter
{
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    private Context context;
    private OnItemClickInterface mClickInterface;
  private boolean expanded;
  private int expandedPosition;
    public ScoresAdapter(Cursor cursor) {
        super(cursor);
    }

    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();

    }
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    @Override public void onBindViewHolderCursor(final RecyclerView.ViewHolder holder, Cursor cursor) {
        ((ViewHolder) holder).home_name.setText(cursor.getString(COL_HOME));
        ((ViewHolder) holder).away_name.setText(cursor.getString(COL_AWAY));
        ((ViewHolder) holder).date.setText(cursor.getString(COL_MATCHTIME));
        ((ViewHolder) holder).score.setText(
            Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        ((ViewHolder) holder).match_id = cursor.getDouble(COL_ID);
        ((ViewHolder) holder).home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
            cursor.getString(COL_HOME)));
        ((ViewHolder) holder).away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
            cursor.getString(COL_AWAY)
        ));
        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = ((ViewHolder) holder).detailFragment;
        if(((ViewHolder) holder).match_id == detail_match_id)
        {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                cursor.getInt(COL_LEAGUE),context));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilies.getLeague(cursor.getInt(COL_LEAGUE),context));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(((ViewHolder) holder).home_name.getText()+" "
                        +((ViewHolder) holder).score.getText()+" "+((ViewHolder) holder).away_name.getText() + " "));
                }
            });

        }else
        {
            container.removeAllViews();
        }

    }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      context = viewGroup.getContext();
        View mItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scores_list_item, viewGroup, false);
        return new ViewHolder(mItem);
    }

    public void setItemClickListener(OnItemClickInterface itemClickListener){
        mClickInterface = itemClickListener;
    }
    interface OnItemClickInterface{
        void setOnItemClickListener(View view,int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView home_name;
        public TextView away_name;
        public TextView score;
        public TextView date;
        public ImageView home_crest;
        public ImageView away_crest;
        public ViewGroup detailFragment;
        public double match_id;


        public ViewHolder(View itemView) {
            super(itemView);
            home_name = (TextView) itemView.findViewById(R.id.home_name);
            away_name = (TextView) itemView.findViewById(R.id.away_name);
            score     = (TextView) itemView.findViewById(R.id.score_textview);
            date      = (TextView) itemView.findViewById(R.id.data_textview);
            home_crest = (ImageView) itemView.findViewById(R.id.home_crest);
            away_crest = (ImageView) itemView.findViewById(R.id.away_crest);
            detailFragment = (ViewGroup) itemView.findViewById(R.id.details_fragment_container);
          itemView.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
          Log.d("it's being clicked","CLICK");
          view.setTag(this);
            if(mClickInterface != null){
                mClickInterface.setOnItemClickListener(view,getAdapterPosition());
            }
        }
    }

}
