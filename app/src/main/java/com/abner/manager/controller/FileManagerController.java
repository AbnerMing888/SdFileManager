
package com.abner.manager.controller;

import android.os.Environment;
import android.text.TextUtils;

import com.abner.manager.model.FileInfo;
import com.abner.manager.util.FileUtils;
import com.abner.manager.util.Logger;
import com.abner.manager.util.TimeUtils;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理控制层
 */
@RestController
@RequestMapping(path = "/file")
class FileManagerController {

    /**
     * 返回文件列表
     *
     * @return
     */
    @GetMapping(path = "/list")
    List<FileInfo> getFileList(@RequestParam(name = "rootPath", required = false) String rootPath) {
        File file;
        if (TextUtils.isEmpty(rootPath)) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        } else {
            file = new File(rootPath);
        }
        List<FileInfo> fileInfoList = new ArrayList<>();
        if (!file.isDirectory()) {
            return fileInfoList;
        }
        File[] list = file.listFiles();
        if (list == null || list.length == 0) {
            return fileInfoList;
        }
        FileInfo fileInfo;
        for (File f : list) {
            String name = f.getName();
            fileInfo = new FileInfo();
            fileInfo.setName(name);
            fileInfo.setUrl(f.getAbsolutePath());
            fileInfo.setDateModified(f.lastModified());
            fileInfo.setDateModifiedString(TimeUtils.INSTANCE.formatDateToStr(f.lastModified(), "yyyy/MM/dd aHH:mm:ss"));
            if (f.isFile()) {
                fileInfo.setIsDir(0);
                fileInfo.setSize(f.length());
                fileInfo.setSizeString(FileUtils.formatFileSize(f.length()));
            } else {
                fileInfo.setIsDir(1);
            }
            fileInfoList.add(fileInfo);
        }
        return fileInfoList;
    }

    @PostMapping("/deleteFile")
    String deleteFile(
            @RequestParam(name = "path") String path) {

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        return "删除成功";
    }

    @PostMapping("/upload")
    String uploadFile(
            @RequestParam(name = "path") String path,
            @RequestParam(name = "file") MultipartFile multipartFile) {

        File dir = Environment.getExternalStorageDirectory();
        if (!"-1".equals(path)) {
            //证明为空
            dir = Environment.getExternalStoragePublicDirectory(path);
        }
        try {
            String fileName = multipartFile.getFilename();
            String filePath = dir + "/" + fileName;
            InputStream inputStream = (multipartFile.getStream());
            FileOutputStream fileWriter = new FileOutputStream(filePath);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                fileWriter.write(bytes, 0, length);
                fileWriter.flush();
            }
            inputStream.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return multipartFile.getFilename();
    }


    @PostMapping(path = "/download")
    ResponseBody download(HttpResponse response, @RequestParam(name = "rootPath", required = false) String rootPath) {

        if (TextUtils.isEmpty(rootPath)) {
            return new StringBody("文件路径为空");
        }
        try {
            File file = new File(rootPath);
            if (file.isFile() && file.exists()) {

                FileBody fileBody = new FileBody(file);
                response.setStatus(200);
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Disposition", "attachment;fileName=" + file.getName());
                return fileBody;
            } else {
                return new StringBody("文件不存在或已经删除");
            }
        } catch (Exception e) {
            Logger.e("下载文件异常：" + e.getMessage());
            return new StringBody("下载文件异常：" + e.getMessage());
        }

    }
}