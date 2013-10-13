package test.xml;

import org.junit.*;
import org.strangeforest.xml.helpers.*;
import org.strangeforest.xml.util.*;

import static org.junit.Assert.*;

public class XMLTest {

	private static ElementHelper xml;

	@BeforeClass
	public static void setUp() throws Exception {
		xml = ParserUtil.parseString(
			"<root>" +
			  "Pera je Konj<ina teska=\"true\">ina</ina>!" +
			"</root>"
		);
	}

	@Test
	public void testTextContent() {
		assertEquals("Pera je Konjina!", xml.getTextContent());
		ElementHelper ina = (ElementHelper)xml.getChild("ina");
		assertEquals("ina", ina.getTextContent());
	}

	@Test
	public void testXPath() {
		assertEquals("ina", xml.find("/root/ina[@teska='true']").getTextContent());
		assertEquals(1, xml.findAll("/root/ina[@teska='true']").getLength());
		assertEquals(true, xml.findBoolean("/root/ina/@teska"));
		assertEquals(true, xml.findBoolean("/root/ina/@teska2", true));
		assertEquals("ina", xml.findString("/root/ina"));
		assertEquals(1, xml.findInt("/root/ina2", 1));
	}
}
