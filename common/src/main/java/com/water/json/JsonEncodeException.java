package com.water.json;

public class JsonEncodeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JsonEncodeException() {
		super();
	}

	public JsonEncodeException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public JsonEncodeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public JsonEncodeException(String arg0) {
		super(arg0);
	}

	public JsonEncodeException(Throwable arg0) {
		super(arg0);
	}
	
	
}
