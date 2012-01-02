package csep.tests;

import java.util.List;

import junit.framework.AssertionFailedError;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit.AbstractXtextTests;
import org.eclipse.xtext.resource.XtextResource;

import csep.CoffeeScriptStandaloneSetup;
import csep.parser.Helper;
import csep.parser.Lexer;

/**
 * Enable testing if a code snippet gets parsed as expected.
 * 
 * @author Adam Schmideg <adam@schmideg.net>
 */

public abstract class ParserTestBase extends AbstractXtextTests {
	private final static Logger logger = Logger.getLogger(ParserTestBase.class);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		with(new CoffeeScriptStandaloneSetup());
	}

	/**
	 * XXX: Always return false, otherwise {@link AbstractXtextTests} will check whitespaces,
	 * so for example "a = 2" would fail, because it's not equal to its serialized version "a=2". 
	 */
	@Override
	protected boolean shouldTestSerializer(XtextResource resource) {
		return false;
	}

	protected void expect(CharSequence input, int errors) {
		List<String> tokens = null;
		try {
			Lexer lexer = new Lexer(input);
			tokens = lexer.tokenizeToStrings();
			EObject parseResult = getModelAndExpect(input.toString(), errors);
			if (logger.isDebugEnabled()) {
				logger.debug("Parsed " + this.getClass().getSimpleName() + " '" + input + "'\n" +Helper.stringify(parseResult));
			}
		} catch (AssertionFailedError afe) {
			logger.warn("Tokens of '" + input + "' -> " + tokens);
			throw new AssertionError(afe);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Input can be parsed without syntax errors
	 * @param input typically a String or a multiline xtend String
	 */
	public void ok(CharSequence input) {
		expect(input, 0);
	}

	/**
	 * Parsing input results in one syntax error
	 * @param input
	 */
	public void error(CharSequence input) {
		expect(input, 1);
	}
	
	/**
	 * Indicate that a test case should parse, but it gives errors
	 */
	 public void shouldBeOk(CharSequence input) {
		 String clazz = this.getClass().getSimpleName();
		 boolean wasOk = false;
		 try {
			 ok(input);	
			 wasOk = true;			
		 }
		 catch (AssertionError afe) {
			 logger.warn("Expected to successfully parse " + clazz + " '" + input + "', but " + afe.getMessage());
		 }
		 if (wasOk) {
			 fail("Expected an error, but parsed successfully '" + input + "'");
		 }
	 }
}