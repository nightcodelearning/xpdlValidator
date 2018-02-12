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





public class Style0115 extends Rule {

	@Override
	public Validation validation(File file) {
		name=" validation Style0115";
		try{
			
			SAXBuilder builder = new SAXBuilder();
			Document doc = (Document) builder.build(file);
			
			Element root = doc.getRootElement();
			Namespace namespace=Namespace.getNamespace("qwerty",root.getNamespace().getURI());
			XPathFactory xpathFactory = XPathFactory.instance();
			String myxPathExpression = "//qwerty:Activity[@Name='']";
			
			XPathExpression<Element> expr = xpathFactory.compile(myxPathExpression, Filters.element(), null, namespace);
						
			List<Element> nodes = expr.evaluate(doc);
			
			for (Element element : nodes) {
				Element event = element.getChild("Event", namespace);
				if (event != null) {
					Element intermediateEvent = event.getChild("IntermediateEvent", namespace);
					if (intermediateEvent != null&& intermediateEvent.hasAttributes()&& intermediateEvent.getAttributeValue("Trigger").equals("Message")) {
						Element triggerResultMessage = intermediateEvent.getChild("TriggerResultMessage", namespace);
						if (triggerResultMessage != null&& triggerResultMessage.hasAttributes() && triggerResultMessage.getAttributeValue("CatchThrow").equals("THROW")) {
							
							XpdlError result=new XpdlError();
							result.setElementID(element.getAttributeValue("Id"));
							result.setElementName("");
							result.setValidationID(this.name);
							
							List<String> xpathElements = new ArrayList<String>(); 
							while (triggerResultMessage.getParentElement() != null) {
								triggerResultMessage = triggerResultMessage.getParentElement();
								xpathElements.add(triggerResultMessage.getName()+triggerResultMessage.getAttributes().toString());
							}
							Collections.reverse(xpathElements);
							result.setElementXpath(xpathElements.toString());
							errorsList.add(result);
							
						}
					}
				}		
			}
			if(!errorsList.isEmpty()) {
				validation.setErrorList(errorsList);
				validation.setStatus(true);
				validation.setMessage("does not comply with the rule "+this.name);
			}

		}catch(Exception e) {
			validation.setStatus(false);
			validation.setMessage("Found some errors validating xpdl file");
		}
		
		
		return validation;
	}
}
	
