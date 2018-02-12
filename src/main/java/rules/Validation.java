package rules;

import java.util.List;

public class Validation {
	private boolean status;
	private String message;
	private List<XpdlError> errorList;
	
	public Validation() {
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<XpdlError> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<XpdlError> errorList) {
		this.errorList = errorList;
	}
	
	

}
