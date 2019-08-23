package wireless.algorithm.io.cshsoft.a2_4gcrytonlib;

import android.provider.Settings;

public class WirelessEncoder {

        // Used to load the 'native-lib' library on application startup.
        static {
            System.loadLibrary("WirelessEncoder");
        }

        public static native void payload(int ble_channel,byte[] rf_payload,byte[] output_ble_payload);

        public static native boolean cipher(byte[] data16bytes,boolean isEncoding);

}
