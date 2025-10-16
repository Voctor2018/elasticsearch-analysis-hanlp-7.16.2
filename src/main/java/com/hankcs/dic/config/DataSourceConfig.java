package com.hankcs.dic.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {

    private static HikariDataSource dataSource;

    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:mysql://172.18.102.230:3306/k8s_es_dic");
                config.setUsername("zhaom");
                config.setPassword("1qazWSX$2");
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);

                dataSource = new HikariDataSource(config);
            } catch (Throwable e) {
                // 不要让ES崩溃
                System.err.println("[ERROR] Failed to initialize DataSource: " + e.getMessage());
            }
        }
        return dataSource;
    }
}

//
//package com.hankcs.dic.config;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DataSourceConfig {
//
//    private static DataSource dataSource;
//
//    // 数据库连接配置（可放入 hanlp.properties 中再读取）
//    private static final String JDBC_URL = "jdbc:mysql://172.18.102.230:3306/k8s_es_dic?useSSL=false&characterEncoding=utf8";
//    private static final String JDBC_USER = "zhaom";
//    private static final String JDBC_PASSWORD = "1qazWSX$2";
//
//    static {
//        try {
//            // 显式加载 MySQL 驱动类（不使用 ClassLoader）
//            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
//
//            // 测试连接是否可用
//            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
//                if (conn != null && !conn.isClosed()) {
//                    System.out.println("[INFO] ✅ Database connection test passed.");
//                }
//            }
//
//            // 使用简单包装 DataSource（不依赖外部库）
//            dataSource = new DataSource() {
//                @Override
//                public Connection getConnection() throws SQLException {
//                    return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//                }
//
//                @Override
//                public Connection getConnection(String username, String password) throws SQLException {
//                    return DriverManager.getConnection(JDBC_URL, username, password);
//                }
//
//                // 其他方法返回默认值
//                @Override public <T> T unwrap(Class<T> iface) { return null; }
//                @Override public boolean isWrapperFor(Class<?> iface) { return false; }
//                @Override public java.io.PrintWriter getLogWriter() { return null; }
//                @Override public void setLogWriter(java.io.PrintWriter out) {}
//                @Override public void setLoginTimeout(int seconds) {}
//                @Override public int getLoginTimeout() { return 0; }
//                @Override public java.util.logging.Logger getParentLogger() { return java.util.logging.Logger.getGlobal(); }
//            };
//
//        } catch (Exception e) {
//            System.err.println("[ERROR] Failed to initialize DataSource: " + e.getMessage());
//            dataSource = null;
//        }
//    }
//
//    public static DataSource getDataSource() {
//        return dataSource;
//    }
//}

