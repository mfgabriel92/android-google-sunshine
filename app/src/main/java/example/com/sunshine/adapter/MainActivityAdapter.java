package example.com.sunshine.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import example.com.sunshine.R;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {

    private String[] mWeatherData;
    private MainActivityAdapterOnClickHandler mClickHandler;

    public MainActivityAdapter(MainActivityAdapterOnClickHandler click) {
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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_main_item, parent, false);

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
        holder.mTvWeatherDataItem.setText(mWeatherData[position]);
    }

    /**
     * Returns the quantity of items to display.
     *
     * @return the size of the list
     */
    @Override
    public int getItemCount() {
        return mWeatherData == null ? 0 : mWeatherData.length;
    }

    /**
     * Sets the weather on the adapter.
     *
     * @param data the new weather data to be displayed.
     */
    public void setWeatherData(String[] data) {
        mWeatherData = data;
        notifyDataSetChanged();
    }

    /**
     * The interface to receive onClickHandler messages
     */
    public interface MainActivityAdapterOnClickHandler {
        void onClickHandler(String data);
    }

    /**
     * The ViewHolder cache of the children for a list of weather
     */
    public class MainActivityViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public final TextView mTvWeatherDataItem;

        public MainActivityViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTvWeatherDataItem = itemView.findViewById(R.id.tvWeatherDataItem);
        }

        @Override
        public void onClick(View v) {
            String data = mWeatherData[getAdapterPosition()];
            Log.v("V/MAA", data);
            mClickHandler.onClickHandler(data);
        }
    }
}