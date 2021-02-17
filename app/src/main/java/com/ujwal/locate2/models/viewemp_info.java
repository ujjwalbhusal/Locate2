package com.ujwal.locate2.models;

public class viewemp_info {

    private String FullName;
    private String EIN;
    private String DOJ;
    private String Mobile;
    private String Email;


    public viewemp_info() {
    }

    public viewemp_info(String FullName, String EIN, String DOJ, String mobile, String email) {
        this.FullName = FullName;
        this.EIN = EIN;
        this.DOJ = DOJ;
        this.Mobile = mobile;
        this.Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public String getEIN() {
        return EIN;
    }

    public String getDOJ() {
        return DOJ;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getEmail() {
        return Email;
    }
}
