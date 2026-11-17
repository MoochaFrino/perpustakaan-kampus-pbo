package model;

import java.util.Date;

public class Skripsi extends Koleksi {
    private static final double DENDA_PER_HARI = 5000;
    private static final double DENDA_TETAP = 10000;

    private String dosenPembimbing;

    public Skripsi(int id, String judul, String pengarang, int tahun, Date tanggalMasuk, String dosenPembimbing) {
        super(id, judul, pengarang, tahun, tanggalMasuk);
        setDosenPembimbing(dosenPembimbing);
    }

    public Skripsi(int id, String judul, String pengarang, int tahun, Date tanggalMasuk) {
        this(id, judul, pengarang, tahun, tanggalMasuk, "-");
    }

    @Override public String getJenis() { return "Skripsi"; }
    @Override public int getLamaPinjam() { return 2; }

    @Override
    public double hitungDenda(int hariTerlambat) {
        if (hariTerlambat <= 0) return 0;
        return DENDA_TETAP + hariTerlambat * DENDA_PER_HARI;
    }

    public String getDosenPembimbing() { return dosenPembimbing; }
    public void setDosenPembimbing(String dosen) {
        this.dosenPembimbing = (dosen == null || dosen.trim().isEmpty()) ? "-" : dosen.trim();
    }
}