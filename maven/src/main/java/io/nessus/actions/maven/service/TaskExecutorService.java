package io.nessus.actions.maven.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.service.AbstractService;

public class TaskExecutorService extends AbstractService<NessusConfig> {

    public TaskExecutorService(NessusConfig config) {
        super(config);
    }
    
    public ExecutorService newExecutorService(String prefix, int maxPoolSize) {
        AtomicInteger count = new AtomicInteger();
    	return new ThreadPoolExecutor(0, maxPoolSize,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                run -> new Thread(run, prefix + "-" + count.incrementAndGet()));
    }
}
