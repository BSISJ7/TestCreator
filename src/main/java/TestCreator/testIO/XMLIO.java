package TestCreator.testIO;

import TestCreator.questions.Question;
import TestCreator.Main;
import TestCreator.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class XMLIO {

    public static final File XML_SAVE_LOCATION = new File(Main.workDir + "\\XMLTest.xml");
    private static Document XMLDocument;
    private static Node testsRootNode;
    private static XMLIO xmlIoInstance = new XMLIO();

    private XMLIO() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            if (!XML_SAVE_LOCATION.exists()) {
                try {
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    XMLDocument = docBuilder.newDocument();
                    testsRootNode = XMLDocument.createElement("Tests");
                    XMLDocument.appendChild(testsRootNode);
                    DOMSource source = new DOMSource(XMLDocument);
                    StreamResult saveLocation = new StreamResult(XML_SAVE_LOCATION);
                    transformer.transform(source, saveLocation);
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    XMLDocument = docBuilder.parse(XML_SAVE_LOCATION);
                    testsRootNode = findNode("Tests", XMLDocument);
                } catch (SAXException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static XMLIO getInstance() {
        return xmlIoInstance;
    }

    public static Node findNode(String childNode, Node parent) throws NullPointerException {
        try {
            return XmlUtil.asList(parent.getChildNodes()).stream()
                    .filter(node -> node.getNodeName().equalsIgnoreCase(childNode))
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public ArrayList<Test> loadTests() {

        if (XML_SAVE_LOCATION == null || !XML_SAVE_LOCATION.exists()) {
            return new ArrayList<Test>();
        }

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        if (XMLDocument.hasChildNodes()) {
            XMLDocument.getDocumentElement().normalize();
        }

        ArrayList<Test> tests = new ArrayList<>();
        for (Node testNode : XmlUtil.asList(testsRootNode.getChildNodes())) {
            Test newTest = new Test();
            newTest.loadFromXMLNode((Element) testNode);
            tests.add(newTest);
        }
        return tests;
    }

    public void saveTests(ArrayList<Test> tests) {
        tests.forEach(test -> {
            testsRootNode.appendChild(test.getTestAsXMLNode(XMLDocument));
            saveChanges();
        });
    }

    private void saveChanges() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Document XMLDocument = docBuilder.newDocument();
            Node testsRootNode = XMLDocument.createElement("Tests");

            XMLDocument.appendChild(testsRootNode);
            TestData.getInstance().getTests().stream().map(test -> testsRootNode.appendChild(test.getTestAsXMLNode(XMLDocument)));
            DOMSource source = new DOMSource(XMLDocument);
            StreamResult result = new StreamResult(XML_SAVE_LOCATION);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private boolean nodeExists(Node searchNode, Node parent) {
        NodeList nodeList = parent.getChildNodes();
        String searchNodeName = searchNode.getNodeName();
        for (int x = 0; x < nodeList.getLength(); x++) {
            String getNodeName = nodeList.item(x).getNodeName();
            if (getNodeName.equalsIgnoreCase(searchNodeName)) {
                return true;
            }
        }

        return false;
    }

    public void deleteTest(Test test) {
        testsRootNode.removeChild(getTestNode(test.getName()));
    }

    private Node getTestNode(String testName) throws NullPointerException {
        for (Node testNode : XmlUtil.asList(testsRootNode.getChildNodes())) {
            if (findNode("TestName", testNode).getTextContent().equals(testName)) {
                return testNode;
            }
        }
        return null;
    }

    public void deleteQuestion(Question question) {
        Node testNode = findNode(question.getOwningTest().getName(), testsRootNode);
        Node questionNode = findNode(question.getName(), testNode);
        testNode.removeChild(questionNode);
    }

    public void updateQuestion(Question question) {
        Node testNode = findNode(question.getOwningTest().getName(), testsRootNode);
        Node questionNode = findNode(question.getName(), testNode);
        testNode.replaceChild(question.getQuestionAsXMLNode(), questionNode);
    }

    public void updateTest(Test oldTest, Test newTest) {
        testsRootNode.replaceChild(getTestNode(oldTest.getName()), newTest.getTestAsXMLNode(XMLDocument));
    }
}
