package legency.june.networktest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Looper;

/**
 * @author legency
 * @date 2018/04/12.
 */
public class ThreadUtil {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
        new LinkedBlockingQueue<Runnable>(200);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ProxyTask #" + mCount.getAndIncrement());
        }
    };

    private static final RejectedExecutionHandler r = new CallerRunsPolicy();
    private static ExecutorService ex;

    public static ExecutorService get() {
        if (ex == null) {
            ex = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory, r);
        }
        return ex;
    }

    public static ExecutorService get(final String threadName) {
        if (ex == null) {
            ex = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, new ThreadFactory() {
                private final AtomicInteger mCount = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, threadName + mCount.getAndIncrement());
                }
            }, r);
        }
        return ex;
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void runOnUI(Runnable run) {
        handler.post(run);
    }
}
