package cn.com.heaton.advertisersdk.interceptor;

import cn.com.heaton.advertisersdk.utils.ByteUtils;

//响应结果
public class Payload {
    private byte[] scanResult;
    private byte[] productCode = new byte[3];
    private byte[] rollingCode = new byte[3];
    private byte[] randomCode = new byte[3];
    private byte commandCode;

    public byte[] getScanResult() {
        return scanResult;
    }

    public void setScanResult(byte[] scanResult) {
        this.scanResult = scanResult;
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

    public void parse(byte[] payload, Strategy strategy){
        if (strategy == null){
            strategy = new Strategy.Builder().build();
        }
        setScanResult(payload);
        setCommandCode(payload[strategy.getCommandIndex()]);
        System.arraycopy(payload, strategy.getProductIndex(), productCode, 0, productCode.length);
        System.arraycopy(payload, strategy.getRollingIndex(), rollingCode, 0, rollingCode.length);
        System.arraycopy(payload, strategy.getRandomIndex(), randomCode, 0, randomCode.length);
    }

    @Override
    public String toString() {
        return "Payload{" +
                "scanResult=" + byte2Str(scanResult) +
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
