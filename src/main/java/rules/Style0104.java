package rules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class Style0104 extends Rule {

	@Override
	public Validation validation(File file) {
		name=" validation Style0104";
		try{
			
			SAXBuilder builder = new SAXBuilder();
			Document doc = (Document) builder.build(file);
			
			Element root = doc.getRootElement();
			Namespace namespace=Namespace.getNamespace("qwerty",root.getNamespace().getURI());
			XPathFactory xpathFactory = XPathFactory.instance();
			String myxPathExpression = "//qwerty:WorkflowProcess[qwerty:Activities]";
			XPathExpression<Element> expr = xpathFactory.compile(myxPathExpression, Filters.element(), null, namespace);
						
			List<Element> nodes = expr.evaluate(doc);
			
			for (Element element : nodes) {
				Element activities=element.getChild("Activities", namespace);
				List<Element> activityList=activities.getChildren("Activity", namespace);
				List<String> nameList=new ArrayList<>();
				for (Element activity : activityList) {
					String name=activity.getAttributeValue("Name");
					
					
					
					if(nameList.contains(name)) {
						XpdlError result=new XpdlError();
						result.setElementID(element.getAttributeValue("Id"));
						result.setElementName(name);
						result.setValidationID(this.name);
						
						List<String> xpathElements = new ArrayList<String>(); 
						while (activity.getParentElement() != null) {
							activity = activity.getParentElement();
							xpathElements.add(activity.getName()+activity.getAttributes().toString());
						}
						Collections.reverse(xpathElements);
						result.setElementXpath(xpathElements.toString());
						errorsList.add(result);
						
					}else {
						nameList.add(name);
					}
				}

				
				
			}
			if(!errorsList.isEmpty()) {
				validation.setErrorList(errorsList);
				validation.setStatus(true);
				validation.setMessage("Validation completed");
			}

		}catch(Exception e) {
			validation.setStatus(false);
			validation.setMessage("threre are some errors validating xpdl file");
		}
		
		
		return validation;
	}

}
