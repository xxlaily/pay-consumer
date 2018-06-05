package cn.dm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  支付宝手机网站接入配置信息
 * @author hduser
 *
 */
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
	private String appID;//    商户appid
	private String rsaPrivateKey;//私钥 pkcs8格式的
	private String notifyUrl;//服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	private String returnUrl;//页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
	private String url;//请求网关地址
	private String charset;//编码
	private String format;//返回格式
	private String alipayPublicKey;//支付宝公钥
	private String logPath;//日志记录目录
	private String signType;//RSA2
	private String paymentSuccessUrl;//支付成功跳转页面
	private String paymentFailureUrl;//支付失败跳转页面
	///////////////////////////////////////////支付结果显示
	
	public String getAppID() {
		return appID;
	}
	public void setAppID(String appID) {
		this.appID = appID;
	}
	public String getRsaPrivateKey() {
		return rsaPrivateKey;
	}
	public void setRsaPrivateKey(String rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getAlipayPublicKey() {
		return alipayPublicKey;
	}
	public void setAlipayPublicKey(String alipayPublicKey) {
		this.alipayPublicKey = alipayPublicKey;
	}
	public String getLogPath() {
		return logPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	
	public String getPaymentSuccessUrl() {
		return paymentSuccessUrl;
	}
	public void setPaymentSuccessUrl(String paymentSuccessUrl) {
		this.paymentSuccessUrl = paymentSuccessUrl;
	}
	public String getPaymentFailureUrl() {
		return paymentFailureUrl;
	}
	public void setPaymentFailureUrl(String paymentFailureUrl) {
		this.paymentFailureUrl = paymentFailureUrl;
	}
}
