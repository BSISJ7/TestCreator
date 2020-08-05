package com.BSISJ7.TestCreator.testIO;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = DynamoDBSaver.TABLE_NAME)
public class ProductCatalogItem {

    private Integer id;  //partition key
    private Pictures pictures;
    /* ...other attributes omitted... */

    @DynamoDBHashKey(attributeName = "Test Id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "Test Name")
    public Pictures getPictures() {
        return pictures;
    }

    public void setPictures(Pictures pictures) {
        this.pictures = pictures;
    }

    // Additional properties go here. 

    @DynamoDBDocument
    public static class Pictures {
        private String frontView;
        private String rearView;
        private String sideView;

        @DynamoDBAttribute(attributeName = "FrontView")
        public String getFrontView() {
            return frontView;
        }

        public void setFrontView(String frontView) {
            this.frontView = frontView;
        }

        @DynamoDBAttribute(attributeName = "RearView")
        public String getRearView() {
            return rearView;
        }

        public void setRearView(String rearView) {
            this.rearView = rearView;
        }

        @DynamoDBAttribute(attributeName = "SideView")
        public String getSideView() {
            return sideView;
        }

        public void setSideView(String sideView) {
            this.sideView = sideView;
        }

    }
}