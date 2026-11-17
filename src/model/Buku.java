package model;

import java.util.Date;

public class Buku extends Koleksi {
    private static final double DENDA_PER_HARI = 1000;
    private static final double DENDA_MAKSIMAL = 50000;

    private String isbn;

    public Buku(int id, String judul, String pengarang, int tahun, Date tanggalMasuk, String isbn) {
        super(id, judul, pengarang, tahun, tanggalMasuk);
        setIsbn(isbn);
    }

    public Buku(int id, String judul, String pengarang, int tahun, Date tanggalMasuk) {
        this(id, judul, pengarang, tahun, tanggalMasuk, "-");
    }

    @Override public String getJenis() { return "Buku"; }
    @Override public int getLamaPinjam() { return 7; }

    @Override
    public double hitungDenda(int hariTerlambat) {
        if (hariTerlambat <= 0) return 0;
        return Math.min(hariTerlambat * DENDA_PER_HARI, DENDA_MAKSIMAL);
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) {
        this.isbn = (isbn == null || isbn.trim().isEmpty()) ? "-" : isbn.trim();
    }
}