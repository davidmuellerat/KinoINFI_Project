package org.example;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/kinosystem?user=root&password=27dave07";

    public static void main(String[] args) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            TableUtils.createTableIfNotExists(connectionSource, Film.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
