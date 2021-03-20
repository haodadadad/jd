package com.jit.jd.vo;


//登录参数


import com.jit.jd.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;

@Data

public class LoginVo {
 @NotNull
 @IsMobile

 private String mobile;

 @NotNull
 @Length(min = 32)
 private String password;

}
