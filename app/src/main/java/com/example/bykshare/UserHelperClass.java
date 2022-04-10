package com.example.bykshare;

public class UserHelperClass {

    String fullname, emailid, pwd, mobileno, gender;

    public UserHelperClass(String fullname, String emailid, String pwd, String mobileno, String gender) {
        this.fullname = fullname;
        this.emailid = emailid;
        this.pwd = pwd;
        this.mobileno = mobileno;
        this.gender = gender;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
