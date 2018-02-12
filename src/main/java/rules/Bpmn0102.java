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

public class Bpmn0102 extends Rule {

	@Override
	public Validation validation(File file) {
		name=" validation Bpmn0102";
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = (Document) builder.build(file);
			
			Element root = doc.getRootElement();
			Namespace namespace=Namespace.getNamespace("qwerty",root.getNamespace().getURI());
			XPathFactory xpathFactory = XPathFactory.instance();
			String myxPathExpression = "//qwerty:Activity[qwerty:Event[qwerty:StartEvent]]";
			XPathExpression<Element> expr = xpathFactory.compile(myxPathExpression, Filters.element(), null, namespace);
						
			List<Element> allstartEvent = expr.evaluate(doc);
			
			
			for (Element element : allstartEvent) {
				
				String activityID=element.getAttributeValue("Id");				
				String allTransitions = "//qwerty:Transition";
				XPathExpression<Element> exprTran = xpathFactory.compile(allTransitions, Filters.element(), null, namespace);
				List<Element> transitions = exprTran.evaluate(doc);
				List<Boolean> booleanList=new ArrayList<>();
				for (Element transition : transitions) {
					if(transition.getAttribute("From").getValue().equals(activityID)) {
						booleanList.add(true);
					}
				}
				
				if(!booleanList.contains(true)) {
					XpdlError result=new XpdlError();
					result.setElementID(element.getAttributeValue("Id"));
					result.setElementName("");
					result.setValidationID(this.name);
					
					List<String> xpathElements = new ArrayList<String>(); 
					while (element.getParentElement() != null) {
						element = element.getParentElement();
						xpathElements.add(element.getName()+element.getAttributes().toString());
					}
					Collections.reverse(xpathElements);
					result.setElementXpath(xpathElements.toString());
					errorsList.add(result);
				}
				
			}
			if(!errorsList.isEmpty()) {
				validation.setErrorList(errorsList);
				validation.setStatus(true);
				validation.setMessage("does not comply with the rule "+this.name);
			}

		}catch(Exception e) {
			e.printStackTrace();
			validation.setStatus(false);
			validation.setMessage("Found some errors validating xpdl file");
		}
	
		return validation;
	}

}
