package ycash.wallet.json.pojo.offerredeem;

import java.util.ArrayList;

public class OffersResponsePojo {
    public ArrayList<OfferDetails> getOfferList() {
        return offerList;
    }

    public void setOfferList(ArrayList<OfferDetails> offerList) {
        this.offerList = offerList;
    }

    ArrayList<OfferDetails> offerList;

}
