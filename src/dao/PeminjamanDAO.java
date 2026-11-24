package dao;

import config.KoneksiDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanDAO {

    public int idBerikutnya() throws SQLException {
        try (Connection conn = KoneksiDatabase.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM peminjaman")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public void pinjamKoleksi(int id, int idAnggota, int idKoleksi, java.util.Date tglPinjam) throws SQLException {
        String sqlInsert = "INSERT INTO peminjaman (id, id_anggota, id_koleksi, tanggal_pinjam, tanggal_kembali, denda) VALUES (?, ?, ?, ?, NULL, 0)";
        String sqlUpdateKoleksi = "UPDATE koleksi SET stok = stok - 1, tersedia = (stok - 1) > 0 WHERE id = ? AND stok > 0 AND referensi = false";

        try (Connection conn = KoneksiDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlInsert);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUpdateKoleksi)) {

                ps1.setInt(1, id);
                ps1.setInt(2, idAnggota);
                ps1.setInt(3, idKoleksi);
                ps1.setDate(4, new java.sql.Date(tglPinjam.getTime()));
                ps1.executeUpdate();

                ps2.setInt(1, idKoleksi);
                if (ps2.executeUpdate() == 0) {
                    throw new SQLException("Stok koleksi habis atau koleksi hanya untuk referensi.");
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void kembalikanKoleksi(int idTrx, java.util.Date tglKembali, double denda) throws SQLException {
        String sqlUpdateTrx = "UPDATE peminjaman SET tanggal_kembali = ?, denda = ? WHERE id = ? AND tanggal_kembali IS NULL";
        String sqlUpdateKoleksi = "UPDATE koleksi SET stok = stok + 1, tersedia = true WHERE id = (SELECT id_koleksi FROM peminjaman WHERE id = ?)";

        try (Connection conn = KoneksiDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdateTrx);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUpdateKoleksi)) {

                ps1.setDate(1, new java.sql.Date(tglKembali.getTime()));
                ps1.setDouble(2, denda);
                ps1.setInt(3, idTrx);
                if (ps1.executeUpdate() == 0) {
                    throw new SQLException("Transaksi tidak ditemukan atau sudah dikembalikan sebelumnya.");
                }

                ps2.setInt(1, idTrx);
                ps2.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void hapusTransaksi(int idTrx) throws SQLException {
        String sqlCek = "SELECT id_koleksi, tanggal_kembali FROM peminjaman WHERE id = ?";
        String sqlUpdateKoleksi = "UPDATE koleksi SET stok = stok + 1, tersedia = true WHERE id = ?";
        String sqlDelete = "DELETE FROM peminjaman WHERE id = ?";

        try (Connection conn = KoneksiDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer idKoleksi = null;
                boolean masihDipinjam = false;

                try (PreparedStatement psCek = conn.prepareStatement(sqlCek)) {
                    psCek.setInt(1, idTrx);
                    try (ResultSet rs = psCek.executeQuery()) {
                        if (rs.next()) {
                            idKoleksi = rs.getInt("id_koleksi");
                            masihDipinjam = (rs.getDate("tanggal_kembali") == null);
                        }
                    }
                }

                // Kembalikan stok hanya jika transaksi yang dihapus belum dikembalikan
                if (masihDipinjam && idKoleksi != null) {
                    try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdateKoleksi)) {
                        ps1.setInt(1, idKoleksi);
                        ps1.executeUpdate();
                    }
                }

                try (PreparedStatement ps2 = conn.prepareStatement(sqlDelete)) {
                    ps2.setInt(1, idTrx);
                    ps2.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Object[]> ambilSemuaPeminjaman() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.id, a.nama, k.judul, k.jenis_koleksi, p.tanggal_pinjam, p.tanggal_kembali, p.denda " +
                     "FROM peminjaman p " +
                     "JOIN anggota a ON p.id_anggota = a.id " +
                     "JOIN koleksi k ON p.id_koleksi = k.id " +
                     "ORDER BY p.id ASC";

        try (Connection conn = KoneksiDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idTrx = rs.getInt("id");
                String namaAnggota = rs.getString("nama");
                String judulKoleksi = rs.getString("judul");
                String jenis = rs.getString("jenis_koleksi");
                java.sql.Date tglPinjam = rs.getDate("tanggal_pinjam");
                java.sql.Date tglKembali = rs.getDate("tanggal_kembali");
                double denda = rs.getDouble("denda");

                int lamaPinjam = 7;
                if ("Jurnal".equalsIgnoreCase(jenis)) lamaPinjam = 3;
                else if ("Skripsi".equalsIgnoreCase(jenis)) lamaPinjam = 2;

                LocalDate jatuhTempo = tglPinjam.toLocalDate().plusDays(lamaPinjam);
                String tglKembaliStr = (tglKembali == null) ? "-" : tglKembali.toString();
                String status = (tglKembali == null) ? "DIPINJAM" : "DIKEMBALIKAN";

                list.add(new Object[]{
                    idTrx, namaAnggota, judulKoleksi, jenis,
                    tglPinjam.toString(), jatuhTempo.toString(), tglKembaliStr,
                    (long) denda, status
                });
            }
        }
        return list;
    }

    public List<Object[]> cariPeminjaman(String kataKunci, String filterStatus) throws SQLException {
        List<Object[]> hasil = new ArrayList<>();
        String key = (kataKunci == null) ? "" : kataKunci.trim().toLowerCase();
        boolean semuaStatus = (filterStatus == null || "Semua".equalsIgnoreCase(filterStatus));

        for (Object[] row : ambilSemuaPeminjaman()) {
            boolean cocokStatus = semuaStatus || row[8].toString().equalsIgnoreCase(filterStatus);
            boolean cocokKey = key.isEmpty()
                    || row[0].toString().contains(key)
                    || String.valueOf(row[1]).toLowerCase().contains(key)
                    || String.valueOf(row[2]).toLowerCase().contains(key);
            if (cocokStatus && cocokKey) {
                hasil.add(row);
            }
        }
        return hasil;
    }
}
