package rules;

public class XpdlError {
	
	private String validationID;
	private String elementID;
	private String elementName;
	private String elementXpath;
	
	public XpdlError(String validationID,String elementID,String elementName,String elementXpath) {
		this.validationID="";
		this.elementID="";
		this.elementName="";
		this.elementXpath="";
	}
	public XpdlError() {
		// TODO Auto-generated constructor stub
	}


	public String getValidationID() {
		return validationID;
	}

	public void setValidationID(String validationID) {
		this.validationID = validationID;
	}

	public String getElementID() {
		return elementID;
	}

	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementXpath() {
		return elementXpath;
	}

	public void setElementXpath(String elementXpath) {
		this.elementXpath = elementXpath;
	}
	
}
