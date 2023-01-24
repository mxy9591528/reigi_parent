package com.mxy.dto;


import com.mxy.entity.Setmeal;
import com.mxy.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
