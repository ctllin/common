package com.ctl.utils.ftp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xilehang.portalpay.po.PortalPayOrderGoodsTitleSynchro;

/**
 * 
 * @author Nan 2015-11
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil {
	public static Logger logger = Logger.getLogger(HttpClientUtil.class);
	private static PoolingHttpClientConnectionManager cm;
	private static String EMPTY_STR = "";
	private static String UTF_8 = "UTF-8";

	private static void init() {
		if (cm == null) {
			cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(50);// 整个连接池最大连接数
			cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认�?�?
		}
	}
	public static void main(String[] args) throws JSONException {
//		
//		  System.getProperties().list(System.out);//得到当前的系统属性�?并将属�?列表输出到控制台   
//		        System.out.println("************************");   
//		 String encoding = System.getProperty("file.encoding");   
//		  System.out.println("Encoding:" + encoding);   

		float totalMoney=1f;
		PortalPayOrderGoodsTitleSynchro  ffff=new PortalPayOrderGoodsTitleSynchro();
		ffff.setTotalMoney(totalMoney);
		
        System.out.println(ffff.getTotalMoney().toString().split("\\.")[0]);
		Map<String,Object> params=new HashMap<String,Object>();
		String orderNum="DK201612161707549717600CB6D1CF23";
		params.put("version", "2.6");
		params.put("serialID", orderNum);//2017011175106295   DKFFD023B949D77EE720170111105445
		params.put("mode", "1");
		params.put("orderID", orderNum);
		params.put("type", "1");
		params.put("partnerID", "11000000473");
		params.put("remark", "1");
		params.put("charset", "1");
		params.put("signType", "1");
		params.put("signMsg", RSASignUtils.getSignMsg(orderNum));
	    
	    try {
			String result="";//httpPostRequest("http://localhost/TradePaySSM/portalpay/getPortalPayResult", params);
			result=httpPostRequest("https://gateway.hnapay.com/website/queryOrderResult.htm", params);
			System.err.println(result);
			//SubmitPost("http://127.0.0.1/TradePaySSM/common/fileUpload2/3","D:\\file.xls");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    
	    params.clear();
	    Map<String,String> params1=new HashMap<String,String>();
	    params1.put("seatNum", "30d");
	    params1.put("idNum","7314");
	    String  requestUrl="http://103.235.220.106:8081/web/login.ashx";
	    jsonSendReturnGZIP(requestUrl,params1);
	   // jsonSendReturnGZIP2(requestUrl,params1);
	}

	/**
	 * 
	 * @param url  上传文件地址
	 * @param fileNamesPath 上传文件
	 */
	public static void submitPost(String url,String ...fileNamesPath){
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity();
			for (int i = 0; i < fileNamesPath.length; i++) {
				//fileNamesPath为请求后台的File upload;属�?   
				reqEntity.addPart("file"+i,new FileBody(new File(fileNamesPath[i])));
				//comment为请求后台的普�?参数属�?	
				StringBody comment = new StringBody(fileNamesPath[i]);
				reqEntity.addPart("file"+i+"comment",comment);
			}
		    httppost.setEntity(reqEntity);
		    HttpResponse response = httpclient.execute(httppost);
		    int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK){
				System.out.println("服务器正常响�?....");
		    	HttpEntity resEntity = response.getEntity();
		    	System.out.println(EntityUtils.toString(resEntity));//httpclient自带的工具类读取返回数据
		    	System.out.println(resEntity.getContent());   
		    	EntityUtils.consume(resEntity);
		    }
			} catch (ParseException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
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
				String result = EntityUtils.toString(entity);
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


	public static void jsonSend(String requestUrl,Map<String,String> data) throws JSONException {
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
			// out.writeBytes(obj.toString());//这个中文会乱�?
			out.write(obj.toString().getBytes());// 这样可以处理中文乱码问题
			out.flush();
			out.close();

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				//lines = new String(lines.getBytes("utf-8"), "utf-8");
				//lines = new String(lines.getBytes("gbk"),"gbk");
//				lines = new String(lines.getBytes("gb2312"),"gb2312");
//				lines = new String(lines.getBytes("ISO8859-1"),"ISO8859-1");
				sb.append(lines);
			}
			System.out.println(sb);
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

	}

	public static void jsonSendReturnGZIP(String requestUrl,Map<String,String> data) throws JSONException {
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
			// out.writeBytes(obj.toString());//这个中文会乱�?
			out.write(obj.toString().getBytes());// 这样可以处理中文乱码问题
			out.flush();
			out.close();

//			// 读取响应
//			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//			String lines;
//			StringBuffer sb = new StringBuffer("");
//			while ((lines = reader.readLine()) != null) {
//				//lines = new String(lines.getBytes("utf-8"), "utf-8");
//				//lines = new String(lines.getBytes("gbk"),"gbk");
////				lines = new String(lines.getBytes("gb2312"),"gb2312");
////				lines = new String(lines.getBytes("ISO8859-1"),"ISO8859-1");
//				sb.append(lines);
//			}
			
			int size=connection.getInputStream().available();
			byte bytes[]=new byte[size];
			connection.getInputStream().read(bytes);
			//logger.info(new String(bytes));
		    ByteArrayOutputStream outb = new ByteArrayOutputStream();  
	        ByteArrayInputStream in = new ByteArrayInputStream(bytes);  
	        try {  
	            GZIPInputStream ungzip = new GZIPInputStream(in);  
	            byte[] buffer = new byte[256];  
	            int n;  
	            while ((n = ungzip.read(buffer)) >= 0) {  
	            	outb.write(buffer, 0, n);  
	            }  
	           logger.info(new String(outb.toByteArray()));
	        } catch (IOException e) {  
	           logger.error("gzip uncompress error.", e);  
	        }  
			
			
			
			
			//System.out.println(sb);
			//reader.close();
			// 断开连接
	        outb.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void jsonSendReturnGZIP2(String requestUrl,Map<String,String> data) throws JSONException {
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
			// out.writeBytes(obj.toString());//这个中文会乱�?
			out.write(obj.toString().getBytes());// 这样可以处理中文乱码问题
			out.flush();
			out.close();

//			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				sb.append(lines);
			}
			
//			int size=connection.getInputStream().available();
//			byte bytes[]=new byte[size];
//			connection.getInputStream().read(bytes);
			logger.info(sb);
			logger.info(new String(sb.toString().getBytes("utf-8")));
		    ByteArrayOutputStream outb = new ByteArrayOutputStream();  
		    byte[] bytess=sb.toString().getBytes("utf-8");
	        ByteArrayInputStream in = new ByteArrayInputStream(bytess);  
	        try {  
	            GZIPInputStream ungzip = new GZIPInputStream(in);  
	            byte[] buffer = new byte[256];  
	            int n;  
	            while ((n = ungzip.read(buffer)) >= 0) {  
	            	outb.write(buffer, 0, n);  
	            }  
	           logger.info(new String(outb.toByteArray()));
	        } catch (IOException e) {  
	           logger.error("gzip uncompress error.", e);  
	        }  
			
			
			
			
			//System.out.println(sb);
			reader.close();
			// 断开连接
	        outb.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	 public static Map<String,Object> getPortalPayOrderRequestParams(PortalPayOrderGoodsTitleSynchro ppogts) {
	       StringBuilder buf=new StringBuilder();
	       Map<String,Object> map=new HashMap<String,Object>();
	       map.put("version",ppogts.getVersion());
	       map.put("tranCode",ppogts.getTranCode());
	       map.put("merId",ppogts.getMerId());
	       map.put("merOrderId",ppogts.getOrderNumber());
	       map.put("submitTime",ppogts.getDate());
	       try {
	    	   map.put("tranAmt",ppogts.getTotalMoney().toString().split("\\.")[0]);
		   } catch (Exception e) {
			   map.put("tranAmt",ppogts.getTotalMoney());
		   }
	       map.put("paySecInfo",ppogts.getOfflineinfo());
	       map.put("goodsName",ppogts.getGoogsName());
	       map.put("recvName",ppogts.getSecondaryContact());
	       map.put("recvPhone",ppogts.getSecondaryPhone());
	       map.put("recvAddress",ppogts.getCustomerAddress());
	       map.put("notifyUrl","http://xilehang.cn/alipay/portalpay/receivePortalPayCallbackOffline");
	       map.put("remark","");
	       map.put("charset","1");
	       map.put("signType","1");
	       buf.append("version=[");
	       buf.append(ppogts.getVersion());
	       buf.append("]tranCode=[");
	       buf.append(ppogts.getTranCode());
	       buf.append("]merId=[");
	       buf.append(ppogts.getMerId());
	       buf.append("]merOrderId=[");
	       buf.append(ppogts.getOrderNumber());
	       buf.append("]submitTime=[");
	       buf.append(ppogts.getDate());
	       buf.append("]tranAmt=[");
	       try {
	    	   buf.append(ppogts.getTotalMoney().toString().split("\\.")[0]);
		   } catch (Exception e) {
			   buf.append(ppogts.getTotalMoney());
		   }
	       
	       buf.append("]paySecInfo=[");
	       buf.append(ppogts.getOfflineinfo());
	       buf.append("]charset=[");
	       buf.append(ppogts.getCharset());
	       buf.append("]signType=[");
	       buf.append(ppogts.getSignType());
	       buf.append("]");
	       map.put("signValue",RSASignUtils.genSignByRSA(buf.toString()));
	       return map;
	}

	
}
