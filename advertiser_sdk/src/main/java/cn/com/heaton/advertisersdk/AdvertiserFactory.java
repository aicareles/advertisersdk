package cn.com.heaton.advertisersdk;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 蓝牙工厂类
 * Created by LiuLei on 2017/5/3.
 */

public class AdvertiserFactory<T extends AdvertiserDevice> {

    public AdvertiserFactory() {
    }

    public static <T extends AdvertiserDevice> T create(Class<T> cls, BluetoothDevice device) {
        try {
            Constructor constructor = cls.getDeclaredConstructor(BluetoothDevice.class);
            constructor.setAccessible(true);
            try {
                T newDevice = (T) constructor.newInstance(device);
                return newDevice;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new ClassCastException("Class must implements AdvertiserDevice");
    }

    public static <T extends AdvertiserDevice> T newDevice(BluetoothDevice device) {
        return (T) new AdvertiserDevice(device);
    }

}
