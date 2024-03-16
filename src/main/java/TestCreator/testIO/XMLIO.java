package TestCreator.testIO;

import TestCreator.Test;
import TestCreator.questions.Question;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.TestManager;
import javafx.scene.layout.StackPane;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class XMLIO {

    public static final String DATABASE_NAME = "Test Data.xml";
    public static final String BACKUP_DATABASE_NAME = "Test Data.bak";
    public static final File XML_SAVE_LOCATION = new File(DATABASE_NAME);
    private static Document XMLDocument;
    private static Node testsXMLNode;
    private static final XMLIO xmlIoInstance = new XMLIO();
    private static StackPane rootNode;

    private XMLIO() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            if (!XML_SAVE_LOCATION.exists()) {
                createSaveFile(docBuilder);
            } else {
                loadExistingFile(docBuilder);
            }
        } catch (TransformerException | ParserConfigurationException e) {
            new StackPaneAlert(rootNode, STR."Could not load save file.  \{e.getMessage()}").show();
        }
    }

    private void loadExistingFile(DocumentBuilder docBuilder) {
        try {
            XMLDocument = docBuilder.parse(XML_SAVE_LOCATION);
            testsXMLNode = findNode("Tests", XMLDocument);
        } catch (SAXException | IOException e) {
            new StackPaneAlert(rootNode, STR."Could not load save file. Loading a backup.  \{e.getMessage()}")
                    .showAndWait().thenAccept(_ -> loadBackup());
        }
    }

    public static XMLIO getInstance() {
        return xmlIoInstance;
    }

    public static void setTestsXMLNode(StackPane stackNode) {
        rootNode = stackNode;
    }

    public static Node findNode(String childNode, Node parent){
        List<Node> nodeList = XmlUtil.asList(parent.getChildNodes());
        for (Node node : nodeList) {
            if (node.getNodeName().equalsIgnoreCase(childNode)) {
                return node;
            }
        }
        return null;
    }

    public void loadTests() {
        try {
            if (XMLDocument.hasChildNodes()) {
                XMLDocument.getDocumentElement().normalize();
            }

            for (Node testNode : XmlUtil.asList(testsXMLNode.getChildNodes())) {
                if(!testNode.getNodeName().equals("Test")) continue;

                Node idNode = findNode("ID", testNode);
                if (idNode == null) throw new NoSuchElementException();

                String testID = idNode.getTextContent();
                if (testNode instanceof Element && !TestManager.getInstance().containsTest(testID)) {
                    Test newTest = new Test();
                    newTest.loadFromXMLNode((Element) testNode);
                    try {
                        TestManager.getInstance().addTest(newTest);
                    }catch (IllegalArgumentException e){}
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            restoreBackup();
        }
    }

    private void restoreBackup() {
        File backupFile = new File(STR."\{BACKUP_DATABASE_NAME}.xml");
        File backupFile2 = new File(STR."\{BACKUP_DATABASE_NAME}2.xml");
        File backupFile3 = new File(STR."\{BACKUP_DATABASE_NAME}3.xml");

        if (backupFile.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                XMLDocument = docBuilder.parse(backupFile);
                testsXMLNode = findNode("Tests", XMLDocument);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else if (backupFile2.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                XMLDocument = docBuilder.parse(backupFile2);
                testsXMLNode = findNode("Tests", XMLDocument);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else if (backupFile3.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                XMLDocument = docBuilder.parse(backupFile3);
                testsXMLNode = findNode("Tests", XMLDocument);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            createSaveFile();
        }


    }

    public void saveTests() {
        if(true) return;
        for (int x = 0; x < TestManager.getInstance().getNumOfTests(); x++) {
            testsXMLNode.appendChild(TestManager.getInstance().getTestAt(x).getTestAsXMLNode(XMLDocument));
        }
        saveChanges();
    }

    public void saveChanges() {
        try {
            Document XMLDocument = createNewDocument();
            Node testsRootNode = createTestsRootNode(XMLDocument);
            appendTestsToRootNode(testsRootNode, XMLDocument);
            transformDocumentToXML(XMLDocument, testsRootNode);
        } catch (TransformerException | ParserConfigurationException | NullPointerException e) {
            new StackPaneAlert(rootNode, STR."Could not save changes.  \{e.getMessage()}").show();
        }
    }

    private Document createNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    private Node createTestsRootNode(Document XMLDocument) {
        return XMLDocument.createElement("Tests");
    }

    private void appendTestsToRootNode(Node testsRootNode, Document XMLDocument) {
        TestManager.getInstance().getTestlistCopy().forEach(test ->
                testsRootNode.appendChild(test.getTestAsXMLNode(XMLDocument)));
        XMLDocument.appendChild(testsRootNode);
    }

    private void transformDocumentToXML(Document XMLDocument, Node testsRootNode) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(XMLDocument);
        StreamResult result = new StreamResult(XML_SAVE_LOCATION);
        transformer.transform(source, result);
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
        testsXMLNode.removeChild(getTestNode(test.getID()));
    }

    private Node getTestNode(String testID) throws NullPointerException {
        for (Node testNode : XmlUtil.asList(testsXMLNode.getChildNodes())) {
            Node idNode = findNode("ID", testNode);
            if (idNode != null && idNode.getTextContent().equals(testID))
                return testNode;
        }
        return null;
    }

    public void deleteQuestion(Question question) {
        Node testNode = findNode(question.getOwningTest().getName(), testsXMLNode);
        Node questionNode = findNode(question.getName(), Objects.requireNonNull(testNode));
        testNode.removeChild(questionNode);
    }

    public void updateQuestion(Question question) {
        Node testNode = findNode(question.getOwningTest().getName(), testsXMLNode);
        Node questionNode = findNode(question.getName(), Objects.requireNonNull(testNode));
        testNode.replaceChild(question.getQuestionAsXMLNode(), questionNode);
    }

    public void updateTest(Test oldTest, Test newTest) {
        testsXMLNode.replaceChild(getTestNode(oldTest.getID()), newTest.getTestAsXMLNode(XMLDocument));
    }


    private static void createSaveFile() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            createSaveFile(docBuilder);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
            new StackPaneAlert(rootNode, "Could not create save file.").show();
        }
    }

    private static void createSaveFile(DocumentBuilder docBuilder) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        XMLDocument = docBuilder.newDocument();
        testsXMLNode = XMLDocument.createElement("Tests");
        XMLDocument.appendChild(testsXMLNode);
        DOMSource source = new DOMSource(XMLDocument);
        StreamResult saveLocation = new StreamResult(XML_SAVE_LOCATION);
        transformer.transform(source, saveLocation);
    }

    public void backupDatabase() {
        File backupFile = new File(STR."\{BACKUP_DATABASE_NAME}.xml");
        File backupFile2 = new File(STR."\{BACKUP_DATABASE_NAME}2.xml");
        File backupFile3 = new File(STR."\{BACKUP_DATABASE_NAME}3.xml");

        if (backupFile.exists()) {
            if (backupFile2.exists()) {
                if (backupFile3.exists()) {
                    backupFile3.delete();
                }
                backupFile2.renameTo(backupFile3);
            }
            backupFile.renameTo(backupFile2);
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document XMLDocument = docBuilder.parse(XML_SAVE_LOCATION);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(XMLDocument);
            StreamResult result = new StreamResult(backupFile);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private void loadBackup() {
        File backupFile = new File(STR."\{DATABASE_NAME}.backup");
        File backupFile2 = new File(STR."\{DATABASE_NAME}.backup2");
        File backupFile3 = new File(STR."\{DATABASE_NAME}.backup3");

        if (backupFile.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                XMLDocument = docBuilder.parse(backupFile);
                testsXMLNode = findNode("Tests", XMLDocument);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else if (backupFile2.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                XMLDocument = docBuilder.parse(backupFile2);
                testsXMLNode = findNode("Tests", XMLDocument);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else if (backupFile3.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                XMLDocument = docBuilder.parse(backupFile3);
                testsXMLNode = findNode("Tests", XMLDocument);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            createSaveFile();
        }
    }
}
