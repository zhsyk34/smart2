package com.dnk.smart.tcp;

import com.dnk.smart.session.SessionMonitor;

public class TCPServerTest {

	public static void main(String[] args) {
		SessionMonitor.monitor();
		new TCPServer().start();
	}

}