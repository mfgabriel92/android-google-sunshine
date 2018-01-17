package example.com.sunshine.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import example.com.sunshine.MainActivity;
import example.com.sunshine.R;
import example.com.sunshine.util.SunshineDateUtils;
import example.com.sunshine.util.SunshineWeatherUtils;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private MainActivityAdapterOnClickHandler mClickHandler;

    public MainActivityAdapter(Context context, MainActivityAdapterOnClickHandler click) {
        mContext = context;
        mClickHandler = click;
    }

    /**
     * It is called when each new ViewHolder is created.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType provides a different layout in case the RecyclerView has more than one type of item
     * @return a new adapter that holds the View for each item in the list.
     */
    @Override
    public MainActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main_item, parent, false);
        view.setFocusable(true);

        return new MainActivityViewHolder(view);
    }

    /**
     * This method is called by the RecyclerView to display the data at the specified position,
     * updating the contents of the ViewHolder to display to the user.
     *
     * @param holder the ViewHolder which should be updated.
     * @param position the position of the item within the adapter's set.
     */
    @Override
    public void onBindViewHolder(MainActivityViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_ID);
        double high = mCursor.getDouble(MainActivity.INDEX_HIGH);
        double low = mCursor.getDouble(MainActivity.INDEX_LOW);

        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        String highAndLow = SunshineWeatherUtils.formatHighLow(mContext, high, low);

        holder.mTvWeatherDataItem.setText(dateString + " - " + description + " - " + highAndLow);
    }

    /**
     * Returns the quantity of items to display.
     *
     * @return the size of the list
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the ForecastAdapter for its weather data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * The interface to receive onClickHandler messages
     */
    public interface MainActivityAdapterOnClickHandler {
        void onClickHandler(long date);
    }

    /**
     * The ViewHolder cache of the children for a list of weather
     */
    public class MainActivityViewHolder extends ViewHolder implements OnClickListener {

        public final TextView mTvWeatherDataItem;

        public MainActivityViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTvWeatherDataItem = itemView.findViewById(R.id.tvWeatherDataItem);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());

            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);

            mClickHandler.onClickHandler(dateInMillis);
        }
    }
}