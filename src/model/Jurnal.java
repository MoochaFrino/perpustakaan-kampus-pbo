package model;

import java.util.Date;

public class Jurnal extends Koleksi {
    private static final double DENDA_PER_HARI = 2000;

    private int volume;

    public Jurnal(int id, String judul, String pengarang, int tahun, Date tanggalMasuk, int volume) {
        super(id, judul, pengarang, tahun, tanggalMasuk);
        setVolume(volume);
    }

    public Jurnal(int id, String judul, String pengarang, int tahun, Date tanggalMasuk) {
        this(id, judul, pengarang, tahun, tanggalMasuk, 1);
    }

    @Override public String getJenis() { return "Jurnal"; }
    @Override public int getLamaPinjam() { return 3; }

    @Override
    public double hitungDenda(int hariTerlambat) {
        return hariTerlambat <= 0 ? 0 : hariTerlambat * DENDA_PER_HARI;
    }

    public int getVolume() { return volume; }
    public void setVolume(int volume) {
        if (volume < 1) throw new IllegalArgumentException("Volume minimal 1.");
        this.volume = volume;
    }
}