package wallet.ooredo.com.live.invoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import wallet.ooredo.com.live.R;


public class SendLinkType extends BaseAdapter {
    List<String> sendLinkList;
    Context context;
    LayoutInflater inflter;

    public SendLinkType(Context applicationContext, List<String> sendlink) {
        this.context = applicationContext;
        this.sendLinkList = sendlink;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return sendLinkList.size();
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
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.language_spinner_row, null);
        TextView namess = (TextView) view.findViewById(R.id.language_name_text);
        namess.setText(sendLinkList.get(position));
        return view;
    }
}
