package coreframework.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import android.graphics.Typeface;
import android.widget.TextView;

public class ExternalUtils {

	public static String getDisplayableDate(Date date) {
		Calendar cal = Calendar.getInstance();
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
		hour = (hour.equals("0")) ? "12" : hour;
		displayTime = Integer.toString(cal.get(Calendar.DATE)) + " " + month
				+ " " + Integer.toString(cal.get(Calendar.YEAR)) + "  " + hour
				+ ":" + minute + " "
				+ (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
		return displayTime;
	}
	public static String getDisplayableDateWithSeconds(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
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
			Integer.toString(cal.get(Calendar.YEAR)%100) + "," +
			hour+ ":" + minute
				+ (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
		return displayTime;
	}


	public static String[] removeDuplicateElemntsInArray(String[] strArray) {

		List<String> list = Arrays.asList(strArray);
		Set<String> set = new HashSet<String>(list);
		String[] result = new String[set.size()];
		set.toArray(result);
		return result;
	}

	public static String getRequiredString(String str) {

		return str.substring(str.indexOf(' ') + 1, str.indexOf("\n")).trim();

	}

	public static String[] getSiliarLengthStrinArray(String[] strArray) {

		String[] strArrayFinal = new String[strArray.length];
		String tempStr = null;
		tempStr = strArray[0];
		String maxLenStr = null;
		for (int i = 1; i < strArray.length; i++)
			if (strArray[i].length() > tempStr.length())
				maxLenStr = strArray[i];

		for (int i = 0; i < strArray.length; i++) {
			if (strArray[i].length() <= maxLenStr.length())
				strArrayFinal[i] = addSpace(strArray[i], maxLenStr.length()
						- strArray[i].length());
		}
		return strArrayFinal;

	}

	public static String addSpace(String str, int n) {

		StringBuffer outputBuffer = new StringBuffer(n + 1);
		for (int i = 0; i < n; i++) {
			outputBuffer.append(" ");
		}
		return str + outputBuffer.toString();

	}

	public static Formatter getFormatedFloatValue(float price) {

		Formatter fmt = new Formatter();
		return fmt = fmt.format("%.2f\n\n", price);
	}
	
	public static void setLayoutFont(Typeface font, TextView... textViews) {
		for (TextView tv : textViews) {
			tv.setTypeface(font, Typeface.NORMAL);
		}
	}
	
	
	public static JSONArray getJsonArrayUsingTag(JSONObject json, String tag){
		JSONArray temp_array = json.optJSONArray(tag);
		if(null == temp_array){
			JSONObject temp_object = json.optJSONObject(tag);
//			if(temp_object == null){
//				//search for key value string pair
//				String _val = json.optString(tag, CustomSharedPreferences.SIMPLE_NULL);
//				if(!_val.equalsIgnoreCase(CustomSharedPreferences.SIMPLE_NULL)){
//					temp_object = new JSONObject();
//					try {
//						temp_object.put(tag, _val);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
			if(null != temp_object){
				temp_array = new JSONArray();
				temp_array.put(temp_object);
			}
		}
		return temp_array;
	}

}
