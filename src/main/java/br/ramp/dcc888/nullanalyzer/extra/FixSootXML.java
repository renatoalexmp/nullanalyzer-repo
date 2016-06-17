package br.ramp.dcc888.nullanalyzer.extra;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FixSootXML {

	private String fileName;

	private Document document = null;

	public FixSootXML(String fileName) {
		this.setFileName(fileName);
		String parseStringAsXML = parseStringAsXML();
		if (parseStringAsXML != null) {
			prepareDocumentFromString(parseStringAsXML);
			saveDocument();
		}
	}

	private String parseStringAsXML() {
		String xmlAsString = readFile();
		String fixedXml = escapeAttributeValue("info", xmlAsString);
		return fixedXml;
	}

	private String readFile() {
		try {
			if (!fileName.isEmpty() && isValidPath(fileName)) {
				String content = new String(Files.readAllBytes(Paths
						.get(fileName)));
				return content;
			} else
				throw new IOException("Arquivo inválido");
		} catch (IOException e) {
			return null;
		}
	}

	private String escapeAttributeValue(String attribute, String input) {
		if (input != null && !input.isEmpty()) {
			String patternString1 = "(?:" + attribute + "=\")([^\"]+)(?=\")";

			Pattern pattern = Pattern.compile(patternString1);
			Matcher matcher = pattern.matcher(input);

			StringBuffer s = new StringBuffer();

			while (matcher.find()){
				matcher.appendReplacement(s,attribute + "=\"" + forXML(matcher.group(1)));
			} 

			matcher.appendTail(s);

			return s.toString();
		} else {
			return null;
		}
	}

	private String forXML(String aText) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	private void prepareDocumentFromString(String source) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(
					source)));
			doc.normalizeDocument();

			setDocument(doc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void saveDocument() {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		transformerFactory.setAttribute("indent-number", 4);

		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(getDocument());
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	private void removeAttribute(String tag, String attribute) {
		// Get the root element
		// Node company = document.getFirstChild();

		// Get the staff element , it may not working if tag has spaces, or
		// whatever weird characters in front...it's better to use
		// getElementsByTagName() to get it directly.
		// Node staff = company.getFirstChild();

		// Get the staff element by tag name directly
		Node staff = document.getElementsByTagName("staff").item(0);

		// update staff attribute
		NamedNodeMap attr = staff.getAttributes();
		Node nodeAttr = attr.getNamedItem("id");
		nodeAttr.setTextContent("2");

		// append a new node to staff
		Element age = document.createElement("age");
		age.appendChild(document.createTextNode("28"));
		staff.appendChild(age);

		// loop the staff child node
		NodeList list = staff.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {

			Node node = list.item(i);

			// get the salary element, and update the value
			if ("salary".equals(node.getNodeName())) {
				node.setTextContent("2000000");
			}

			// remove firstname
			if ("firstname".equals(node.getNodeName())) {
				staff.removeChild(node);
			}

		}
	}

	private void removeTag(String tag) {
		NodeList foundTags = document.getElementsByTagName(tag);

		for (int i = 0; i < foundTags.getLength(); i++) {

			Node node = foundTags.item(i);

			document.removeChild(node);
		}

	}

	private boolean isValidPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}

		return true;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
