package com.water.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.water.util.file.FileUtil;
import com.water.util.number.NumberUtil;
import com.water.util.string.StringUtil;
import com.water.util.thread.ThreadUtil;

public class HttpClient {
	
	private static int delay = 30;//ms
	private final static Log logger = LogFactory.getLog(HttpClient.class);
	protected final static Pattern Pattern_Charset = Pattern.compile("\\scharset\\s*=\\s*([\\w-\"']+)");
	private static HttpClient instance;
	private long sendtime;
	private long lastReqUrl;
	
	public static void main(String[] args) {
		try {
			HttpResponse response = HttpClient.getDefault().send(new HttpRequest("http://oidref.com/1.3.6.1.4.1.2620.1",HttpRequest.REQ_GET));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HttpClient getDefault() {
		if(instance != null) return instance;
		else{
			instance = new HttpClient();
			return instance;
		}
	}
	/**
	 * 限制同一系统请求过于频繁
	 * @param req
	 */
	private synchronized void delay(HttpRequest req){
		int _delay = delay;
		if(req.getMethod() == HttpRequest.REQ_POS){
			_delay = delay + 10;
		}
		long now = System.currentTimeMillis();
		long url = Long.valueOf(NumberUtil.fetchNumberFrom(req.getUrl(), '/'));
		if((now - sendtime) > _delay){
		}else{
			if(url == lastReqUrl){
				ThreadUtil.sleep(_delay);
			}
		}
		sendtime = System.currentTimeMillis();
		lastReqUrl = url;
	}
	
	public HttpResponse send(HttpRequest req)throws Exception{
		delay(req);
		if(logger.isDebugEnabled())logger.debug(req.getFullUrl());
		HttpResponse httpResponser = new HttpResponse();
		HttpURLConnection 	urlConnection = null;
		HttpsURLConnection  urlConnections = null;
		try {
			String ecod = req.getEncoding();
			InputStream in = null;
			try {
				if(req instanceof JksHttpsRequest){
					urlConnections = openHttpsConnection((JksHttpsRequest)req);
					in = urlConnections.getInputStream();
				}else{
					urlConnection = openConnection(req);
					in = urlConnection.getInputStream();
				}
			} catch (IOException e) {			
				if(urlConnection != null){
					in = urlConnection.getErrorStream();
				}
				if (in == null)throw e;
			}
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName(ecod)));
			httpResponser.contentCollection = new Vector<String>();
			StringBuilder temp = new StringBuilder();
			String line = bufferedReader.readLine();
			String charset = null;
			while (line != null) {
				httpResponser.contentCollection.add(line);
				temp.append(line).append("\r\n");
				if(charset == null && line != null){//获取字符集
					String content = line.toLowerCase();
					if(content.indexOf("charset") > -1){
						String charsetstr = StringUtil.fetch(Pattern_Charset, content);
						if(charsetstr != null){
							charsetstr = charsetstr.replaceAll("\"|'", "");
							charset = charsetstr.split("=")[1];
						}
					}
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();


			httpResponser.urlString = req.getFullUrl();

			URL url = null;
			if(urlConnection != null){
				url =  urlConnection.getURL();
			}else{
				url =  urlConnections.getURL();
			}
			httpResponser.defaultPort = url.getDefaultPort();
			httpResponser.file = url.getFile();
			httpResponser.host = url.getHost();
			httpResponser.path = url.getPath();
			httpResponser.port = url.getPort();
			httpResponser.protocol = url.getProtocol();
			httpResponser.query = url.getQuery();
			httpResponser.ref = url.getRef();
			httpResponser.userInfo = url.getUserInfo();

			httpResponser.content = temp.toString();
			httpResponser.contentEncoding = ecod;
			
			if(charset != null){
				httpResponser.charset = charset;
				httpResponser.contentEncoding = charset;
			}
			
			if(urlConnection != null){
				httpResponser.code = urlConnection.getResponseCode();
				httpResponser.message = urlConnection.getResponseMessage();
				httpResponser.contentType = urlConnection.getContentType();
				httpResponser.method = urlConnection.getRequestMethod();
				httpResponser.connectTimeout = urlConnection.getConnectTimeout();
				httpResponser.readTimeout = urlConnection.getReadTimeout();
			}else{
				httpResponser.code = urlConnections.getResponseCode();
				httpResponser.message = urlConnections.getResponseMessage();
				httpResponser.contentType = urlConnections.getContentType();
				httpResponser.method = urlConnections.getRequestMethod();
				httpResponser.connectTimeout = urlConnections.getConnectTimeout();
				httpResponser.readTimeout = urlConnections.getReadTimeout();
			}
			return httpResponser;
		} catch (IOException e) {
			throw e;
		} finally {
			if (urlConnection != null)urlConnection.disconnect();
			if (urlConnections != null)urlConnections.disconnect();
		}
	}
	
	private HttpURLConnection openConnection(HttpRequest req) throws IOException {
		String urlreq = req.getFullUrl();
		URL url = new URL(urlreq);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(req.getConnectTimeout());
		urlConnection.setReadTimeout(req.getReadTimeout());
		urlConnection.setRequestMethod(req.getMothodStr());
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		Map<String,String> headerMap = req.getRequestHeaders();
		for(String key:headerMap.keySet()){
			urlConnection.addRequestProperty(key, headerMap.get(key));
		}
		if (req.isPost()) {
			urlConnection.getOutputStream().write(req.getPostParam().getBytes(req.getEncoding()));
			urlConnection.getOutputStream().flush();
			urlConnection.getOutputStream().close();
		}
		return urlConnection;
	}
	
	private HttpsURLConnection openHttpsConnection(JksHttpsRequest req) throws Exception {
		String urlreq = req.getFullUrl();
		URL url = new URL(urlreq);
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(req.getConnectTimeout());
		urlConnection.setReadTimeout(req.getReadTimeout());
		urlConnection.setRequestMethod(req.getMothodStr());
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		Map<String,String> headerMap = req.getRequestHeaders();
		for(String key:headerMap.keySet()){
			urlConnection.addRequestProperty(key, headerMap.get(key));
		}
		if (req.isPost()) {
			urlConnection.getOutputStream().write(req.getPostParam().getBytes(req.getEncoding()));
			urlConnection.getOutputStream().flush();
			urlConnection.getOutputStream().close();
		}
		urlConnection.setSSLSocketFactory(req.getSSLContext().getSocketFactory());
		urlConnection.setHostnameVerifier(JksHttpsRequest.ALLOWALL);
		return urlConnection;
	}
	
	public File downloadFile(HttpRequest req,String dir) throws IOException{
		FileOutputStream foStream = null;
		InputStream is = null;
		try {
			HttpURLConnection urlConnection = openConnection(req);
			is = urlConnection.getInputStream();
			String filename = urlConnection.getHeaderFields().get("Content-Disposition").get(0).substring(21);
			is = (InputStream) urlConnection.getContent();
			File d = new File(FileUtil.toAbsolutePath(dir));
			if(!d.exists())d.mkdirs();
			File dist = new File(FileUtil.toAbsolutePath(dir + File.separator + filename));
			foStream = new FileOutputStream(dist);
			byte[] buffer = new byte[1024];
			while(true){
				int n = is.read(buffer);
				if(n < 1)break;
				foStream.write(buffer, 0, n);
			}
			return dist;
		} catch (IOException e) {
			logger.error("下载失败：" + dir,e);
		}finally{
			if(null != is){
				is.close();
			}
			if(null != foStream){
				foStream.close();
			}
		}
		return null;
	}
}
