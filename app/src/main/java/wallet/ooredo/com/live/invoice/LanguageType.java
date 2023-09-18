package wallet.ooredo.com.live.invoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import wallet.ooredo.com.live.R;


public class LanguageType extends BaseAdapter {
    List<String> languageList;
    Context context;
    LayoutInflater inflter;

    public LanguageType(Context applicationContext, List<String> languages) {
        this.context = applicationContext;
        this.languageList = languages;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return languageList.size();
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
        namess.setText(languageList.get(position));
        return view;
    }
}
