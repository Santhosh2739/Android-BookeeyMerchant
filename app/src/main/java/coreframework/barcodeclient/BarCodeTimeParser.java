package coreframework.barcodeclient;

import java.util.Calendar;
import java.util.TimeZone;

import coreframework.utils.Hex;

public class BarCodeTimeParser {
	
	static final String tag = BarCodeTimeParser.class.getSimpleName();
	
	byte month; 	//range 0-11 size 12 
	byte date;  	//range 0-30 size 31
	byte hour;  	//range 0-23 size 24
	byte minute;	//range 0-59 size 60
	byte seconds;	//range 0-59 size 60


	byte[] encoded = null;
	final int module = 60;
	
	static final int ALLOWED_LIMIT_MILLI_SECONDS = 10000;
	
	public BarCodeTimeParser(){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("Month="+calendar.get(Calendar.MONTH)+" Date: = "+calendar.get(Calendar.DATE)+" HourOfTheday="+calendar.get(Calendar.HOUR_OF_DAY)+" Minute="+calendar.get(Calendar.MINUTE)+" Seconds="+calendar.get(Calendar.SECOND)  );
		//Log.d(tag, "Computed Offset Time:="+logBuffer.toString());
		
		set(	calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY), 
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND)
				);
		encode();
	}
	private void set(int month, int date, int hour,int minute, int seconds){
		this.month 		= (byte)(month & 0x000000ff);
		this.date 		= (byte)(date & 0x000000ff);
		this.hour 		= (byte)(hour & 0x000000ff);
		this.minute 	= (byte)(minute & 0x000000ff);
		this.seconds 	= (byte)(seconds & 0x000000ff);
	}
	public BarCodeTimeParser(int month, int date, int hour,int minute, int seconds){
		set(month, date, hour, minute, seconds);
		encode();
	}
	public BarCodeTimeParser(byte month, byte date, byte hour,byte minute, byte seconds){
		this.month 		= month;
		this.date 		= date;
		this.hour 		= hour;
		this.minute 	= minute;
		this.seconds 	= seconds;
	}
	public BarCodeTimeParser(byte[] encoded) throws IllegalArgumentException{
		try{
			if(encoded.length!=4){
				throw new IllegalArgumentException();
			}
			//start decoding:
			int number = Hex.byteArrayToInt(encoded);

			
			this.month 		= (byte)(number % module);
			number/=module;
			this.date 		= (byte)(number % module);
			number/=module;
			this.hour 		= (byte)(number % module);
			number/=module;
			this.minute 	= (byte)(number % module);
			number/=module;
			this.seconds 	= (byte)(number % module);
			number/=module;
			
			this.encoded = encoded;
		}catch (Exception e) {
			throw new IllegalArgumentException("General Encoding Exception");
		}
	}
	private void encode(){
		//LSB-MSB -- MINUTE-DATE-HOUR-MONTH
		int number = 0;
		number += (1) * month;//1=module^0
		number += (module) * date;
		number += (module * module) * hour;
		number += (module * module * module) * minute;
		number += (module * module * module * module) * seconds;
		encoded = Hex.intToByteArray(number);
	}
	public String log(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Month="+month);
		buffer.append("\n");
		buffer.append("Date="+date);
		buffer.append("\n");
		buffer.append("Hour="+hour);
		buffer.append("\n");
		buffer.append("Minute="+minute);
		buffer.append("\n");
		buffer.append("Seconds="+seconds);
		buffer.append("\n");
		buffer.append("Encoded<byteArray>="+Hex.toHex(encoded));
		buffer.append("\n");
		return buffer.toString();
	}
	public byte[] getEncoded(){
		return encoded;
	}
	public boolean isCodeTimeValidAndNotExpired(){
		//Get The System Time:
		Calendar server = Calendar.getInstance();
		Calendar client = Calendar.getInstance();
		//Remove Time Gap In Execution of these 2 statements
		//Very Funny :) Precision
		client.setTime(server.getTime());
		
		client.set(Calendar.MONTH, 			this.month);
		client.set(Calendar.DATE, 			this.date);
		client.set(Calendar.HOUR_OF_DAY, 	this.hour);
		client.set(Calendar.MINUTE, 		this.minute);
		client.set(Calendar.SECOND, 		this.seconds);

		long serverTimeInMillis = server.getTime().getTime();
		long clientTimeInMillis	= client.getTime().getTime();
		
		if(clientTimeInMillis > serverTimeInMillis){
			//Major Issue:::
			return false;
		}
		return ((serverTimeInMillis - clientTimeInMillis) <= ALLOWED_LIMIT_MILLI_SECONDS);
	}
}
