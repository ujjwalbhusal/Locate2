package com.ujwal.locate2.models;

public class txn_layout {

    private String FullName;
    private String NetPay;
    private String PayDate;


    public txn_layout() {
    }

    public txn_layout(String fullName, String netPay, String payDate) {
        FullName = fullName;
        NetPay = netPay;
        PayDate = payDate;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getNetPay() {
        return NetPay;
    }

    public void setNetPay(String netPay) {
        NetPay = netPay;
    }

    public String getPayDate() {
        return PayDate;
    }

    public void setPayDate(String payDate) {
        PayDate = payDate;
    }
}
