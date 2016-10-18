package com.dnk.smart.tcp;

import com.dnk.smart.session.SessionManager;

public class TCPServerTest {

	public static void main(String[] args) {
		SessionManager.monitor();
		TCPServer.start();
	}

}