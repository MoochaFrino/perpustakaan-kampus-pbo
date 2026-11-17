package dao;

import config.KoneksiDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanDAO {

    public void pinjamKoleksi(int id, int idAnggota, int idKoleksi, java.util.Date tglPinjam) throws SQLException {
        String sqlInsert = "INSERT INTO peminjaman (id, id_anggota, id_koleksi, tanggal_pinjam, denda) VALUES (?, ?, ?, ?, 0)";
        String sqlUpdateKoleksi = "UPDATE koleksi SET tersedia = false WHERE id = ?";
        
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
        String sqlUpdateKoleksi = "UPDATE koleksi SET tersedia = true WHERE id = (SELECT id_koleksi FROM peminjaman WHERE id = ?)";
        String sqlDelete = "DELETE FROM peminjaman WHERE id = ?";

        try (Connection conn = KoneksiDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdateKoleksi);
                 PreparedStatement ps2 = conn.prepareStatement(sqlDelete)) {

                ps1.setInt(1, idTrx);
                ps1.executeUpdate();

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
                
                int lamaPinjam = 7;
                if ("Jurnal".equalsIgnoreCase(jenis)) lamaPinjam = 3;
                else if ("Skripsi".equalsIgnoreCase(jenis)) lamaPinjam = 2;

                LocalDate jatuhTempo = tglPinjam.toLocalDate().plusDays(lamaPinjam);

                list.add(new Object[]{
                    idTrx, namaAnggota, judulKoleksi, jenis,
                    tglPinjam.toString(), jatuhTempo.toString(), "-", 0, "DIPINJAM"
                });
            }
        }
        return list;
    }
}