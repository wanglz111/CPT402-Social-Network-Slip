package com.xjtlu.slip.controller;

import cn.hutool.crypto.digest.MD5;
import com.qiniu.util.StringUtils;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.utils.*;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;
//cookie controller
    @Resource
    private CookieUtil cookieUtil;

//    @GetMapping("/")
//    public String welcome() {
//        return "redirect:/login";
//    }

    @GetMapping("/login")
    public String login(Model model, String username, String password, String verifyCode , HttpSession session, HttpServletResponse response) {
        User user;
        //if cookie exists and useful, redirect to index
        if (cookieUtil.getCookie("_userSession") != null) {
            Object rawData = redisService.get("User:Session:".concat(cookieUtil.getCookie("_userSession")));
            if (rawData != null) {
                user = (User) rawData;
                session.setAttribute("loginUser", user);
                return "redirect:/";
            }
        }


        if (StringUtils.isNullOrEmpty(username)||StringUtils.isNullOrEmpty(password)) {
            model.addAttribute("msg", "username or password is null");
            return "login";
        }
        //determine if password is correct
        user = userService.getByUsernameAndPassword(username, password);
        if (user == null) {
            model.addAttribute("msg", "username or password is wrong");
            return "login";
        }
        //determine if verifyCode is correct
        String verifyCodeSession = (String) session.getAttribute("verifyCode");
        if (verifyCodeSession == null || !verifyCodeSession.equalsIgnoreCase(verifyCode)) {
            model.addAttribute("msg", "verifyCode is wrong");
            return "login";
        }
        //remove the verifyCode from session for security
        session.removeAttribute("verifyCode");
        session.setAttribute("loginUser", user);
        //force cookie different from next time(SALT, TIME)
        String _userSession = MD5.create().digestHex(user + Constant.SALT + LocalTime.now().toString());
        //store user in redis and set cookie
        //expire time is 1 day
        redisService.set("User:Session:".concat(_userSession), user, Constant.SESSION_TIME);
        cookieUtil.setCookie("_userSession", _userSession);

        return "redirect:/";
    }

    /**
     * 本来的 index page 现在index page直接由topic page替代
     * 这一部分是测试登陆的一部分，具体是否使用后续在看
     * TODO:
     */
//    @GetMapping("/index")
//    public String index(Model model, HttpServletRequest request, HttpSession session) {
//        if (session.getAttribute("loginUser") != null) {
//            return "redirect:/topic";
//        }
//        if (cookieUtil.getCookie("_userSession") != null) {
//            User user = (User) redisService.get("User:Session:".concat(cookieUtil.getCookie("_userSession")));
//            if (user != null) {
//                session.setAttribute("loginUser", user);
//                return "redirect:/topic";
//            }
//        }
//
//        session.setAttribute("msg", "please login first");
//        return "redirect:/";
//    }

    @PostMapping("/upload")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           @RequestParam("email") String email,
                           @RequestParam("telephone") String telephone,
                           @RequestPart("avatar") @DefaultValue("") MultipartFile file,
                           Model model) throws IOException {
        //check if the username is already registered
        User user = userService.getByUsername(username);
        if (user != null) {
            model.addAttribute("msg", "username is already registered");
            return "register";
        }
        //check if the password is consistent
        if (!password.equals(confirmPassword)) {
            model.addAttribute("msg", "password is inconsistent");
            return "register";
        }
        //validate the telephone
        if (!telephone.matches("^1[3|4|5|7|8]\\d{9}$")) {
            model.addAttribute("msg", "telephone is invalid");
            return "register";
        }


        //check if the file is empty or suffix is not image format
        String newFileName;
        InputStream uploadFile;
        String newName = UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.ROOT);
        if (file.isEmpty()) {
            //generate a default avatar
            uploadFile = GenerateAvatar.generateOneAvatar();
            newFileName = newName.concat(".png");
        } else {
            //check if the file is image format
            String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            if (!".jpg".equals(suffix) && !".png".equals(suffix) && !".jpeg".equals(suffix) && !".gif".equals(suffix) && !".bmp".equals(suffix) && !".ico".equals(suffix) && !".svg".equals(suffix) && !".webp".equals(suffix)) {
                model.addAttribute("msg", "file suffix is not image format");
            }
            //check if the file is too large
            if (file.getSize() > 1024 * 1024 * 20) {
                model.addAttribute("msg", "avatar is too large");
            }
            newFileName = newName.concat(suffix);
            uploadFile = file.getInputStream();
        }
        //save the file
        UploadFile.uploadFile(newFileName, uploadFile);

        String prefix = Constant.OSS_WEBSITE_URL;


        //save the user information
        user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(telephone);
        user.setAvatar(prefix + newFileName);

        userService.save(user);
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        //clear the session
        session.invalidate();
        //clear the cookie if it exists
        redisService.del("User:Session:".concat(cookieUtil.getCookie("_userSession")));
        cookieUtil.clearCookie("_userSession");
        return "goodbye";
    }


}


