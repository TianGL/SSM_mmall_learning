package cn.geliang.mmall.controller.backend;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.ResponseCode;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.Product;
import cn.geliang.mmall.pojo.User;
import cn.geliang.mmall.service.IFileService;
import cn.geliang.mmall.service.IProductService;
import cn.geliang.mmall.service.IUserServcie;
import cn.geliang.mmall.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserServcie iUserServcie;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 保存商品
     *
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登录管理员");
        }
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 增加产品业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登录管理员");
        }
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 修改产品状态业务逻辑
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 获取产片详情
     *
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登录管理员");
        }
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 获取商品信息
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登录管理员");
        }
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 获取商品列表
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    /**
     * 搜索商品
     *
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登录管理员");
        }
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 查询商品
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 上传文件到ftp服务器
     *
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        System.out.println("uplaod.do");
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登录管理员");
        }
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 上传文件
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);

            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        // 富文本对返回值有要求，
//        JSON response after uploading complete:
//
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 上传文件
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if(StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传成功");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            // 富文本对返回的头也有要求
            response.addHeader("Access-Control-Allow-Headers", "X-File_Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }




}
