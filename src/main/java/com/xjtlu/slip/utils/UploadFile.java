package com.xjtlu.slip.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * @project: zhxy
 * @description: 上传文件的工具类
 */
public class UploadFile {

    public static void uploadFile(String fileName, InputStream input) throws QiniuException {
        String accessKey = Constant.OSS_ACCESS_KEY_ID;
        String secretKey = Constant.OSS_ACCESS_KEY_SECRET;
        String bucketName = Constant.OSS_BUCKET_NAME;
        //Region.region0() is the default region means the bucket is in East China
        Configuration cfg = new Configuration();
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        Response r = uploadManager.put(input, fileName, token,null,null);
        System.out.println(r.bodyString());

    }

}
