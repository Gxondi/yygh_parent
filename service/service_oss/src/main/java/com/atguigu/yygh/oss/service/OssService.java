package com.atguigu.yygh.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.atguigu.yygh.oss.prop.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssService {
    @Autowired
    private OssProperties ossPrperties;
    public String upload(MultipartFile file) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ossPrperties.getEndpoint();
        String accessKeyId = ossPrperties.getKeyid();
        String accessKeySecret = ossPrperties.getKeysecret();
        String bucketName = ossPrperties.getBucketname();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String originalFileName = new DateTime().toString("yyyy/MM/dd") +  UUID.randomUUID().toString().replaceAll("-", "")+file.getOriginalFilename();
        try {
            ossClient.putObject(bucketName, originalFileName, file.getInputStream());
            return "https://" + ossPrperties.getBucketname() + "." + ossPrperties.getEndpoint() + "/" + originalFileName;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
