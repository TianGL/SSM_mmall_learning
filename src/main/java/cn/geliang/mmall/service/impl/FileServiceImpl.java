package cn.geliang.mmall.service.impl;

import cn.geliang.mmall.service.IFileService;
import cn.geliang.mmall.util.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 上传文件到ftp服务器
     * @param file
     * @param path
     * @return
     */
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();

        String fileExtensionName = fileName.substring(fileName.lastIndexOf('.') + 1);
        String uploadFileName = UUID.randomUUID() + "." +fileExtensionName;

        logger.info("开始上传文件, 上传文件名: {}, 上传的路径: {}, 新文件名: {}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()) {
            fileDir.setWritable(true); // 增加写权限
            fileDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);

        try {
            // 上传文件到指定文件
            file.transferTo(targetFile);

            // 上传文件到ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            // 上传完成后，删除uplaod下文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }

}
