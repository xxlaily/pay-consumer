package cn.dm.service.impl;

import cn.dm.client.RestDmOrderClient;
import cn.dm.client.RestDmTradeClient;
import cn.dm.common.BaseException;
import cn.dm.common.Constants;
import cn.dm.config.AlipayConfig;
import cn.dm.exception.PayErrorCode;
import cn.dm.pojo.DmOrder;
import cn.dm.service.DmTradeService;
import cn.dm.vo.DmItemMessageVo;
import com.alipay.api.internal.util.AlipaySignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-5-22.
 */
@Service
public class DmTradeServiceImpl implements DmTradeService {
    private static final Logger logger = LoggerFactory.getLogger(DmTradeServiceImpl.class);
    @Resource
    private RestDmTradeClient restDmTradeClient;
    @Resource
    private RestDmOrderClient restDmOrderClient;
    @Resource
    private AlipayConfig alipayConfig;

    @Override
    public Integer insertTrade(String orderNo, String tradeNo, Integer payMethod) throws Exception {
        //预留——根据订单编号获取订单信息
        DmOrder dmOrder = loadDmOrderByOrderNo(orderNo);
        //如果订单已经支付则终止后续业务的执行
        if (2 == dmOrder.getOrderType()) {
            return 0;
        }
        DmItemMessageVo dmItemMessageVo = new DmItemMessageVo();
        dmItemMessageVo.setTradeNo(tradeNo);
        dmItemMessageVo.setOrderNo(orderNo);
        dmItemMessageVo.setItemId(dmOrder.getItemId().toString());
        dmItemMessageVo.setUserId(dmOrder.getUserId().toString());
        dmItemMessageVo.setAmount(dmOrder.getTotalAmount());
        dmItemMessageVo.setPayMethod(payMethod);
        return restDmTradeClient.insertTrade(dmItemMessageVo);
    }

    @Override
    public DmOrder loadDmOrderByOrderNo(String orderNo) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderNo", orderNo);
        //根据订单编号查询订单——待引入
        List<DmOrder> dmOrders = restDmOrderClient.getDmOrderListByMap(params);
        if (dmOrders.size() == 0) {
            throw new BaseException(PayErrorCode.PAY_NO_EXISTS);
        }
        return dmOrders.get(0);
    }

    @Override
    public boolean asyncVerifyResult(Map<String, Object> params) throws Exception {
        Map<String, String> verifyParams = new HashMap<String, String>();
        Map<String, String[]> requestParams = (Map<String, String[]>) params.get("requestParams");
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            verifyParams.put(name, valueStr);
        }
        // 计算得出通知验证结果
        boolean verify_result = AlipaySignature.rsaCheckV1(verifyParams, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), "RSA2");
        return verify_result;
    }

    public boolean syncVerifyResult(Map<String, String[]> requestParams) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        // 计算得出通知验证结果
        boolean verify_result = AlipaySignature.rsaCheckV1(params,
                alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), "RSA2");
        return verify_result;
    }

    @Override
    @RabbitListener(queues = Constants.RabbitQueueName.TO_UPDATED_ORDER_QUEUE)
    public void testRabbitMq(DmItemMessageVo dmItemMessageVo) throws Exception {
        logger.info(">>>>>>>>>>>>>>>>>收到消息"+ dmItemMessageVo.getOrderNo());
    }

    @Override
    public boolean processed(String orderNo, Integer flag) throws Exception {
        return restDmOrderClient.processed(orderNo, flag);
    }
}
