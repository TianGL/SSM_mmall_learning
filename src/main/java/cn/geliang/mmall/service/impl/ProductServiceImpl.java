package cn.geliang.mmall.service.impl;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.ResponseCode;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.dao.CategoryMapper;
import cn.geliang.mmall.dao.ProductMapper;
import cn.geliang.mmall.pojo.Category;
import cn.geliang.mmall.pojo.Product;
import cn.geliang.mmall.service.ICategoryService;
import cn.geliang.mmall.service.IProductService;
import cn.geliang.mmall.util.DateTimeUtil;
import cn.geliang.mmall.util.PropertiesUtil;
import cn.geliang.mmall.vo.ProductDetailVo;
import cn.geliang.mmall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加或删除产品
     * @param product
     * @return
     */
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product != null) {
            if(StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }

            if(product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if(rowCount > 0) {
                    return ServerResponse.createBySuccess("产品更新成功");
                } else {
                    return ServerResponse.createBySuccess("产品更新失败");
                }
            } else {
                int rowCount = productMapper.insert(product);
                if(rowCount > 0) {
                    return ServerResponse.createBySuccess("产品添加成功");
                } else {
                    return ServerResponse.createBySuccess("产品添加失败");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("新增产品参数布正确");
        }
    }

    /**
     * 设置销售状态
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if(productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    /**
     * 管理商品信息
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVO = this.assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    /**
     * product 转换为 ProductDetailVo
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVO = new ProductDetailVo();
        productDetailVO.setId(product.getId());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setName(product.getName());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());

        // imageHost
        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.geliang.cn/"));

        // parentCategoryID
        Category category = categoryMapper.selectByPrimaryKey(productDetailVO.getCategoryId());
        if(category == null) {
            productDetailVO.setParentCategoryId(0); // 默认根节点
        } else {
            productDetailVO.setParentCategoryId(category.getParentId());
        }

        // createTime
        productDetailVO.setCreateTime(DateTimeUtil.DateToStr(product.getCreateTime()));
        // updateTIme
        productDetailVO.setUpdateTime(DateTimeUtil.DateToStr(product.getUpdateTime()));

        return productDetailVO;
    }


    /**
     * 获取商品分页后的列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList) {
            productListVoList.add(this.assembleProductListVo(productItem));
        }
        PageInfo pageReslut = new PageInfo(productList);
        pageReslut.setList(productListVoList);
        return ServerResponse.createBySuccess(pageReslut);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();

        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.geliang.cn/"));
        product.setStatus(product.getStatus());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setPrice(product.getPrice());

        return productListVo;
    }

    /**
     * 查询商品并实现分页
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList) {
            productListVoList.add(assembleProductListVo(productItem));
        }
        PageInfo pageReslut = new PageInfo(productList);
        pageReslut.setList(productListVoList);
        return ServerResponse.createBySuccess(pageReslut);
    }

    // portal

    /**
     * 前端获取商品信息
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已下架或者删除");
        }
        ProductDetailVo productDetailVO = this.assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    /**
     * 根据关键字和category_id获取商品列表并分页
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if(StringUtils.isBlank(keyword) && categoryId == null) {
            // 两个参数不能都为空
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)) {
                // 如果分类id不存在，返回空集，不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductDetailVo> productDetailVos = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productDetailVos);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = categoryService.selectChildrenAndCategory(categoryId).getData();
        }

        PageHelper.startPage(pageNum, pageSize);
        String orderByClause = null;

        // 排序处理
        if( StringUtils.isNotBlank(orderBy) && Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            int split = orderBy.lastIndexOf("_");
            orderByClause = orderBy.substring(0, split) + " " + orderBy.substring(split+1, orderBy.length());
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,
                categoryIdList.size()==0?null:categoryIdList, orderByClause);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList) {
            productListVoList.add(assembleProductListVo(product));
            System.out.println(product);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    // 主函数测试
    public static void main(String[] args) {
        String str = "price_desc";
        int split = str.lastIndexOf("_");
        String orderByClause = str.substring(0, split) + " " + str.substring(split+1, str.length());
        System.out.println(orderByClause);
    }


}
