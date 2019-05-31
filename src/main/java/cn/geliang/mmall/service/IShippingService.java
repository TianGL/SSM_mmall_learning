package cn.geliang.mmall.service;

import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface IShippingService {
    ServerResponse<Map> add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
