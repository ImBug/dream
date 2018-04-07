package com.water.http;

import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 基于jks证书的https请求认证
 * @author honghm
 *
 */
public class JksHttpsRequest extends HttpRequest {
	
	private String password;
	private String keyStorePath;
	private String trustStorePath;
	private SSLContext sslContext;
	
	public final static HostnameVerifier ALLOWALL = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	public final static TrustManager TrustAll = new X509TrustManager() {
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
	};
	
	public final static TrustManager[] Trust_All = {TrustAll};
	
	public JksHttpsRequest(String url, int method) throws NoSuchAlgorithmException, KeyManagementException {
		super(url, method);
		sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, Trust_All, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultHostnameVerifier(ALLOWALL);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}

	public JksHttpsRequest(String url, int method, String password, String keyStorePath, String trustStorePath) {
		super(url, method);
		this.password = password;
		this.keyStorePath = keyStorePath;
		this.trustStorePath = trustStorePath;
		HttpsURLConnection.setDefaultHostnameVerifier(ALLOWALL);
	}

	/**
	 * 切换证书时调用该方法
	 * @param pass
	 * @param keyStorePath
	 * @param trustStorePath
	 */
	public void initJks(String pass,String keyStorePath,String trustStorePath){
		this.password = pass;
		this.keyStorePath = keyStorePath;
		this.trustStorePath = trustStorePath;
		try {
			SSLContext sslContext = getSSLContext(password, keyStorePath, trustStorePath);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(ALLOWALL);
		} catch (Exception e) {
			throw new RuntimeException("初始化证书失败",e);
		}
	}
	
	public KeyStore getKeyStore()throws Exception{
		return getKeyStore(password,keyStorePath);
	}
	
	private KeyStore getKeyStore(String password, String keyStorePath) throws Exception {
		// 实例化密钥库
		KeyStore ks = KeyStore.getInstance("JKS");
		// 获得密钥库文件流
		FileInputStream is = new FileInputStream(keyStorePath);
		// 加载密钥库
		ks.load(is, password.toCharArray());
		// 关闭密钥库文件流
		is.close();
		return ks;
	}
	
	public SSLContext getSSLContext() throws Exception {
		if(sslContext != null)return sslContext;
		return getSSLContext(password, keyStorePath, trustStorePath);
	}
	
	private SSLContext getSSLContext(String password, String keyStorePath, String trustStorePath)throws Exception {
		// 实例化密钥库
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// 获得密钥库
		KeyStore keyStore = getKeyStore(password, keyStorePath);
		// 初始化密钥工厂
		keyManagerFactory.init(keyStore, password.toCharArray());
		// 实例化信任库
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		// 获得信任库
		KeyStore trustStore = getKeyStore(password, trustStorePath);
		// 初始化信任库
		trustManagerFactory.init(trustStore);
		// 实例化SSL上下文
		SSLContext ctx = SSLContext.getInstance("TLS");
		// 初始化SSL上下文
		ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		// 获得SSLSocketFactory
		return ctx;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKeyStorePath() {
		return keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public String getTrustStorePath() {
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}
	
	
}
