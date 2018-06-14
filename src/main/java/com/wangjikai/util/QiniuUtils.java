package com.wangjikai.util;

import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/**
 * Created by jikai_wang on 2018/3/15.
 * 七牛云，上传base64 头像图片
 */
public class QiniuUtils {
    private String ak = "UPl0zmY7s0UpmlN05soGqRnVVt2zoxMqwB7yeLKB";
    /**
     * 密钥配置
     */
    private String sk = "wSquF-i3n49rNzflNM_MKyRdCb1Tm-DGrReIPCja";
    private Auth auth = Auth.create(ak, sk);
    private String bucketname = "head-images";
    /**
     * 上传的图片名
     */
    private String key = UUID.randomUUID().toString();

    public String getUpToken() {
        return auth.uploadToken(bucketname, null, 3600, new StringMap().put("insertOnly", 1));
    }
    public String put64image(String base64) throws Exception {
        String url = "http://upload.qiniu.com/putb64/-1/key/"+ UrlSafeBase64.encodeToString(key);
        //非华东空间需要根据注意事项 1 修改上传域名
        RequestBody rb = RequestBody.create(null, base64);
        Request request = new Request.Builder().url(url).
                addHeader("Content-Type", "application/octet-stream")
                .addHeader("Authorization", "UpToken " + getUpToken())
                .post(rb).build();
        System.out.println(request.headers());

        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = client.newCall(request).execute();
        System.out.println(response);
        return key;
    }
}
