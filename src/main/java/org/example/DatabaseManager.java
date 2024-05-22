package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/kinosystem?user=root&password=27dave07";
    public static void main(String[] args) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Film, Integer> filmDao = DaoManager.createDao(connectionSource, Film.class);
            Dao<User, Integer> userDao = DaoManager.createDao(connectionSource, User.class);
            Dao<Booking, Integer> bookingDao = DaoManager.createDao(connectionSource, Booking.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void createTables(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Film.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Booking.class);

        System.out.println("Datenbank-Tabellen erfolgreich erstellt!");
    }
}
