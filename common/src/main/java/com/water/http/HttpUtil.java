package com.water.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;

import com.water.json.CustomObjectMapper;
import com.water.util.string.StringUtil;

/**
 * 采用apache包获取网页内容
 * 用自己写的代码存在403问题，暂时未解决TODO
 * @author honghm
 *
 */
public class HttpUtil {

	private static HttpClient client;
	public static HttpClient getClient(){
		if(client == null){
			client = HttpClientBuilder.create().build();
		}
		return client;
	}
	
	public static void main(String[] args) {
		Board board = new Board();
		List<Board> boards = new ArrayList<>();
		board.setCode("it_perf_overview_4");
		board.setApi("/chatops/public/board/it_perf_overview.html");
		board.setName("IT资源性能总览");
		boards.add(board);
		com.water.http.HttpResponse response = HttpUtil.post("http://49.4.69.251/chatops/serviceapi/v2/chat/board/regist", boards);
		if(response != null){
			System.out.println(response.getContent());
		}
	}
	
	public static com.water.http.HttpResponse post(String url,Object param){
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
		try {
			HttpEntity enity = new StringEntity(CustomObjectMapper.encodeJson(param),"utf-8");
			httpPost.setEntity(enity);
			HttpResponse response = getClient().execute(httpPost);
			if(200 == response.getStatusLine().getStatusCode()){
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				String charset = null;
				com.water.http.HttpResponse resp = new com.water.http.HttpResponse();
				while ((line = rd.readLine()) != null) {
					result.append(line);
					if(charset == null){//获取字符集
						String content = line.toLowerCase();
						if(content != null && content.indexOf("charset") > -1){
							String charsetstr = StringUtil.fetch(com.water.http.HttpClient.Pattern_Charset, content);
							if(charsetstr != null){
								charsetstr = charsetstr.replaceAll("\"|'", "");
								charset = charsetstr.split("=")[1];
							}
						}
					}
				}
				rd.close();
				if(charset != null)resp.setCharset(charset);
				resp.content = result.toString();
				return resp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static com.water.http.HttpResponse getHtml(String url) throws ClientProtocolException, IOException{
		HttpGet request = new HttpGet(url);
		HttpResponse response = getClient().execute(request);
		if(200 == response.getStatusLine().getStatusCode()){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			String charset = null;
			com.water.http.HttpResponse resp = new com.water.http.HttpResponse();
			while ((line = rd.readLine()) != null) {
				result.append(line);
				if(charset == null){//获取字符集
					String content = line.toLowerCase();
					if(content != null && content.indexOf("charset") > -1){
						String charsetstr = StringUtil.fetch(com.water.http.HttpClient.Pattern_Charset, content);
						if(charsetstr != null){
							charsetstr = charsetstr.replaceAll("\"|'", "");
							charset = charsetstr.split("=")[1];
						}
					}
				}
			}
			rd.close();
			resp.setCharset(charset);
			resp.content = result.toString();
			return resp;
		}
		return null;
	}
	
}
