package com.jit.jd.validator;

import com.jit.jd.utils.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
//手机号码格式注解
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {


    private boolean required=false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {

        required=constraintAnnotation.required();



    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if (required){
            return ValidatorUtil.isMobile(value);

        }else {
            if(StringUtils.isEmpty(value)){
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }

        }

    }
}
