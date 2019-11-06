package cn.com.heaton.avertisetest.app;

import java.util.Random;

import com.heaton.advertisersdk.AdvertiserClient;
import com.heaton.advertisersdk.AdvertiserDevice;

/**
 * description 2.4G发送数据协议类
 * created by jerry on 2018/11/28.
 */

public class Protocol {
    private static Protocol sInstance;
    private int mFrameIndex = 1;//帧序号，只要确定用户按下app的按键或者松开，触发手机发送广播时数据就累加1，否则就继续保持当前值发送出去。（车子长按动作时，此值就保持不变）
    private byte[] payload = new byte[16];//装载数据  16byte

    private Protocol() {}

    public static Protocol getInstance() {
        if (sInstance == null) {
            synchronized (AdvertiserClient.class) {
                if (sInstance == null) {
                    sInstance = new Protocol();
                }
            }
        }
        return sInstance;
    }

    private int getFrameIndex() {
        return mFrameIndex > 255 ? mFrameIndex = 1 : mFrameIndex++;
    }

    public void sendCommand(AdvertiserDevice device, byte[] data) {
        payload[0] = (byte) (new Random().nextInt(256) & 0xff);//随机数
        if (device != null) {
            byte[] rolling_code = device.getRollingCode();
            if (rolling_code != null) {
                System.arraycopy(rolling_code, 0, payload, 1, rolling_code.length);
            }
            byte[] random_code = device.getRandomCode();
            if (random_code != null) {
                System.arraycopy(random_code, 0, payload, 13, random_code.length);
            }
        }
        payload[4] = (byte) 0xC0;
        payload[5] = (byte) (getFrameIndex() & 0xff);
        //5、开启广播
        AdvertiserClient.getDefault().startAdvertising(payload);
    }

    public void stopCommand(int bleChannel) {
        payload[0] = (byte) (new Random().nextInt(256) & 0xff);//随机数
        payload[4] = (byte) 0xC0;
        payload[5] = (byte) (getFrameIndex() & 0xff);
        payload[6] = 0x00;
        payload[7] = 0x00;
        payload[8] = 0x00;
        AdvertiserClient.getDefault().startAdvertising(payload, bleChannel);
    }

}
