package cn.geliang.mmall.service;

import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectChildrenAndCategory(Integer categoryId);
}
