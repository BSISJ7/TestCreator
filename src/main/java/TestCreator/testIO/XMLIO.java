package TestCreator.testIO;

import TestCreator.Test;
import TestCreator.questions.Question;
import TestCreator.utilities.TestManager;
import javafx.scene.control.Alert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class XMLIO {

    public static final File XML_SAVE_LOCATION = new File("SavedTests.xml");
    private static Document XMLDocument;
    private static Node testsRootNode;
    private static final XMLIO xmlIoInstance = new XMLIO();

    private XMLIO() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            if (!XML_SAVE_LOCATION.exists()) {
               createSaveFile(docBuilder);
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
        } catch (TransformerException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not create save file.");
            alert.showAndWait();
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

    public void loadTests() {
        try {
            if (XMLDocument.hasChildNodes()) {
                XMLDocument.getDocumentElement().normalize();
            }

            for (Node testNode : XmlUtil.asList(testsRootNode.getChildNodes())) {
                if (testNode instanceof Element) {
                    Test newTest = new Test();
                    newTest.loadFromXMLNode((Element) testNode);
                    TestManager.getInstance().addTest(newTest);
                }
            }
        }catch (NullPointerException e) {
            new File("SavedTests.xml").delete();
            createSaveFile();
            e.printStackTrace();
            
        }
    }

    public void saveTests() {
        for(int x = 0; x < TestManager.getInstance().getNumOfTests(); x++){
            testsRootNode.appendChild(TestManager.getInstance().getTestAt(x).getTestAsXMLNode(XMLDocument));
        }
        saveChanges();
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
            TestManager.getInstance().getObservableTestList().forEach(test ->
                    testsRootNode.appendChild(test.getTestAsXMLNode(XMLDocument)));
            DOMSource source = new DOMSource(XMLDocument);
            StreamResult result = new StreamResult(XML_SAVE_LOCATION);
            transformer.transform(source, result);
        } catch (TransformerException | ParserConfigurationException | NullPointerException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not save changes.").showAndWait();
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
        testsRootNode.removeChild(getTestNode(test.getID()));
    }

    private Node getTestNode(String testID) throws NullPointerException {
        for (Node testNode : XmlUtil.asList(testsRootNode.getChildNodes())) {
            Node idNode = findNode("ID", testNode);
            if (idNode != null && idNode.getTextContent().equals(testID))
                return testNode;
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
        testsRootNode.replaceChild(getTestNode(oldTest.getID()), newTest.getTestAsXMLNode(XMLDocument));
    }


    private static void createSaveFile(){
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            createSaveFile(docBuilder);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not create save file.");
            alert.showAndWait();
        }
    }

    private static void createSaveFile(DocumentBuilder docBuilder) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        XMLDocument = docBuilder.newDocument();
        testsRootNode = XMLDocument.createElement("Tests");
        XMLDocument.appendChild(testsRootNode);
        DOMSource source = new DOMSource(XMLDocument);
        StreamResult saveLocation = new StreamResult(XML_SAVE_LOCATION);
        transformer.transform(source, saveLocation);
    }
}
