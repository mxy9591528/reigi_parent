package com.mxy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.entity.Category;

public interface CategoryService extends IService<Category> {
  void remove(Long id);
}
