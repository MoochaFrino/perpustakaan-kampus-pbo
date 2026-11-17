package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TesModel {
    public static void main(String[] args) {
        List<Koleksi> daftar = new ArrayList<>();
        daftar.add(new Buku(1, "Pemrograman Java", "Budi", 2020, new Date(), "978-1"));
        daftar.add(new Jurnal(2, "Jurnal Informatika", "Siti", 2022, new Date(), 3));
        daftar.add(new Skripsi(3, "Sistem Perpustakaan", "Andi", 2023, new Date()));

        for (Koleksi k : daftar) {
            System.out.println(k.getJenis() + " telat 5 hari -> Rp" + k.hitungDenda(5));
        }

        Anggota a = new Anggota(1, "Khiesi", "12345", "Informatika", "Laki-laki", new Date());        Peminjaman p = new Peminjaman(1, a, daftar.get(0), LocalDate.now().minusDays(12));
        p.kembalikan(LocalDate.now());
        System.out.println("Denda peminjaman: Rp" + p.hitungDenda());

        try {
            daftar.get(0).setTahun(1800);
        } catch (IllegalArgumentException e) {
            System.out.println("Validasi jalan: " + e.getMessage());
        }

        DaftarObjek<Koleksi> repo = new DaftarObjek<>();
        repo.tambah(new Buku(0, "Pemrograman Java", "Budi", 2020, new Date()));
        repo.tambah(new Jurnal(0, "Jurnal Informatika", "Siti", 2022, new Date()));
        System.out.println("Jumlah: " + repo.jumlah());
    }
}