package cn.dm.exception;

import cn.dm.common.IErrorCode;

public enum PayErrorCode implements IErrorCode {
    /**通用异常**/
    COMMON_NO_LOGIN("0001","用户未登录"),
    COMMON_Exception("0002","系统异常"),
    /**节目项目异常**/
    PAY_ORDER_CODE("4001","订单状态异常"),
    PAY_ALIPAY_ERROR_CODE("4002","订单支付异常"),
    PAY_NO_EXISTS("4003","订单不存在"),
    ;
    private String errorCode;
    private String errorMessage;

    private PayErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
