package coreframework.utils;

/**
 * THIS IS NOT A POJO
 *
 * @author mohit
 */
public enum TransType {



    /*-----------------------------------DEMO KRYPC SERVER----------------------------------------*/
//    ADDRESS_BASE("http://demo.krypc.com/mno/ooredooServerRequest"),


    /*------------------------------------LOCAL SERVER--------------------------------------------*/
//    ADDRESS_BASE("http://192.168.43.188:8089/mno/ooredooServerRequest"),

//    ADDRESS_BASE("http://192.168.200.249:8089/mno/ooredooServerRequest"),

//Bookeey network 1
//    ADDRESS_BASE("http://192.168.8.178:8089/mno/ooredooServerRequest"),

    //Mobile network
//    ADDRESS_BASE("http://192.168.43.188:8089/mno/ooredooServerRequest"),

    //Video promotion
//    ADDRESS_BASE("http://192.168.8.140:8089/mno/ooredooServerRequest"),


    //Bookeey network 2
//    ADDRESS_BASE("http://192.168.8.191:8089/mno/ooredooServerRequest"),

    //Bookeey SHARQ
//    ADDRESS_BASE("http://192.168.8.137:8089/mno/ooredooServerRequest"),

    //Bookeey SHARQ Office Oct09
//    ADDRESS_BASE("http://192.168.2.16:8089/mno/ooredooServerRequest"),


    /*-----------------------------------UAT TEST SERVER------------------------------------------*/
//    ADDRESS_BASE("http://103.195.186.249/mno/ooredooServerRequest"),

//    ADDRESS_BASE("http://192.168.2.27/mno/ooredooServerRequest"),

//    ADDRESS_BASE("http://83.96.116.3:8089/mno/ooredooServerRequest"),


    /*-----------------------------------DEMO SERVER----------------------------------------------*/
   // ADDRESS_BASE("https://demo.bookeey.com/mno/ooredooServerRequest"),



//    ADDRESS_BASE("http://192.168.2.27:8089/mno/ooredooServerRequest"),

//    ADDRESS_BASE("http://192.168.2.24:8089/mno/ooredooServerRequest"),

//    ADDRESS_BASE("http://192.168.43.188:8089/mno/ooredooServerRequest"),

//    ADDRESS_BASE("http://192.168.2.85:8089/mno/ooredooServerRequest"),

//    ADDRESS_BASE("http://192.168.2.7:8089/mno/ooredooServerRequest"),


    /*-----------------------------------DEMO SERVER FOR L2 FLOW----------------------------------------------*/
//    ADDRESS_BASE("http://83.96.116.3:8089/mno/ooredooServerRequest"),


    /*-------------------------------------PUBLIC SERVER------------------------------------------*/
    ADDRESS_BASE("https://demo.bookeey.com/mno/ooredooServerRequest"),
    //ADDRESS_BASE("https://172.20.10.4:8089/mno/ooredooServerRequest"),
 //   ADDRESS_BASE("https://192.168.200.249:8089/mno/ooredooServerRequest"),
    //LIVE
    //ADDRESS_BASE("https://api.bookeey.com/mno/ooredooServerRequest"),



//Work from home
//ADDRESS_BASE("http://172.20.10.6:8089/mno/ooredooServerRequest"),



    //------------------------------------------------------------------------------------------------





    REGISTER(ADDRESS_BASE.getURL()),
    FETCH_BALANCE(ADDRESS_BASE.getURL()),
    SEND_MONEY_REQUEST(ADDRESS_BASE.getURL()),
    SEND_MONEY_COMMIT_REQUEST(ADDRESS_BASE.getURL()),
    PAY_TO_MERCHANT(ADDRESS_BASE.getURL()),
    SEND_MONEY_TO_BANK(ADDRESS_BASE.getURL()),
    GET_USER_DETAILS(ADDRESS_BASE.getURL()),
    LOGIN_MERCHANT(ADDRESS_BASE.getURL()),
    MERCHANT_TRAN_HISTORY_REQUEST(ADDRESS_BASE.getURL()),
    MERCHANT_REPORT_EMAIL_REQUEST(ADDRESS_BASE.getURL()),
    MERCHANTAPPVERSION(ADDRESS_BASE.getURL()),
    MERCHANT_INFO_REQUEST(ADDRESS_BASE.getURL()),


    PAY_TO_MERCHANT_COMMIT_REQUEST(ADDRESS_BASE.getURL()),

    INTERNAL_SERVER_ERROR,
    INVALID_USER,
    MERCHANT_REPORT_EMAIL_RESPONSE,
    REGISTER_RESPONSE,
    FETCH_BALANCE_RESPONSE,
    //SEND_MONEY_REQUEST_RESPONSE,
    //SEND_MONEY_COMMIT_REQUEST_RESPONSE,
    PAY_TO_MERCHANT_RESPONSE,
    SEND_MONEY_TO_BANK_RESPONSE,
    GET_USER_DETAILS_RESPONSE,
    MERCHANT_INFO_RESPONSE,
    LOGIN_MERCHANT_RESPONSE,
    PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE,
    MERCHANT_TRAN_HISTORY_RESPONSE,

    LOGOUT_MERCHANT(ADDRESS_BASE.getURL()),
    LOGOUT_MERCHANT_RESPONSE,

    MERCHANT_REPORT_REQUEST(ADDRESS_BASE.getURL()),
    MERCHANT_REPORT_RESPONSE,

    OFFER_BARCODE_VALIDATION_REQUEST(ADDRESS_BASE.getURL()),
    OFFER_BARCODE_VALIDATION_RESPONSE,
    OFFER_COMMIT_REQUEST(ADDRESS_BASE.getURL()),
    OFFER_COMMIT_RESPONSE,
    GENERATE_INVOICE_REQUEST(ADDRESS_BASE.getURL()),
    GENERATE_INVOICE_RESPONSE,
    INVOICE_TRAN_RESPONSE,
    INVOICE_REMINDER_REQUEST(ADDRESS_BASE.getURL()),
    INVOICE_REMINDER_RESPONSE,
    EDIT_INVOICE_REQUEST(ADDRESS_BASE.getURL()),
    EDIT_INVOICE_RESPONSE,
    MERCHANT_OFFER_REQUEST(ADDRESS_BASE.getURL()),
    MERCHANT_OFFER_RESPONSE,

    INVOICE_EXPIRY_REQUUEST(ADDRESS_BASE.getURL()),
    INVOICE_EXPIRY_RESPONSE;


    private String server_controller_mapping;

    TransType() {
        this.server_controller_mapping = null;
    }

    TransType(String url) {
        this.server_controller_mapping = url;
    }

    public void setURL(String url) {
        this.server_controller_mapping = url;
    }

    public String getURL() {
        return server_controller_mapping;
    }


}