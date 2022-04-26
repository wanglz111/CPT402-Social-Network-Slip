package com.xjtlu.slip.controller;

import com.qiniu.util.StringUtils;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.utils.Constant;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.GenerateAvatar;
import com.xjtlu.slip.utils.MD5;
import com.xjtlu.slip.utils.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.List;
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

    @GetMapping("/")
    public String welcome() {
        return "login";
    }

    @GetMapping("/login")
    public String login(Model model, String username, String password, String verifyCode , HttpSession session, HttpServletResponse response) {
        //redis中试着找到当前User

        if (StringUtils.isNullOrEmpty(username)||StringUtils.isNullOrEmpty(password)) {
            model.addAttribute("msg", "username or password is null");
            return "login";
        }
        //determine if password is correct
        User user = userService.getByUsernameAndPassword(username, password);
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
        String _userSession = MD5.encrypt(user + Constant.SALT + LocalTime.now().toString());
        //store user in redis and set cookie
        //expire time is 1 day
        redisService.set("User:Session:".concat(_userSession), user, Constant.SESSION_TIME);
        Cookie cookie = new Cookie("_userSession", _userSession);
        cookie.setMaxAge((int) Constant.SESSION_TIME);
        cookie.setPath("/index");
        cookie.setPath("/logout");
        response.addCookie(cookie);

        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model, HttpServletRequest request, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        if (session.getAttribute("loginUser") != null) {
            List<User> users = userService.list();
            model.addAttribute("users", users);
            return "/index";
        } else if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("_userSession")) {
                    String _userSession = cookie.getValue();
                    User user = (User) redisService.get("User:Session:".concat(_userSession));
                    if (user != null) {
                        model.addAttribute("loginUser", user);
                        session.setAttribute("loginUser", user);
                        List<User> users = userService.list();
                        model.addAttribute("users", users);
                        return "/index";
                    }
                }
            }
        } else {
            session.setAttribute("msg", "please login first");
            return "redirect:/";
        }
        return "redirect:/";
    }

    @PostMapping("/upload")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           @RequestParam("email") String email,
                           @RequestParam("telephone") String telephone,
                           @RequestPart("avatar") MultipartFile file,
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
    public String logout(HttpSession session, HttpServletRequest request) {
        //clear the session
        session.invalidate();
        //clear the cookie if it exists
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("cookie name: " + cookie.getValue());
                redisService.del("User:Session:".concat(cookie.getValue()));
            }
        }

        return "goodbye";
    }


}


