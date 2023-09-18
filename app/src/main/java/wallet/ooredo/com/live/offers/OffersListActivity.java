package wallet.ooredo.com.live.offers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.YPCHeadlessCallback;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import ycash.wallet.json.pojo.offerredeem.OfferDetails;
import ycash.wallet.json.pojo.offerredeem.OffersResponsePojo;

public class OffersListActivity extends GenericActivity implements YPCHeadlessCallback {
    ListView offers_list;
    ArrayList<OfferDetails> arrayList;
    String response_str = null;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_list_activity);
        offers_list = (ListView) findViewById(R.id.offers_list);

        response_str = getIntent().getStringExtra("OFFERS_DATA");
        arrayList = new ArrayList<>();
        OffersResponsePojo response = new Gson().fromJson(response_str, OffersResponsePojo.class);
        for (int i = 0; i < response.getOfferList().size(); i++) {
            arrayList.add(new OfferDetails(response.getOfferList().get(i).getId(),
                    response.getOfferList().get(i).getOfferId(),
                    response.getOfferList().get(i).getOfferStartDate(),
                    response.getOfferList().get(i).getOfferEndDate(),
                    response.getOfferList().get(i).getOfferName(),
                    response.getOfferList().get(i).getBannerText(),
                    response.getOfferList().get(i).getOfferStartTime(),
                    response.getOfferList().get(i).getOfferEndTime(),
                    response.getOfferList().get(i).getGroup_name(),
                    response.getOfferList().get(i).getMerchant_name(),
                    response.getOfferList().get(i).getMerchantRefNumber(),
                    response.getOfferList().get(i).getMerchant_branch()));
        }

        adapter = new CustomAdapter(arrayList, getApplicationContext());
        offers_list.setAdapter(adapter);

    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());
    }

    private class CustomAdapter extends ArrayAdapter<OfferDetails> {
        private ArrayList<OfferDetails> dataSet;
        Context mContext;

        public CustomAdapter(ArrayList<OfferDetails> data, Context context) {
            super(context, R.layout.offer_list_row, data);
            this.dataSet = data;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            OfferDetails dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            OfferHolder offerHolder; // view lookup cache stored in tag
            if (convertView == null) {
                offerHolder = new OfferHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.offer_list_row, parent, false);
                offerHolder.offer_name = (TextView) convertView.findViewById(R.id.offer_name);
                offerHolder.offer_desc = (TextView) convertView.findViewById(R.id.offer_desc);
                convertView.setTag(offerHolder);
            } else {
                offerHolder = (OfferHolder) convertView.getTag();
            }
            offerHolder.offer_name.setText(dataModel.getOfferName().toUpperCase());

            try {
                //create SimpleDateFormat object with source string date format
                //SimpleDateFormat sdfSource = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
                SimpleDateFormat sdfSource = new SimpleDateFormat("MM dd, yyyy hh:mm:ss a");

                //parse the string into Date object
                // Date date = sdfSource.parse(""+dataModel.getOfferEndDate());

                //create SimpleDateFormat object with desired date format
                SimpleDateFormat sdfDestination = new SimpleDateFormat("MMM dd, yyyy");
                String endDate = sdfDestination.format(dataModel.getOfferEndDate());

                offerHolder.offer_desc.setText("EXPIRES : " + " " + endDate.toUpperCase() + " " + dataModel.getOfferEndTime());

            } catch (Exception pe) {
                System.out.println("Parse Exception : " + pe);
            }

            return convertView;
        }

        private class OfferHolder {
            TextView offer_name,
                    offer_desc;

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
