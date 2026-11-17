package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiDatabase {

    private static final String URL = "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?user=postgres.ucmgpnafgfoheegoryey&password=perpustakaanpastisukses99";
    private static final String USER = "postgres.ucmgpnafgfoheegoryey";
    private static final String PASSWORD = "perpustakaanpastisukses99";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL tidak ditemukan!");
        }
    }
}