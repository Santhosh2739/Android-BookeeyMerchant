package ycash.wallet.json.pojo.decodedbarcode;

import android.os.Parcel;
import android.os.Parcelable;

import coreframework.barcodeclient.BcodeHeaderEncoder;

/**
 * Created by mohit on 03-07-2015.
 */
public class DecodedQrPojo implements Parcelable{

    private byte[] completeCode;
    private byte[] encodedHeader;
    private byte[] enciphered;
    private BcodeHeaderEncoder bcodeHeaderEncoder;

    public BcodeHeaderEncoder getBcodeHeaderEncoder() {
        return bcodeHeaderEncoder;
    }


    public DecodedQrPojo(byte[] completeCode,byte[] encodedHeader,byte[] enciphered,BcodeHeaderEncoder bcodeHeaderEncoder){
        if(completeCode==null || encodedHeader==null || enciphered==null){
            throw new IllegalArgumentException("");
        }
        this.completeCode = completeCode;
        this.encodedHeader = encodedHeader;
        this.enciphered = enciphered;
        this.bcodeHeaderEncoder = bcodeHeaderEncoder;
    }

    public byte[] getCompleteCode() {
        return completeCode;
    }
    public byte[] getEncodedHeader() {
        return encodedHeader;
    }
    public byte[] getEnciphered() {
        return enciphered;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(completeCode.length);
        dest.writeByteArray(completeCode);
        dest.writeInt(encodedHeader.length);
        dest.writeByteArray(encodedHeader);
        dest.writeInt(enciphered.length);
        dest.writeByteArray(enciphered);
        dest.writeParcelable(bcodeHeaderEncoder,BcodeHeaderEncoder.CONTENTS_FILE_DESCRIPTOR);
    }
    private DecodedQrPojo(Parcel in){
        completeCode = new byte[in.readInt()];
        in.readByteArray(completeCode);
        encodedHeader = new byte[in.readInt()];
        in.readByteArray(encodedHeader);
        enciphered = new byte[in.readInt()];
        in.readByteArray(enciphered);
        bcodeHeaderEncoder = in.readParcelable(BcodeHeaderEncoder.class.getClassLoader());
    }
    public static Parcelable.Creator<DecodedQrPojo> CREATOR = new Creator<DecodedQrPojo>() {
        @Override
        public DecodedQrPojo[] newArray(int size) {
            return new DecodedQrPojo[size];
        }
        @Override
        public DecodedQrPojo createFromParcel(Parcel source) {
            return new DecodedQrPojo(source);
        }
    };
}
