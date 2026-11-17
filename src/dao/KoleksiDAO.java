package dao;

import config.KoneksiDatabase;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KoleksiDAO {

    public void tambahKoleksi(Koleksi k) throws SQLException {
        String sql = "INSERT INTO koleksi (id, judul, pengarang, tahun, tanggal_masuk, jenis_koleksi, isbn_vol_prodi, tersedia, referensi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, k.getId());
            ps.setString(2, k.getJudul());
            ps.setString(3, k.getPengarang());
            ps.setInt(4, k.getTahun());
            ps.setDate(5, new java.sql.Date(k.getTanggalMasuk().getTime()));
            ps.setString(6, k.getJenis());
            
            if (k instanceof Buku) {
                ps.setString(7, ((Buku) k).getIsbn());
            } else if (k instanceof Jurnal) {
                ps.setString(7, String.valueOf(((Jurnal) k).getVolume()));
            } else if (k instanceof Skripsi) {
                ps.setString(7, ((Skripsi) k).getDosenPembimbing());
            } else {
                ps.setString(7, "-");
            }
            
            ps.setBoolean(8, k.isTersedia());
            ps.setBoolean(9, k.isReferensi());
            
            ps.executeUpdate();
        }
    }

    public List<Koleksi> ambilSemuaKoleksi() throws SQLException {
        List<Koleksi> listKoleksi = new ArrayList<>();
        String sql = "SELECT * FROM koleksi ORDER BY id ASC";
        
        try (Connection conn = KoneksiDatabase.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                listKoleksi.add(petakanBarisKeObjek(rs));
            }
        }
        return listKoleksi;
    }

    public void ubahKoleksi(Koleksi k) throws SQLException {
        String sql = "UPDATE koleksi SET judul = ?, pengarang = ?, tahun = ?, jenis_koleksi = ?, isbn_vol_prodi = ?, referensi = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, k.getJudul());
            ps.setString(2, k.getPengarang());
            ps.setInt(3, k.getTahun());
            ps.setString(4, k.getJenis());
            
            if (k instanceof Buku) {
                ps.setString(5, ((Buku) k).getIsbn());
            } else if (k instanceof Jurnal) {
                ps.setString(5, String.valueOf(((Jurnal) k).getVolume()));
            } else if (k instanceof Skripsi) {
                ps.setString(5, ((Skripsi) k).getDosenPembimbing());
            } else {
                ps.setString(5, "-");
            }
            
            ps.setBoolean(6, k.isReferensi());
            
            // Kunci utama (WHERE id = ?)
            ps.setInt(7, k.getId());
            
            ps.executeUpdate();
        }
    }

    public void hapusKoleksi(int idKoleksi) throws SQLException {
        String sql = "DELETE FROM koleksi WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKoleksi);
            ps.executeUpdate();
        }
    }

    private Koleksi petakanBarisKeObjek(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String judul = rs.getString("judul");
        String pengarang = rs.getString("pengarang");
        int tahun = rs.getInt("tahun");
        java.util.Date tanggalMasuk = new java.util.Date(rs.getDate("tanggal_masuk").getTime());
        String jenis = rs.getString("jenis_koleksi");
        String atributKhusus = rs.getString("isbn_vol_prodi");

        Koleksi obj;
        if ("Buku".equalsIgnoreCase(jenis)) {
            obj = new Buku(id, judul, pengarang, tahun, tanggalMasuk, atributKhusus);
        } else if ("Jurnal".equalsIgnoreCase(jenis)) {
            int vol = 1;
            try {
                if (atributKhusus != null && !atributKhusus.isEmpty() && !atributKhusus.equals("-")) {
                    vol = Integer.parseInt(atributKhusus);
                }
            } catch (Exception ignored) {}
            obj = new Jurnal(id, judul, pengarang, tahun, tanggalMasuk, vol);
        } else {
            obj = new Skripsi(id, judul, pengarang, tahun, tanggalMasuk, atributKhusus);
        }
        
        obj.setTersedia(rs.getBoolean("tersedia"));
        obj.setReferensi(rs.getBoolean("referensi"));
        
        return obj;
    }
}