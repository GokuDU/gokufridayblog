package com.guo.service.impl;

import com.guo.entity.Category;
import com.guo.mapper.CategoryMapper;
import com.guo.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
