package com.smartparking.amit.parksmart;

import java.util.ArrayList;

public class LevelParams {

    private String Rows;
    private String Columns;
    private ArrayList<SlotIds> slotIdsArrayList = new ArrayList<>();

    public LevelParams(){

    }

    public LevelParams(String rows, String columns) {
        this.Rows = rows;
        this.Columns = columns;
    }

    public String getRows() {
        return Rows;
    }

    public String getColumns() {
        return Columns;
    }
}
