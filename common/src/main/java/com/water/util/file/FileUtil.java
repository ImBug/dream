package com.water.util.file;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.water.json.CustomObjectMapper;

public class FileUtil {
	
	/**
	 * 相对路径转换成绝对路径
	 * @param relativePath
	 * @return
	 */
	public static String toAbsolutePath(String relativePath){
		return System.getProperty("user.dir") + File.separator + relativePath;
	}
	
	/**
	 * 屏幕截图
	 * @param fileName
	 * @throws Exception
	 */
	public static void saveImage(String fileName) throws Exception{
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle screenRectangle = new Rectangle(screenSize);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRectangle);
			ImageIO.write(image, "png", new File(fileName));
		} catch (HeadlessException | AWTException | IOException e) {
			throw e;
		}
	}
	
	/**
	 * 逐行处理文件
	 * @param file
	 * @param charset
	 * @param processor
	 * @throws IOException
	 */
	public static void processLine(File file,String charset,LineProcessor processor) throws IOException{
		LineIterator iterator = FileUtils.lineIterator(file, charset);
		try{
			for(;iterator.hasNext();){
				try {
					processor.processLine(file,iterator.next());
				} catch (Exception e) {
				}
			}
		}finally {
			LineIterator.closeQuietly(iterator);
		}
	}
	
	public static boolean write(String fName, String content){
		return write(new File(fName),content);
	}
	
	public static void saveAsJson(String name,List<?> list,int pageSize){
		int batch = pageSize < 1 ? 50 : pageSize;
		if (list.size() > 0) {
			String fileName = name != null ?name:list.get(0).getClass().getSimpleName();
			for (int i = 0;; i++) {
				int end = (i + 1) * batch;
				if (end > list.size() - 1) {
					write(String.format("%s_%s.json", fileName,i), CustomObjectMapper.encodeJson(list.subList(i * batch, list.size() - 1)));
					return;
				} else {
					write(String.format("%s_%s.json", fileName,i), CustomObjectMapper.encodeJson(list.subList(i * batch, end - 1)));
				}
			}
		}
	}
	
	public static String readFile(String file,String encoding) throws Exception{
		File f = new File(file);
		if(!f.exists()) return null;
		byte[] buf = new byte[1024];
		StringBuffer stringBuffer = new StringBuffer();
		FileInputStream fileInputStream = new FileInputStream(f);
		while(true){
			int len = fileInputStream.read(buf);
			if(len < 1)break;
			stringBuffer.append(new String(buf,0,len,encoding));
		}
		fileInputStream.close();
		return stringBuffer.toString();
	}
	
	public static String read(File file){
		if(file != null && file.exists()){
			try {
				FileReader fReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fReader);
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(new String(line.getBytes("utf-8")));
				}
				bufferedReader.close();
				return stringBuffer.toString();
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	public static String read(String file) {
		FileReader fReader= null;
		File f = new File(file);
		if(!f.exists())  return null;
		try {
			fReader = new FileReader(f);
			BufferedReader bufferedReader = new BufferedReader(fReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(new String(line.getBytes("utf-8")));
			}
			bufferedReader.close();
			return stringBuffer.toString();
		} catch (Exception e) {
		}
		return null;
	}
	
	public static boolean write(File f, String content) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f.getAbsoluteFile()); // true表示可以追加新内容
			bw = new BufferedWriter(fw);
			bw.write(content);
			bw.flush();
			bw.close();
			fw.close();
			return true;
		} catch (Exception e) {
			return false;
		}finally {
		}
	}
	
	/**
	 * 当文件上级目录不存在是 会创建失败
	 * @param f
	 * @param content
	 * @param nextLine
	 * @return
	 * @throws IOException
	 */
	public static boolean writeAppend(File f, String content,boolean nextLine) throws IOException {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (!f.exists()) {
				if(f.getParentFile()!= null && !f.getParentFile().exists()){
					f.getParentFile().mkdirs();
				}
				f.createNewFile();
			}
			fw = new FileWriter(f.getAbsoluteFile(),true); // true表示可以追加新内容
			bw = new BufferedWriter(fw);
			if(f.length() > 0){
				if(nextLine){
					bw.write("\n");
				}
			}
			bw.write(content);
			bw.flush();
			return true;
		} finally {
			if(bw != null)bw.close();
			if(fw != null)fw.close();
		}
	}
}
