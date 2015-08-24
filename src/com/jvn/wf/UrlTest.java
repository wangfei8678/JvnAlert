/**
 * 
 */
package com.jvn.wf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author wang_fei_gn
 *　脆弱性情報を取得、判断、関連者にメールを送信する機能を実現する
 */
public class UrlTest {
	/**
	 * windowsで使ってるpath
	 * */
	private static final String path = "/C:/Users/wang_fei_gn/Desktop/test.xml";
	/**
	 * linuxで使ってるpath
	 * */
//	private static final String path = "/home/wang_fei/test.xml";
	private static BufferedWriter writeFile;
	/**
	 * MyJVNのAPIを利用して
	 * @param year
	 * @param month
	 * @param date
	 * */
	public static void writeXml(int year,int month,int date)throws MalformedURLException, IOException{
		writeFile = new BufferedWriter(new FileWriter(path));
		String url = "http://jvndb.jvn.jp/myjvn?method=getVulnOverviewList&lang=ja&"
				+ "datePublishedEndM="+month+"&rangeDatePublic=n&datePublishedEndD="
				+date+"&rangeDatePublished=n&datePublishedEndY="+year;
		System.out.println(url);
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        InputStream in = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append("\n");
            writeFile.write(line + System.getProperty("line.separator"));
        }
        writeFile.close();
//        System.out.println(out.toString());
        reader.close();
	}
	
	@SuppressWarnings("finally")
	public static boolean queryAndSendMail(List<String> titlelist,List<String> linklist,List<String> descriptionlist,String date){
		  boolean sendSuc;
		  Connection connection = null;
		  Statement statement = null;
		  ResultSet rs = null;
		  ResultSet rs2 = null;
		  ResultSet rs3 = null;
		  ResultSet rs4 = null;
		  List<String> wareNameList = new ArrayList<String>();
		  List<String> mailAddressList = new ArrayList<String>();
		  List<Integer> servicesIdList = new ArrayList<Integer>();
		  List<String> serviceNameList = new ArrayList<String>();
		  List<Integer> middlewareIdList = new ArrayList<Integer>();
		  String vulnTitle = null;
		  String vulnLink = null;
		  String vulnDescription = null;
		  String subject = null;
		  String content = null;
		  String contentPart1 = "JVNに脆弱性情報が掲載されました。" + System.getProperty("line.separator") +
				  				"サービスの脆弱性情報は以下になります。"+ System.getProperty("line.separator") +
				  				"詳細を確認いただき、対応の判断をお願いします。"+ System.getProperty("line.separator") +
				  				System.getProperty("line.separator");
		  String contentPart2 = null;
		  String contentPart3 = null;
		  String contentPart4 = "アラートメールの変更はこちら( http://192.168.9.xxx/microad_jvn )" + System.getProperty("line.separator") +
				  				"不明点は ui-architect@microad.co.jp までお願いします。"+ System.getProperty("line.separator") +
				  				System.getProperty("line.separator") +"以上、よろしくお願いします。";
		  try {
			     String driverName="com.mysql.jdbc.Driver";
			     String userName="root";
			     String userPasswd="root";
			     String dbName="microad_jvn";
			     String table_middleware="middleware_master";
			     String table_user="user_master";
			     String table_service = "service_master";
			     String table_service_user = "service_user_relation";
			     String table_service_middleware = "service_middleware_relation";
			     String url="jdbc:mysql://192.168.10.129/"+dbName+"?user="+userName+"&password="+userPasswd;
			     Class.forName(driverName);
			     connection=DriverManager.getConnection(url);
			     statement = connection.createStatement();
			     String sql="select * from "+table_middleware;
			     rs = statement.executeQuery(sql);
			     while(rs.next()){
			    	 	wareNameList.add(rs.getString("middleware_name"));
			     }
			     for(int i = 0;i < titlelist.size();i++){
			    	 for(int j = 0;j < wareNameList.size();j++){
			    		 if(titlelist.get(i).contains(wareNameList.get(j))){
			    			 System.out.println(titlelist.get(i));
			    			 System.out.println(linklist.get(i));
			    			 vulnTitle = titlelist.get(i);
			    			 vulnLink = linklist.get(i);
			    			 vulnDescription = descriptionlist.get(i);
			    			 String sql2 = "select * from " + table_middleware +" where middleware_name = '" + wareNameList.get(j) +"'";
			    			 System.out.println(sql2);
			    			 rs2 = statement.executeQuery(sql2);
			    			 while(rs2.next()){
			    				 middlewareIdList.add(rs2.getInt("middleware_id"));
			    			 }
			    			 for(int k = 0;k < middlewareIdList.size();k++){
			    				 String sql3 = "select * from " + table_service_middleware + " where middleware_id = " + middlewareIdList.get(k);
				    			 System.out.println(sql3);
				    			 rs3 = statement.executeQuery(sql3);
				    			 while(rs3.next()){
				    				 servicesIdList.add(rs3.getInt("service_id"));
				    			 }
			    			 }
			    			 for(int m = 0;m < servicesIdList.size();m++){
			    				 String sql4 = "select * from " + table_user + "," + table_service + "," + table_service_user + 
			    						 " where " + table_service + ".service_id = " + table_service_user + ".service_id" + 
			    						 " and " + table_user + ".user_id = " + table_service_user + ".user_id" +
			    						 " and " + table_service_user +".service_id = " + servicesIdList.get(m) +
			    						 " and mail_flag = 1";
			    				 System.out.println(sql4);
			    				 rs4 = statement.executeQuery(sql4);
			    				 while(rs4.next()){
			    					 mailAddressList.add(rs4.getString("user_mailaddress"));
			    					 serviceNameList.add(rs4.getString("service_name"));
			    				 }
			    				 for(int t = 0;t < mailAddressList.size();t++){
			    					 System.out.println(mailAddressList.get(t));
			    					 System.out.println(serviceNameList.get(t));
			    				 }
			    			 }
			    			 for(int n = 0;n < mailAddressList.size();n++){
			    				 String mailAddress = mailAddressList.get(n);
			    				 String serviceName = serviceNameList.get(n);
			    				 System.out.println(mailAddressList.get(n));
			    				 subject = "脆弱性通知　" + serviceName + date;
			    				 contentPart2 = "サービス名　:" + serviceName + System.getProperty("line.separator");
			    				 contentPart3 = System.getProperty("line.separator") + 
			    						 		"タイトル       :" + vulnTitle + 
			    						 		System.getProperty("line.separator") +
			    						 		"URL      :" + vulnLink + 
			    						 		System.getProperty("line.separator") +
			    						 		"説明           :" + vulnDescription + System.getProperty("line.separator") +
			    						 		"-----------------------------------------------------------------";
			    				 content = contentPart1 + contentPart2 + contentPart3 + contentPart4;
			    				 sendSuc = sendMailForJavaByBoLun(subject, content, mailAddress);
			    				 if(sendSuc){
			    					 System.out.println("送信成功");
			    				 }else{
			    					 System.out.println("送信失敗");
			    				 }
			    			 }
			    			 middlewareIdList.clear();
			    			 mailAddressList.clear();
			    			 servicesIdList.clear();
			    			 serviceNameList.clear();
			    		 }else{
			    			 continue;
			    		 }
			    	 }
			     }
		} catch (ClassNotFoundException e) {
			   e.printStackTrace();
		} catch (SQLException e) {
			   e.printStackTrace();
		}finally{
			if(vulnTitle == null){
				 try {
					rs.close();
					statement.close();  
				    connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	 return false;
		     }else{
		    	 try {
					rs.close();
					rs2.close();
					rs3.close();
					rs4.close();
					statement.close();  
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}  
		    	 return true;
		     }
			}
		}
	
	public static boolean sendMailForJavaByBoLun(String subject , String bodyContent , String to){
		try{
			Email themail = new Email("smtp.gmail.com");
			themail.setNeedAuth(true);
			themail.setSubject(subject);
			themail.setBody(bodyContent);
			themail.setTo(to);
			themail.setFrom(new String("wang_fei_gn@microad.co.jp"));
			themail.setNamePass("wang_fei_gn@microad.co.jp", "w8678f@hotmail");
			if(themail.sendout())
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) throws SAXException {
		String weekString = null;
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int date = ca.get(Calendar.DATE);
		int week = ca.get(Calendar.DAY_OF_WEEK);
		switch(week){
		case 1: weekString = "日";break;
		case 2: weekString = "月";break;
		case 3: weekString = "火";break;
		case 4: weekString = "水";break;
		case 5: weekString = "木";break;
		case 6: weekString = "金";break;
		case 7: weekString = "土";break;
		}
		String dateString = year + "年" + month + "月" + date + "日" + "(" + weekString + ")";
		if(date == 1){
			month = month - 1;
			ca.set(Calendar.YEAR, year);
			ca.set(Calendar.MONTH, month);
			date = ca.getActualMaximum(Calendar.DATE);
		}else{
			date = date - 1; 
		}
		try {
			writeXml(year,month,date);
			ReadXml readfile = new ReadXml();
			List<String> vulntitle = readfile.saveTitle();
			List<String> vulnlink = readfile.saveLink();
			List<String> vulndescription = readfile.saveDescription();
			if(vulntitle.isEmpty()){
				System.out.println("脆弱性を発見しません.");
				sendMailForJavaByBoLun("脆弱性通知"+dateString, dateString+" 脆弱性を発見しません。", "wang_fei_gn@microad.co.jp");
			}else{
				boolean decide = queryAndSendMail(vulntitle,vulnlink,vulndescription,dateString);	
				if(decide){
					System.out.println("関連する脆弱性が発見しました。");
				}else {
					System.out.println("関連する脆弱性が発見しません。");
					sendMailForJavaByBoLun("脆弱性通知"+dateString, dateString+" DBと一致する脆弱性を発見しません。", "wang_fei_gn@microad.co.jp");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
