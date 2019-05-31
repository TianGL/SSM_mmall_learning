package cn.geliang.mmall.service.impl;

import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.dao.ShippingMapper;
import cn.geliang.mmall.pojo.Shipping;
import cn.geliang.mmall.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cn.geliang.mmall.common.ServerResponse.createBySuccessMessage;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 添加地址
     *
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse<Map> add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map resultMap = Maps.newHashMap();
            resultMap.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess(resultMap);
        }
        return ServerResponse.createByErrorMessage("添加收货地址失败");
    }

    /**
     * 删除地址
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        Integer resultCount = shippingMapper.deleteByShippingIdAndUserId(userId, shippingId);
        System.out.println(resultCount);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    /**
     * 更新地址
     *
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0) {
            return createBySuccessMessage("更新商品成功");
        }
        return ServerResponse.createByErrorMessage("更新商品失败");
    }

    /**
     * 获取地址
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
        if (shipping != null) {
            return ServerResponse.createBySuccess("查询地址成功", shipping);
        }
        return ServerResponse.createByErrorMessage("无法查询到该地址");
    }

    /**
     * 获取地址列表
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
