package com.xjtlu.slip.controller;

import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.Constant;
import com.xjtlu.slip.utils.GenerateAvatar;
import com.xjtlu.slip.utils.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Controller
public class registerController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

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
            model.addAttribute("msgUsername", "username is already registered");
            return "register";
        }
        //check if the password is consistent
        if (!password.equals(confirmPassword)) {
            model.addAttribute("msgPassword", "password is inconsistent");
            return "register";
        }
        //validate the telephone
        if (!telephone.matches("^1[3|4|5|7|8]\\d{9}$")) {
            model.addAttribute("msgTelephone", "telephone is invalid");
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

}
