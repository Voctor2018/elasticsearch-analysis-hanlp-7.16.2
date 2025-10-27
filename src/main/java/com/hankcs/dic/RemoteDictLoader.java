package com.hankcs.dic;

import com.hankcs.dic.config.DataSourceConfig;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemoteDictLoader {

    private final DataSource dataSource;

    public RemoteDictLoader() {
        this.dataSource = DataSourceConfig.getDataSource();
        if (this.dataSource == null) {
            System.err.println("[WARN] DataSource not available, remote DB dict loading disabled.");
        }
    }

    /**
     * 从数据库中读取远程扩展词典
     * @param key 词典类型 ("DICT" 或 "STOP")
     * @return 词条列表
     */
    public List<String> getRemoteExtWords(String key) {
        List<String> words = new ArrayList<>();

        if (dataSource == null) {
            return words;
        }

        String sql = "SELECT name, nature, freq FROM hanlp_remote_dict WHERE dict_type = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String nature = rs.getString("nature");
                int freq = rs.getInt("freq");

                if (name != null && !name.trim().isEmpty()) {
                    words.add(String.format("%s %s %d", name.trim(), nature != null ? nature : "n", freq > 0 ? freq : 1));
                }
            }

            System.out.printf("[INFO] ✅ Loaded %d entries from DB for type=%s%n", words.size(), key);

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to load dict from DB: " + e.getMessage());
        }

        return words;
    }

    /**
     *  获取版本号
     * @param versionId
     * @return
     */
    public String getVersion(int versionId) {
        String version = "default";

        if (dataSource == null) {
            return version;
        }

        String sql = "SELECT version FROM dict_version WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

             ps.setInt(1, versionId);

             ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String dv = rs.getString("version");

                if (dv != null && !dv.trim().isEmpty()) {
                    version = dv;
                }
            }

            System.out.printf("[INFO] ✅ Loaded version from DB for id=%s%n", versionId);

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to load dict version from DB: " + e.getMessage());
        }

        return version;
    }
}
