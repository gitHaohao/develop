package com.blog.web.util;

import java.util.ArrayList;
import java.util.List;

public class URLBean {
	
	private int visitLimit = 600;//ÿ3���ӷ��ʴ�������
	
	private String unRestrictIp ;//�����Ʒ��ʵ�ip��ַǰ׺
	
	private List<String> countFilterURL = new ArrayList<String>();//��Ҫ���Ʒ��ʵĽӿ�

	 public List<String> getCountFilterURL() {
	  return countFilterURL;
	 }

	 public void setCountFilterURL(List<String> countFilterURL) {
	  this.countFilterURL = countFilterURL;
	 }

	 public int getVisitLimit() {
	  return visitLimit;
	 }

	 public void setVisitLimit(int visitLimit) {
	  this.visitLimit = visitLimit;
	 }

	 public String getUnRestrictIp() {
	  return unRestrictIp;
	 }

	 public void setUnRestrictIp(String unRestrictIp) {
	  this.unRestrictIp = unRestrictIp;
	 }
	 
	 
}
