package com.xjtlu.slip.controller;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.Constant;
import com.xjtlu.slip.utils.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@Slf4j
public class ResetController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/reset")
    public String reset(Model model, HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("error","please log in first");
            return "redirect:/login";
        }
        return "reset";
    }

    @PostMapping("/resetCheck")
    public String resetCheck(String telephone, String password, String oldPassword, String confirmPassword, String email, @RequestPart("avatar") @DefaultValue("") MultipartFile avatar, Model model, HttpSession session) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");
        User updateUser = new User();
        updateUser.setAvatar(loginUser.getAvatar());
        if (!MD5.create().digestHex(oldPassword).equals(loginUser.getPassword())) {
            session.setAttribute("error","The original password is wrong");
            return "redirect:/reset";
        }
        if (!password.equals(confirmPassword)) {
            session.setAttribute("error","The two passwords do not match");
            return "redirect:/reset";
        }
        if (!avatar.isEmpty()) {
            String suffix = Objects.requireNonNull(avatar.getOriginalFilename()).substring(avatar.getOriginalFilename().lastIndexOf("."));
            if (!".jpg".equals(suffix) && !".png".equals(suffix) && !".jpeg".equals(suffix) && !".gif".equals(suffix) && !".bmp".equals(suffix) && !".ico".equals(suffix) && !".svg".equals(suffix) && !".webp".equals(suffix)) {
                session.setAttribute("error", "avatar suffix is not image format");
            }
            //check if the file is too large
            if (avatar.getSize() > 1024 * 1024 * 20) {
                session.setAttribute("error", "avatar is too large");
            }
            String newName = UUID.randomUUID().toString().replace("-", "");
            String newFileName = newName.concat(suffix);
            InputStream uploadFile = avatar.getInputStream();
            UploadFile.uploadFile(newFileName, uploadFile);

            updateUser.setAvatar(Constant.OSS_WEBSITE_URL+newFileName);
        }
        updateUser.setEmail(email);
        updateUser.setPassword(MD5.create().digestHex(password));
        updateUser.setPhone(telephone);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", loginUser.getId());
        userService.update(updateUser, queryWrapper);
        // Update current user to redis
        redisService.set("User:userId:" + loginUser.getId(), userService.getById(loginUser.getId()));
        // Update User Info in redis
        Map<Long, User> users = userService.getUserMap();
        redisService.set("User:AllUserInfo", users);
        return "redirect:/logout";
    }
}
