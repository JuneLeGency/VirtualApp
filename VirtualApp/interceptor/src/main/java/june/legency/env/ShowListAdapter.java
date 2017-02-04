package june.legency.env;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apkfuns.logutils.utils.ObjectUtil;
import com.lc.puppet.storage.IObIndex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by legency on 2017/1/29.
 */
public class ShowListAdapter extends BaseAdapter {

    private ArrayList<KV> data = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public ShowListAdapter(Context context) {
        this.context = context;
    }

    public void bindHash(HashMap<IObIndex, Object> hashMap) {
        Iterator<Map.Entry<IObIndex, Object>> i = hashMap.entrySet().iterator();
        ArrayList<KV> list = new ArrayList<>();
        while (i.hasNext()) {
            Map.Entry<IObIndex, Object> n = i.next();
            list.add(new KV(n.getKey(), n.getValue()));
        }
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public KV getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_show, null);
            viewHolder.txtKey = (TextView) convertView.findViewById(R.id.text_key);
            viewHolder.txtValue = (TextView) convertView.findViewById(R.id.text_value);
            convertView.setTag(viewHolder);
        }
        KV i = getItem(position);
        viewHolder.txtKey.setText(i.key.toString());
        viewHolder.txtValue.setText(ObjectUtil.objectToString(i.value));
        return convertView;
    }

    public class KV implements Serializable{
        private final IObIndex key;
        private final Object value;

        public KV(IObIndex key, Object value) {
            this.key = key;
            this.value = value;
        }

        public IObIndex getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }

    private class ViewHolder {
        public TextView txtKey;
        public TextView txtValue;
    }
}
