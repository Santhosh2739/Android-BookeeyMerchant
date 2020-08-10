package coreframework.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import coreframework.database.CustomSharedPreferences;

/**
 * Created by mohit on 6/1/16.
 */
public class TimeUtils {

    public static String getDisplayableDateWithSeconds(String timezone, Date date) {


        Calendar cal= null;
        if(timezone!=null) {
             cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        }
        else{
            cal= Calendar.getInstance(TimeZone.getDefault());
        }
        cal.setTime(date);
        Date cur = new Date(System.currentTimeMillis());
        Calendar calcur = Calendar.getInstance();
        calcur.setTime(cur);
        String displayTime = "";
        String month = null;
        switch (cal.get(Calendar.MONTH)) {
            case 0:
                month = "Jan";
                break;
            case 1:
                month = "Feb";
                break;
            case 2:
                month = "Mar";
                break;
            case 3:
                month = "Apr";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "Jun";
                break;
            case 6:
                month = "Jul";
                break;
            case 7:
                month = "Aug";
                break;
            case 8:
                month = "Sep";
                break;
            case 9:
                month = "Oct";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dec";
                break;
            default:
                month = " ";
                break;
        }
        String minute = Integer.toString(cal.get(Calendar.MINUTE));
        minute = (minute.length() == 1) ? "0" + minute : minute;
        String hour = Integer.toString(cal.get(Calendar.HOUR));
        String seconds = Integer.toString(cal.get(Calendar.SECOND));

        hour = (hour.equals("0")) ? "12" : hour;
        displayTime =
                Integer.toString(cal.get(Calendar.DATE)) + " " +
                        month + " " +
                        Integer.toString(cal.get(Calendar.YEAR)%100) + ";" +
                        hour+ ":" + minute
                        + (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM")
//                        +"," +TimeZone.getDefault().getDisplayName();
        +","+ TimeZone.getTimeZone("GMT--1200").getDisplayName();


        String[] sa= displayTime.split(",");
        displayTime=sa[0];
        return displayTime;
    }
}
