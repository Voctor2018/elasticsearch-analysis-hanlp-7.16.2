package com.hankcs.dic;

import com.hankcs.dic.config.RemoteDictConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomDictionaryCheck implements Runnable {

    private static final Logger logger = LogManager.getLogger(CustomDictionaryCheck.class);

    private static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1, new ThreadFactory() {

        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            String threadName = "remote-dict-monitor-check-" + counter.getAndIncrement();
            return new Thread(r, threadName);
        }
    });

    private static volatile String currentVersion = "default";


    @Override
    public void run() {
        if(checkVersionChange())
        {
            reloadDictionary();
        }
    }


    /**
     * 立即执行
     */
    private static void extracted() {
        // 启用远程词典，轮询远程自定义词典文件地址
        for (String location : RemoteDictConfig.getSingleton().getRemoteExtDictionaries()) {
            pool.schedule(new RemoteMonitor(location, "custom"), 5, TimeUnit.SECONDS);
        }

        // 启用远程停用词词典文件地址
        for (String location : RemoteDictConfig.getSingleton().getRemoteExtStopWordDictionaries()) {
            pool.schedule(new RemoteMonitor(location, "stop"), 5, TimeUnit.SECONDS);
        }
    }


    // 检查外部版本变化
    private static boolean checkVersionChange() {
        String newVersion = RemoteDictConfig.getSingleton().getRemoteVersion();
        logger.info("新版本号：" + newVersion, currentVersion);
        // 如果版本未变化，直接返回（可选）
        if (newVersion.equals(currentVersion)) {
            logger.info("词典版本未变化，跳过加载");
            return false;
        }
        return true;
    }

    // 通用重新加载词典
    private static void reloadDictionary() {
        extracted();
        logger.info("加载最新版本的远程词典");
        String newVersion = RemoteDictConfig.getSingleton().getRemoteVersion();
        logger.info("更正词库至新版本：" + newVersion);
        currentVersion = newVersion;
    }


}
