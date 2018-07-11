package cn.dm.controller;

import cn.dm.common.BaseException;
import cn.dm.common.Constants;
import cn.dm.common.Dto;
import cn.dm.common.DtoUtil;
import cn.dm.config.WXPayConfig;
import cn.dm.exception.PayErrorCode;
import cn.dm.pojo.DmOrder;
import cn.dm.service.DmTradeService;
import cn.dm.util.WXPayRequest;
import cn.dm.util.WXPayUtil;
import cn.dm.vo.OrderPayStatusVo;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018-5-22.
 */
@Controller
@RequestMapping("/api/v/wxpay")
public class WxPayController {
    //**根据订单号生成二维码**//
    private Logger logger = Logger.getLogger(WxPayController.class);

    @Resource
    private DmTradeService dmTradeService;

    @Resource
    private WXPayConfig wxPayConfig;

    /**
     * 订单微信支付
     *
     * @param orderNo
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "/createqccode/{orderNo}", method = RequestMethod.GET)
    @ResponseBody
    public Dto createQcCode(@PathVariable String orderNo, HttpServletResponse response,
                            HttpServletRequest request) throws Exception {
        DmOrder order = dmTradeService.loadDmOrderByOrderNo(orderNo);
        //定义map类型的变量data，其中存放进行请求统一下单所需要的参数
        HashMap<String, String> data = new HashMap<String, String>();
        HashMap<String, Object> result = new HashMap<String, Object>();
        WXPayRequest wxPayRequest = new WXPayRequest(this.wxPayConfig);
        order = dmTradeService.loadDmOrderByOrderNo(orderNo);
        if (order == null || order.getOrderType() != 0) {
            throw new BaseException(PayErrorCode.PAY_ORDER_CODE);
        }
        logger.info("[createQcCode]" + "获取订单信息成功，订单编号为：" + order.getOrderNo());
        data.put("body", "微信订单支付");
        data.put("out_trade_no", orderNo);
        data.put("device_info", "");
        data.put("total_fee", "1");
        data.put("spbill_create_ip", "169.254.193.209");
        //请求支付接口并返回参数
        Map<String, String> r = wxPayRequest.unifiedorder(data);
        String resultCode = r.get("result_code");
        if (resultCode.equals("SUCCESS")) {
            //根据订单获取对应的剧集的信息，这里获取的是剧集的编号
            result.put("itemName", order.getItemName());
            result.put("orderNo", order.getOrderNo());
            //获取订单总金额
            result.put("totalAmount", order.getTotalAmount());
            //二维码对应的url
            result.put("codeUrl", r.get("code_url"));
            return DtoUtil.returnDataSuccess(result);
        } else {
            logger.info(r.get("return_msg"));
            throw new BaseException(PayErrorCode.PAY_ORDER_CODE);
        }
    }

    /***
     *
     * 监听订单支付状态
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/queryorderstatus/{orderNo}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<DmOrder> queryOrderIsSuccess(@PathVariable String orderNo, HttpServletResponse response) throws Exception {
        DmOrder order = null;
        OrderPayStatusVo orderPayStatusVo = new OrderPayStatusVo();
        order = dmTradeService.loadDmOrderByOrderNo(orderNo);
        orderPayStatusVo.setOrderNo(order.getOrderNo());
        orderPayStatusVo.setOrderType(order.getOrderType());
        return DtoUtil.returnDataSuccess(orderPayStatusVo);
    }

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    @ResponseBody
    public String paymentCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WXPayRequest wxPayRequest = new WXPayRequest(this.wxPayConfig);
        Map<String, String> result = new HashMap<String, String>();
        Map<String, String> params = null;
        String returnxml = "";
        InputStream inputStream;
        StringBuilder sb = new StringBuilder();
        //以字节流的形式读取request中的数据
        inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();
        params = WXPayUtil.xmlToMap(sb.toString());
        logger.info("1.notify-params>>>>>>>>>>>:" + params);
        //判断签名是否正确
        boolean flag = wxPayRequest.isResponseSignatureValid(params);
        logger.info("2.notify-flag:" + flag);
        if (flag) {
            String returnCode = params.get("return_code");
            logger.info("3.returnCode:" + returnCode);
            if (returnCode.equals("SUCCESS")) {
                //获取微信订单号
                String transactionId = params.get("transaction_id");
                //获取商户订单号
                String outTradeNo = params.get("out_trade_no");
                if (dmTradeService.processed(outTradeNo, Constants.PayMethod.WEIXIN)) {
                    logger.info("[paymentCallBack]" + "订单" + outTradeNo + "已经支付成功！可以开始修改系统自身业务了");
                    dmTradeService.insertTrade(outTradeNo, transactionId, Constants.PayMethod.WEIXIN);
                    logger.info("[paymentCallBack]" + "系统业务修改完成！交易编号为："+transactionId);
                }
                logger.info("4.订单：" + outTradeNo + " 交易完成" + ">>>" + transactionId);
            } else {
                result.put("return_code", "FAIL");
                result.put("return_msg", "支付失败");
                logger.info("");
            }
        } else {
            result.put("return_code", "FAIL");
            result.put("return_msg", "签名失败");
            logger.info("签名验证失败>>>>>>>>>>>>");
        }
        returnxml = WXPayUtil.mapToXml(result);
        return returnxml;
    }
}
