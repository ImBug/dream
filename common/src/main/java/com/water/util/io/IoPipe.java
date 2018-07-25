package com.water.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IO 对接
 * @author honghm
 *
 */
public class IoPipe implements Runnable{
	
	private Logger logger = LoggerFactory.getLogger(IoPipe.class);
	private InputStream in;
	private OutputStream out;
	private boolean close = false;
	private byte[] buffer = new byte[1024];
	private ReadOutEventListener listener;
	
	public IoPipe(InputStream in, OutputStream out) {
		super();
		this.in = in;
		this.out = out;
	}

	@Override
	public void run() {
		if(in == null || out == null) {
			return ;
		}
		while(!close) {
			try {
				int len = in.read(buffer);
				if(len > 0) {
					out.write(buffer, 0, len);
				}else {
					try {
						fireOn(new ReadOutEvent(in,out));
						return ;
					} catch (Exception e) {
						logger.error("event deal error",e);
					}
				}
			} catch (IOException e) {
				logger.error("read IO error,read out",e);
				fireOn(new ReadOutEvent(in,out));
				return;
			}
		}
		try {
			shutdown();
		} catch (IOException e) {
			logger.error("close IO error",e);
		}
	}
	
	public void close() {
		close = true;
	}
	
	private void shutdown() throws IOException {
		try {
			if(in != null)in.close();
		}finally {
			if(out != null)out.close();
		}
	}
	
	private void fireOn(ReadOutEvent event) {
		if(listener != null)listener.dowith(event);
	}

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setListener(ReadOutEventListener listener) {
		this.listener = listener;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
}
