package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CinemaHall {
    @JsonProperty("rows")
    private int rows;
    @JsonProperty("cols")
    private int cols;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }
}
