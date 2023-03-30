package com.example.arcus.signin;

import java.util.Arrays;

public class Register {

    private String standName;
    private int standNum, regNum;
    private Item [] items;

    public Register(){

    }

    public Register(String standName, int standNum, int regNum, Item [] items){

        this.standName = standName;
        this.standNum = standNum;
        this.regNum = regNum;
        this.items = items;

    }


    public String getStandName() {
        return standName;
    }

    public void setStandName(String standName) {
        this.standName = standName;
    }

    public int getStandNum() {
        return standNum;
    }

    public void setStandNum(int standNum) {
        this.standNum = standNum;
    }

    public int getRegNum() {
        return regNum;
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Register{" +
                "standName='" + standName + '\'' +
                ", standNum=" + standNum +
                ", regNum=" + regNum +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
