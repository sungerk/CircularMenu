package sunger.net.org;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import sunger.net.org.a.R;

/**
 * Created by sunger on 16/1/26.
 */
public class PanAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, null,false);
        textView.setText("Item" + position);
        return textView;
    }
}
