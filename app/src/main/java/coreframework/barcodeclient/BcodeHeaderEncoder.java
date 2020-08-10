package coreframework.barcodeclient;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.math.BigInteger;

import coreframework.utils.Hex;


/**
 * 
 *@author 20016 - Mohit Kumar Sethi
 *@version 2.0
 *@since 09-10-2012
 *Fixed issue of decimal place encoding and decoding when the the value looks like x.01 - x.09. The decoder was taking it as x.10 - x.90.
 *The encoding and decoding are changed to fix the issue
 */
public class BcodeHeaderEncoder implements Parcelable{

	public static final String tag = BcodeHeaderEncoder.class.getCanonicalName();

	public byte[] staticId;
	public byte spIndex;
	public float amount;
	public byte[] encoded;

	//BigInteger module = new BigInteger(1, Hex.longToByteArray(0xffffffffffffffffl));
	BigInteger module = new BigInteger(1, Hex.toByteArr("ffffffffffffffff"));

	public BcodeHeaderEncoder(byte[] static_id, float amount, byte spIndex) {
		this.staticId = static_id;
		this.amount = amount;
		this.spIndex = spIndex;
		encode();
	}

	public void encode() {
		byte[] composite = new byte[5];
		byte[] am_bytes = Hex.floatToByteArray(amount);

		composite[0] = spIndex;
		composite[1] = am_bytes[0];
		composite[2] = am_bytes[1];
		composite[3] = am_bytes[2];
		composite[4] = am_bytes[3];

		byte[] dump = new byte[8];
		System.arraycopy(composite, 0, dump, 3, 5);

		BigInteger value = new BigInteger(new byte[]{0x00});
		value = value.add(module.pow(0).multiply(new BigInteger(1, composite)));
		value = value.add(module.pow(1).multiply(new BigInteger(1, staticId)));

		this.encoded = value.toByteArray();
	}

	public byte[] getEncoded() {
		return this.encoded;
	}

	public BcodeHeaderEncoder(byte[] encoded) {
		this.encoded = encoded;
		BigInteger value = new BigInteger(1, this.encoded);

		BigInteger big_composite = value.mod(module);
		value = value.divide(module);
		BigInteger big_static_id = value.mod(module);
		//value = value.divide(module);

		//this.staticId = big_static_id.toByteArray();
		this.staticId = big_static_id.toByteArray();
		if(this.staticId.length<3){
			byte[] tmp = new byte[3];
			System.arraycopy(this.staticId,0,tmp,3-this.staticId.length,this.staticId.length);
			this.staticId=tmp;
		}
		//this.staticId=Hex.toByteArr("0004DE");

		byte[] composite = big_composite.toByteArray();
		//composite = Hex.intToByteArray(big_composite.intValue());

		this.spIndex = composite[0];

		byte[] am_bytes = new byte[4];
		am_bytes[0] = composite[1];
		am_bytes[1] = composite[2];
		am_bytes[2] = composite[3];
		am_bytes[3] = composite[4];

		this.amount = Hex.byteArrayToFloat(am_bytes);
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(staticId.length);
		dest.writeByteArray(staticId);
		dest.writeByte(spIndex);
		dest.writeFloat(amount);
		dest.writeInt(encoded.length);
		dest.writeByteArray(encoded);
	}
	private BcodeHeaderEncoder(Parcel in){
		staticId = new byte[in.readInt()];
		in.readByteArray(staticId);
		spIndex = in.readByte();
		amount = in.readFloat();
		encoded = new byte[in.readInt()];
		in.readByteArray(encoded);
	}
	public static Parcelable.Creator<BcodeHeaderEncoder> CREATOR = new Creator<BcodeHeaderEncoder>() {
		@Override
		public BcodeHeaderEncoder[] newArray(int size) {
			return new BcodeHeaderEncoder[size];
		}
		@Override
		public BcodeHeaderEncoder createFromParcel(Parcel source) {
			return new BcodeHeaderEncoder(source);
		}
	};
}