package com.water.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xerial.snappy.Snappy;

import com.water.util.file.FileUtil;

/**
 * 压缩工具
 * @author honghm
 *
 */
public class CompressTool {
	
	private final static int File_Max_Size = Integer.valueOf(System.getProperty("system.file.maxSize", "1024000"));
	
	public static byte[] compress(byte[] source) throws IOException{
		return Snappy.compress(source);
	}
	
	public static byte[]  uncompress(byte[] compressed) throws IOException{
		return Snappy.uncompress(compressed);
	}
	
	/**
	 * 压缩保存
	 * !!!小文件
	 * @param file
	 * @param source
	 * @throws IOException 
	 */
	public static void save(File file,byte[] source) throws IOException{
		byte[] dest = compress(source);
		if(dest.length > File_Max_Size){
			throw new IOException("压缩后文件大于限定额度：[" + File_Max_Size + "]byte");
		}
		if(!file.exists()){
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		try {
			fos.write(dest);
			fos.flush();
		} catch (IOException e) {
		}finally {
			fos.close();
		}
	}
	
	/**
	 * !!!小文件
	 * 解压还原
	 * @param file
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static byte[] read(File file) throws Exception{
		long len = file.length();
		if(len > 10240000){
			throw new RuntimeException(file.getName() + ",文件过大");
		}
		byte[] bytes = new byte[(int)len];
		FileInputStream fis = new FileInputStream(file);
		try{
			fis.read(bytes);
			return uncompress(bytes);
		}finally {
			fis.close();
		}
	}
	
	public static void main(String[] args) {
		String text = "1、UNIX文件系统采用多级索引结构,每个文件的索引表为13个索引项,每项2个字节." + 
			"2、前10个索引项直接存放文件信息的物理块号（直接寻址）,最多寻址10个物理块." +
			"3、如果文件大于10块,则利用第11项指向一个物理块,该块中最多可放256个文件物理块的块号（一次间接寻址）." +
			"4、对于更大的文件可利用第12个索引项（二次间接寻址）,最多可寻址256*256个物理块." +
			"5、再大的文件可以利用第13项作三次间接寻址,采用三级索引结构,文件最大可达256*256*256个物理块." +
			"对于2583个物理块的文件,用到二次间接寻址就可能满足了.";
		File dest = new File("test.txt");
		System.out.println(dest.renameTo(new File("aa.txt")));
		String file = "e:/logs/1521601145238.json";
		String json = FileUtil.read(file);
		System.out.println(json.length());
		try {
			String cJson = new String(compress(json.getBytes()));
			System.out.println(cJson.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		try {
//			save(dest, text.getBytes());
//			System.out.println(new String(read(dest)));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
