package com.mxy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.dto.SetmealDto;
import com.mxy.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);
    void removeWithDish(List<Long>ids);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void updateStatusById(Integer status, Long id);
}
