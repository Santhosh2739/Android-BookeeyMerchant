package wallet.ooredo.com.live.consolidatedreports;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import coreframework.processing.MerchantLoginProcessing;
import coreframework.processing.ReportsProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import ycash.wallet.json.pojo.consolidatedreports.MerchantReportRequest;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;

public class Consolidated_Reports_Selection extends GenericActivity implements TimePickerDialog.OnTimeSetListener,YPCHeadlessCallback {
    int year,month,day,mYear,mMonth,mDay;
    TextView select_from_date_tv1,select_to_date_tv1,from_time_tv1,to_time_tv1;
    Button submit_btn;
    String hourString,minuteString;
    String hourString1,minuteString1;
    ImageView image_from_date_calendar,image_to_date_calendar,image_from_time,image_to_time;
    String time,time1;
    private String processFromDate,processToDate,fromTime,toTime=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consolidated_selection);
        getActionBar().hide();
        enableUndoBar();
        select_from_date_tv1 = (TextView) findViewById(R.id.select_from_date_tv1);
        select_to_date_tv1 = (TextView) findViewById(R.id.select_to_date_tv1);
        from_time_tv1=(TextView)findViewById(R.id.from_time_tv1);
        to_time_tv1=(TextView)findViewById(R.id.to_time_tv1);
        submit_btn=(Button)findViewById(R.id.submit_btn);
        image_from_date_calendar=(ImageView)findViewById(R.id.image_from_date_calendar);
        image_to_date_calendar=(ImageView)findViewById(R.id.image_to_date_calendar);
        image_from_time=(ImageView)findViewById(R.id.image_from_time);
        image_to_time=(ImageView)findViewById(R.id.image_to_time);
        image_from_date_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
//                showDialog(1);
                DatePickerDialog dp = new DatePickerDialog(
                        Consolidated_Reports_Selection.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        year = yearSelected;
                        month = monthOfYear+1;
                        day = dayOfMonth;
                        String month1=month < 10 ? "0" + month : "" + month;

                        // Set the Selected Date in Select date Button
//            customer_select_date_tv.setText(day + "-" + month + "-" + year);

                        String day1=day < 10 ? "0" + day : "" + day;
                        String month2=month < 10 ? "0" + month : "" + month;


                        select_from_date_tv1.setText(day1 + "-" + month2 + "-" + year);
//                select_date_tv1.setText(day + "-" + month1 + "-" + year);
                        processFromDate=year + "-" + month2 + "-" + day1;

                    }
                },mYear, mMonth, mDay);

                dp.setTitle("Select Date");
                dp.show();
            }
        });
        image_to_date_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
