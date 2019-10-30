package cn.com.heaton.avertisetest.model;

import java.io.Serializable;

public class SPData implements Serializable {
    private int send_duration = 1000;
    private String address_code = "";
    private String payload = "";

    public SPData(int send_duration, String address_code, String payload) {
        this.send_duration = send_duration;
        this.address_code = address_code;
        this.payload = payload;
    }

    public int getSend_duration() {
        return send_duration;
    }

    public void setSend_duration(int send_duration) {
        this.send_duration = send_duration;
    }

    public String getAddress_code() {
        return address_code;
    }

    public void setAddress_code(String address_code) {
        this.address_code = address_code;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "SPData{" +
                "send_duration=" + send_duration +
                ", address_code='" + address_code + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
