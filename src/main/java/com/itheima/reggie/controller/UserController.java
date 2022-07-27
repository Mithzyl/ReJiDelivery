package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // 获取验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String number = user.getPhone();

        // 生成验证码 测试时不使用短信服务
        if(StringUtils.isNotEmpty(number)){
            String integer = ValidateCodeUtils.generateValidateCode(4).toString();
            String testCode = "1234";


            // 阿里云短信发送服务
//        try{
//            SMSUtils.send();
//        }catch (Exception e){
//            log.info("无法发送验证码");
//        }

            // 生成的验证码保存到Session
            session.setAttribute(number, testCode);

            return R.success("发送成功");
        }


        return R.error("短信发送失败");
    }

    // 用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhone, phone);


        Object verifyCode = session.getAttribute(phone);

        if(!ObjectUtils.isEmpty(verifyCode) && verifyCode.equals(code)){
            User user = userService.getOne(userLambdaQueryWrapper);

            if(!ObjectUtils.isEmpty(user)){
                session.setAttribute("user", user.getId());

                return R.success(user);
            } else{  // 注册新用户
                User newUser = new User();
                newUser.setPhone(phone);

                userService.save(newUser);
                session.setAttribute("user", newUser.getId());

                return R.success(newUser);


            }

        }else{
            return R.error("验证码错误");
        }



    }

}
