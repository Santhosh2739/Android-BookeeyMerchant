package ycash.wallet.json.pojo;

import ycash.wallet.json.pojo.generic.GenericResponse;

/**
 * Created by 10030 on 11/26/2016.
 */
public class MerchantAppVersionResponse extends GenericResponse {
    private String curentVersion;

    public String getCurentVersion() {
        return curentVersion;
    }

    public void setCurentVersion(String curentVersion) {
        this.curentVersion = curentVersion;
    }

    @Override
    public String toString() {
        return "MerchantAppVersionResponse [curentVersion=" + curentVersion + "]";
    }

    public MerchantAppVersionResponse() {
        super();
        // TODO Auto-generated constructor stub
    }




}
