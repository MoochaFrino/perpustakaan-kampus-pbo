package dao;

import config.KoneksiDatabase;
import model.Anggota;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnggotaDAO {
    
    public void tambahAnggota(Anggota a, String kategori) throws SQLException {
        String sql = "INSERT INTO anggota (id, nama, nim, kategori, prodi, gender, tanggal_daftar, aktif) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, a.getId());
            ps.setString(2, a.getNama());
            ps.setString(3, a.getNim());
            ps.setString(4, kategori); 
            ps.setString(5, a.getProdi());
            ps.setString(6, a.getGender());
            ps.setDate(7, new java.sql.Date(a.getTanggalDaftar().getTime()));
            ps.setBoolean(8, a.isAktif());
            
            ps.executeUpdate();
        }
    }

    public List<Object[]> ambilSemuaAnggota() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM anggota ORDER BY id ASC";
        try (Connection conn = KoneksiDatabase.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"), 
                    rs.getString("nim"), 
                    rs.getString("nama"), 
                    rs.getString("kategori"),
                    rs.getString("prodi"), 
                    rs.getString("gender"), 
                    rs.getBoolean("aktif") ? "Aktif" : "Non-Aktif"
                });
            }
        }
        return list;
    }

    public void ubahAnggota(Anggota a, String kategori) throws SQLException {
        String sql = "UPDATE anggota SET nama = ?, nim = ?, kategori = ?, prodi = ?, gender = ?, aktif = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, a.getNama());
            ps.setString(2, a.getNim());
            ps.setString(3, kategori);
            ps.setString(4, a.getProdi());
            ps.setString(5, a.getGender());
            ps.setBoolean(6, a.isAktif());
            
            ps.setInt(7, a.getId());
            
            ps.executeUpdate();
        }
    }

    public void hapusAnggota(int id) throws SQLException {
        String sql = "DELETE FROM anggota WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}