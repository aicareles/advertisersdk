package cn.com.heaton.avertisetest.app;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建时间:  2018/1/5
 * 创建人:Alex-Jerry
 * 功能描述: 全局静态常量值
 */

public final class Constant {
    /**
     * SharePreferences常量保存类
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface SP {

    }

    //API静态
    @Retention(RetentionPolicy.SOURCE)
    public @interface API {

    }

    //设置  id
    @Retention(RetentionPolicy.SOURCE)
    public @interface SET {

    }

    //语言  id
    @Retention(RetentionPolicy.SOURCE)
    public @interface LANGUAGE {

    }

    //eventbus常量
    @Retention(RetentionPolicy.SOURCE)
    public @interface MAIN_EVENT {

    }

}
