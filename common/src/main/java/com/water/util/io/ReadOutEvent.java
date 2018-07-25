package com.water.util.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 输入流读完事件
 * @author honghm
 *
 */
public class ReadOutEvent {
	
	private InputStream in;
	private OutputStream out;
	
	public ReadOutEvent(InputStream in, OutputStream out) {
		super();
		this.in = in;
		this.out = out;
	}

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		return out;
	}
	
}
