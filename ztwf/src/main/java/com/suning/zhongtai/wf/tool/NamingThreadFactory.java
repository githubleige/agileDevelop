package com.suning.zhongtai.wf.tool;


import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 为线程池中的线程指定线程名
 * @author 18040994
 */
public class NamingThreadFactory implements ThreadFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamingThreadFactory.class);

    private String name;

    private boolean daemon;

    private UncaughtExceptionHandler uncaughtExceptionHandler;

    private final ConcurrentHashMap<String, AtomicLong> sequences;

    public NamingThreadFactory() {
        this(null, false, null);
    }

    public NamingThreadFactory(String name) {
        this(name, false, null);
    }

    public NamingThreadFactory(String name, boolean daemon) {
        this(name, daemon, null);
    }

    public NamingThreadFactory(String name, boolean daemon, UncaughtExceptionHandler handler) {
        this.name = name;
        this.daemon = daemon;
        this.uncaughtExceptionHandler = handler;
        this.sequences = new ConcurrentHashMap<String, AtomicLong>();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(this.daemon);

        String prefix = this.name;
        if (StringUtils.isBlank(prefix)) {
            prefix = getInvoker(2);
        }
        thread.setName(prefix + "-" + getSequence(prefix));

        // 没指定uncaughtExceptionHandler, 单纯打印日志.
        if (this.uncaughtExceptionHandler != null) {
            thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
        } else {
            thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    LOGGER.error("unhandled exception in thread: " + t.getId() + ":" + t.getName(), e);
                }
            });
        }

        return thread;
    }

    /**
     * 获取方法调用类类名
     * @param depth
     * @return
     */
    private String getInvoker(int depth) {
        Exception e = new Exception();
        StackTraceElement[] stes = e.getStackTrace();
        if (stes.length > depth) {
            return ClassUtils.getShortClassName(stes[depth].getClassName());
        }
        return getClass().getSimpleName();
    }

    /**
     * 名字后缀
     * @param invoker
     * @return
     */
    private long getSequence(String invoker) {
        AtomicLong r = this.sequences.get(invoker);
        if (r == null) {
            r = new AtomicLong(0);
            AtomicLong previous = this.sequences.putIfAbsent(invoker, r);
            if (previous != null) {
                r = previous;
            }
        }

        return r.incrementAndGet();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        this.uncaughtExceptionHandler = handler;
    }

}
