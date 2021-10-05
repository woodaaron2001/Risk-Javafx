package application;
//small class to deal with object to be added to observable list in tableview
public class TableInfo {
	private  String Case;
	private  String info;
	
	
	public TableInfo(String case1, String info) {
		super();
		Case = case1;
		this.info = info;
	}
	
	public String getCase() {
		return Case;
	}

	public void setCase(String case1) {
		Case = case1;
	}

	public String getInfo() {
		return info;
	}



	public void setInfo(String info) {
		this.info = info;
	}

	
	
}
