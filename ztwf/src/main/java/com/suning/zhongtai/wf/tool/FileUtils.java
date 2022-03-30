package com.suning.zhongtai.wf.tool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    /**
    * 写文件
    * @param filePath 文件路径
     *@param content 文件数据
    * @return false文件写入失败    true文件写入成功
    * @author 18040994
    * @date 2019/9/9 16:31
    */
    public  static boolean writeFile(String filePath, byte[] content){
        boolean success = false;
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            File file = new File(filePath);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            channel.truncate(0);
            ByteBuffer buffer = ByteBuffer.allocate(content.length);
            buffer.put(content);
            buffer.flip();
            channel.write(buffer);
            success = true;
        } catch (IOException e) {
            LOGGER.error("write file error.",e);
        }
        finally {
            close(raf);
            close(channel);
        }
        return success;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.error("Closeable can't close.",e);
            }
        }
    }

    /**
    * 为指定文件生成MD5摘要
    * @param file 带生成MD5摘要的文件
    * @return
    * @author 18040994
    * @date 2019/9/9 19:22
    */
    public static String generateMd5Digest(File file) throws FileNotFoundException{
        if(!file.isFile()){
            throw new FileNotFoundException(file.getName()+" 文件不存在!");
        }
        FileInputStream fis = null;
        byte[] bytes = null;
        DigestInputStream dis = null;

        try {
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
           dis = new DigestInputStream(fis,md5);
            byte[] buffer = new byte[4096];
            while(dis.read(buffer)>0){};
            md5 = dis.getMessageDigest();
            bytes = md5.digest();
        } catch (Exception e) {
            LOGGER.error("generate md5 failed.",e);
        }
        finally {
            close(fis);
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length; i++){
            String s = Integer.toHexString(0XFF & bytes[i]);
            if(s.length() < 2){
                s = "0"+s;
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
    * zip压缩指定目录中的文件
    * @param fileDir 压缩目录
     * @param filterParam 文件过滤参数
    * @return
    * @author 18040994
    * @date 2019/9/10 11:00
    */
    public static File zipFile(String fileDir, String ... filterParam){
        File zipFile = null;
        List<File> files = new ArrayList<>();
        File zipfileDir = new File(fileDir);
        String zipFileName = "";
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            if(zipfileDir.exists()){
                File[] allFiles = zipfileDir.listFiles();
                if(filterParam.length > 0){
                    String filter = filterParam[0];
                    allFiles = zipfileDir.listFiles(new ZtwfmtFileFilter(filter));
                    zipFileName = filter+".zip";
                }
                for(int i = 0; i < allFiles.length; i++){
                    files.add(allFiles[i]);
                }
                if(StringUtils.isEmpty(zipFileName)){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    zipFileName = dateFormat.format(new Date())+".zip";
                }
                zipFile = new File(fileDir+ File.separator +zipFileName);
                fileOutputStream = new FileOutputStream(zipFile);
                zipOutputStream = new ZipOutputStream(fileOutputStream);
                zipFile(files,zipOutputStream);
            }
        } catch (Exception e) {
            LOGGER.error("zip file failed.",e);
        }
        finally {
            close(zipOutputStream);
            close(fileOutputStream);
        }
        return zipFile;
    }

    public static void zipFile(List<File> files,  ZipOutputStream zipOutputStream){
        for(int i =0; i<files.size(); i++){
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                File file = files.get(i);
                if(file.exists()&&file.isFile()){
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    final int  MAX_BYTE = 10*1024*1024;
                    int streamTotal;
                    int streamNum;
                    int leaveByte;
                    byte[] buffer;
                    streamTotal = bis.available();
                    streamNum = (int)Math.floor(streamTotal / MAX_BYTE);
                    leaveByte = streamTotal % MAX_BYTE;
                    if(streamNum > 0){
                        for(int j=0 ; j < streamNum; j++){
                            buffer = new byte[MAX_BYTE];
                            bis.read(buffer,0,MAX_BYTE);
                            zipOutputStream.write(buffer,0,MAX_BYTE);
                        }
                    }
                    buffer = new byte[leaveByte];
                    bis.read(buffer,0,leaveByte);
                    zipOutputStream.write(buffer);
                    zipOutputStream.closeEntry();
                }
            } catch (Exception e) {
                LOGGER.error("zip file error.",e);
            }
            finally {
                close(fis);
                close(bis);
            }
        }
    }

    /**
    * 将压缩文件解压到指定目录
    * @param srcFile 源文件
     * @param destFilePath 解压目录
    * @return
    * @author 18040994
    * @date 2019/9/10 17:38
    */
    public static boolean unZipFile(File srcFile, String destFilePath){
        boolean success = true;
        if(!srcFile.exists()){
            success = false;
            LOGGER.error(srcFile.getName() + "文件解压失败!");
        }
        //开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()){
                ZipEntry zipEntry = entries.nextElement();
                if(zipEntry.isDirectory()){
                    String dirPath = destFilePath+File.separator+zipEntry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                }
                else{
                    File targetFile = new File(destFilePath+File.separator+zipEntry.getName());
                    if(!targetFile.getParentFile().exists()){
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = zipFile.getInputStream(zipEntry);
                        fos = new FileOutputStream(targetFile);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = is.read(buffer))!= -1){
                            fos.write(buffer,0,len);
                        }
                    } finally {
                        close(is);
                        close(fos);
                    }

                }
            }
        } catch (IOException e) {
            success = false;
            LOGGER.error(srcFile.getName()+" unzip failed",e);
        } finally {
            close(zipFile);
        }
        return success;
    }

    /**
    * 根据条件删除目录下指定文件
    * @param fileDir 待删除目录
     * @param filterParam 文件过滤参数
    * @return
    * @author 18040994
    * @date 2019/9/10 14:52
    */
    public  static void deleteFiles(String fileDir, String ... filterParam){
        File deleteFileDir = new File(fileDir);
        if(!deleteFileDir.exists()){
            return;
        }
        if(deleteFileDir.isDirectory()){
            String filter = "";
            File[] allFiles = deleteFileDir.listFiles();
            if(filterParam.length > 0){
                filter = filterParam[0];
                allFiles = deleteFileDir.listFiles(new ZtwfmtFileFilter(filter));
            }
            for(int i = 0; i < allFiles.length; i++){
                String path = allFiles[i].getAbsolutePath();
                deleteFiles(path,filter);
            }
        }
        deleteFileDir.delete();
    }

    /**
    * ztwfmt文件过滤器内部类
    * @author 18040994
    * @date 2019/9/10 15:10
    */
    static class ZtwfmtFileFilter implements FileFilter{
        String prefix = "";
        ZtwfmtFileFilter(String prefix){
            this.prefix = prefix;
        }
        @Override
        public boolean accept(File file) {
            if(file.isDirectory()){
                return true;
            }
            else{
                String fileName = file.getName();
                if(!StringUtils.isEmpty(prefix)&&fileName.contains(prefix)){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
    }

    /**
    * 读取文件内容
    * @param filePath 文件路径
    * @return
    * @author 18040994
    * @date 2019/9/11 16:38
    */
    public static String readFileContent(String filePath){
        List<String> fileContents = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
            String lineContent = "";
            while ((lineContent=br.readLine())!=null){
                fileContents.add(lineContent);
            }
        } catch (Exception e) {
            LOGGER.error(filePath+" 读取失败",e);
        } finally {
            close(br);
        }
        StringBuilder sb = new StringBuilder();
        for(String line: fileContents){
            sb.append(line);
        }
        return sb.toString();
    }

    /**
    * 读取文件指定行的内容
    * @param filePath 目标文件
     * @param beginLineNum 开始行号
     * @param endLineNum 结束行号
    * @return
    * @author 18040994
    * @date 2019/9/10 17:49
    */
    public static List<String> readLineContent(String filePath, int beginLineNum, int endLineNum){
        List<String> lineContents = new ArrayList<>();
        BufferedReader br = null;
        try {
            int count = 0;
            br = new BufferedReader(new FileReader(filePath));
            String content = br.readLine();
            while (content != null){
                if(count >= beginLineNum && count <= endLineNum){
                    lineContents.add(content);
                }
                content = br.readLine();
                count++;
            }
        } catch (Exception e) {
            LOGGER.error(filePath+" 读取失败",e);
        } finally {
            close(br);
        }
        return  lineContents;
    }
}
