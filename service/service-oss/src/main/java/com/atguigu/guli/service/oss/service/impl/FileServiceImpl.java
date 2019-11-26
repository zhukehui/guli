package com.atguigu.guli.service.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.atguigu.guli.service.oss.service.FileService;
import com.atguigu.guli.service.oss.util.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author eternity
 * @create 2019-11-25 20:45
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OssProperties ossProperties;

    @Override
    public String upload(InputStream inputStream, String module, String originalFilename) {

        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketName = ossProperties.getBucketName();

        //判断oss实例是否存在：如果不存在则创建，如果存在则获取
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
        if (!ossClient.doesBucketExist(bucketName)) {
            //创建bucket
            ossClient.createBucket(bucketName);
            //设置oss实例的访问权限：公共读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }

        //构建日期路径：avatar/2019/02/26/文件名
        String folder = new DateTime().toString("yyyy/MM/dd");

        //文件名：uuid.扩展名
        String fileName = UUID.randomUUID().toString();
        String fileExtention = originalFilename.substring(originalFilename.lastIndexOf("."));
        String key = new StringBuffer()
                .append(module)
                .append("/")
                .append(folder)
                .append("/")
                .append(fileName)
                .append(fileExtention)
                .toString();

        //文件上传至阿里云
        ossClient.putObject(ossProperties.getBucketName(), key, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        //返回url地址
        return new StringBuffer()
                .append("https://")
                .append(ossProperties.getBucketName())
                .append(".")
                .append(ossProperties.getEndpoint())
                .append("/")
                .append(key)
                .toString();
    }

    @Override
    public void removeFile(String url) {

        String endpoint = ossProperties.getEndpoint();
        String accessKeyId = ossProperties.getKeyid();
        String accessKeySecret = ossProperties.getKeysecret();
        String bucketName = ossProperties.getBucketName();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String host = "https://" + bucketName + "." + endpoint + "/";
        String key = url.substring(host.length());
        System.out.println(key);
        // 删除文件。
        ossClient.deleteObject(bucketName, key);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
