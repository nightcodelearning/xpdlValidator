package rules;

public class RuleFactory implements RuleFactoryMethod {

	@Override
	public Rule createRule(String name) throws Exception {
		Class<?> clase = Class.forName(name);				
		return (Rule) clase.newInstance();
		
	}

}
