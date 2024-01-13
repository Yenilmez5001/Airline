public class Airport {
    String airportCode;
    String airfieldName;
    double latitude;
    double longitude;
    int parkingCost;

    public Airport(String airportCode, String airfieldName, double latitude, double longitude, int parkingCost) {
        this.airportCode = airportCode;
        this.airfieldName = airfieldName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingCost = parkingCost;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "airportCode='" + airportCode + '\'' +
                ", airfieldName='" + airfieldName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", parkingCost=" + parkingCost +
                '}';
    }
}
