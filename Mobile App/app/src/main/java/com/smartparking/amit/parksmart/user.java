package com.smartparking.amit.parksmart;

public class user {

    public String phoneNo,firstName,emailId; //public made it work (declare private)

    public user(){

    }

    public user(String phoneNo, String firstName, String emailId) {
        this.phoneNo = phoneNo;
        this.firstName = firstName;
        this.emailId = emailId;
    }

    public String getFirstName(){
        return firstName;
    }
    public String getEmailId(){
        return emailId;
    }
    public String getPhoneNo(){
        return phoneNo;
    }
    public void setFirstName(String firstName){
        this.firstName  = firstName;
    }
    public void setEmailId(String emailId){
        this.emailId  = emailId;
    }
    public void setPhoneNo(String phoneNo){
        this.phoneNo = phoneNo;
    }
}
