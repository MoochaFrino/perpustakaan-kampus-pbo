package config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Pengelola koneksi database PostgreSQL (Supabase) dengan mini connection pool
 * bawaan (tanpa library eksternal) agar koneksi TCP/SSL dipakai ulang
 * sehingga perpindahan tab dan operasi CRUD jauh lebih cepat & responsif.
 */
public class KoneksiDatabase {

    private static final String URL =
        "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:5432/postgres";
    private static final String USER = "postgres.ishjsvczmhsysljrlhll";
    private static final String PASSWORD = "7Ql4vMNCKaQQ3JVZ";

    private static final int MAX_POOL_SIZE = 4;
    private static final long IDLE_CHECK_MS = 25_000L;

    private static final ConcurrentLinkedDeque<PooledConn> POOL = new ConcurrentLinkedDeque<>();

    private static final class PooledConn {
        final Connection raw;
        volatile long lastUsed;

        PooledConn(Connection raw) {
            this.raw = raw;
            this.lastUsed = System.currentTimeMillis();
        }
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {}

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PooledConn item;
            while ((item = POOL.poll()) != null) {
                tutupDiam(item.raw);
            }
        }));
    }

    private static Connection buatKoneksiBaru() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL tidak ditemukan!");
        }
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("tcpKeepAlive", "true");
        props.setProperty("prepareThreshold", "0"); // Aman untuk Supabase transaction/session pooler
        return DriverManager.getConnection(URL, props);
    }

    private static boolean masihHidup(PooledConn pc) {
        try {
            if (pc.raw.isClosed()) return false;
            long idle = System.currentTimeMillis() - pc.lastUsed;
            if (idle > IDLE_CHECK_MS) {
                return pc.raw.isValid(2);
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void tutupDiam(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static Connection getConnection() throws SQLException {
        PooledConn item;
        while ((item = POOL.pollFirst()) != null) {
            if (masihHidup(item)) {
                return bungkusProxy(item.raw);
            }
            tutupDiam(item.raw);
        }
        return bungkusProxy(buatKoneksiBaru());
    }

    private static Connection bungkusProxy(Connection rawConn) {
        final boolean[] sudahDitutup = {false};
        return (Connection) Proxy.newProxyInstance(
            KoneksiDatabase.class.getClassLoader(),
            new Class<?>[]{Connection.class},
            (proxy, method, args) -> {
                String nama = method.getName();
                if ("close".equals(nama)) {
                    if (!sudahDitutup[0]) {
                        sudahDitutup[0] = true;
                        kembalikanKePool(rawConn);
                    }
                    return null;
                }
                if ("isClosed".equals(nama)) {
                    return sudahDitutup[0] || rawConn.isClosed();
                }
                try {
                    return method.invoke(rawConn, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        );
    }

    private static void kembalikanKePool(Connection rawConn) {
        try {
            if (rawConn.isClosed()) return;
            if (!rawConn.getAutoCommit()) {
                rawConn.setAutoCommit(true);
            }
            if (POOL.size() < MAX_POOL_SIZE) {
                POOL.offerFirst(new PooledConn(rawConn));
            } else {
                tutupDiam(rawConn);
            }
        } catch (SQLException e) {
            tutupDiam(rawConn);
        }
    }
}
