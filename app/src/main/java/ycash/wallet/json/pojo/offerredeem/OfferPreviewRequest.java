package ycash.wallet.json.pojo.offerredeem;

import ycash.wallet.json.pojo.generic.GenericRequest;

public class OfferPreviewRequest extends GenericRequest {

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    private String merchantId;
}
