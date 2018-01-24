package com.ctl.utils.ftp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.alibaba.druid.support.json.JSONUtils;
import com.ctl.ftp.SqlSessionFactoryUtil;
import com.xilehang.portalpay.po.PortalPayComment;
import com.xilehang.portalpay.po.PortalPayFtpDownloadFile;
import com.xilehang.portalpay.po.PortalPayOrder;
import com.xilehang.portalpay.po.PortalPayOrderGoods;
import com.xilehang.portalpay.po.PortalPayOrderGoodsTitleSynchro;
import com.xilehang.portalpay.po.PortalPaySynchro;
class HandleFtpDownloadZip implements Runnable{
	private static final ReentrantLock lock = new ReentrantLock();
	public static Logger logger = Logger.getLogger(HandleFtpDownloadZip.class);
	private volatile  int  activeCount;
	private volatile String uuid;
	private volatile String filename;
	public HandleFtpDownloadZip(int activeCount) {
		this.activeCount=activeCount;
	}
	public HandleFtpDownloadZip(int activeCount,String uuid) {
		this.activeCount=activeCount;
		this.uuid=uuid;
	}
	public HandleFtpDownloadZip(int activeCount,String uuid,String filename) {
		this.activeCount=activeCount;
		this.uuid=uuid;
		this.filename=filename;
	}
	public HandleFtpDownloadZip() {
		
	}
	public void run() {
		lock.lock(); 
		logger.info("当前正在执行解压线程个数�?+activeCount);
		//String filename=null;
		//文件解压处理成功，订单处理成�?
		boolean unzipFlag=false;
		boolean handleDataFlat=false;
		//文件中是否包含订单如果，不包含则认为处理陈功，包含在按照订单处理的结果为处理结果
		boolean fileHavaOrder=false;
		String savepath = null;
		try {
		   // filename=FtpDownLoad.queue.peek();//先获�?
			savepath= filename.substring(0, filename.lastIndexOf(".")) + File.separator; //保存解压文件目录
			if(filename!=null&&!"".equals(filename)&&new File(filename).exists()){
				//UnZipResult此处传引用用来处理底层�?归过程中如果遇到任何�?��压缩文件解压失败，认为本次解压失�?
				unzipFlag=ZipUtil.unZip2(filename,filename,new UnZipResult(false),uuid);
				if(unzipFlag){
					//获取解压后所有的文件夹列�?
					List<String> listAllFlies=new ArrayList<String>();
					FileUtil.listAllDirs(savepath, true, listAllFlies);
					int fileDirSize=listAllFlies.size();
					for (int i = 0; i < fileDirSize; i++) {
						String dirPath = listAllFlies.get(i);
						if (dirPath.endsWith("portal_comments")) {// 读取评论保存文件，最终入�?
							handlePortalComments(dirPath);
						} else if (dirPath.endsWith("portal_orders")) {// 读取订单保存文件，发送代扣，�?��入库
							fileHavaOrder=true;
							handleDataFlat=handlePortalOrders(dirPath);
						} else if (dirPath.endsWith("portal_signImages")) {// 将用户签名文件保�?
							handlePortalSignImages(dirPath);
						}
					}
				}else{
					logger.info("文件解压失败，不再进行文件解析处�?);
				}
			}
		} catch (Exception e) {
			logger.error("处理压缩文件失败:",e);
		}finally{
			try {
				//FtpDownLoad.queue.poll();
				//解压处理后删除数据库中的下载记录
				if(handleDataFlat||(!fileHavaOrder&&unzipFlag)){//如果订单处理成功，或zip不包含订单都认为处理成功
					SqlSessionFactoryUtil.delPortalPayFtpDownloadFile(uuid);
					logger.info("压缩文件处理结束,删除库中记录�?+uuid+"处理结果：成�?);
				}else if(!handleDataFlat&&unzipFlag){
					SqlSessionFactoryUtil.updateFlagByUUID(uuid, PortalPayFtpDownloadFile.HANDLEDATEFAIL);
					logger.info("压缩文件处理结束,更新库中记录�?+uuid+"处理结果：解压成功处理数据失�?);
				}
				FileUtil.deleteDir(savepath);
			} catch (Exception e2) {
				//logger.error("删除失败:"+uuid,e2);
				logger.error("更新处理状�?失败:"+uuid,e2);
			}
			
//			if(flag){//本次解压及订单处理成功后  将处理成功后的压缩包对应文件夹该名为 filename+="_success"
//				try {
//					File file=new File(filename.split("\\.")[0]);
//					if(file.isDirectory()&&new File(filename).exists()){//如果是文件夹切对应的压缩包还存在(解压失败后对应的文件会删�?
//						file.renameTo(new File(file+"_success"));
//					}
//				} catch (Exception e2) {
//					logger.error("处理结果");
//				}
//			}
			lock.unlock();
		}
	}

	private void handlePortalSignImages(String dirPath) {// 将用户签名文件保�?
		// 本地文件list
		Map<String, String> map = new HashMap<String, String>();
		// 获取本地对应日期文件夹所有文件列�?文件名去除空�?
		FileUtil.listAllFliesAndPath(dirPath, false, map);
		String picStorePath = ConfigUtils.getType("pic.cart.path");
		Set<String> key = map.keySet();
		Iterator<String> iter = key.iterator();
		while (iter.hasNext()) {
			String keyStr = iter.next();
			try {
				new File(map.get(keyStr)).renameTo(new File(picStorePath+ File.separator + keyStr));
			} catch (Exception e) {
				logger.error("签名图片文件转移路径失败�? + keyStr, e);
			}
		}
	}
	@SuppressWarnings("unchecked")
	private boolean handlePortalOrders(String dirPath) {// 读取订单保存文件，发送代扣，�?��入库
		boolean handleResult=true;
		List<String> listAllLocalFlies = new ArrayList<String>();
		FileUtil.listAllFlies(dirPath, false, listAllLocalFlies);
		int ordersSize = listAllLocalFlies.size();
		for (int j = 0; j < ordersSize; j++) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(listAllLocalFlies.get(j)));
				Date dateCur = new Date();
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line != null && !"".endsWith(line)) {
						PortalPayOrderGoodsTitleSynchro parse = new PortalPayOrderGoodsTitleSynchro();
						BeanConverter.toJavaBean(parse, BeanConverter.toDecryptMap((Map<String, String>) JSONUtils.parse(line)));
						try {
							if (!ConfigUtils.getType("portal.trancode.offline").equals(parse.getTranCode())) {
								continue;
							}
						} catch (Exception e) {
						}
						List<?> portalPayOrderGoodsList = parse.getSubOrderList();
						for (int k = 0; k < portalPayOrderGoodsList.size(); k++) {
							PortalPayOrderGoods ppog = new PortalPayOrderGoods();
							BeanConverter.toJavaBean(ppog,(Map<String, String>) portalPayOrderGoodsList.get(k));
							ppog.setOrderNumber(parse.getOrderNumber());
							ppog.setSavetime(dateCur);
							SqlSessionFactoryUtil.savePortalPayOrderGoods(ppog);
						}

						PortalPayOrder portalPayOrder = new PortalPayOrder();
						BeanUtils.copyProperties(portalPayOrder, parse);
						portalPayOrder.setSavetime(dateCur);
						SqlSessionFactoryUtil.savePortalPayOrder(portalPayOrder);

						PortalPaySynchro portalPaySynchro = new PortalPaySynchro();
						Map<String, Object> portalPayOrderRequestParams = HttpClientUtil.getPortalPayOrderRequestParams(parse);
						String result = null;
						try {// 地址错误,或是网络连接问题
							result = HttpClientUtil.httpPostRequest("https://gateway.hnapay.com/shareco/offlinePay.do",portalPayOrderRequestParams);
							BeanConverter.toJavaBean(portalPaySynchro,(Map<String, String>) JSONUtils.parse(result));
						} catch (Exception e) {
							logger.error("代扣地址错误,或是网络连接问题:", e);
							continue;
						}
						portalPaySynchro.setMerOrderId(portalPayOrder.getOrderNumber());
						portalPaySynchro.setSavetime(dateCur);
						portalPaySynchro.setPayflag(parse.getPayflag());
						SqlSessionFactoryUtil.savePortalPaySynchro(portalPaySynchro);
					}
				}
			} catch (Exception e) {
				handleResult=false;
				logger.error("订单信息解析失败:", e);
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
		return handleResult;
	}
	@SuppressWarnings("unchecked")
	private void handlePortalComments(String dirPath) {// 读取评论保存文件，最终入�?
		List<String> listAllLocalFlies = new ArrayList<String>();
		FileUtil.listAllFlies(dirPath, false, listAllLocalFlies);
		int commentsSize = listAllLocalFlies.size();
		for (int j = 0; j < commentsSize; j++) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(listAllLocalFlies.get(j)));
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line != null && !"".endsWith(line)) {
						PortalPayComment parse = new PortalPayComment();
						BeanConverter.toJavaBean(parse,(Map<String, String>) JSONUtils.parse(line));
						SqlSessionFactoryUtil.savePortalPayComment(parse);
					}
				}
			} catch (Exception e) {
				logger.error("处理评论失败�?, e);
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
	}
}
public class FtpDownLoad {
	public static Logger logger = Logger.getLogger(FtpDownLoad.class);
	//public static Queue<String> queue=new ConcurrentLinkedQueue<String>();
	public static ExecutorService executor=Executors.newFixedThreadPool(Integer.parseInt(ConfigUtils.getType("unzip.thread.num")));
	public static String curDate;
	//当日下载前几日的次数，默认下载三�?防止目录变更失败后不再下载前几日文件
	//public static int preDownloadTime=0;
	//应用是否是第�?��执行，第�?��执行，需要将为下载的文件下载，经未处理的的数据继续处理掉
	//private static boolean isFirstRun=false;
	public void downloadOrderZip() {
		ApplicationContext dataBaseContext = SqlSessionFactoryUtil.getDataBaseContext();
		if(dataBaseContext==null){
			logger.info("数据库连接失败，�?��下载");
			return;
		}
		FTPClient client=FTPClientUtil.ftpLogin();
		if(client==null){
			logger.info("ftp连接失败，�?出下�?);
			return;
		}
    	FTPClientUtil.toParentDir();//切换到根目录
		Date dateCur=new Date();
		//当前日期字符�?
		String curDateStr=DateUtil.sdfdatemmdd2.format(dateCur);
		if(null==curDate||"".equals(curDate)||!curDate.equals(curDateStr)){////当日下载前几日的次数，默认下载三�?防止目录变更失败后不再下载前几日文件
			//++preDownloadTime;
			//如果是刚启动应用，或是当前日期不是今�?都去�?��前几日处理情况，如果有为下载，未处理去处�?
			logger.info("下载前几日文件启�?);
			downloadOrderZipPre();
		}
		curDate=curDateStr;

		//下载到本地的路径
		String ftpClientDownloadDir=new StringBuilder(FTPClientUtil.ftpLocalDir).append(File.separator).append(curDateStr).append(File.separator).toString();
		new File(ftpClientDownloadDir).mkdirs();
		
		//获取为处理完成的文件�?删除文件夹及其对应的文件，从新下载在处理
//		List<String> listAllDirsUnhandle=new ArrayList<String>();
//		FileUtil.listAllDirsUnhandle(ftpClientDownloadDir, false, listAllDirsUnhandle);
//		if(listAllDirsUnhandle.size()>0){
//			logger.info("解压后待处理数据个数:"+listAllDirsUnhandle.size());
//			for (int i = 0; i < listAllDirsUnhandle.size(); i++) {
//				String dir=listAllDirsUnhandle.get(i);
//				try {//删除情况�?错误zip文件 解压后的文件�?另一种上次应用�?出后还没有处理完成的数据 （数据处理完成后会将文件夹名称修改为filename+='_success'�?
//					if(!queue.contains(dir+".zip")){//如果正在
//						FileUtil.deleteDir(dir);
//						new File(dir+".zip").delete();
//					}
//				} catch (Exception e) {
//					logger.error("删除未处理完成的文件夹失�?"+e);
//				}
//			}
//		}
		
		//本地文件list
		List<String>  listAllLocalFlies=new ArrayList<String>();
		//获取本地对应日期文件夹所有文件列�? 文件名去除空�?
		FileUtil.listAllFliesJustFileName(FTPClientUtil.ftpLocalDir+File.separator+curDateStr,false,listAllLocalFlies);
		logger.info("当日-ftpClientFiles size:"+listAllLocalFlies.size());
		
		//服务器文件list文件名去除空�?
		List<String>  listAllFtpServerFliesTrim=new ArrayList<String>();
		//服务器文件list文件名不除去空格
		List<String>  listAllFtpServerFliesUnTrim=new ArrayList<String>();
		try {
			//获取ftp server 对应日期文件夹所有文件列�?  文件名字去除空格
			boolean listResult=FTPClientUtil.listFilesStr2(false,curDateStr,listAllFtpServerFliesUnTrim,listAllFtpServerFliesTrim);
			if(!listResult){
				logger.info("当日-目录变更失败�?��下载:"+curDateStr);
				return;
			}
		} catch (IOException e) {
			logger.error("当日-get FtpServer fileList fail �?��下载:",e);
			return;
		}
		logger.info("当日-ftpServer cur size:"+listAllFtpServerFliesTrim.size());
		
		//获取本地list和ftpserver list 的差�?
		listAllFtpServerFliesTrim.removeAll(listAllLocalFlies);
		int  difListSize=listAllFtpServerFliesTrim.size();
		logger.info("当日-after ftpServerFiles remove ftpClientFiles size:"+difListSize);
		
		//差集下载到本�?
		if(difListSize<1){//如果没有下载任务则检查是否有为解压的文件，有则解�?
			//下载之前�?�� 是否有待解压文件 每次�?��下载100�?
			List<PortalPayFtpDownloadFile> listData=SqlSessionFactoryUtil.getPortalPayFtpDownloadFileList();
			int listSize=listData.size();
			if(listSize>0){
				logger.info("尚未解压文件本次个数(�?��100)�?+listSize);
				for(int i=0;i<listSize;i++){
					PortalPayFtpDownloadFile beanData=listData.get(i);
					//queue.add(beanData.getFilePath());
					executor.execute(new HandleFtpDownloadZip(((ThreadPoolExecutor) executor).getActiveCount(),beanData.getUuid(),beanData.getFilePath()));
				}
			}
			logger.info("数据库文件列表预处理结束");
			return;
		}
		//FTPClientUtil.changeDir(curDateStr);
		for(int i=0;i<listAllFtpServerFliesUnTrim.size();i++){
			String fileNameFtpServer=listAllFtpServerFliesUnTrim.get(i);
			if(!listAllFtpServerFliesTrim.contains(StringUtils.trimAllWhitespace(fileNameFtpServer))){//如果本地和服务器对比�? 不是新文�?则不下载
				continue;
			}
			String fileNameFtpServerTrim=StringUtils.trimAllWhitespace(fileNameFtpServer);
			
			String filePath=ftpClientDownloadDir+fileNameFtpServerTrim;
			boolean downFlag=false;
			try {
				downFlag=FTPClientUtil.downloadFile(filePath, fileNameFtpServer);
			} catch (Exception e) {
				logger.error("当日-文件下载异常�?+filePath+"\t"+curDateStr,e);
				continue;
			}
			if(downFlag){
				//先把要下载的文件入库，文件下载解析后，再删除入库文件
				//PortalPayFtpDownloadFile bean=new PortalPayFtpDownloadFile();
				String uuid=UUID.randomUUID().toString().replaceAll("-", "");
				SqlSessionFactoryUtil.savePortalPayFtpDownloadFile(new PortalPayFtpDownloadFile(uuid,filePath,new Date()));
				//queue.add(FTPClientUtil.ftpLocalDir+File.separator+curDateStr+File.separator+fileNameFtpServerTrim);
				//queue.add(filePath);
				executor.execute(new HandleFtpDownloadZip(((ThreadPoolExecutor) executor).getActiveCount(),uuid,filePath));
			}else{
				try {
					new File(filePath).delete();
					logger.info("删除下载失败文件 success"+filePath);
				} catch (Exception e) {
					logger.error("删除下载失败文件 fail"+filePath,e);
				}
			}
		}
		//FTPClientUtil.close();
	}
	private boolean downloadOrderZipPre() {
		int downloaddays=2;
		try {//得到处理近几天的压缩�?
			downloaddays=Integer.parseInt(ConfigUtils.getType("ftp.downloaddays"));
		} catch (Exception e) {
		}finally{
			class RunPreDownload implements Runnable{
				private int downloaddays;
				public RunPreDownload(int downloaddays){
					this.downloaddays=downloaddays;
				}
				public void run() {
					downloadOrderZipPreDo(downloaddays);
				}
				
			}
			for(int i=1;i<downloaddays;i++){
				RunPreDownload rpdl=new RunPreDownload(i);
				new Thread(rpdl).start();
			}
		}
		return false;
		
	}
	@SuppressWarnings("deprecation")
	private void downloadOrderZipPreDo(int datePre) {

		FTPClient fClient=FTPClientUtil.getNewFtpClient();
		FTPClientUtil ftpClientUtil=new FTPClientUtil();
		ftpClientUtil.toParentDirPre(fClient);//切换到根目录
		Date dateCur=new Date();
		dateCur.setDate(dateCur.getDate()-datePre);
		//当前日期字符�?
		String curDateStr=DateUtil.sdfdatemmdd2.format(dateCur);
		//现在到本地的路径
		String ftpClientDownloadDir=new StringBuilder(FTPClientUtil.ftpLocalDir).append(File.separator).append(curDateStr).append(File.separator).toString();
		new File(ftpClientDownloadDir).mkdirs();
		
		//本地文件list
		List<String>  listAllLocalFlies=new ArrayList<String>();
		//获取本地对应日期文件夹所有文件列�? 文件名去除空�?
		FileUtil.listAllFliesJustFileName(FTPClientUtil.ftpLocalDir+File.separator+curDateStr,false,listAllLocalFlies);
		logger.info("ftpClientFiles size:"+listAllLocalFlies.size()+"\t "+curDateStr);
		
		//服务器文件list文件名去除空�?
		List<String>  listAllFtpServerFliesTrim=new ArrayList<String>();
		//服务器文件list文件名不除去空格
		List<String>  listAllFtpServerFliesUnTrim=new ArrayList<String>();
		try {
			//获取ftp server 对应日期文件夹所有文件列�?  文件名字去除空格
			boolean  listResult=ftpClientUtil.listFilesStrPre(false,curDateStr,listAllFtpServerFliesUnTrim,listAllFtpServerFliesTrim,fClient);
			if(!listResult){
				logger.info("目录变更失败�?��下载:"+curDateStr);
				return;
			}
		} catch (IOException e) {
			logger.error("get FtpServer fileList fail �?��下载:"+"\t"+curDateStr,e);
			return;
		}
		logger.info("ftpServer cur size:"+listAllFtpServerFliesTrim.size()+"\t "+curDateStr);
		
		//获取本地list和ftpserver list 的差�?
		listAllFtpServerFliesTrim.removeAll(listAllLocalFlies);
		logger.info("after ftpServerFiles remove ftpClientFiles size:"+listAllFtpServerFliesTrim.size()+"\t "+curDateStr);
		
		//差集下载到本�?
		if(listAllFtpServerFliesTrim.size()<1){
			logger.info("ftpServer ftpClient  文件列表�?���?��本次下载"+"\t "+curDateStr);
			return;
		}
		//ftpClientUtil.changeDirPre(curDateStr, fClient);
		for(int i=0;i<listAllFtpServerFliesUnTrim.size();i++){
			String fileNameFtpServer=listAllFtpServerFliesUnTrim.get(i);
			if(!listAllFtpServerFliesTrim.contains(StringUtils.trimAllWhitespace(fileNameFtpServer))){//如果本地和服务器对比�? 不是新文�?则不下载
				continue;
			}
			String fileNameFtpServerTrim=StringUtils.trimAllWhitespace(fileNameFtpServer);
			String filePath=ftpClientDownloadDir+fileNameFtpServerTrim;
			boolean downFlag=false;
			try {
				downFlag=FTPClientUtil.downloadFilePre(filePath, fileNameFtpServer,fClient);
			} catch (Exception e) {
				logger.error("文件下载异常�?+filePath+"\t"+curDateStr,e);
				continue;
			}
			if(downFlag){
				//先把要下载的文件入库，文件下载解析后，再删除入库文件
				//PortalPayFtpDownloadFile bean=new PortalPayFtpDownloadFile();
				String uuid=UUID.randomUUID().toString().replaceAll("-", "");
				SqlSessionFactoryUtil.savePortalPayFtpDownloadFile(new PortalPayFtpDownloadFile(uuid,filePath,new Date()));
				//queue.add(FTPClientUtil.ftpLocalDir+File.separator+curDateStr+File.separator+fileNameFtpServerTrim);
				//queue.add(filePath);
				executor.execute(new HandleFtpDownloadZip(((ThreadPoolExecutor) executor).getActiveCount(),uuid,filePath));
			}else{
				try {
					//今日下载昨日数据，下载失败后不在重复下载
					String uuid=UUID.randomUUID().toString().replaceAll("-", "");
					SqlSessionFactoryUtil.savePortalPayFtpDownloadFile(new PortalPayFtpDownloadFile(uuid,filePath,new Date(),PortalPayFtpDownloadFile.UNDOWNLOAD));
					new File(filePath).delete();
					logger.info("删除下载失败文件 success"+filePath+"\t"+curDateStr);
				} catch (Exception e) {
					logger.error("删除下载失败文件 fail"+filePath+"\t"+curDateStr,e);
				}
			}
		}
		//FTPClientUtil.close();
		//ftpClientUtil.closePre(fClient);
		logger.info("下载前几日结束："+curDateStr);
	}
	
	public static void main(String[] args) {
		new FtpDownLoad().downloadOrderZip();
		//executor.shutdown();
	}
}
