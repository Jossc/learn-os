package com.cn.jvm.test;


/**
 * @author caoyuliang
 */
public class Point111 {
    /**
     * 纬度
     */
    private double lat;
    /**
     * 经度
     */
    private double lon;

    public Point111(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Point111() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
