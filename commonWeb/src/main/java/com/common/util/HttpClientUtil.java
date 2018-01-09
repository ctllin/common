package com.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Nan 2015-11
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil {
	public static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	private static PoolingHttpClientConnectionManager cm;
	private static String EMPTY_STR = "";
	private static String UTF_8 = "UTF-8";

	private static void init() {
		if (cm == null) {
			cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(50);// 整个连接池最大连接数
			cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
		}
	}
	public static String sendMessage() {
		String sendMsgUrl=ConfigUtils.getType("message.platform.sendMsgUrl"); 
		StringBuilder msgBuf = new StringBuilder(); 
		msgBuf.append("username=").append(ConfigUtils.getType("message.platform.username"));//用户名 
		msgBuf.append("&password=").append(ConfigUtils.getType("message.platform.password"));//密码 444335 
		msgBuf.append("&content=").append("您已成功订购梵克雅宝");//短信内容 
		msgBuf.append("&mobile=").append("18515287139");//手机号,多个以分号分隔 
		msgBuf.append("&extcode=").append("18515287139");//扩展号，如果需要短信报告以及回复功能必须填写此项 
		msgBuf.append("&senddate=").append("");//预约时间，如果为空代表即时发送 

		String result = httpPostRequest(sendMsgUrl+"?"+msgBuf.toString()); //发送post请求得到反馈结果
		String str = "";
		if ("0".equals(result)) {
			str = "数据提交成功";
		} else if ("-2".equals(result)) {
			str = "提交的号码中包含不符合格式的手机号码";
		} else if ("-1".equals(result)) {
			str = "数据保存失败";
		} else if ("1001".equals(result)) {
			str = "用户名或密码错误";
		} else if ("1002".equals(result)) {
			str = "余额不足";
		} else if ("1003".equals(result)) {
			str = "参数错误，请输入完整的参数";
		} else if ("1004".equals(result)) {
			str = "其他错误";
		} else if ("1005".equals(result)) {
			str = "预约时间格式不正确";
		}else{ str = "非正常返回值"; }
		logger.info("短信平台返回信息:"+str);
		return result;
	}
	public static String sendMessage(Map<String,Object> dataMap) {
		String sendMsgUrl=ConfigUtils.getType("message.platform.sendMsgUrl"); 
		dataMap.put("username", ConfigUtils.getType("message.platform.username"));//用户名 
		dataMap.put("password", ConfigUtils.getType("message.platform.password"));//密码
		dataMap.put("content","【喜乐航】"+"订购阿玛尼银色不锈钢表带黑色表盘圆形石英时尚成功"+" 回复TD退订");//短信内容 
		dataMap.put("mobile","18515287139");//手机号,多个以分号分隔 
		dataMap.put("extcode","18515287139");//扩展号，如果需要短信报告以及回复功能必须填写此项 
		dataMap.put("senddate","");//预约时间，如果为空代表即时发送 yyyyMMddHHmmss
		String result = null;
		try {
			result = httpPostRequest(sendMsgUrl,dataMap);
		} catch (UnsupportedEncodingException e) {
			logger.error("请求短信平台失败",e);
		} //发送post请求得到反馈结果
		String str = "";
		if ("0".equals(result)) {
			str = "数据提交成功";
		} else if ("-2".equals(result)) {
			str = "提交的号码中包含不符合格式的手机号码";
		} else if ("-1".equals(result)) {
			str = "数据保存失败";
		} else if ("1001".equals(result)) {
			str = "用户名或密码错误";
		} else if ("1002".equals(result)) {
			str = "余额不足";
		} else if ("1003".equals(result)) {
			str = "参数错误，请输入完整的参数";
		} else if ("1004".equals(result)) {
			str = "其他错误";
		} else if ("1005".equals(result)) {
			str = "预约时间格式不正确";
		}else{ str = "非正常返回值"; }
		logger.info("短信平台返回信息:"+str);
		return str;
	}
	/**
	 * 
	 * @param url  上传文件地址
	 * @param fileNamesPath 上传文件
	 */
	@SuppressWarnings("resource")
	public static String submitPost(String url,String ...fileNamesPath){
		String result="";
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity();
			for (int i = 0; i < fileNamesPath.length; i++) {
				//fileNamesPath为请求后台的File upload;属性   
				reqEntity.addPart("files",new FileBody(new File(fileNamesPath[i])));
				//comment为请求后台的普通参数属性	
				//StringBody comment = new StringBody(fileNamesPath[i]);
				//reqEntity.addPart("files",comment);
			}
		    httppost.setEntity(reqEntity);
		    HttpResponse response = httpclient.execute(httppost);
		    int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK){
		    	HttpEntity resEntity = response.getEntity();
		    	result=EntityUtils.toString(resEntity);//httpclient自带的工具类读取返回数据
		    	EntityUtils.consume(resEntity);
		    }
			} catch (ParseException e) {
				logger.error("submitPost",e);
			} catch (IOException e) {
				logger.error("submitPost",e);
			} finally {
			    try { 
			    	httpclient.getConnectionManager().shutdown(); 
			    } catch (Exception ignore) {
			    	
			    }
			}
		return result;
	}
	/**
	 * 
	 * @param url 上传地址
	 * @param fileNamesPathList 文件列表  name默认都是files
	 */
	public static void submitPost2(String url,List<String> fileNamesPathList){
		submitPost2(url,fileNamesPathList,null);
	}
	/**
	 * 
	 * @param url 上传地址
	 * @param fileNamesPathList 文件列表  name默认都是files
	 * @param commentMap 普通参数
	 */
	public static void submitPost2(String url,List<String> fileNamesPathList,Map<String,Object> commentMap){
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity();
			for (int i = 0; i < fileNamesPathList.size(); i++) {
				reqEntity.addPart("files",new FileBody(new File(fileNamesPathList.get(i))));
			}
			if(commentMap!=null){//普通参数
				Iterator<String> iterator = commentMap.keySet().iterator();
				while (iterator.hasNext()) {
					String key=iterator.next();
					String value=commentMap.get(key)==null?null:String.valueOf(commentMap.get(key));
				    StringBody comment = new StringBody(value);
					reqEntity.addPart(key,comment);
				}
			}
		    httppost.setEntity(reqEntity);
		    HttpResponse response = httpclient.execute(httppost);
		    int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK){
				logger.info("服务器正常响应.....");
		    	HttpEntity resEntity = response.getEntity();
		    	logger.info(EntityUtils.toString(resEntity));//httpclient自带的工具类读取返回数据
		    	EntityUtils.consume(resEntity);
		    }
		} catch (ParseException e) {
			logger.error("submitPost",e);
		} catch (IOException e) {
			logger.error("submitPost",e);
		} finally {
		    try { 
		    	httpclient.getConnectionManager().shutdown(); 
		    } catch (Exception ignore) {
		    	
		    }
		}
	}

	
	/**
	 * 通过连接池获取HttpClient
	 * 
	 * @return
	 */
	private static CloseableHttpClient getHttpClient() {
		init();
		return HttpClients.custom().setConnectionManager(cm).build();
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String httpGetRequest(String url) {
		HttpGet httpGet = new HttpGet(url);
		return getResult(httpGet);
	}

	public static String httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException {
		URIBuilder ub = new URIBuilder();
		ub.setPath(url);

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		ub.setParameters(pairs);

		HttpGet httpGet = new HttpGet(ub.build());
		return getResult(httpGet);
	}

	public static String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)throws URISyntaxException {
		URIBuilder ub = new URIBuilder();
		ub.setPath(url);

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		ub.setParameters(pairs);

		HttpGet httpGet = new HttpGet(ub.build());
		for (Map.Entry<String, Object> param : headers.entrySet()) {
			httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
		}
		return getResult(httpGet);
	}

	public static String httpPostRequest(String url) {
		HttpPost httpPost = new HttpPost(url);
		return getResult(httpPost);
	}

	public static String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
		return getResult(httpPost);
	}

	public static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
			throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);

		for (Map.Entry<String, Object> param : headers.entrySet()) {
			httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
		}

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));

		return getResult(httpPost);
	}

	private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
		}

		return pairs;
	}

	/**
	 * 处理Http请求
	 * 
	 * @param request
	 * @return
	 */
	private static String getResult(HttpRequestBase request) {
		// CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpClient httpClient = getHttpClient();
		try {
			CloseableHttpResponse response = httpClient.execute(request);
			// response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// long len = entity.getContentLength();// -1 表示长度未知
				String result = EntityUtils.toString(entity,UTF_8);
				response.close();
				// httpClient.close();
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

		return EMPTY_STR;
	}
	public static String jsonSend(String requestUrl,Map<String,Object> data) {
		StringBuffer sb = new StringBuffer("");
		try {
			// 创建连接
			URL url = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);

			// connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
			connection.connect();
			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			JSONObject obj = new JSONObject();
            Set<String> key=data.keySet();
            Iterator<String> iter=key.iterator();
            while(iter.hasNext()){
            	String keyStr=iter.next();
            	obj.put(keyStr, data.get(keyStr));
            }

			// System.out.println(obj.toString());
			// out.writeBytes(obj.toString());//这个中文会乱码
			out.write(obj.toString().getBytes());// 这样可以处理中文乱码问题
			out.flush();
			out.close();

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			while ((lines = reader.readLine()) != null) {
				//lines = new String(lines.getBytes("utf-8"), "utf-8");
				//lines = new String(lines.getBytes("gbk"),"gbk");
//				lines = new String(lines.getBytes("gb2312"),"gb2312");
//				lines = new String(lines.getBytes("ISO8859-1"),"ISO8859-1");
				sb.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();

	}
	public static void main(String[] args) {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("id", "123");
		List<String> list=new ArrayList<String>();
		list.add("D:/sys_data_dictionary.sql");
		list.add("d:/20170720.txt");
		//submitPost2("http://localhost:8080/vote/image/uploadFiles", list,map);
		try {
			System.out.println(httpPostRequest("http://10.0.1.61:8080/airshop/member/memberLogin.do", map));;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Map<String,Object> map=new HashMap<String, Object>();
//		Map<String,Object> map2=new HashMap<String, Object>();
//		map2.put("id", 5);
//		map.put("map", map2);
//		jsonSend("http://localhost:8080/vote/imageController/fileDelete", map);
	}
}
