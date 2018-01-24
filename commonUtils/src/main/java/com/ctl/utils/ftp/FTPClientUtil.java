package com.ctl.utils.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.ctl.ftp.ConfigUtils;
public class FTPClientUtil {
	static Logger logger = Logger.getLogger(FTPClientUtil.class);
	public static FTPClient fClient;
	private static String username; // 登陆FTP 用户�?
	private static String password; // 用户密码，支持强密码
	private static String url; // FTP 地址
	private static int port;// FTP 端口
	private static String remoteDir;// FTP 远程目录
	private static String ftpname;// FTP 链接的名�?
	public static String downloaDays;//下载天数  1 今天 2今天和昨�?
	public static String ftpLocalDir;//ftp下载到本地路�?
	//public static SimpleDateFormat sdfdate_mm_dd = new SimpleDateFormat("yyyy-MM-dd");
	static {
		try {
			username = ConfigUtils.getType("ftp.username");
			password = ConfigUtils.getType("ftp.password");
			url = ConfigUtils.getType("ftp.url");
			port = Integer.parseInt(ConfigUtils.getType("ftp.port"));
			remoteDir = ConfigUtils.getType("ftp.remoteDir");
			if(null==remoteDir||"".equals(remoteDir.trim())){
				remoteDir=File.separator;
			}
			ftpLogin();
		} catch (Exception e) {
			logger.error("登录失败:",e);
		} finally {
			ftpname = ConfigUtils.getType("ftp.ftpname");
			ftpLocalDir=ConfigUtils.getType("ftp.local.dir");
			downloaDays=ConfigUtils.getType("ftp.downloaddays");
			logger.info(new StringBuilder("ftpname:").append(ftpname).append(" ftpLocalDir:").append(ftpLocalDir).append(" downloaDays:").append(downloaDays));
		}

	}
	public static FTPClient ftpLogin() {
		   if(fClient==null||!FTPReply.isPositiveCompletion(fClient.getReplyCode())||!fClient.isAvailable()||!fClient.isRemoteVerificationEnabled()){
			   fClient = new FTPClient();
			   try {
				   fClient.connect(url,port);
				   fClient.login(username, password);
				   int reply = fClient.getReplyCode();
				   if (FTPReply.isPositiveCompletion(reply)) {// 登陆到ftp服务�?
					   fClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
					   fClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
					   //fClient.setControlEncoding("UTF-8");
					  // fClient.enterLocalPassiveMode();
					   //fClient.setBufferSize(2048);
					   logger.info("Client loin success�?+fClient.getStatus());
				   }else{
					   fClient.disconnect();
					   fClient=null;
				   }
				  // fClient.changeWorkingDirectory(remoteDir);
			   } catch (SocketException e) {
				   fClient=null;
				   logger.error("Client login fail:",e);
			   } catch (IOException e) {
				   fClient=null;
				   logger.error("Client login fail:",e);
			   }
		   }
		return fClient;
	}
	public static FTPClient getNewFtpClient() {
			   FTPClient  fClientPre = new FTPClient();
			   try {
				   fClientPre.connect(url,port);
				   fClientPre.login(username, password);
				   int reply = fClientPre.getReplyCode();
				   if (FTPReply.isPositiveCompletion(reply)) {// 登陆到ftp服务�?
					   fClientPre.setFileType(FTPClient.BINARY_FILE_TYPE);  
					   fClientPre.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
					   //fClient.setControlEncoding("UTF-8");
					   //fClient.enterLocalPassiveMode();
					   //fClient.setBufferSize(2048);
					   logger.info("Client loin success�?+fClientPre.getStatus());
				   }else{
					   fClientPre.disconnect();
					   fClientPre=null;
				   }
				   
				   //linux 没有下面两句话会报错
				  // fClient.changeWorkingDirectory(remoteDir);
			   } catch (SocketException e) {
				   fClientPre=null;
				   logger.error("Client login fail:",e);
			   } catch (IOException e) {
				   fClientPre=null;
				   logger.error("Client login fail:",e);
			   }
			return fClientPre;
	}
	/**
	 * 返回上一级目�?父目�?
	 */
	public static void toParentDir() {
		try {
			fClient.changeToParentDirectory();
		} catch (IOException e) {
			logger.error("返回上一级目�?父目�?失败�?,e);
		}
		logger.info(ftpname + " " + url + " 返回到上层目录�?");
	}
	public  void toParentDirPre(FTPClient fClientPre) {
		try {
			fClientPre.changeToParentDirectory();
		} catch (IOException e) {
			logger.error("返回上一级目�?父目�?失败�?,e);
		}
		logger.info(ftpname + " " + url + " 返回到上层目录�?");
	}
	
	/**
	 * 变更工作目录
	 * 
	 * @param remoteDir
	 *            --目录路径
	 */
	public  boolean changeDirPre(String remoteDir,FTPClient fClientPre) {
		boolean result = false;
		try {
			//FTPClientUtil.remoteDir = remoteDir;
			boolean changeWorkingDirectorySuccess = fClientPre.changeWorkingDirectory(remoteDir);
			if (changeWorkingDirectorySuccess) {
				result = true;
				logger.info(ftpname + " " + url + " 变更工作目录�?" + remoteDir);
			} else {
				logger.info(ftpname + " " + url + " 变更工作目录" + remoteDir + "失败");
			}
		} catch (IOException e) {
			logger.error(ftpname + " " + url + " 变更工作目录" + remoteDir + "失败,异常 ",e);
		}
		return result;
	}
	/**
	 * 变更工作目录
	 * 
	 * @param remoteDir
	 *            --目录路径
	 */
	public static boolean changeDir(String remoteDir) {
		boolean result = false;
		try {
			//FTPClientUtil.remoteDir = remoteDir;
			boolean changeWorkingDirectorySuccess = fClient.changeWorkingDirectory(remoteDir);
			if (changeWorkingDirectorySuccess) {
				result = true;
				logger.info(ftpname + " " + url + " 变更工作目录�?" + remoteDir);
			} else {
				logger.info(ftpname + " " + url + " 变更工作目录" + remoteDir + "失败");
			}
		} catch (IOException e) {
			logger.error(ftpname + " " + url + " 变更工作目录" + remoteDir + "失败,异常 ",e);
		}
		return result;
	}
	/**
	 * 
	 * @param childDir 是否包含子目�?
	 * @throws Exception
	 */
	public static void listFiles(boolean childDir) throws Exception {
		FTPFile[] files = fClient.mlistDir();
		if (files.length > 0) {
			for (FTPFile filename : files) {
				if (filename.getType() == 1&& (!filename.getName().equals(".")) && childDir) {// did
					changeDir(filename.getName());
					listFiles(childDir);
				} else if (filename.getType() == 0) {// file
				    logger.info(filename.getName());
				}
			}
			fClient.changeToParentDirectory();
		}
	}
	public static void listFilesStr(List<String> list) throws IOException{
		 listFilesStr(false,list);
	}
	public static void listFilesStr(boolean childDir,List<String> list) throws IOException{
		listFilesStr(childDir,null,list);
	}
	
	public static void listFilesStr(boolean childDir,String currentWorkingDirectory,List<String> list) throws IOException{
		if(currentWorkingDirectory==null||"".equals(currentWorkingDirectory.trim())){
			fClient.changeWorkingDirectory(remoteDir);
		}else{
			changeDir(currentWorkingDirectory);
			FTPFile[] clientTmp=fClient.listFiles();
			for (int i = 0; i < clientTmp.length; i++) {
				if(clientTmp[i].isDirectory()){
					if(childDir&&!".".equals(clientTmp[i].getName())&&!"..".equals(clientTmp[i].getName())){
					String getName=clientTmp[i].getName();
					//System.err.println(getName);
						listFilesStr(childDir,getName,list);
					}
				}else{
					list.add(clientTmp[i].getName());
				}
			}
			toParentDir();
		}
	}
	
	public static boolean listFilesStr2(boolean childDir,String currentWorkingDirectory,List<String> listUnTrim,List<String> listTrim) throws IOException{
		boolean listResult=false;
		if(currentWorkingDirectory==null||"".equals(currentWorkingDirectory.trim())){
			fClient.changeWorkingDirectory(remoteDir);
		}else{
			if(!(listResult=changeDir(currentWorkingDirectory))){
				return listResult;
			}
			fClient.enterLocalPassiveMode();
			FTPFile[] clientTmp=fClient.listFiles();
			for (int i = 0; i < clientTmp.length; i++) {
				String getName=clientTmp[i].getName();
				if(clientTmp[i].isDirectory()){
					if(childDir&&!".".equals(clientTmp[i].getName())&&!"..".equals(clientTmp[i].getName())){
					   listFilesStr2(childDir,getName,listUnTrim,listTrim);
					}
				}else{
					 if(getName.endsWith(".zip")&&clientTmp[i].getSize()>0){//如果解压文件包含zip文件那么才下�?
						 listUnTrim.add(getName);
						 listTrim.add(StringUtils.trimAllWhitespace(getName));
					 }
				}
			}
			//toParentDir();
		}
		return listResult;
	}
	public  boolean listFilesStrPre(boolean childDir,String currentWorkingDirectory,List<String> listUnTrim,List<String> listTrim,FTPClient fClientPre) throws IOException{
		boolean listResult=false;
		if(currentWorkingDirectory==null||"".equals(currentWorkingDirectory.trim())){
			fClientPre.changeWorkingDirectory(remoteDir);
		}else{
			if(!(listResult=this.changeDirPre(currentWorkingDirectory,fClientPre))){
				return listResult;
			}
			fClientPre.enterLocalPassiveMode();
			FTPFile[] clientTmp=fClientPre.listFiles();
			for (int i = 0; i < clientTmp.length; i++) {
				String getName=clientTmp[i].getName();
				if(clientTmp[i].isDirectory()){
					if(childDir&&!".".equals(clientTmp[i].getName())&&!"..".equals(clientTmp[i].getName())){
					//System.err.println(getName);
					 this.listFilesStrPre(childDir,getName,listUnTrim,listTrim,fClientPre);
					}
				}else{
					 if(getName.endsWith(".zip")&&clientTmp[i].getSize()>0){//如果解压文件包含zip文件那么才下�?
						 listUnTrim.add(getName);
						 listTrim.add(StringUtils.trimAllWhitespace(getName));
					 }
				}
			}
			//toParentDirPre(fClient);
		}
		return listResult;
	}
	/**
	 * 关闭client连接
	 */
	public static void close(){
		try {
			fClient.logout();
			fClient.disconnect();
			fClient=null;
		} catch (IOException e) {
		    logger.error("disconnect fail:",e);
		}
	}
	/**
	 * 关闭client连接
	 */
	public  void closePre(FTPClient fClient){
		try {
			fClient.disconnect();
			fClient.logout();
			fClient=null;
		} catch (IOException e) {
		    logger.error("disconnect fail:",e);
		}
	}
	
	/**
	 * 下载文件
	 * 
	 * @param localFilePath
	 *            本地文件名及路径
	 * @param remoteFileName
	 *            远程文件名称
	 * @return
	 */
	public static  boolean downloadFilePre(String localFilePath,String remoteFileName,FTPClient fClientPre) {
		BufferedOutputStream outStream = null;
		boolean success = false;
		try {
			outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
			fClientPre.enterLocalPassiveMode();
			success = fClientPre.retrieveFile(remoteFileName, outStream);
			if (success) {
				logger.info(ftpname + " " + url + " 文件下载:" + remoteFileName+ " 成功");
			} else {
				logger.info(ftpname + " " + url + " 文件下载:" + remoteFileName+ " 失败:");
			}
		} catch (Exception e) {
			logger.error(ftpname + " " + url + " 文件下载:" + remoteFileName+ " 失败,异常:" , e);
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
		return success;
	}
	/**
	 * 下载文件
	 * 
	 * @param localFilePath
	 *            本地文件名及路径
	 * @param remoteFileName
	 *            远程文件名称
	 * @return
	 */
	public static boolean downloadFile(String localFilePath,String remoteFileName) {
		BufferedOutputStream outStream = null;
		boolean success = false;
		try {
			outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
			fClient.enterLocalPassiveMode();
			success = fClient.retrieveFile(remoteFileName, outStream);
			if (success) {
				logger.info(ftpname + " " + url + " 文件下载:" + remoteFileName+ " 成功");
			} else {
				logger.info(ftpname + " " + url + " 文件下载:" + remoteFileName+ " 失败:");
			}
		} catch (Exception e) {
			logger.error(ftpname + " " + url + " 文件下载:" + remoteFileName+ " 失败,异常:" , e);
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
//		System.out.println();
//		fClient.changeWorkingDirectory(File.separator+"2017-01-07\\12");
//		List<String>  lists=new ArrayList<String>();
//		listFilesStr(true,File.separator+"2017-01-07\\12",lists);
//		for (int i = 0; i < lists.size(); i++) {
//		System.out.println(lists.get(i));
//			
//		}
//		listFilesStr(true,File.separator,lists);
//		for (int i = 0; i < lists.size(); i++) {
//		System.out.println(lists.get(i));
//			
//		}
		//服务器文件list文件名去除空�?
//		List<String>  listAllFtpServerFliesTrim=new ArrayList<String>();
//		//服务器文件list文件名不除去空格
//		List<String>  listAllFtpServerFliesUnTrim=new ArrayList<String>();
//	    new FTPClientUtil().listFilesStr2(false, "2017-01-14", listAllFtpServerFliesUnTrim, listAllFtpServerFliesTrim);
//		
		System.out.println(fClient.getReplyCode());
		 FTPReply.isPositiveCompletion(fClient.getReplyCode());
		 System.out.println(FTPReply.isPositiveCompletion(fClient.getReplyCode()));
		
		
	}
}
