package Game.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class GlobalExecutors {
    private GlobalExecutors() {}

    private static ThreadFactory named(String prefix, int prio, boolean daemon) {
        AtomicInteger n = new AtomicInteger(1);
        return r -> {
            Thread t = new Thread(r, prefix + n.getAndIncrement());
            t.setPriority(prio);
            t.setDaemon(daemon);
            return t;
        };
    }

    public static final ThreadPoolExecutor IO = new ThreadPoolExecutor(
            4, Math.max(16, Runtime.getRuntime().availableProcessors()*4),
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000),
            named("io-", Thread.NORM_PRIORITY, true),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static final ThreadPoolExecutor CPU = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            named("cpu-", Thread.NORM_PRIORITY, true),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static final ScheduledThreadPoolExecutor SCHED = new ScheduledThreadPoolExecutor(
            Math.max(2, Runtime.getRuntime().availableProcessors()/2),
            named("sched-", Thread.NORM_PRIORITY, true)
    );

    static {
        SCHED.setRemoveOnCancelPolicy(true);
        IO.allowCoreThreadTimeOut(true);
        CPU.allowCoreThreadTimeOut(true);
    }

    public static void executeIO(Runnable r){ IO.execute(r); }
    public static void executeCPU(Runnable r){ CPU.execute(r); }
    public static ScheduledFuture<?> everyFixed(long initialDelay, long period, TimeUnit unit, Runnable r){
        return SCHED.scheduleAtFixedRate(r, initialDelay, period, unit);
    }
    public static ScheduledFuture<?> everyDelay(long initialDelay, long delay, TimeUnit unit, Runnable r){
        return SCHED.scheduleWithFixedDelay(r, initialDelay, delay, unit);
    }
    public static ScheduledFuture<?> after(long delay, TimeUnit unit, Runnable r){
        return SCHED.schedule(r, delay, unit);
    }
}
