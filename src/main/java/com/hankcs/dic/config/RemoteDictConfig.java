package com.hankcs.dic.config;

import com.hankcs.dic.Dictionary;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.elasticsearch.core.internal.io.IOUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: 远程词典配置
 * Author: Kenn
 * Create: 2018-12-18 15:23
 */
public class RemoteDictConfig {

    /**
     * 远程词典配置实例
     */
    private static RemoteDictConfig singleton;

    private static final Logger logger = LogManager.getLogger(RemoteDictConfig.class);

    /**
     * 远程扩展字典
     */
    private static final String REMOTE_EXT_DICT = "remote_ext_dict";

    /**
     * 远程扩展停止词字典
     */
    private static final String REMOTE_EXT_STOP = "remote_ext_stopwords";
    /**
     * 远程拓展词版本号
     */
    private static final String REMOTE_EXT_VERSION = "remote_ext_version_check";


    private  HikariDataSource dataSource = null;


    private final Properties props;

    private final String configFile;

    private RemoteDictConfig(String configFile) {
        this.configFile = configFile;
        this.props = new Properties();
        loadConfig();
    }

    public static synchronized void initial(String configFile) {
        if (singleton == null) {
            synchronized (Dictionary.class) {
                if (singleton == null) {
                    singleton = new RemoteDictConfig(configFile);
                }
            }
        }
    }

    public void loadConfig() {
        InputStream input = null;
        try {
            logger.info("try load remote hanlp config from {}", configFile);
            input = new FileInputStream(configFile);
            props.loadFromXML(input);
        } catch (FileNotFoundException e) {
            logger.error("remote hanlp config isn't exist", e);
        } catch (Exception e) {
            logger.error("can not load remote hanlp config", e);
        } finally {
            IOUtils.closeWhileHandlingException(input);
        }
    }

    public List<String> getRemoteExtDictionaries() {
        // 从远端接口或者文件获取词库
        return getRemoteExtFiles(REMOTE_EXT_DICT);
        // 从mysql数据库动态获取词库
//        return getRemoteExtFromSql("DICT");
    }

    public List<String> getRemoteExtStopWordDictionaries() {
        // 从远端接口或者文件获取词库
        return getRemoteExtFiles(REMOTE_EXT_STOP);
        // 从mysql数据库动态获取词库
//        return getRemoteExtFromSql("STOP");
    }

    /// 获取远程词库版本号
//    public  String  getRemoteExtDictVersion(int versionId) {
//        // 切换至远程接口，es插件中不支持查数据库
//        RemoteDictLoader loader = new RemoteDictLoader();
//        return loader.getVersion(versionId);
//    }
//
//    // mysql动态加载词库
//    private List<String> getRemoteExtFromSql(String key) {
//        RemoteDictLoader loader = new RemoteDictLoader();
//        return loader.getRemoteExtWords(key);
//    }

    // 获取版本号
    public String getRemoteVersion() {
        String versionUrl = getProperty(REMOTE_EXT_VERSION);
        return AccessController.doPrivileged((PrivilegedAction<String>) () -> {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpGet get = new HttpGet(versionUrl);
                try (CloseableHttpResponse response = httpclient.execute(get)) {
                    if (response != null && response.getEntity() != null) {
                        String version = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                        logger.info("remote version: {}", version);
                        return version.trim();
                    }
                }
            } catch (Exception e) {
                logger.error("can not get remote version", e);
            }
            return "default";
        });
    }

    // 加载远程词库
    private List<String> getRemoteExtFiles(String key) {
        List<String> remoteExtFiles = new ArrayList<>(2);
        String remoteExtStopWordDictCfg = getProperty(key);
        if (remoteExtStopWordDictCfg != null) {

            String[] filePaths = remoteExtStopWordDictCfg.split(";");
            for (String filePath : filePaths) {
                if (filePath != null && !"".equals(filePath.trim())) {
                    remoteExtFiles.add(filePath);
                }
            }
        }
        return remoteExtFiles;
    }

    private String getProperty(String key) {
        if (props != null) {
            return props.getProperty(key);
        }
        return null;
    }

    /**
     * 获取远程词典配置实例
     *
     * @return Dictionary 单例对象
     */
    public static RemoteDictConfig getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("远程词典配置尚未初始化，请先调用initial方法");
        }
        return singleton;
    }
}
