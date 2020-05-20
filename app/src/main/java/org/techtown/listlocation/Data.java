package org.techtown.listlocation;

public class Data {
    private double latitude;
    private double longitude;
    private int range;

    public Data(){}

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
