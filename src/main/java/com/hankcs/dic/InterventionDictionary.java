package com.hankcs.dic;

import com.hankcs.cfg.HanlpConfig;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IOUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hankcs.hanlp.utility.Predefine.logger;

public class InterventionDictionary {
    public static Map<String, String> interventionMap = new ConcurrentHashMap<>();
    public final static String path = HanlpConfig.get("IntervetionPath");

    // 自动加载词典
    static {
        long start = System.currentTimeMillis();
        if (!load(path)) {
            throw new IllegalArgumentException("业务干预词典" + path + "加载失败");
        } else {
            logger.info(path + "加载成功，" + interventionMap.size() + "个词条，耗时" + (
                    System.currentTimeMillis() - start) + "ms");
        }
    }

    private static boolean load(String path) {
        logger.info("业务干预词典开始加载:" + path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(IOUtil.newInputStream(path), "UTF-8"));
            String line;
            long start = System.currentTimeMillis();
            while ((line = br.readLine()) != null) {
                String param[] = line.split("=");
                interventionMap.put(param[0], param[1]);
            }
            logger.info("业务干预词典读入词条" + interventionMap.size() + "，耗时" + (
                    System.currentTimeMillis() - start) + "ms");
            br.close();
        } catch (FileNotFoundException e) {
            logger.warning("业务干预词典" + path + "不存在！" + e);
            return false;
        } catch (IOException e) {
            logger.warning("业务干预词典" + path + "读取错误！" + e);
            return false;
        }
        return true;
    }

    public static void insert(String key, String value) {
        if (key == null || value == null) {
            logger.warning("Key and value must not be null");
        }
        interventionMap.put(key, value);
    }

    public static void remove(String key) {
        if (key == null) {
            logger.warning("Key must not be null");
        }
        interventionMap.remove(key);
    }
}
