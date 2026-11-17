package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Anggota extends Orang {
    private String nim;
    private String prodi;
    private String gender;
    private Date tanggalDaftar;
    private boolean aktif = true;
    private final List<Peminjaman> riwayat = new ArrayList<>();

    public Anggota(int id, String nama, String nim, String prodi, String gender, Date tanggalDaftar) {
        super(id, nama);
        setNim(nim);
        setProdi(prodi);
        setGender(gender);
        setTanggalDaftar(tanggalDaftar);
    }

    @Override public String getPeran() { return "Anggota"; }

    public String getNim() { return nim; }
    public void setNim(String nim) {
        if (nim == null || nim.trim().isEmpty())
            throw new IllegalArgumentException("NIM tidak boleh kosong.");
        this.nim = nim.trim();
    }

    public String getProdi() { return prodi; }
    public void setProdi(String prodi) {
        if (prodi == null || prodi.trim().isEmpty())
            throw new IllegalArgumentException("Prodi tidak boleh kosong.");
        this.prodi = prodi.trim();
    }

    public String getGender() { return gender; }
    public void setGender(String gender) {
        if (!"Laki-laki".equals(gender) && !"Perempuan".equals(gender))
            throw new IllegalArgumentException("Gender harus Laki-laki atau Perempuan.");
        this.gender = gender;
    }

    public Date getTanggalDaftar() { return tanggalDaftar; }
    public void setTanggalDaftar(Date tanggalDaftar) {
        this.tanggalDaftar = (tanggalDaftar == null) ? new Date() : tanggalDaftar;
    }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public List<Peminjaman> getRiwayat() { return Collections.unmodifiableList(riwayat); }
    void tambahRiwayat(Peminjaman p) { riwayat.add(p); }
}