package com.example.bykshare;

public class AddRemClass {
    String uname, emailid, password;

    public AddRemClass(String uname, String emailid) {
        this.uname = uname;
        this.emailid = emailid;
    }

    public AddRemClass(String uname, String emailid, String password) {
        this.uname = uname;
        this.emailid = emailid;
        this.password = password;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
