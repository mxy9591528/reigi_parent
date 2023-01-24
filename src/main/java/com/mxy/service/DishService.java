package com.mxy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.dto.DishDto;
import com.mxy.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);
    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void updateStatusById(Integer status, Long id);
}
