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
}