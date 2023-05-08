package com.example.arcus.ui.register;

import java.text.DecimalFormat;

public class Sales {

    private String name;
    private int amount;
    private double price;


    Sales(){
        this.name = "";
        this.amount = 0;
        this.price = 0.00;
    }

    Sales(String name, int amount, double price){
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    DecimalFormat moneyFormat = new DecimalFormat("$0.00");
    @Override
    public String toString() {
        return "x"+ amount + " " + name + " " + moneyFormat.format(price * amount);
    }
}
