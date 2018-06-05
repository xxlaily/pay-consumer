package cn.dm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/****
 * 微信的配置类
 */
@Component
@ConfigurationProperties(prefix = "wxpay")
public class WXPayConfig {

    private String appID;//微信支付分配的公众账号ID（企业号corpid即为此appId）

    private String mchID;//微信支付分配的商户号

    private String key;//用于加密生成signValue的一个参数

    private String notifyUrl;//支付成功后微信通知商户支付结果的请求地址

    private String successUrl;//支付成功后的商户平台跳转地址

    private String failUrl;//支付失败后的商户平台跳转地址

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getFailUrl() {
        return failUrl;
    }

    public void setFailUrl(String failUrl) {
        this.failUrl = failUrl;
    }
}
