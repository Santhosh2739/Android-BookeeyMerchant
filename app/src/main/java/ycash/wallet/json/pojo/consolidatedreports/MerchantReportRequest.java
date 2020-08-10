package ycash.wallet.json.pojo.consolidatedreports;

import java.util.Date;

import ycash.wallet.json.pojo.generic.GenericRequest;


public class MerchantReportRequest extends GenericRequest {
	
	public MerchantReportRequest(){
	}

    private String processDateFrom;
    private String fromTime;
    private String toTime;
    private String processDateTo;
    

	public String getFromTime() {
		return fromTime;
	}
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}
	public String getToTime() {
		return toTime;
	}
	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

    public String getProcessDateFrom() {
        return processDateFrom;
    }

    public void setProcessDateFrom(String processDateFrom) {
        this.processDateFrom = processDateFrom;
    }

    public String getProcessDateTo() {
        return processDateTo;
    }

    public void setProcessDateTo(String processDateTo) {
        this.processDateTo = processDateTo;
    }
}
