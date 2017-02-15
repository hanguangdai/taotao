package com.taotao.common.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.httpclient.HttpResult;


@Service
public class ApiService implements BeanFactoryAware{
	
	private BeanFactory beanFactory;
	
	@Autowired(required=false)
	private RequestConfig requestConfig;
	
	/**
	 * 执行GET请求，响应200返回内容，404返回null
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String doGet(String url) throws ClientProtocolException, IOException{
		//创建GET请求
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		try {
			//执行请求
			response = getHttpClient().execute(httpGet);
			//判断返回状态是否为200
			if(response.getStatusLine().getStatusCode()==200){
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} finally{
			if(response!=null)
				response.close();
		}
		return null;
	}
	
	/**
	 * 执行带有参数的GET请求，响应200返回内容，404返回null
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String toGet(String url,Map<String,String> params) throws ClientProtocolException, IOException, URISyntaxException{
		URIBuilder uriBuilder = new URIBuilder(url);
		for(Entry<String, String> entry : params.entrySet()){
			uriBuilder.setParameter(entry.getKey(), entry.getValue());
		}
		return this.doGet(uriBuilder.build().toString());
	}
	
	/**
	 * POST 请求
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public HttpResult toPost(String url,Map<String, String> params) throws IOException{
		HttpPost httPost = new HttpPost(url);
		httPost.setConfig(requestConfig);
		if(params != null){
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			for(Entry<String, String> entry : params.entrySet()){
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);
			httPost.setEntity(formEntity);
		}
		CloseableHttpResponse response = null;
		try {
			response = this.getHttpClient().execute(httPost);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(),"UTF-8"));
		} finally{
			if(response != null){
				response.close();
			}
		}
		
	}
	
	/**
	 * POST 请求
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public HttpResult toPostJson(String url,String jsonData) throws IOException{
		HttpPost httPost = new HttpPost(url);
		httPost.setConfig(requestConfig);
		if(null != jsonData && !"".equals(jsonData)){
			StringEntity stringEntiry = new StringEntity(jsonData,ContentType.APPLICATION_JSON);
			httPost.setEntity(stringEntiry);
		}
		CloseableHttpResponse response = null;
		try {
			response = this.getHttpClient().execute(httPost);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(),"UTF-8"));
		} finally{
			if(response != null){
				response.close();
			}
		}
		
	}
	
	private CloseableHttpClient getHttpClient(){
		return this.beanFactory.getBean(CloseableHttpClient.class);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
		this.beanFactory = beanFactory;
	}

}
