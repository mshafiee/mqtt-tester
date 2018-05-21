package persianswitch.com.mqtt;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private static final int MAX_LIST_SIZE = 100;
    private ArrayList<String> logArrayList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.row_text);
        }
    }

    public LogAdapter(ArrayList<String> dataSet) {
        logArrayList = dataSet;
    }

    @Override
    public LogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create View
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_row, parent, false);

        return new ViewHolder(v);
    }

    public void add(String data) {
        if (logArrayList.size() > MAX_LIST_SIZE) {
            delete(logArrayList.size() - 1);
        }

        logArrayList.add(0, data);
        this.notifyItemInserted(0);
    }


    public void delete(int position) {
        logArrayList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(logArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return logArrayList.size();
    }


}