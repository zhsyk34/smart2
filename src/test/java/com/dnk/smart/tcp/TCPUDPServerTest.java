package com.dnk.smart.tcp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dnk.smart.tcp.UDPSponsor.awake;

public class TCPUDPServerTest {

	public static void main(String[] args) {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

		Runnable taskGroup = () -> {
			System.err.println("-----------awake schedule begin.-------------------");
			ExecutorService service = Executors.newCachedThreadPool();

			for (int i = 0; i < 5; i++) {
				final int k = i;
				service.submit(() -> awake(null, 50000 + k));
			}

			service.shutdown();
		};

		executorService.scheduleAtFixedRate(taskGroup, 1, 5, TimeUnit.SECONDS);
	}
}