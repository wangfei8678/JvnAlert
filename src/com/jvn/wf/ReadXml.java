/**
 * 
 */
package com.jvn.wf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author wang_fei_gn
 * 既存ファイルを読み込む
 * 
 */
public class ReadXml {
	/**
	 * windowsで使ってるpath
	 * */
		private static final String path = "/C:/Users/wang_fei_gn/Desktop/test.xml";
	/**
	 * linuxで使ってるpath
	 * */
//	private static final String path = "/home/wang_fei/test.xml";
	File readFile = new File(path);
	Element element = null;
	DocumentBuilder dbXml = null;
	DocumentBuilderFactory dbfXml = null;
	NodeList nodeDetail = null;
	/**
	 * <title>...</title>の中身を記録する
	 * */
	public List<String> saveTitle(){
		List<String> vulntitle_list = new ArrayList<String>();
		dbfXml = DocumentBuilderFactory.newInstance();
		try {
			dbXml = dbfXml.newDocumentBuilder();
			Document document = dbXml.parse(readFile);
			element = document.getDocumentElement();//根元素
			NodeList childNodes = element.getChildNodes();
			for(int i = 0;i < childNodes.getLength();i++){
				Node node1 = childNodes.item(i);
				if("item".equals(node1.getNodeName())){
//					System.out.println("\r\n脆弱性が発見: " + node1.getAttributes().getNamedItem("rdf:about").getNodeValue() + ". ");
					nodeDetail = node1.getChildNodes();
					for(int j = 0;j < nodeDetail.getLength();j++){
						Node detail = nodeDetail.item(j);
						if("title".equals(detail.getNodeName())){
							vulntitle_list.add(detail.getTextContent());
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vulntitle_list;
	}
	/**
	 * <link>...</link>の中身を記録する 
	 * */
	public List<String> saveLink(){
		List<String> vulnlink_list = new ArrayList<String>();
		dbfXml = DocumentBuilderFactory.newInstance();
		try {
			dbXml = dbfXml.newDocumentBuilder();
			Document document = dbXml.parse(readFile);
			element = document.getDocumentElement();//根元素
			NodeList childNodes = element.getChildNodes();
			for(int i = 0;i < childNodes.getLength();i++){
				Node node1 = childNodes.item(i);
				if("item".equals(node1.getNodeName())){
					nodeDetail = node1.getChildNodes();
					for(int j = 0;j < nodeDetail.getLength();j++){
						Node detail = nodeDetail.item(j);
						if("link".equals(detail.getNodeName())){
							vulnlink_list.add(detail.getTextContent());
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vulnlink_list;
	}	
	
	/**
	 * <description>...</description>の中身を記録する
	 * */
	public List<String> saveDescription(){
		List<String> vulndescription_list = new ArrayList<String>();
		dbfXml = DocumentBuilderFactory.newInstance();
		try {
			dbXml = dbfXml.newDocumentBuilder();
			Document document = dbXml.parse(readFile);
			element = document.getDocumentElement();//根元素
			NodeList childNodes = element.getChildNodes();
			for(int i = 0;i < childNodes.getLength();i++){
				Node node1 = childNodes.item(i);
				if("item".equals(node1.getNodeName())){
					nodeDetail = node1.getChildNodes();
					for(int j = 0;j < nodeDetail.getLength();j++){
						Node detail = nodeDetail.item(j);
						if("description".equals(detail.getNodeName())){
							vulndescription_list.add(detail.getTextContent());
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vulndescription_list;
	}
}
