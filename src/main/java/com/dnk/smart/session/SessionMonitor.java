package com.dnk.smart.session;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionMonitor {

	public static void monitor() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

		Runnable task = GatewayManager::monitor;
		service.scheduleWithFixedDelay(task, 1000, 2000, TimeUnit.MILLISECONDS);
	}
}
