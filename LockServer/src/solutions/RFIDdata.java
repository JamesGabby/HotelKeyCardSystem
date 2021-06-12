package solutions;

public class RFIDdata {
	
	String tagid;
	int readerid;
	String valid;
	String doorid;
	
	public RFIDdata(String tagid, int readerid, String valid, String doorid) {
		super();
		this.tagid = tagid;
		this.readerid = readerid;
		this.valid = valid;
		this.doorid = doorid;
	}
	
	public RFIDdata(String tagid, int readerid) {
		super();
		this.tagid = tagid;
		this.readerid = readerid;
		this.valid = "INVALID";
		this.doorid = "unknown";
	}
	
	public RFIDdata() {}

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public int getReaderid() {
		return readerid;
	}

	public void setReaderid(int reader) {
		this.readerid = reader;
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
