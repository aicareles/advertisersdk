package cn.com.heaton.avertisetest.model;

public class SendRecord {
    private String hexPayload;
    private String time;

    public String getHexPayload() {
        return hexPayload;
    }

    public void setHexPayload(String hexPayload) {
        this.hexPayload = hexPayload;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SendRecord{" +
                "hexPayload='" + hexPayload + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
