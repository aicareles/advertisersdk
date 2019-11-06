package com.heaton.advertisersdk;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;


/**
 *
 * Created by LiuLei on 2016/11/26.
 */

public class AdvertiserDevice implements Parcelable {

    public final static String          TAG                      = AdvertiserDevice.class.getSimpleName();

    /**
     *  连接状态
     *  2503 未连接状态
     *  2504 正在连接
     *  2505 连接成功
     */
    private int pairState = AdvertiserStates.StatusCode.UNPAIR;

    /*蓝牙地址*/
    private String bleAddress;

    /*蓝牙名称*/
    private String bleName;

    /*蓝牙重命名名称（别名）*/
    private String bleAlias;

    private boolean isOn;//硬件是否开启

    private byte[] rollingCode;//X1 X2 X3为2.4G变形车的滚码值，每个产品是唯一的值

    private byte[] randomCode;//Y1 Y2 Y3为生成的随机码（每台app第一次生成，以后相同）

    protected AdvertiserDevice() {}

    /**
     * Use the address and name of the BluetoothDevice object
     * to construct the address and name of the {@code AdvertiserDevice} object
     *
     * @param device AdvertiserDevice
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public AdvertiserDevice(BluetoothDevice device) {
        this.bleAddress = device.getAddress();
        this.bleName = device.getName();
    }

    protected AdvertiserDevice(Parcel in) {
        pairState = in.readInt();
        bleAddress = in.readString();
        bleName = in.readString();
        bleAlias = in.readString();
        isOn = in.readByte() != 0;
        rollingCode = in.createByteArray();
        randomCode = in.createByteArray();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static final Creator<AdvertiserDevice> CREATOR = new Creator<AdvertiserDevice>() {
        @Override
        public AdvertiserDevice createFromParcel(Parcel in) {
            return new AdvertiserDevice(in);
        }

        @Override
        public AdvertiserDevice[] newArray(int size) {
            return new AdvertiserDevice[size];
        }
    };

    public boolean isPaired() {
        return pairState == AdvertiserStates.StatusCode.PAIRED;
    }

    public boolean isPairing() {
        return pairState == AdvertiserStates.StatusCode.PAIRING;
    }

    public int getPairState() {
        return pairState;
    }

    public void setPairState(@AdvertiserStates.StatusCode int state){
        pairState = state;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String address) {
        this.bleAddress = address;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String name) {
        this.bleName = name;
    }

    public String getBleAlias() {
        return bleAlias;
    }

    public void setBleAlias(String alias) {
        this.bleAlias = alias;
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

    public byte[] getProductCode() {
        return AdvertiserConfig.config().getAvertiseProductCode();
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    @Override
    public String toString() {
        return "AdvertiserDevice{" +
                "pairState=" + pairState +
                ", bleAddress='" + bleAddress + '\'' +
                ", bleName='" + bleName + '\'' +
                ", bleAlias='" + bleAlias + '\'' +
                ", isOn=" + isOn +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pairState);
        dest.writeString(bleAddress);
        dest.writeString(bleName);
        dest.writeString(bleAlias);
        dest.writeByte((byte) (isOn ? 1 : 0));
        dest.writeByteArray(rollingCode);
        dest.writeByteArray(randomCode);
    }
}
