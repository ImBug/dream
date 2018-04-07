package com.water.util.file;

import java.io.File;

/**
 * 行内容处理
 * @author honghm
 *
 */
public interface LineProcessor {
	
	void processLine(File file,String line);
	
}
