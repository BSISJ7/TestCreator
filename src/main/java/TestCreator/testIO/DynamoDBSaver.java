package TestCreator.testIO;

import TestCreator.Test;
import TestCreator.questions.MultipleChoice;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDBSaver {

    public final static String TABLE_NAME = "DDB_Test_Table";
    private static String username = "";
    public static final AmazonDynamoDB clientDDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new ProfileCredentialsProvider(username)).build();
    private AmazonDynamoDB ddbClient = null;

    public DynamoDBSaver() {
        try {
            //Requires a / in front to see the file {https://forums.aws.amazon.com/thread.jspa?messageID=336576}
            PropertiesCredentials credentials = new PropertiesCredentials(DynamoDBSaver.class.getResourceAsStream("/AwsCredentials.properties"));
            //DDBClient = new AmazonDynamoDBClient(credentials);
            ddbClient = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new ProfileCredentialsProvider(username)).build();

        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
    }

    @SuppressWarnings("unused")
    public static void main(String... args) {
        List<Test> tests = new ArrayList<Test>();
        String testTableName = DynamoDBSaver.TABLE_NAME;
        DynamoDBSaver DDBSaver = new DynamoDBSaver();
        //DDBSaver.deleteTable(testTableName);
        DDBSaver.createTable(testTableName);
        //DDBSaver.tableExists(testTableName);

        //tests = DDBSaver.getTests(testTableName);
        //DDBSaver.addTest(testTableName, test);
        //DDBSaver.addQuestion(testTableName, tests.get(0), new MultipleChoiceQuestion("1+1=?", "2", Arrays.asList("1", "3", "4")));
    }

    public void createTable(String tableName) {
        List<AttributeDefinition> attriDefinitions = new ArrayList<AttributeDefinition>();
        attriDefinitions.add(new AttributeDefinition().withAttributeName("Question Name").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("Question Name").withKeyType(KeyType.HASH));
        keySchema.add(new KeySchemaElement().withAttributeName("Test Name").withKeyType(KeyType.RANGE));

        ProvisionedThroughput provThroughput = new ProvisionedThroughput().withReadCapacityUnits(10L).withWriteCapacityUnits(10L);

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withAttributeDefinitions(attriDefinitions)
                .withKeySchema(keySchema)
                .withProvisionedThroughput(provThroughput);

        try {
            CreateTableResult result = ddbClient.createTable(request);
            JOptionPane.showMessageDialog(null, "Table Created");
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (ResourceInUseException e) {
            e.printStackTrace();
        }
    }

    public boolean tableExists(String tableName) {
        String lastEvaluatedTableName = null;

        do {

            ListTablesRequest tableList = new ListTablesRequest().withLimit(10).withExclusiveStartTableName(lastEvaluatedTableName);
            ListTablesResult result = ddbClient.listTables(tableList);
            lastEvaluatedTableName = result.getLastEvaluatedTableName();

            for (String name : result.getTableNames()) {
                if (name.equalsIgnoreCase(tableName)) {
                    JOptionPane.showMessageDialog(null, "Table :" + tableName + " exists.");
                    return true;
                }
            }
        } while (lastEvaluatedTableName != null);

        JOptionPane.showMessageDialog(null, "Table :" + tableName + " does not exist.");
        return false;
    }

    public void deleteTable(String tableName) {
        try {
            DeleteTableRequest deleteTable = new DeleteTableRequest().withTableName(tableName);
            DeleteTableResult deleteResult = ddbClient.deleteTable(deleteTable);
            JOptionPane.showMessageDialog(null, "Deleted Table: " + deleteResult.getTableDescription().getTableName());
        } catch (ResourceNotFoundException e) {
            System.out.println("ResourceNotFoundException");/*e.printStackTrace();*/
        } catch (ResourceInUseException e) {
            System.out.println("ResourceInUseException");/*e.printStackTrace();*/
        }
        try {
            new Thread().sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Test> getTests(String tableName) {
        List<Test> tests = new ArrayList<Test>();
        ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
        ScanResult result = ddbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            Test newTest = new Test(item.get("Test Name").getS());
            newTest.setID(item.get("Test ID").getS());
            tests.add(newTest);
        }
        return tests;
    }

    public void addQuestion(String tableName, Test test, MultipleChoice multipleChoiceQuestion) {
        Map<String, AttributeValueUpdate> itemUpdate = new HashMap<String, AttributeValueUpdate>();

        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("Test ID", new AttributeValue().withS(test.getID()));

        itemUpdate.put("Question Name", new AttributeValueUpdate()
                .withAction(AttributeAction.PUT)
                .withValue(new AttributeValue()
                        .withS(multipleChoiceQuestion.getName())));
        itemUpdate.put("Answer", new AttributeValueUpdate()
                .withAction(AttributeAction.PUT)
                .withValue(new AttributeValue()
                        .withN(multipleChoiceQuestion.getAnswer())));

        itemUpdate.put("Choices", new AttributeValueUpdate()
                .withAction(AttributeAction.PUT)
                .withValue(new AttributeValue()
                        .withSS((String[]) multipleChoiceQuestion.getChoicesCopy().toArray())));

        ReturnValue returnValues = ReturnValue.ALL_NEW;

        UpdateItemRequest updateRequest = new UpdateItemRequest()
                .withKey(key)
                .withTableName(tableName)
                .withAttributeUpdates(itemUpdate)
                .withReturnValues(returnValues);

        UpdateItemResult updateResult = ddbClient.updateItem(updateRequest);
    }

    public void addTest(String tableName, Test newTest) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("Test ID", new AttributeValue().withS(newTest.getID()));
        item.put("Test Name", new AttributeValue().withS(newTest.getName()));

        PutItemRequest itemRequest = new PutItemRequest().withTableName(TABLE_NAME).withItem(item);
        ddbClient.putItem(itemRequest);
        item.clear();
    }
}


