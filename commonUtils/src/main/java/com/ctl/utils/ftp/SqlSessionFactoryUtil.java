package com.ctl.utils.ftp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xilehang.portalpay.mapper.PortalPayCommentMapper;
import com.xilehang.portalpay.mapper.PortalPayFtpDownloadFileMapper;
import com.xilehang.portalpay.po.PortalPayComment;
import com.xilehang.portalpay.po.PortalPayCommentExample;
import com.xilehang.portalpay.po.PortalPayFtpDownloadFile;
import com.xilehang.portalpay.po.PortalPayFtpDownloadFileExample;
import com.xilehang.portalpay.mapper.PortalPayOrderGoodsMapper;
import com.xilehang.portalpay.mapper.PortalPayOrderMapper;
import com.xilehang.portalpay.mapper.PortalPaySynchroMapper;
import com.xilehang.portalpay.po.PortalPayOrder;
import com.xilehang.portalpay.po.PortalPayOrderExample;
import com.xilehang.portalpay.po.PortalPayOrderGoods;
import com.xilehang.portalpay.po.PortalPayOrderGoodsExample;
import com.xilehang.portalpay.po.PortalPaySynchro;
import com.xilehang.portalpay.po.PortalPaySynchroExample;

public class SqlSessionFactoryUtil {
	static Logger logger = Logger.getLogger(SqlSessionFactoryUtil.class);
	//private static SqlSessionFactory sqlSessionFactory=getSqlSessionFactory();
	private static ApplicationContext context=getDataBaseContext();
	public static String getProgectPath() {
		logger.info(ClassLoader.getSystemResource("")); 
		logger.info(SqlSessionFactoryUtil.class.getResource("")); 
		logger.info(SqlSessionFactoryUtil.class.getResource("/")); 
		logger.info(new File("").getAbsolutePath()); 

		String strResult = null;
		if (System.getProperty("os.name").toLowerCase().indexOf("window") > -1) {
			strResult = SqlSessionFactoryUtil.class.getResource("/").toString().replace("file:/", "").replace("%20", " ");
		} else {
			strResult = SqlSessionFactoryUtil.class.getResource("/").toString().replace("file:", "").replace("%20", " ");
		}
		try {
			logger.info("getProgectPath1:"+strResult);
			strResult=URLDecoder.decode(strResult,"utf-8");
			logger.info("sqlMapConfig路径�?"+strResult);
			return strResult;
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return strResult;
	}
	
	public static String getProgectPath2(){
		String filePath = System.getProperty("java.class.path");
		String pathSplit = System.getProperty("path.separator");//windows下是";",linux下是":"
		
		if(filePath.contains(pathSplit)){
			filePath = filePath.substring(0,filePath.indexOf(pathSplit));
		}else if (filePath.endsWith(".jar")) {//截取路径中的jar包名,可执行jar包运行的结果里包�?.jar"
			logger.info("filePath.endsWith jar "+filePath+"\t");
			//此时的路径是"E:\workspace\Demorun\Demorun_fat.jar"，用"/"分割不行
			//下面的语句输出是-1，应该改为lastIndexOf("\\")或�?lastIndexOf(File.separator)
//			System.out.println("getPath2:"+filePath.lastIndexOf("/"));
			filePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
			
		}
		return filePath;
	}

	
//    public static SqlSessionFactory getSqlSessionFactory(){
//    	if(sqlSessionFactory!=null){
//    		return sqlSessionFactory;
//    	}else{
//    		   SqlSessionFactory sqlSessionFactory=null;
//    		   // String resource =null;//getProgectPath()+ConfigUtils.getType("sqlmapconfig.path");
//    			// 得到配置文件�?
//    			InputStream inputStream = null;
//    			try {
//    				//inputStream = new FileInputStream(new File(resource));
//    				//SqlSessionFactoryUtil.class.getClass().getResource("/mybatis/sqlMapConfig.xml"); 
//    				//inputStream = new FileInputStream(new File("mybatis/sqlMapConfig.xml"));
//    				//此种写法兼容代码直接在项目中运行，和打包成jar文件运行
//    				inputStream = SqlSessionFactoryUtil.class.getClass().getResourceAsStream("/mybatis/sqlMapConfig.xml");
//    				sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//    			} catch (Exception e) {
//    				logger.info("mybatis配置文件加载失败�?,e);
//    			}
//
//    			// 创建会话工厂，传入mybatis的配置文件信�?
//    		    return sqlSessionFactory;
//    	}
//    }
    public static int savePortalPayFtpDownloadFile(PortalPayFtpDownloadFile bean){
//			SqlSession sqlSession = SqlSessionFactoryUtil.getSqlSessionFactory().openSession();
//			PortalPayFtpDownloadFileMapper mapper=sqlSession.getMapper(PortalPayFtpDownloadFileMapper.class);
//			mapper.insert(bean);
//			sqlSession.commit();
//			sqlSession.close();
		    PortalPayFtpDownloadFileMapper mapper = (PortalPayFtpDownloadFileMapper)context.getBean("portalPayFtpDownloadFileMapper");
		    PortalPayFtpDownloadFileExample example=new PortalPayFtpDownloadFileExample();
		    example.createCriteria().andFilePathEqualTo(bean.getFilePath());
		    List<PortalPayFtpDownloadFile> selectByExample = mapper.selectByExample(example);
		    if(selectByExample.size()>=1){
		    	logger.info("该待下载文件已经入库");
		    	return 1;
		    }
		    return mapper.insert(bean);
    }
    
    public static int savePortalPayComment(PortalPayComment bean){
    	PortalPayCommentMapper mapper = (PortalPayCommentMapper)context.getBean("portalPayCommentMapper");
    	PortalPayCommentExample example=new PortalPayCommentExample();
    	example.createCriteria().andOrderNumberEqualTo(bean.getOrderNumber());
    	List<PortalPayComment> selectByExample = mapper.selectByExample(example);
    	if(selectByExample.size()>=1){
    		logger.info("评论已经存在 orderNumber�?+bean.getOrderNumber());
    		return 1;
    	}
	    return mapper.insert(bean);
   }
    
    public static int savePortalPayOrder(PortalPayOrder bean){
    	PortalPayOrderMapper mapper = (PortalPayOrderMapper)context.getBean("portalPayOrderMapper");
    	PortalPayOrderExample example=new PortalPayOrderExample();
    	example.createCriteria().andOrderNumberEqualTo(bean.getOrderNumber());
    	List<PortalPayOrder> selectByExample = mapper.selectByExample(example);
    	if(selectByExample.size()>=1){
    		logger.info("订单已经存在 orderNumber�?+bean.getOrderNumber());
    		return 1;
    	}
	    return mapper.insert(bean);
   }
    public static int savePortalPayOrderGoods(PortalPayOrderGoods bean){
    	PortalPayOrderGoodsMapper mapper = (PortalPayOrderGoodsMapper)context.getBean("portalPayOrderGoodsMapper");
    	PortalPayOrderGoodsExample example=new PortalPayOrderGoodsExample();
    	example.createCriteria().andOrderNumberEqualTo(bean.getOrderNumber());
    	List<PortalPayOrderGoods> selectByExample = mapper.selectByExample(example);
    	if(selectByExample.size()>=1){
    		logger.info("同一订单商品已经存在�?+bean);
    		return 1;
    	}
	    return mapper.insert(bean);
   }
    public static int savePortalPaySynchro(PortalPaySynchro bean){
    	PortalPaySynchroMapper mapper = (PortalPaySynchroMapper)context.getBean("portalPaySynchroMapper");
    	PortalPaySynchroExample example=new PortalPaySynchroExample();
    	example.createCriteria().andMerOrderIdEqualTo(bean.getMerOrderId());
    	List<PortalPaySynchro> selectByExample = mapper.selectByExample(example);
    	if(selectByExample.size()>=1){
    		PortalPaySynchro pps=selectByExample.get(0);
    		if(null==pps.getResultCode()||!"0000".equals(pps.getResultCode())&&bean.getMerId()!=null){
    			bean.setId(pps.getId());
    			mapper.updateByPrimaryKey(bean);
    		}
    		logger.info("同一订单代扣反馈信息已经存在�?+bean);
    		return 1;
    	}
	    return mapper.insert(bean);
   }
    public static int updateFlagByUUID(String uuid,String flag){
//		SqlSession sqlSession = SqlSessionFactoryUtil.getSqlSessionFactory().openSession();
//		PortalPayFtpDownloadFileMapper mapper=sqlSession.getMapper(PortalPayFtpDownloadFileMapper.class);
//		mapper.insert(bean);
//		sqlSession.commit();
//		sqlSession.close();
	    PortalPayFtpDownloadFileMapper mapper = (PortalPayFtpDownloadFileMapper)context.getBean("portalPayFtpDownloadFileMapper");
	    PortalPayFtpDownloadFile selectByPrimaryKey = mapper.selectByPrimaryKey(uuid);
	    if(selectByPrimaryKey==null){
	    	logger.info("待更新记录不存在�?+uuid);
	    	return -1;
	    }
	    selectByPrimaryKey.setFlag(flag);
	    return mapper.updateByPrimaryKey(selectByPrimaryKey);
}
    
    public static int delPortalPayFtpDownloadFile(String uuid){
//		SqlSession sqlSession = SqlSessionFactoryUtil.getSqlSessionFactory().openSession();
//		PortalPayFtpDownloadFileMapper mapper=sqlSession.getMapper(PortalPayFtpDownloadFileMapper.class);
//		mapper.deleteByPrimaryKey(uuid);
//		sqlSession.commit();
//		sqlSession.close();
	    PortalPayFtpDownloadFileMapper mapper = (PortalPayFtpDownloadFileMapper)context.getBean("portalPayFtpDownloadFileMapper");
	    PortalPayFtpDownloadFile selectByPrimaryKey = mapper.selectByPrimaryKey(uuid);
	    if(selectByPrimaryKey!=null){
	    	String flag=selectByPrimaryKey.getFlag();
	    	if(!"1".equals(flag)||!"2".equals(flag)||!"3".equals(flag)){
	    		return mapper.deleteByPrimaryKey(uuid);
	    	}
	    }
		return 0;
    }
    public static List<PortalPayFtpDownloadFile> getPortalPayFtpDownloadFileList(){
//    	SqlSession sqlSession = SqlSessionFactoryUtil.getSqlSessionFactory().openSession();
//    	PortalPayFtpDownloadFileMapper mapper=sqlSession.getMapper(PortalPayFtpDownloadFileMapper.class);
//    	List<PortalPayFtpDownloadFile> dateList=mapper.selectLimit();
//    	sqlSession.close();
//		return dateList;
	    PortalPayFtpDownloadFileMapper mapper = (PortalPayFtpDownloadFileMapper)context.getBean("portalPayFtpDownloadFileMapper");
	    List<PortalPayFtpDownloadFile> dateList=mapper.selectLimit();
	    return dateList;
    }
    
    
    public static ApplicationContext getDataBaseContext(){
    	if(context!=null){
    		return context;
    	}else{
    		try {
    			context= new ClassPathXmlApplicationContext("/spring/applicationContextDB.xml");
			} catch (Exception e) {
				logger.error("获取数据库连接失败：",e);
				return null;
			}
    	}
		return context;
    }
	public static void main(String[] args) {
		PortalPayFtpDownloadFile bean=new PortalPayFtpDownloadFile();
		String uuid=UUID.randomUUID().toString().replaceAll("-", "");
		String filePath="D:\\ftpclient\\2017-01-17\\dl_2017-01-15170806_9_f.zip";
		bean.setUuid(uuid);
		bean.setFilePath(filePath);
		//savePortalPayFtpDownloadFile(bean);
//		delPortalPayFtpDownloadFile("b6b8f280273347ce904297178fc96b28");
//		System.out.println(getPortalPayFtpDownloadFileList().size());
//		System.out.println(getPortalPayFtpDownloadFileList());;
		savePortalPayFtpDownloadFile(bean);
	}
}
