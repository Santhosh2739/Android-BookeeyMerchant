package coreframework.processing;

import android.app.Activity;
import android.os.Message;
import android.util.Log;

import java.math.BigInteger;
import java.util.Arrays;

import coreframework.barcodeclient.BcodeHeaderEncoder;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.UserInterfaceBackgroundProcessing;
import coreframework.utils.Hex;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import ycash.wallet.json.pojo.decodedbarcode.DecodedQrPojo;

/**
 * Created by mohit on 02-07-2015.
 */
public class DecodeIncomingQrPaymentCode extends BackgroundProcessingAbstractFilter{
    private String qr_code_data = null;
    private DecodedQrPojo decodedQrPojo;

    public DecodeIncomingQrPaymentCode(String qr_code_data){
        this.qr_code_data = qr_code_data;
    }
    @Override
    public void performTask() {
        try{

//            String rizwan_data1 = "135285012375776061546130055958616059612907034417487174168226227812302864";
//            String rizwan_data2 = "135285012375776049286166378800520765742849470040381368002479942908510224";

            BigInteger bg = new BigInteger(this.qr_code_data.trim());

//            Log.e("rizwan_data2",""+rizwan_data2);
//            BigInteger bg = new BigInteger(rizwan_data2);
            Log.e("bg",""+bg);
            byte[] encoded = bg.toByteArray();
            Log.e("encoded",""+""+Arrays.toString(encoded));
            byte[] len_bytes_enc_length = new byte[4];
            Log.e("len_bytes_enc_length bc",""+Arrays.toString(len_bytes_enc_length));
            System.arraycopy(encoded,encoded.length-4,len_bytes_enc_length,0,4);
            Log.e("len_bytes_enc_length ac",""+Arrays.toString(len_bytes_enc_length));
            int length_of_enc_data = Hex.byteArrayToInt(len_bytes_enc_length);
            Log.e("length_of_enc_data",""+length_of_enc_data);
            int length_of_header_data = encoded.length - length_of_enc_data -4;
            Log.e("length_of_header_data",""+length_of_header_data);
            byte[] header = new byte[length_of_header_data];
            Log.e("header before copy",""+Arrays.toString(header));
            System.arraycopy(encoded,0,header,0,length_of_header_data);
            Log.e("header after copy",""+Arrays.toString(header));
            byte[] enciphered = new byte[length_of_enc_data];
            Log.e("enciphered before copy",""+Arrays.toString(enciphered));
            System.arraycopy(encoded,length_of_header_data,enciphered,0,length_of_enc_data);
            Log.e("enciphered after copy",""+Arrays.toString(enciphered));
            BcodeHeaderEncoder bcodeHeaderEncoder = new BcodeHeaderEncoder(header);

            decodedQrPojo = new DecodedQrPojo(encoded,header,enciphered,bcodeHeaderEncoder);

        }catch (Exception e){
            decodedQrPojo = null;
        }
    }
    @Override
    public String captureURL() {
        return null;
    }
    @Override
    public void processResponse(Message msg) {

    }
    @Override
    public void performUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment) {
        dialogueFragment.dismiss();
        ((MainActivity)activity).onBarcodeDecoded(decodedQrPojo);
    }
    @Override
    public boolean isPost() {
        return false;
    }

    @Override
    public boolean isLocalProcess() {
        return true;
    }
}
