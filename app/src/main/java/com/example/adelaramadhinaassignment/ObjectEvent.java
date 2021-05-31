package com.example.adelaramadhinaassignment;


public class ObjectEvent {
    String title;
    String description;
    String imageFilename;
    String databaseKey = "";

    public ObjectEvent(String title, String description, String imageFilename) {
        this.title = title;
        this.description = description;
        this.imageFilename = imageFilename;

    }

    public String getTitle(){

        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public String getImageFilename(){ return this.imageFilename;}

    public String setKeyDatabase() {
        return this.databaseKey;
    }

    public void getDatabaseKey(String databaseKey)
    {
       this.databaseKey = databaseKey;
    }


    public String toString(){
        return this.title;
    }
}

