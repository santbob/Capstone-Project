package com.letmeeat.letmeeat.models;

/**
 * Created by santhosh on 04/06/2017.
 * Category object represnting the cuisine category
 */

public class Category {

    private String alias;
    private String title;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString(){
        return title;
    }
}
