package model;

import java.time.Year;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public abstract class Koleksi implements Identifiable {
    private int id;
    private String judul;
    private String pengarang;
    private int tahun;
    private Date tanggalMasuk;
    private boolean tersedia = true;
    private boolean referensi;

    protected Koleksi(int id, String judul, String pengarang, int tahun, Date tanggalMasuk) {
        this.id = id;
        setJudul(judul);
        setPengarang(pengarang);
        setTahun(tahun);
        setTanggalMasuk(tanggalMasuk);
    }

    public abstract String getJenis();
    public abstract int getLamaPinjam();
    public abstract double hitungDenda(int hariTerlambat);

    public double hitungDenda(LocalDate jatuhTempo, LocalDate tanggalKembali) {
        long telat = ChronoUnit.DAYS.between(jatuhTempo, tanggalKembali);
        return hitungDenda((int) Math.max(0, telat));
    }

    public boolean bisaDipinjam() {
        return tersedia && !referensi;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) {
        if (judul == null || judul.trim().isEmpty())
            throw new IllegalArgumentException("Judul tidak boleh kosong.");
        this.judul = judul.trim();
    }

    public String getPengarang() { return pengarang; }
    public void setPengarang(String pengarang) {
        if (pengarang == null || pengarang.trim().isEmpty())
            throw new IllegalArgumentException("Pengarang tidak boleh kosong.");
        this.pengarang = pengarang.trim();
    }

    public int getTahun() { return tahun; }
    public void setTahun(int tahun) {
        int sekarang = Year.now().getValue();
        if (tahun < 1900 || tahun > sekarang)
            throw new IllegalArgumentException("Tahun terbit harus antara 1900 dan " + sekarang + ".");
        this.tahun = tahun;
    }

    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) {
        this.tanggalMasuk = (tanggalMasuk == null) ? new Date() : tanggalMasuk;
    }

    public boolean isTersedia() { return tersedia; }
    public void setTersedia(boolean tersedia) { this.tersedia = tersedia; }

    public boolean isReferensi() { return referensi; }
    public void setReferensi(boolean referensi) { this.referensi = referensi; }

    public String getStatus() { return tersedia ? "Tersedia" : "Dipinjam"; }

    @Override
    public String toString() {
        return getJenis() + " #" + id + ": " + judul + " (" + pengarang + ", " + tahun + ")";
    }
}