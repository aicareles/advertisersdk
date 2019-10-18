package cn.com.heaton.advertisersdk.config;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.Size;
import android.text.TextUtils;

import java.util.Random;

import cn.com.heaton.advertisersdk.AdvertiserClient;
import cn.com.heaton.advertisersdk.AdvertiserDevice;
import cn.com.heaton.advertisersdk.interceptor.Interceptor;
import cn.com.heaton.advertisersdk.interceptor.ParseStrategy;
import cn.com.heaton.advertisersdk.utils.ByteUtils;
import cn.com.heaton.advertisersdk.utils.SPUtils;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */

public class AdvertiserConfig {

    /**
     * SharePreferences常量保存类
     */
    private final static String RANDOM_VALUE = "random_value";//Y1,Y2,Y3为随机数  2.4G相关
    private final static String DEFALUT_PREFIX_NAME = "BXC-";
    //2.4G产品值
    private final static byte[] DEFALUT_AVERTISE_PRODUCT = {0x54, 0x00, 0x01};

    private Interceptor interceptor;

    private ParseStrategy parseStrategy;

    private static AdvertiserConfig sConfig;

    private AdvertiserConfig(){}

    public static AdvertiserConfig config(){
        if(sConfig == null){
            sConfig = new AdvertiserConfig();
        }
        return sConfig;
    }

    private String logTAG = "AdvertiserSDK";
    /**
     * 是否打印蓝牙日志
     */
    private boolean logAvertiseExceptions = true;
    /**
     * 是否抛出蓝牙异常
     */
    private boolean throAvertiseException = true;

    /**
     * 蓝牙连接超时时长
     */
    private long connectTimeout = 10 * 1000;

    /**
     * 蓝牙扫描周期时长
     */
    private long scanPeriod = 10 * 1000;

    /**
     * 连接重试次数
     */
    private int retryConnect = 4;

    /**
     * 2.4G 蓝牙名称前缀
     */
    private String prefixAvertiseName = DEFALUT_PREFIX_NAME;

    /**
     * 2.4G 产品码
     */
    private byte[] avertiseProductCode = DEFALUT_AVERTISE_PRODUCT;

    /**
     * 蓝牙MAC地址前3byte：0x54 0x52 0x22(过滤设备，不然iOS广播包会影响)
     * 设备地址前缀
     */
    private String addressPrefix = "";

    public String getLogTAG() {
        return logTAG;
    }

    public void setLogTAG(String logTAG) {
        this.logTAG = logTAG;
    }

    public boolean isLogAvertiseExceptions() {
        return logAvertiseExceptions;
    }

    public AdvertiserConfig setLogAvertiseExceptions(boolean logAvertiseExceptions) {
        this.logAvertiseExceptions = logAvertiseExceptions;
        return this;
    }

    public boolean isThroAvertiseException() {
        return throAvertiseException;
    }

    public AdvertiserConfig setThroAvertiseException(boolean throAvertiseException) {
        this.throAvertiseException = throAvertiseException;
        return this;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public AdvertiserConfig setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public long getScanPeriod() {
        return scanPeriod;
    }

    public AdvertiserConfig setScanPeriod(long scanPeriod) {
        this.scanPeriod = scanPeriod;
        return this;
    }

    public int getRetryConnect() {
        return retryConnect;
    }

    public AdvertiserConfig setRetryConnect(@IntRange(from = 1,to = 5) int retryConnect) {
        this.retryConnect = retryConnect;
        return this;
    }

    public String getPrefixAvertiseName() {
        return prefixAvertiseName;
    }

    public AdvertiserConfig setPrefixAvertiseName(String prefixAvertiseName) {
        this.prefixAvertiseName = prefixAvertiseName;
        return this;
    }

    public byte[] getAvertiseProductCode() {
        return avertiseProductCode;
    }

    public AdvertiserConfig setAvertiseProductCode(@Size(value = 3) byte[] avertiseProductCode) {
        this.avertiseProductCode = avertiseProductCode;
        return this;
    }

    public String getAddressPrefix() {
        return addressPrefix;
    }

    public AdvertiserConfig setAddressPrefix(String addressPrefix) {
        this.addressPrefix = addressPrefix;
        return this;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public AdvertiserConfig setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public ParseStrategy getParseStrategy() {
        return parseStrategy;
    }

    public AdvertiserConfig setParseStrategy(ParseStrategy parseStrategy) {
        this.parseStrategy = parseStrategy;
        return this;
    }

    public AdvertiserClient<AdvertiserDevice> create(Context context){
        return AdvertiserClient.create(context);
    }

    public static byte[] generateRandom() {
        String random_value = SPUtils.get(AdvertiserClient.getDefault().getContext(), RANDOM_VALUE, "");
        if (!TextUtils.isEmpty(random_value)) {
            return ByteUtils.hexStrToByteArray(random_value);
        }
        byte[] random = new byte[3];
        random[0] = (byte) (new Random().nextInt(256) & 0xff);
        random[1] = (byte) (new Random().nextInt(256) & 0xff);
        random[2] = (byte) (new Random().nextInt(256) & 0xff);
        SPUtils.put(AdvertiserClient.getDefault().getContext(), RANDOM_VALUE, ByteUtils.byteArrayToHexStr(random));
        return random;
    }
}
