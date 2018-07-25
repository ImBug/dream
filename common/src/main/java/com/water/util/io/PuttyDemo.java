package com.water.util.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PuttyDemo {
	
	public static void main(String[] args) throws IOException {
		ServerSocket agentServe = new ServerSocket(9999);
		final Socket putty = agentServe.accept();
		final Socket sshSocket = new Socket("10.1.60.105", 22);
		if(sshSocket.isConnected() && putty.isConnected()) {
			System.out.println("connected ....");
			IoPipe puttyIn = new IoPipe(sshSocket.getInputStream(),putty.getOutputStream());
			IoPipe puttyOut = new IoPipe(putty.getInputStream(),sshSocket.getOutputStream());
			puttyOut.setListener((e)->{
				try {
					putty.sendUrgentData(0xFF);
				} catch (IOException e1) {
				}finally {
					try {
						puttyIn.close();
						puttyOut.close();
						putty.close();
						sshSocket.close();
						agentServe.close();
						System.out.println("close socket,exit");
					}catch (Exception e2) {
						
					}
				}
			});
			new Thread(puttyIn).start();
			new Thread(puttyOut).start();
		}
	}
	
}
