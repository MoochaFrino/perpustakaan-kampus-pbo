package controller;

import dao.AnggotaDAO;
import dao.KoleksiDAO;
import dao.PeminjamanDAO;
import model.Koleksi;
import view.PanelPeminjaman;

import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class PeminjamanController {
    private final PanelPeminjaman view;
    private final AnggotaDAO anggotaDAO;
    private final KoleksiDAO koleksiDAO;
    private final PeminjamanDAO peminjamanDAO;

    public PeminjamanController(PanelPeminjaman view) {
        this.view = view;
        this.anggotaDAO = new AnggotaDAO();
        this.koleksiDAO = new KoleksiDAO();
        this.peminjamanDAO = new PeminjamanDAO();

        inisialisasiEvent();
        resetTampilanDropdown();
        muatDataTabel();
        tampilkanIdBerikutnya();
    }

    private void tampilkanIdBerikutnya() {
        try {
            view.getTxtIdTransaksi().setText(String.valueOf(peminjamanDAO.idBerikutnya()));
            view.getTxtIdTransaksi().setEditable(false);
        } catch (SQLException ex) {
            view.getTxtIdTransaksi().setText("");
        }
    }

    private void inisialisasiEvent() {
        view.getBtnPinjam().addActionListener(e -> prosesPinjam());
        view.getBtnKembalikan().addActionListener(e -> prosesPengembalian());
        view.getBtnHapusTrx().addActionListener(e -> hapusTransaksi());
        view.getBtnCari().addActionListener(e -> cariTransaksi());
        view.getCmbFilterStatus().addActionListener(e -> cariTransaksi());

        view.getBtnReset().addActionListener(e -> {
            view.bersihkanForm();
            resetTampilanDropdown();
            tampilkanIdBerikutnya();
        });

        view.getCmbAnggota().addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { muatDropdownAnggota(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        view.getCmbKoleksi().addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { muatDropdownKoleksi(); }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        view.getTabelTransaksi().getSelectionModel().addListSelectionListener(e -> {
            int baris = view.getTabelTransaksi().getSelectedRow();
            if (baris != -1 && !e.getValueIsAdjusting()) {
                view.getTxtIdTransaksi().setText(view.getModelTabel().getValueAt(baris, 0).toString());
                view.getTxtIdTransaksi().setEditable(false);

                try {
                    String tglStr = view.getModelTabel().getValueAt(baris, 4).toString();
                    Date tgl = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglStr);
                    view.getSpnTanggalPinjam().setValue(tgl);
                } catch (Exception ex) {}

                String namaAnggota = view.getModelTabel().getValueAt(baris, 1).toString();
                String judulKoleksi = view.getModelTabel().getValueAt(baris, 2).toString();

                aturDropdownSementara(view.getCmbAnggota(), namaAnggota);
                aturDropdownSementara(view.getCmbKoleksi(), judulKoleksi);
            }
        });
    }

    private void aturDropdownSementara(JComboBox<String> cmb, String kataKunci) {
        boolean ketemu = false;
        for (int i = 0; i < cmb.getItemCount(); i++) {
            if (cmb.getItemAt(i) != null && cmb.getItemAt(i).contains(kataKunci)) {
                cmb.setSelectedIndex(i);
                ketemu = true;
                break;
            }
        }
        if (!ketemu) {
            cmb.addItem("[Riwayat] - " + kataKunci);
            cmb.setSelectedItem("[Riwayat] - " + kataKunci);
        }
    }

    private void resetTampilanDropdown() {
        view.getCmbAnggota().removeAllItems();
        view.getCmbKoleksi().removeAllItems();
        view.getCmbAnggota().addItem("-- Pilih Anggota --");
        view.getCmbKoleksi().addItem("-- Pilih Koleksi Tersedia --");
    }

    public void muatDataTabel() {
        try {
            tampilkanKeTabel(peminjamanDAO.ambilSemuaPeminjaman());
        } catch (SQLException e) {
            view.tampilkanPesanError("Gagal memuat tabel transaksi: " + e.getMessage());
        }
    }

    private void cariTransaksi() {
        try {
            String kataKunci = view.getTxtCariTransaksi().getText();
            String status = view.getCmbFilterStatus().getSelectedItem().toString();
            tampilkanKeTabel(peminjamanDAO.cariPeminjaman(kataKunci, status));
        } catch (SQLException e) {
            view.tampilkanPesanError("Gagal mencari transaksi: " + e.getMessage());
        }
    }

    private void tampilkanKeTabel(List<Object[]> listTrx) {
        DefaultTableModel model = view.getModelTabel();
        model.setRowCount(0);
        for (Object[] baris : listTrx) {
            model.addRow(baris);
        }
    }

    private void muatDropdownAnggota() {
        Object terpilih = view.getCmbAnggota().getSelectedItem();
        view.getCmbAnggota().removeAllItems();
        view.getCmbAnggota().addItem("-- Pilih Anggota --");

        try {
            List<Object[]> listAnggota = anggotaDAO.ambilSemuaAnggota();
            for (Object[] a : listAnggota) {
                if ("Aktif".equals(a[6].toString())) {
                    view.getCmbAnggota().addItem(a[0] + " - " + a[2]);
                }
            }
            if (terpilih != null && !terpilih.toString().startsWith("[Riwayat]")) {
                view.getCmbAnggota().setSelectedItem(terpilih);
            }
        } catch (SQLException e) {
            view.tampilkanPesanError("Gagal memuat Anggota: " + e.getMessage());
        }
    }

    private void muatDropdownKoleksi() {
        Object terpilih = view.getCmbKoleksi().getSelectedItem();
        view.getCmbKoleksi().removeAllItems();
        view.getCmbKoleksi().addItem("-- Pilih Koleksi Tersedia --");

        try {
            List<Koleksi> listKoleksi = koleksiDAO.ambilSemuaKoleksi();
            for (Koleksi k : listKoleksi) {
                if (k.bisaDipinjam()) {
                    view.getCmbKoleksi().addItem(k.getId() + " - " + k.getJudul() + " (Stok: " + k.getStok() + ")");
                }
            }
            if (terpilih != null && !terpilih.toString().startsWith("[Riwayat]")) {
                view.getCmbKoleksi().setSelectedItem(terpilih);
            }
        } catch (SQLException e) {
            view.tampilkanPesanError("Gagal memuat Koleksi: " + e.getMessage());
        }
    }

    private void prosesPinjam() {
        try {
            Object itemAnggota = view.getCmbAnggota().getSelectedItem();
            Object itemKoleksi = view.getCmbKoleksi().getSelectedItem();
            if (view.getCmbAnggota().getSelectedIndex() <= 0
                    || view.getCmbKoleksi().getSelectedIndex() <= 0
                    || itemAnggota == null || itemKoleksi == null
                    || itemAnggota.toString().startsWith("[Riwayat]")
                    || itemKoleksi.toString().startsWith("[Riwayat]")) {
                view.tampilkanPesanError("Pilih Anggota aktif dan Koleksi tersedia terlebih dahulu!\n(Klik 'Bersihkan Form' jika sedang memilih baris riwayat.)");
                return;
            }

            int idTrx = peminjamanDAO.idBerikutnya(); // ID Transaksi otomatis
            Date tglPinjam = (Date) view.getSpnTanggalPinjam().getValue();
            int idAnggota = Integer.parseInt(itemAnggota.toString().split(" - ")[0].trim());
            int idKoleksi = Integer.parseInt(itemKoleksi.toString().split(" - ")[0].trim());

            peminjamanDAO.pinjamKoleksi(idTrx, idAnggota, idKoleksi, tglPinjam);

            view.tampilkanPesanInfo("Peminjaman berhasil diproses dengan ID Transaksi " + idTrx + "!");
            view.bersihkanForm();
            resetTampilanDropdown();
            muatDataTabel();
            tampilkanIdBerikutnya();

        } catch (Exception ex) {
            view.tampilkanPesanError("Gagal memproses peminjaman: " + ex.getMessage());
        }
    }

    private void prosesPengembalian() {
        int baris = view.getTabelTransaksi().getSelectedRow();
        if (baris == -1) {
            view.tampilkanPesanError("Pilih transaksi di tabel yang ingin dikembalikan!");
            return;
        }

        String statusSaatIni = view.getModelTabel().getValueAt(baris, 8).toString();
        if ("DIKEMBALIKAN".equalsIgnoreCase(statusSaatIni)) {
            view.tampilkanPesanError("Transaksi ini sudah berstatus DIKEMBALIKAN!");
            return;
        }

        try {
            int idTrx = (int) view.getModelTabel().getValueAt(baris, 0);
            String jenis = view.getModelTabel().getValueAt(baris, 3).toString();
            LocalDate tglPinjamLocal = LocalDate.parse(view.getModelTabel().getValueAt(baris, 4).toString());
            LocalDate tglJatuhTempo = LocalDate.parse(view.getModelTabel().getValueAt(baris, 5).toString());

            Date tglKembali = (Date) view.getSpnTanggalKembali().getValue();
            LocalDate tglKembaliLocal = tglKembali.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (tglKembaliLocal.isBefore(tglPinjamLocal)) {
                view.tampilkanPesanError("Tanggal kembali tidak boleh sebelum tanggal pinjam (" + tglPinjamLocal + ")!");
                return;
            }

            long hariTerlambat = ChronoUnit.DAYS.between(tglJatuhTempo, tglKembaliLocal);
            double denda = 0;
            if (hariTerlambat > 0) {
                double tarif = "Skripsi".equalsIgnoreCase(jenis) ? 5000 : 2000;
                denda = hariTerlambat * tarif;
                if ("Skripsi".equalsIgnoreCase(jenis)) denda += 10000;

                if (view.getChkDiskonDenda().isSelected()) {
                    int diskon = (int) view.getSpnPersenDiskon().getValue();
                    denda -= (denda * diskon / 100.0);
                }
            }

            peminjamanDAO.kembalikanKoleksi(idTrx, tglKembali, denda);

            view.tampilkanPesanInfo(denda > 0
                ? "Koleksi berhasil dikembalikan!\nDenda Keterlambatan: Rp " + String.format("%,.0f", denda) + "\nStatus diperbarui dan tersimpan di tabel riwayat."
                : "Koleksi dikembalikan tepat waktu!\nStatus diperbarui dan tersimpan di tabel riwayat.");

            view.bersihkanForm();
            resetTampilanDropdown();
            muatDataTabel();
            tampilkanIdBerikutnya();

        } catch (Exception ex) {
            view.tampilkanPesanError("Gagal memproses pengembalian: " + ex.getMessage());
        }
    }

    private void hapusTransaksi() {
        int baris = view.getTabelTransaksi().getSelectedRow();
        if (baris == -1) {
            view.tampilkanPesanError("Pilih riwayat transaksi di tabel terlebih dahulu yang ingin dihapus!");
            return;
        }

        int idTrx = (int) view.getModelTabel().getValueAt(baris, 0);
        if (view.konfirmasiHapus(String.valueOf(idTrx))) {
            try {
                peminjamanDAO.hapusTransaksi(idTrx);
                view.tampilkanPesanInfo("Riwayat transaksi [" + idTrx + "] berhasil dihapus.");

                view.bersihkanForm();
                resetTampilanDropdown();
                muatDataTabel();
                tampilkanIdBerikutnya();
            } catch (SQLException ex) {
                view.tampilkanPesanError("Gagal menghapus dari database: " + ex.getMessage());
            }
        }
    }
}
