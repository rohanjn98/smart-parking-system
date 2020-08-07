package com.smartparking.amit.parksmart;

public class BookedSlots extends Slots{
    private int OTP;
    private String Id;
    public void Slots(){

    }
    public void setAvailability(int OTP){
        this.OTP= OTP;
    }
    public void setId(String Id){
        this.Id = Id;
    }
}
