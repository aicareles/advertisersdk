package cn.com.heaton.advertisersdk.interceptor;

import cn.com.heaton.advertisersdk.utils.ByteUtils;

//请求体
public class Request {
    private byte[] payload;
    private byte[] productCode = new byte[3];
    private byte[] rollingCode = new byte[3];
    private byte[] randomCode = new byte[3];
    private byte commandCode ;

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getProductCode() {
        return productCode;
    }

    public void setProductCode(byte[] productCode) {
        this.productCode = productCode;
    }

    public byte[] getRollingCode() {
        return rollingCode;
    }

    public void setRollingCode(byte[] rollingCode) {
        this.rollingCode = rollingCode;
    }

    public byte[] getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(byte[] randomCode) {
        this.randomCode = randomCode;
    }

    public byte getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(byte commandCode) {
        this.commandCode = commandCode;
    }

    public void parse(byte[] payload){
        setPayload(payload);
        setCommandCode(payload[4]);
        System.arraycopy(payload, 1, productCode, 0, productCode.length);
        System.arraycopy(payload, 6, rollingCode, 0, rollingCode.length);
        System.arraycopy(payload, 13, randomCode, 0, randomCode.length);
    }

    @Override
    public String toString() {
        return "Request{" +
                "payload=" + byte2Str(payload) +
                ", productCode=" + byte2Str(productCode) +
                ", rollingCode=" + byte2Str(rollingCode) +
                ", randomCode=" + byte2Str(randomCode) +
                ", commandCode=" + byte2Str(new byte[]{commandCode}) +
                '}';
    }

    private String byte2Str(byte[] bytes){
        return ByteUtils.BinaryToHexString(bytes);
    }
}
