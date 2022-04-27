package com.xjtlu.slip;

import ch.qos.logback.core.util.FileUtil;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.xjtlu.slip.controller.LoginController;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.Constant;
import com.xjtlu.slip.utils.GenerateAvatar;
import com.xjtlu.slip.utils.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static com.baomidou.mybatisplus.generator.config.OutputFile.service;
import static com.xjtlu.slip.utils.GenerateAvatar.generateOneAvatar;
import static org.springframework.http.HttpHeaders.USER_AGENT;


@Slf4j
@SpringBootTest
public class TestFaker {
    @Autowired
    UserService userService;
    @Autowired
    private LoginController loginController;

    @Test
    public void testFaker() throws IOException {
        //执行100次,封装成一个
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Faker faker = new Faker();
            Name rawName = faker.name();
            String username = rawName.lastName();
            //输出一个10000到99999的随机数
            int random = faker.number().numberBetween(111111111, 999999999);
            String telephone = "15".concat(String.valueOf(random));
            String password = "123456";
            String email = faker.internet().emailAddress();


            String newFileName;
            InputStream uploadFile;
            String newName = UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.ROOT);
            uploadFile = GenerateAvatar.generateOneAvatar();
            newFileName = newName.concat(".png");



            //save the file
            UploadFile.uploadFile(newFileName, uploadFile);

            String prefix = Constant.OSS_WEBSITE_URL;


            //save the user information
            User user = new User();
            user.setName(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(telephone);
            user.setAvatar(prefix + newFileName);

            users.add(user);

        }
        userService.saveOrUpdateBatch(users);
    }

    private void sendPost(String urlParameters) throws Exception {

        String url = "127.0.0.1:8080/upload";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "multipart/form-data");

        //发送Post请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();


    }
}
