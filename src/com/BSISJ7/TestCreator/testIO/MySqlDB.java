package com.BSISJ7.TestCreator.testIO;

import com.BSISJ7.TestCreator.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class MySqlDB implements Runnable {

    private static Connection dbConn;
    private String dbName;
    private String serverName;
    private String userName;
    private String password;
    private String hostName;
    private String portNum;
    private Properties connProps;
    private boolean isOpen = false;
    private boolean localDB = true;

    private ArrayList<Test> tests;


    /**
     * Sets the fields required to login to the database, creates a new database and table if they do not exist
     */
    public MySqlDB(String serverName, String hostName, String portNum, String dbName, String userName, String password) {
        this.serverName = serverName;
        this.hostName = hostName;
        this.portNum = portNum;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;

        connProps = new Properties();
        connProps.put("user", userName);
        connProps.put("password", password);
        connectToDB();
        disconnectFromDB();
        //createDB();
        //createTable();
    }

    public static void main(String[] args) {
        //MySQLDB testDB = new MySQLDB("mysql", "localhost", "3306", "demo", "student", "student");
        //MySQLDB testMakerDB = new MySQLDB("mysql", "localhost", "3306", "TestMakerDB", "user", "pass");
        //testDB.showTables();
        MySqlDB testDB = new MySqlDB("mysql", "testdb.clmixbwwn9me.us-east-2.rds.amazonaws.com", "3306", "TestDB", "user", "pass");
    }

    /**
     * Creates a new database if it does not already exist
     */
    public void createDB() {
        try {
            dbConn = DriverManager.getConnection("jdbc:" + serverName + "://" + hostName + ":" + portNum, connProps);
            PreparedStatement stmnt = dbConn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmnt.execute();
            System.out.println("Setup Complete");
        } catch (SQLException SQLE) {
            System.out.println("Setup Database Error: " + SQLE);
        }
    }

    /**
     * Creates the Tests table if it does not exist
     */
    public void createTable() {
        try {
            connectToDB();
            Statement stmnt = dbConn.createStatement();
            stmnt.executeUpdate("CREATE TABLE Tests"
                    + " test_name VARCHAR(255), "
                    + " description VARCHAR(255), "
                    + " total_questions VARCHAR(255), "
                    + " review_date VARCHAR(255), "
                    + " question_name VARCHAR(255), "
                    + " question_type VARCHAR(255), "
                    + " test_name VARCHAR(255), "
                    + " question_type INT(10)");
        } catch (SQLException SQLE) {
            System.out.println("MySQLDB.setupTable: " + SQLE);
        }
    }

    /**
     * Connects to database
     */
    public void connectToDB() {
        try {
            //Connect to DB
            dbConn = DriverManager.getConnection("jdbc:" + serverName + "://" + hostName + ":" + portNum + "/" + dbName, connProps);
            System.out.println("Connection Complete");
        } catch (Exception dbError) {
            System.out.println("Error connecting to database: " + dbError);
        }
    }

    /**
     * Disconnects from database
     */
    public void disconnectFromDB() {
        try {
            //dbConn = DriverManager.
            System.out.println("Connection Complete");
        } catch (Exception dbError) {
            System.out.println("Error connecting to database: " + dbError);
        }
    }

    public void queryDB(String query) {
        try {
            connectToDB();
            Statement stmnt = dbConn.createStatement();
            ResultSet resultSet = stmnt.executeQuery("select * from employees");
            System.out.println("\n============================================================\n");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("id") + ". " + resultSet.getString("last_name") + ", " + resultSet.getString("first_name") + ", " + resultSet.getString("email"));
            }
        } catch (SQLException SQLE) {
            System.out.println("Setup Database Error: " + SQLE);
        }
    }

    /**
     * Updates a value in the database
     */
    public void updateTable() {
        try {
            connectToDB();
            Statement stmnt = dbConn.createStatement();
            stmnt.executeUpdate("update employees "
                    + " set email='demo@javatestemail.com'"
                    + " where id=13");
        } catch (SQLException SQLE) {
            System.out.println("Setup Database Error: " + SQLE);
        }
    }

    /**
     * Inserts data into the database
     */
    public void insertData(String data) {
        try {
            connectToDB();
            Statement stmnt = dbConn.createStatement();
            stmnt.executeUpdate("insert into employees "
                    + " (last_name, first_name, email)"
                    + " values ('Brown', 'David', 'david.brown@foo.com')");
            System.out.println("Insert Complete");
        } catch (SQLException SQLE) {
            System.out.println("Setup Database Error: " + SQLE);
        }
    }

    /**
     * Deletes data in the database
     */
    public void deleteData() {
        try {
            connectToDB();
            Statement stmnt = dbConn.createStatement();
            stmnt.executeUpdate("delete from employees where last_name='Brown'");
            System.out.println("Delete Successful!!");
        } catch (SQLException SQLE) {
            System.out.println("MySQLDB.deleteData Error: " + SQLE);
        }
    }

    /**
     * Prints the name of all tables in the database
     */
    public void showTables() {
        try {
            connectToDB();
            DatabaseMetaData dbMetaData = dbConn.getMetaData();
            ResultSet resultSet = dbMetaData.getTables(null, null, "%", null);
            while (resultSet.next()) {
                System.out.println("Database: " + resultSet.getString(1));
                System.out.println("Table: " + resultSet.getString(3));
            }
        } catch (SQLException SQLE) {
            System.out.println("MySQLDB.showTables Error: " + SQLE);
        }
    }

    /**
     * Removes a single test from the database
     */
    public void deleteTest(Test test) {
        try {
            connectToDB();
            Statement stmnt = dbConn.createStatement();
            stmnt.executeUpdate("delete from " + dbName + " where test_name='" + test.getName() + "'");
        } catch (SQLException SQLE) {
            System.out.println("MySQLDB.deleteTestKeyPress SQL Exception: " + SQLE);
        }
    }

    /**
     * Saves all tests
     */
    public void saveTests(ArrayList<Test> tests) {

    }

    /**
     * Saves a single test.
     */
    public void saveTest(Test test) {

    }

    /**
     * Queries the database for tests and returns them as an ArrayList<Test>
     */
    public ArrayList<Test> getTests() {
        return null;
    }

    /**
     * Removes a question from the database
     */
    public void deleteQuestion() {

    }

    public void run() {

    }
}
	
