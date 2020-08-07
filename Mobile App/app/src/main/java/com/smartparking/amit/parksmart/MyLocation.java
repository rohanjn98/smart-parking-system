package com.smartparking.amit.parksmart;

import java.util.ArrayList;

public class MyLocation {
    private double Latitude,Longitude;
    private String Help;
    private ArrayList<Slots> Slots = new ArrayList<>();
    private String Address;

    public MyLocation() {
    }

    public MyLocation(double Latitude, double Longitude, String Help, ArrayList<Slots> slots, String Address) {
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Help = Help;
        this.Slots = slots;
        this.Address = Address;
    }

    public double getLatitude() {
        return Latitude;
    }
    public String getHelp() {
        return Help;
    }
    public double getLongitude() {
        return Longitude;
    }
    public String getAddress(){return Address;}

}
