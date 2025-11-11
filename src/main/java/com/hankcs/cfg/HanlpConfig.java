package com.hankcs.cfg;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HanlpConfig {

    private static final Logger logger = LoggerFactory.getLogger(HanlpConfig.class);
    private static final Properties props = new Properties();
    private static boolean loaded = false;
    private static final boolean debug = true;

    /**
     * 初始化或重新加载配置文件
     */
    public static synchronized void reload() {
        String filePath = AccessController.doPrivileged(
                (PrivilegedAction<String>) () -> HanlpPath.hanlpPropertiesPath);
        if (filePath == null || filePath.isEmpty()) {
            logger.warn("HanLP properties path not set, skip loading.");
            return;
        }

        if(debug){
            filePath = filePath.replaceAll("config/analysis-hanlp/", "src/main/resources/config/");
        }

        Path path = Paths.get(filePath).toAbsolutePath();
        try (InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(path), StandardCharsets.UTF_8)) {

            props.clear();
            props.load(reader);
            loaded = true;
            logger.info("Loaded HanLP properties from {}", path);

        } catch (IOException e) {
            logger.error("Failed to load HanLP properties: {}", path, e);
            loaded = false;
        }
    }


    public static Properties loadProperties(String filePath) throws IOException {
        Properties props = new Properties();

        // 1. 优先从文件系统加载（用户配置）
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try (InputStreamReader reader = new InputStreamReader(
                    Files.newInputStream(path), StandardCharsets.UTF_8)) {
                props.load(reader);
                return props;
            }
        }

        // 2. 若文件系统中不存在，则尝试从 classpath 中加载（resources）
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = HanlpConfig.class.getClassLoader();
        }

        InputStream stream = loader.getResourceAsStream(filePath);
        if (stream == null) {
            // 尝试再加上常见前缀（例如 config/）
            stream = loader.getResourceAsStream("config/" + filePath);
        }

        if (stream == null) {
            throw new FileNotFoundException("Cannot find " + filePath + " in filesystem or classpath.");
        }

        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            props.load(reader);
        }

        return props;
    }


    /**
     * 获取配置项（带默认值）
     */
    public static String get(String key, String defaultValue) {
        ensureLoaded();
        return props.getProperty(key, defaultValue);
    }

    /**
     * 获取配置项（无默认值）
     */
    public static String get(String key) {
        ensureLoaded();
        return props.getProperty(key);
    }

    /**
     * 获取配置项并转为布尔
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        ensureLoaded();
        String value = props.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    /**
     * 获取配置项并转为整数
     */
    public static int getInt(String key, int defaultValue) {
        ensureLoaded();
        String value = props.getProperty(key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer for key '{}': {}", key, value);
            return defaultValue;
        }
    }

    private static void ensureLoaded() {
        if (!loaded) {
            reload();
        }
    }
}