//                showDialog(1);
                DatePickerDialog dp = new DatePickerDialog(
                        Consolidated_Reports_Selection.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        year = yearSelected;
                        month = monthOfYear+1;
                        day = dayOfMonth;
                        String month1=month < 10 ? "0" + month : "" + month;

                        // Set the Selected Date in Select date Button
//            customer_select_date_tv.setText(day + "-" + month + "-" + year);

                        String day1=day < 10 ? "0" + day : "" + day;
                        String month2=month < 10 ? "0" + month : "" + month;


                        select_to_date_tv1.setText(day1 + "-" + month2 + "-" + year);
//                select_date_tv1.setText(day + "-" + month1 + "-" + year);
                        processToDate=year + "-" + month2 + "-" + day1;

                    }
                },mYear, mMonth, mDay);

                dp.setTitle("Select Date");
                dp.show();
            }
        });
        select_from_date_tv1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dp = new DatePickerDialog(
                        Consolidated_Reports_Selection.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        year = yearSelected;
                        month = monthOfYear+1;
                        day = dayOfMonth;
                        String month1=month < 10 ? "0" + month : "" + month;

                        // Set the Selected Date in Select date Button
//            customer_select_date_tv.setText(day + "-" + month + "-" + year);

                        String day1=day < 10 ? "0" + day : "" + day;
                        String month2=month < 10 ? "0" + month : "" + month;


                        select_from_date_tv1.setText(day1 + "-" + month2 + "-" + year);
//                select_date_tv1.setText(day + "-" + month1 + "-" + year);
                        processFromDate=year + "-" + month2 + "-" + day1;

                    }
                },mYear, mMonth, mDay);

                dp.setTitle("Select Date");
                dp.show();
            }
        });

        select_to_date_tv1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dp = new DatePickerDialog(
                        Consolidated_Reports_Selection.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        year = yearSelected;
                        month = monthOfYear+1;
                        day = dayOfMonth;
                        String month1=month < 10 ? "0" + month : "" + month;

                        // Set the Selected Date in Select date Button
//            customer_select_date_tv.setText(day + "-" + month + "-" + year);

                        String day1=day < 10 ? "0" + day : "" + day;
                        String month2=month < 10 ? "0" + month : "" + month;


                        select_to_date_tv1.setText(day1 + "-" + month2 + "-" + year);
//                select_date_tv1.setText(day + "-" + month1 + "-" + year);
                        processToDate=year + "-" + month2 + "-" + day1;

                    }
                },mYear, mMonth, mDay);

                dp.setTitle("Select Date");
                dp.show();
            }
        });


        image_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Consolidated_Reports_Selection.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String aMpM = "AM";
                        if (selectedHour > 11) {
                            aMpM = "PM";
                        }

                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if (selectedHour > 11) {
                            currentHour = selectedHour - 12;
                        } else {
                            currentHour = selectedHour;
                        }
                        hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        from_time_tv1.setText( hourString + ":" + minuteString);
                        fromTime=hourString + ":" + minuteString;
                        time=String.valueOf(selectedHour)+String.valueOf(selectedMinute)+aMpM;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        });
        from_time_tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Consolidated_Reports_Selection.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String aMpM = "AM";
                        if (selectedHour > 11) {
                            aMpM = "PM";
                        }

                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if (selectedHour > 11) {
                            currentHour = selectedHour - 12;
                        } else {
                            currentHour = selectedHour;
                        }
                        hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        from_time_tv1.setText( hourString + ":" + minuteString);
                        fromTime=hourString + ":" + minuteString;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        });

        to_time_tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Consolidated_Reports_Selection.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    /*public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String aMpM = "AM";
                        if (selectedHour > 11) {
                            aMpM = "PM";
                        }

                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if (selectedHour > 11) {
                            currentHour = selectedHour - 12;
                        } else {
                            currentHour = selectedHour;
                        }
                        hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;

                        to_time_tv1.setText(hourString + ":" + minuteString );
                        toTime=hourString + ":" + minuteString;

                    }*/
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String aMpM = "AM";
                        if (selectedHour > 11) {
                            aMpM = "PM";
                        }

                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if (selectedHour > 11) {
                            currentHour = selectedHour - 12;
                        } else {
                            currentHour = selectedHour;
                        }
                        hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        to_time_tv1.setText( hourString + ":" + minuteString);
                        toTime=hourString + ":" + minuteString;
                    }
                }, hour, minute, true);
                //Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

//                Calendar now = Calendar.getInstance();

            }
        });//
        image_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Consolidated_Reports_Selection.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    /*public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String aMpM = "AM";
                        if (selectedHour > 11) {
                            aMpM = "PM";
                        }

                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if (selectedHour > 11) {
                            currentHour = selectedHour - 12;
                        } else {
                            currentHour = selectedHour;
                        }
                        hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;

                        to_time_tv1.setText(hourString + ":" + minuteString );
                        toTime=hourString + ":" + minuteString;
                        time1=String.valueOf(selectedHour)+String.valueOf(selectedMinute)+aMpM;
                    }*/
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String aMpM = "AM";
                        if (selectedHour > 11) {
                            aMpM = "PM";
                        }

                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if (selectedHour > 11) {
                            currentHour = selectedHour - 12;
                        } else {
                            currentHour = selectedHour;
                        }
                        hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        to_time_tv1.setText( hourString + ":" + minuteString);
                        toTime=hourString + ":" + minuteString;
                    }
                }, hour, minute, true);
                //Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

//                Calendar now = Calendar.getInstance();

            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(processFromDate==null||fromTime==null||toTime==null||processToDate==null){
                    showNeutralDialogue("Alert!",getString(R.string.all_fields_are_mandatory));
                    return;
                }

                MerchantReportRequest merchantReportRequest = new MerchantReportRequest();
                merchantReportRequest.setProcessDateFrom(processFromDate);
                merchantReportRequest.setFromTime(fromTime);
                merchantReportRequest.setToTime(toTime);
                merchantReportRequest.setProcessDateTo(processToDate);

                //Code to start server thread and display the progress fragment dialogue (retained)
                CoreApplication application = (CoreApplication)getApplication();
                String uiProcessorReference = application.addUserInterfaceProcessor(new ReportsProcessing(merchantReportRequest,application,true));
                ProgressDialogFrag progress = new ProgressDialogFrag();
                Bundle bundle = new Bundle();
                bundle.putString("uuid", uiProcessorReference);
                progress.setCancelable(true);
                progress.setArguments(bundle);
                progress.show(getFragmentManager(), "progress_dialog");


            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        final ImageView  back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo()); ;
//        MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
//        new DownloadImageTask(back_logo).execute(merchantLoginRequestResponse.getMerchantLogo());


    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            pd.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
//            pd.dismiss();
            bmImage.setImageBitmap(result);
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

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