package com.common.util;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/**
 *  解压Zip文件工具类
 * @author zhangyongbo
 *
 */
public class ZipUtil {
	private static final ReentrantLock lock = new ReentrantLock();
	public static Logger logger = Logger.getLogger(ZipUtil.class);
	private static final int buffer = 2048; 
	public static void main(String[] args){
		String str="D:\\ftpserver\\2017-01-20\\adt_test_ctl.zip";
		// str="D:\\ftpclient\\GZ.zip";
	   // unZip2(str,str,new UnZipResult(false));
	   // str="D:\\ftpclient\\bj.zip";
		//str="E:\\Users\\ctl\\Desktop\\2_191_106__ok805C80456AE6BA53326EEFD44073DB4D600210$JD$170101$350$12447$88$01152320.zip";
		//str="D:\\ftpserver\\GZ_2017-01-11015051_122.zip";
	    unZip2(str,str,new UnZipResult(false),"test0000000000000000000000000000");
	}
	/**
     * 解压Zip文件
     * @param path 文件目录
     */
	public static void unZip(String path){
    	 lock.lock();
    	 int count = -1;
         String savepath = "";
         File file = null;
         InputStream is = null;
         FileOutputStream fos = null;
         BufferedOutputStream bos = null;

         savepath = path.substring(0, path.lastIndexOf(".")) + File.separator; //保存解压文件目录
         new File(savepath).mkdir(); //创建保存目录
         ZipFile zipFile = null;
         try
         {
         	 zipFile = new ZipFile(path,"gbk"); //解决中文乱码问题
             Enumeration<?> entries = zipFile.getEntries();

             while(entries.hasMoreElements())
             {
                 byte buf[] = new byte[buffer];
                 ZipEntry entry = (ZipEntry)entries.nextElement();

                 String filename = entry.getName();
                 boolean ismkdir = false;
                 if(filename.lastIndexOf("/") != -1){ //检查此文件是否带有文件夹
                 	ismkdir = true;
                 }
                 filename = savepath + filename;
                 if(entry.isDirectory()){ //如果是文件夹先创建
                 	file = new File(filename);
                 	file.mkdirs();
                 	 continue;
                 }
                 file = new File(filename);
                 if(!file.exists()){ //如果是目录先创建
                 	if(ismkdir){
                 	new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); //目录先创建
                 	}
                 }
                 file.createNewFile(); //创建文件

                 is = zipFile.getInputStream(entry);
                 fos = new FileOutputStream(file);
                 bos = new BufferedOutputStream(fos, buffer);

                 while((count = is.read(buf)) > -1)
                 {
                     bos.write(buf, 0, count);
                 }
                 
                 bos.flush();
                 bos.close();
                 fos.close();

                 is.close();
                 if(filename.endsWith(".zip")){//如果解压文件包含zip文件那么进入深度解压
                 	 unZip(filename);
                 }
             }
             zipFile.close();
             logger.info(path+" 解压成功");
         }catch(IOException ioe){
        	 logger.error(path+" 解压失败",ioe);
         }finally{
            	try{
            	if(bos != null){
            		bos.close();
            	}
            	if(fos != null) {
            		fos.close();
            	}
            	if(is != null){
            		is.close();
            	}
            	if(zipFile != null){
            		zipFile.close();
            	}
            	}catch(Exception e) {
            		 logger.error("IO Stream close fail:",e);
            	}
            }
         	lock.unlock();
        }
    
	/**
	 * 无论第一层还是最后一层文件解压失败，都将该文件删除
     * 解压Zip文件 
     * @param path 文件目录
     */
   
    public static boolean unZip2(String path,final String filePath,UnZipResult isDelete,String uuid ){
    	// lock.lock();
    	 int count = -1;
         String savepath = "";
         File file = null;
         InputStream is = null;
         FileOutputStream fos = null;
         BufferedOutputStream bos = null;

         savepath = path.substring(0, path.lastIndexOf(".")) + File.separator; //保存解压文件目录
         new File(savepath).mkdir(); //创建保存目录
         ZipFile zipFile = null;
         try
         {
         	 zipFile = new ZipFile(path,"gbk"); //解决中文乱码问题
             Enumeration<?> entries = zipFile.getEntries();
             while(entries.hasMoreElements())
             {
                 byte buf[] = new byte[buffer];
                 ZipEntry entry = (ZipEntry)entries.nextElement();
                 String filename = entry.getName();
                 boolean ismkdir = false;
                 if(filename.lastIndexOf("/") != -1){ //检查此文件是否带有文件夹
                 	ismkdir = true;
                 }
                 filename = savepath + filename;
                 if(entry.isDirectory()){ //如果是文件夹先创建
                 	 continue;
                 }
                 String parentPath=new File(filename).getParent();
                 if(!(filename.endsWith(".zip")
                		 ||parentPath.endsWith("ddml_cart")
                		 ||parentPath.endsWith("ddml_cart_sdy")
                		 ||parentPath.endsWith("portal_comments")
                		 ||parentPath.endsWith("sdy_comments")
                		 ||parentPath.endsWith("portal_orders")
                		 ||parentPath.endsWith("sdy_orders")
                		 ||parentPath.endsWith("portal_signImages")
                		 ||parentPath.endsWith("sdy_signImages")
                		 ||parentPath.endsWith("portal_ledous")
                		 ||parentPath.endsWith("json_orders"))){
                	 continue;
                 }
                 file = new File(filename);
                 if(!file.exists()){ //如果是目录先创建
                 	if(ismkdir){
                 		new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); //目录先创建
                 	}
                 }
                 file.createNewFile(); //创建文件

                 is = zipFile.getInputStream(entry);
                 fos = new FileOutputStream(file);
                 bos = new BufferedOutputStream(fos, buffer);

                 while((count = is.read(buf)) > -1){
                     bos.write(buf, 0, count);
                 }
                 
                 bos.flush();
                 bos.close();
                 fos.close();

                 is.close();
                 if(filename.endsWith(".zip")){//如果解压文件包含zip文件那么进入深度解压
                	 unZip2(filename,filePath,isDelete,uuid);
                 }
             }
             zipFile.close();
            
         }catch(IOException ioe){
        	 isDelete.setDeleteFlag(true);;
        	 logger.error(path+" 解压失败",ioe);
        	 return !isDelete.isDeleteFlag();
         }finally{
	        	if(isDelete.isDeleteFlag()&&path.equals(filePath)){//文件解压失败删除文件
	        		File toDelete=new File(filePath);
	        		if(toDelete.exists()){
	        			try {
	        				//SqlSessionFactoryUtil.updateFlagByUUID(uuid, PortalPayFtpDownloadFile.UNZIPFAIL);
	        				logger.info("更新PortalPayFtpDownloadFile.UNZIPFAIL成功："+filePath);
						} catch (Exception e) {
							logger.info("更新PortalPayFtpDownloadFile.UNZIPFAIL失败："+e);
						}
	        		}
	        	}else if(!isDelete.isDeleteFlag()&&path.equals(filePath)){
	        		logger.info(path+" 解压成功");
	        	 }
            	try{
	            	if(bos != null){
	            		bos.close();
	            	}
	            	if(fos != null) {
	            		fos.close();
	            	}
	            	if(is != null){
	            		is.close();
	            	}
	            	if(zipFile != null){
	            		zipFile.close();
	            	}
            	}catch(Exception e) {
            		 logger.error("IO Stream close fail:",e);
            	}
            }
         	 //lock.unlock();
         	 return !isDelete.isDeleteFlag();
        }
}