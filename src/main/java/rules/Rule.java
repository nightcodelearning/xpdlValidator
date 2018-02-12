package rules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Rule {
	
	public String name;
	public String message;
	public Validation validation;
	public List<XpdlError> errorsList;
	
	
	public Rule() {
	this.name="";
	this.message="";
	this.errorsList=new ArrayList();
	this.validation=new Validation();
		
	}
	
	public abstract Validation validation(File file);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<XpdlError> getErrorsList() {
		return errorsList;
	}

	public void setErrorsList(List<XpdlError> errorsList) {
		this.errorsList = errorsList;
	}



}
