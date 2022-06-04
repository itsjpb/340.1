package com.itsjpb13;
public class Cam{
    String url;
    double[] coordinates;
    String desc;
    String image;
    String type;

    public Cam(double[] coordinates, String desc, String image, String type) {
        this.coordinates = coordinates;
        this.desc = desc;
        this.image = image;
        this.type = type;

        if (type.equals("sdot")) {
            this.url = "https://www.seattle.gov/trafficcams/images/" + image;
        } else {
            this.url = "https://images.wsdot.wa.gov/nw/" + image;
        }

    }

    public String getUrl() {
        return url;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public double getLat() {return coordinates[0];}

    public double getLong() {return coordinates[1];}

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}