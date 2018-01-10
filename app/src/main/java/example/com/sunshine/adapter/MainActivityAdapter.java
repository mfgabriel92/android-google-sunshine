package example.com.sunshine.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import example.com.sunshine.R;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {

    private String[] mWeatherData;

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

    public class MainActivityViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTvWeatherDataItem;

        public MainActivityViewHolder(View itemView) {
            super(itemView);
            mTvWeatherDataItem = itemView.findViewById(R.id.tvWeatherDataItem);
        }
    }
}