package bank.axis.nearbyme.Database;

/**
 * Created by LAKSHESH on 22-Jun-17.
 */

public class PublicInfo {
    private String uid;

    public PublicInfo(){}

    public coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(coordinates coordinates) {
        this.coordinates = coordinates;
    }

    private coordinates coordinates;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static class coordinates{

        public coordinates(){}

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        private Double latitude;
        private Double longitude;
    }
}
