package com.ctl.utils;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import sun.reflect.Reflection;



public class CommonTest9 {

	public static void main(String[] args) {
		String reqStr="http://vote.ukebms.com/common/req";
		//reqStr="http://localhost:8080/vote/common/req";
		Map<String, Object> requestRootMap = new HashMap<String, Object>();
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Map<String, Object> headMap = new HashMap<String, Object>();
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		requestRootMap.put("request", requestMap);
		requestMap.put("head", headMap);
		requestMap.put("body", bodyMap);
//		bodyMap.put("followIds", "95,94");
//		bodyMap.put("nickNames", "95,94");
		//bodyMap.put("nickName", "94");
		String returnStr ="";
		
//		headMap.put("userId", 6);
//		headMap.put("type", "unlogin.publish.article.byUserId");
//		//bodyMap.put("userId", 110);
//		// returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
//		// System.out.println(returnStr);
//		
//		headMap.put("type", "search.userList");
//		bodyMap.put("keyword", "ctl");
//		//returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
//		//System.out.println(returnStr);
//		
//		headMap.clear();
//		bodyMap.clear();
//		headMap.put("userId", "24");
//		headMap.put("type", "user.login");
//		bodyMap.put("telePhone", "13810096466");
//		bodyMap.put("password", "c485480af689492ebf74d544cd52a985");
//
//		//bodyMap.put("feedbackContent", "ctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlctctlct1");
//		//returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
//		//System.out.println(returnStr);
////		headMap.put("userId", "24");
////		headMap.put("type", "user.sendCheckCode");
////		bodyMap.put("telePhone", 18515287139l);
////		returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
////		System.out.println(returnStr);
//		
//		headMap.put("userId", "24");
//		headMap.put("type", "user.thirdParty.bind.telePhone");
//		bodyMap.put("password", "c485480af689492ebf74d544cd52a985");
//		bodyMap.put("telePhone", 185152871398l);
//		bodyMap.put("checkCode", "541247");
//		//returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
//		//System.out.println(returnStr);
//		headMap.clear();
//		bodyMap.clear();
//		//headMap.put("userId", "24");
//		headMap.put("type", "user.login");
//		bodyMap.put("password", "284b746d7fd3b17cbf74d544cd52a985");
//		bodyMap.put("telePhone", 13426217524l);
//		//bodyMap.put("checkCode", "541247");
//		returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
//		System.out.println(returnStr);
		
//		headMap.put("userId", "2");
//		headMap.put("type", "user.thirdPartyUnbind");
//		bodyMap.put("thirdPartyType", 1);
//		bodyMap.put("userUuidWB", "testuserUuidWBqqqq");
//		bodyMap.put("userUuidWX", "testuserUuidWXqqqq");
		//bodyMap.put("checkCode", "541247");
//		   headMap.put("userId", "2");
//		    bodyMap.put("followIds","3");
//		    bodyMap.put("nickName","ctllin");
//		    bodyMap.put("nickNames","ctllin");
//		    
//		    headMap.put("type", "user.updateFollow");
//		    returnStr=HttpClientUtil.jsonSend(reqStr, requestRootMap); 
//		    System.out.println(returnStr);
//		    
		    
		    bodyMap.clear();
		    bodyMap.put("unFollowIds","3");
		    headMap.put("type", "user.updateFollow");
//		    returnStr=HttpClientUtil.jsonSend(reqStr, requestRootMap); 
//		    System.out.println(returnStr);
//		    
		    headMap.put("type", "notify.info.update");
		    bodyMap.put("notifyId","3");
//		    returnStr=HttpClientUtil.jsonSend(reqStr, requestRootMap); 
//		    System.out.println(returnStr);
		    
		    
		    bodyMap.put("sendUuid","67CA3110-8235-45D4-B4FC-F2FA25E3C493");
		    headMap.put("type", "notify.info.update.bySendUUID");
//		    returnStr=HttpClientUtil.jsonSend(reqStr, requestRootMap); 
//		    System.out.println(returnStr);
		   
//		    headMap.clear();
//		    bodyMap.clear();
//		    List<Integer> list=new ArrayList<Integer>();
//		    list.add(19);
//		    bodyMap.put("commentId","19");
//		    bodyMap.put("commentIds",list);
//		    headMap.put("type", "user.comment.info");
//		    returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
//		    System.out.println(returnStr);
//		   // System.out.println(JSONObject.fromObject(requestRootMap));
//		    headMap.clear();
//		    bodyMap.clear();
//		    list=new ArrayList<Integer>();
//		    list.add(43);
//		    bodyMap.put("commentIds",list);
//		    List<Integer> commentUserIdList=new ArrayList<Integer>();
//		    commentUserIdList.add(6);
//		    bodyMap.put("commentUserIds",commentUserIdList);
//		    headMap.put("type", "user.praise.info");
//		    returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
//		    System.out.println(returnStr);
//		    System.err.println(JSONObject.fromObject(requestRootMap));
		    headMap.clear();
		    bodyMap.clear();
		    reqStr="http://vote.ukebms.com/common/req";
		 reqStr="http://localhost:8080/vote/common/req";
		    headMap.put("type", "first.page.articleList");
		    headMap.put("userId", 2);
		   // returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
		   // System.out.println(returnStr);
		    //int commentId=(int) JSONObject.fromObject(returnStr).getJSONObject("response").getJSONObject("body").get("commentId");
		    headMap.put("type", "user.follow.articleList");
		    bodyMap.put("resourceId", 2);
		    bodyMap.put("commentId", 12);
		    bodyMap.put("content", "ctl123");
		    bodyMap.put("attitude", "d");
		    bodyMap.put("nickName", "nickName");
		    bodyMap.put("articleId", "12");
		    bodyMap.put("replayCommentUserId", "123");
		    bodyMap.put("articleUserId", "123");
		    bodyMap.put("articleNickName", "123");
		    bodyMap.put("replayCommentNickName", "commentNickNam"+System.currentTimeMillis());
		   // returnStr=HttpClientUtil.jsonSend(reqStr, requestRootMap); 
		   // System.out.println(returnStr);
		    bodyMap.clear();
		    headMap.clear();
		    headMap.put("type", "unlogin.publish.article.byUserId");
		    bodyMap.put("userId", 11);
		    headMap.put("userId", null);
		   // bodyMap.put("loginUserId", 2);
			//returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
			//System.out.println(returnStr);
			 bodyMap.clear();
			    headMap.clear();
			///bodyMap.put("userId", 11);
			    bodyMap.put("userId",-2);
			    bodyMap.put("userIdQuery", 2);
			 //   bodyMap.put("loginUserId", 23);

			//headMap.put("type", "unlogin.participation.article.byUserId");
			headMap.put("type", "unlogin.follow.byUserId");
			headMap.put("type", "app.version.check");
			 bodyMap.put("version","1");
			// returnStr = HttpClientUtil.jsonSend(reqStr, requestRootMap);
			 try {
				 bodyMap.clear();
				 bodyMap.put("platform", "IFCHU");
				// bodyMap.put("platform", "IFEIU");
				 bodyMap.put("userPhone", "18515287139");
				// bodyMap.put("takenKey", "123456");
				 bodyMap.put("userId", "IFC18515287139");
				 bodyMap.put("userPassword", MD5("18515287139"));
				 bodyMap.put("olduserPassword", MD5("18515287139_"));
				 bodyMap.put("newuserPassword", MD5("18515287139"));
				 bodyMap.put("question3", "姓名");
				 bodyMap.put("answer3", "ctl");
				 bodyMap.put("question2","地址");
				 bodyMap.put("answer2", "xx");
				 bodyMap.put("question1", "生日");
				 bodyMap.put("answer1", "518");
				 String url="http://103.235.230.213/airshop/member/memberLogin.do";
				     url="http://103.235.230.213:20081/airshop/member/memberLogin.do";
				     
				String restult= HttpClientUtil.httpPostRequest(url, bodyMap);
				System.err.println(restult);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
	}
	private static String MD5(String pwd) {

		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(pwd.getBytes(), 0, pwd.length());
		// get md5 bytes
		byte p_md5Data[] = m.digest();

		String str = byte2hex(p_md5Data);
		return str;
	}
		
		
	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs;
		}
		return hs.toLowerCase();
	}

}
