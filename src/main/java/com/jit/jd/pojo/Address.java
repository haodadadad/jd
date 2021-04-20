package com.jit.jd.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    private String address;

    private String name;
    private String phone;

}
