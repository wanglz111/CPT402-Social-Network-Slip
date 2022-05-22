package com.xjtlu.slip;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.xjtlu.slip.utils.Constant;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalTime;

public class UpLoadJavaFileTest {

    @Test
    public void uploadFile() throws QiniuException {
        File file = new File("/Users/wangluzhi/Luzhi/slip/target/slip-0.0.1-SNAPSHOT.jar");

        String accessKey = Constant.OSS_ACCESS_KEY_ID;
        String secretKey = Constant.OSS_ACCESS_KEY_SECRET;
        String bucketName = "file-backup-java";
        //Region.region0() is the default region means the bucket is in East China
        Configuration cfg = new Configuration(Region.regionAs0());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        LocalTime time = LocalTime.now();
        String fileName = time.toString().substring(0, 5).replace(":", "").concat(".jar");

        Response r = uploadManager.put(file, fileName, token);
        String uri = "file.cpt402.fun/";
        System.out.println(uri.concat(fileName));
    }
}
