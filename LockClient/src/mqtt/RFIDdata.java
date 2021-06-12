package mqtt;

public class RFIDdata {
	
	String tagid;
	String readerid;
	String valid;
	String doorid;
	
	public RFIDdata(String tagid, String readerid, String valid, String doorid) {
		super();
		this.tagid = tagid;
		this.readerid = readerid;
		this.valid = valid;
		this.doorid = doorid;
	}
	
	public RFIDdata() {}

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public String getReaderid() {
		return readerid;
	}

	public void setReaderid(String readerid) {
		this.readerid = readerid;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getDoorid() {
		return doorid;
	}

	public void setDoorid(String doorid) {
		this.doorid = doorid;
	}

	@Override
	public String toString() {
		return "RFIDdata [tagid=" + tagid + ", readerid=" + readerid + ", valid=" + valid + ", doorid=" + doorid + "]";
	}

	
}
