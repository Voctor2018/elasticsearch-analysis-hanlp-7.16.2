package com.hankcs.dic;

import com.hankcs.cfg.Configuration;
import com.hankcs.cfg.HanlpPath;
import com.hankcs.dic.cache.DictionaryFileCache;
import com.hankcs.dic.config.RemoteDictConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: 词典类
 * Author: Kenn
 * Create: 2018-12-14 15:10
 */
public class Dictionary {
    /**
     * 词典实例
     */
    private static Dictionary singleton;

    private static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1, new ThreadFactory() {

        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            String threadName = "remote-dict-monitor-" + counter.getAndIncrement();
            return new Thread(r, threadName);
        }
    });

    private final Configuration configuration;

    private Dictionary(Configuration configuration) {
        this.configuration = configuration;
    }

    private void setUp() {
//        Path configDir = configuration.getEnvironment().configFile().resolve(AnalysisHanLPPlugin.PLUGIN_NAME);
        DictionaryFileCache.configCachePath(configuration);
        DictionaryFileCache.loadCache();
        Path path = Paths.get(AccessController.doPrivileged((PrivilegedAction<String>) () -> HanlpPath.remoteConfigFileName)
        ).toAbsolutePath();

        RemoteDictConfig.initial(path.toString());
    }

    public static synchronized void initial(Configuration configuration) {
        if (singleton == null) {                                     // 第一次检查
            synchronized (Dictionary.class) {
                if (singleton == null) {                             // 第二次检查
                    singleton = new Dictionary(configuration);       // 构建单例
                    singleton.setUp();                              // 初始化词典

                    pool.scheduleAtFixedRate(new ExtMonitor(), 10, 60, TimeUnit.SECONDS); // 定期监控本地扩展词典
                    if (configuration.isEnableRemoteDict()) {        // 启用远程词典
                        // 定时触发更新
//                         extractedAtFixedRate();

                        // 手动触发更新 每5秒检查一次版本号
                        pool.scheduleAtFixedRate(new CustomDictionaryCheck(), 5, 10, TimeUnit.SECONDS);
                    }
                }
            }
        }
    }

    /**
     * 定时执行
     */
    private static void extractedAtFixedRate() {
        // 启用远程词典，轮询远程自定义词典文件地址
        for (String location : RemoteDictConfig.getSingleton().getRemoteExtDictionaries()) {
            pool.scheduleAtFixedRate(new RemoteMonitor(location, "custom"), 10, 60*60*4, TimeUnit.SECONDS);
        }

        // 启用远程停用词词典文件地址
        for (String location : RemoteDictConfig.getSingleton().getRemoteExtStopWordDictionaries()) {
            pool.scheduleAtFixedRate(new RemoteMonitor(location, "stop"), 10, 60*60*4, TimeUnit.SECONDS);
        }
    }

}
