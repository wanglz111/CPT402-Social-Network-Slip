package com.xjtlu.slip;

import com.xjtlu.slip.utils.GenerateAvatar;
import com.xjtlu.slip.utils.UploadFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class TestAvatar {
    @Test
    public void testAvatar() throws IOException {
        InputStream inputStream = GenerateAvatar.generateOneAvatar();
        UploadFile.uploadFile(UUID.randomUUID().toString().concat(".png"), inputStream);
    }
}
