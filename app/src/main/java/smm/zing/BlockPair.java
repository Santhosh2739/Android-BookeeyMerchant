package smm.zing;


//import com.google.zxing.common.ByteArray;

final class BlockPair {

  private final ByteArray dataBytes;
  private final ByteArray errorCorrectionBytes;

  BlockPair(ByteArray data, ByteArray errorCorrection) {
    dataBytes = data;
    errorCorrectionBytes = errorCorrection;
  }

  public ByteArray getDataBytes() {
    return dataBytes;
  }

  public ByteArray getErrorCorrectionBytes() {
    return errorCorrectionBytes;
  }

}
