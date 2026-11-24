package model;

import java.time.LocalDate;

public class Peminjaman implements Identifiable {
    private int id;
    private final Anggota anggota;
    private final Koleksi koleksi;
    private final LocalDate tanggalPinjam;
    private LocalDate tanggalKembali;

    public Peminjaman(int id, Anggota anggota, Koleksi koleksi, LocalDate tanggalPinjam) {
        if (anggota == null || koleksi == null)
            throw new IllegalArgumentException("Anggota dan koleksi wajib diisi.");
        if (!anggota.isAktif())
            throw new IllegalStateException("Anggota tidak aktif, tidak boleh meminjam.");
        if (!koleksi.bisaDipinjam())
            throw new IllegalStateException("Koleksi tidak bisa dipinjam (sedang dipinjam atau koleksi referensi).");

        this.id = id;
        this.anggota = anggota;
        this.koleksi = koleksi;
        this.tanggalPinjam = (tanggalPinjam == null) ? LocalDate.now() : tanggalPinjam;

        koleksi.setStok(koleksi.getStok() - 1);
        anggota.tambahRiwayat(this);
    }

    public void kembalikan(LocalDate tanggal) {
        if (isKembali()) throw new IllegalStateException("Koleksi sudah dikembalikan.");
        LocalDate t = (tanggal == null) ? LocalDate.now() : tanggal;
        if (t.isBefore(tanggalPinjam))
            throw new IllegalArgumentException("Tanggal kembali tidak boleh sebelum tanggal pinjam.");
        this.tanggalKembali = t;
        koleksi.setStok(koleksi.getStok() + 1);
    }

    public LocalDate getJatuhTempo() { return tanggalPinjam.plusDays(koleksi.getLamaPinjam()); }
    public boolean isKembali() { return tanggalKembali != null; }

    public double hitungDenda() {
        LocalDate acuan = isKembali() ? tanggalKembali : LocalDate.now();
        return koleksi.hitungDenda(getJatuhTempo(), acuan);
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public Anggota getAnggota() { return anggota; }
    public Koleksi getKoleksi() { return koleksi; }
    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public LocalDate getTanggalKembali() { return tanggalKembali; }
}