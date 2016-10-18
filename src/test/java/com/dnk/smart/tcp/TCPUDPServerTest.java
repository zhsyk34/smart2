package com.dnk.smart.tcp;

import com.dnk.smart.udp.UDPSponsor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TCPUDPServerTest {

	public static void main(String[] args) {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

		Runnable taskGroup = () -> {
			System.err.println("-----------awake schedule begin.-------------------");
			ExecutorService service = Executors.newCachedThreadPool();

			/*for (int i = 0; i < 5; i++) {
				final int k = i;
				service.submit(() -> UDPSponsor.awake(null, 50000 + k));
			}*/

			service.submit(() -> UDPSponsor.awake(null, 6666));
			service.shutdown();

			while (!service.isTerminated()) {

			}
			System.err.println("-----------awake schedule end.-------------------");
		};

		executorService.scheduleAtFixedRate(taskGroup, 1, 10, TimeUnit.SECONDS);
	}
}