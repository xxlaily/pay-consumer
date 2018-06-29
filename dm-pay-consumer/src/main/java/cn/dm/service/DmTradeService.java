package cn.dm.service;

import cn.dm.pojo.DmOrder;
import cn.dm.vo.DmItemMessageVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Created by Administrator on 2018-5-22.
 */
public interface DmTradeService {
    /**
     * 插入交易记录
     * @param orderNo，tradeNo
     * @return
     * @throws Exception
     */
    public Integer insertTrade(String orderNo, String tradeNo, Integer payMethod) throws Exception;

    /**
     * 根据订单编号查询订单信息
     * @param orderNo
     * @return
     * @throws Exception
     */
    public DmOrder loadDmOrderByOrderNo(String orderNo) throws Exception;

    /**
     * 支付成功之后支付宝异步返回参数给商户
     * @param params
     * @return
     * @throws Exception
     */
    public boolean asyncVerifyResult(Map<String, Object> params) throws Exception;

    /**
     * 支付成功之后支付宝同步返回参数给商户
     * @param params
     * @return
     * @throws Exception
     */
    public boolean syncVerifyResult(Map<String, String[]> params) throws Exception;

    public void testRabbitMq(DmItemMessageVo dmItemMessageVo) throws Exception;

    /**
     * 判断订单是否支付过
     * @param orderNo
     * @param flag
     * @return
     * @throws Exception
     */
    public boolean processed(String orderNo, Integer flag) throws Exception;
}
