package controller;

import dao.KoleksiDAO;
import exception.ValidasiInputException;
import model.*;
import view.PanelKoleksi;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PerpustakaanController {
    private final PanelKoleksi view;
    private final KoleksiDAO dao;
    private List<Koleksi> daftarKoleksiTampil = new ArrayList<>();

    public PerpustakaanController(PanelKoleksi view) {
        this.view = view;
        this.dao = new KoleksiDAO();

        KoleksiDAO.pastikanKolomStok();
        KoleksiDAO.pastikanKolomIssn();
        inisialisasiEvent();
        muatDataTabel();
        tampilkanIdBerikutnya();
    }

    private void tampilkanIdBerikutnya() {
        try {
            view.getTxtIdKoleksi().setText(String.valueOf(dao.idBerikutnya()));
        } catch (SQLException ex) {
            view.getTxtIdKoleksi().setText("");
        }
    }

    private void inisialisasiEvent() {
        view.getBtnTambah().addActionListener(e -> tambahDataKoleksi());
        view.getBtnUbah().addActionListener(e -> ubahDataKoleksi()); // Fitur Edit
        view.getBtnHapus().addActionListener(e -> hapusDataKoleksi());
        view.getBtnReset().addActionListener(e -> {
            view.bersihkanForm();
            tampilkanIdBerikutnya();
        });

        view.getTabelKoleksi().getSelectionModel().addListSelectionListener(e -> {
            int baris = view.getTabelKoleksi().getSelectedRow();
            if (baris != -1 && !e.getValueIsAdjusting()) {
                view.getTxtIdKoleksi().setText(view.getModelTabel().getValueAt(baris, 0).toString());
                view.getTxtIdKoleksi().setEditable(false); // Kunci ID

                String jenis = view.getModelTabel().getValueAt(baris, 1).toString();
                view.getCmbJenisKoleksi().setSelectedItem(jenis); // Ini akan otomatis mengubah label atribut khusus

                view.getTxtJudul().setText(view.getModelTabel().getValueAt(baris, 2).toString());
                view.getTxtPenulis().setText(view.getModelTabel().getValueAt(baris, 3).toString());
                view.getSpnTahunTerbit()
                        .setValue(Integer.parseInt(view.getModelTabel().getValueAt(baris, 4).toString()));

                view.getSpnStok().setValue(Integer.parseInt(view.getModelTabel().getValueAt(baris, 5).toString()));
                view.getChkReferensiSaja()
                        .setSelected("Ya".equals(view.getModelTabel().getValueAt(baris, 8).toString()));

                String atributKhusus = view.getModelTabel().getValueAt(baris, 7).toString();
                if (baris < daftarKoleksiTampil.size()) {
                    Koleksi k = daftarKoleksiTampil.get(baris);
                    if (k instanceof Buku) {
                        view.getTxtAtributKhusus1().setText(((Buku) k).getIsbn());
                    } else if (k instanceof Jurnal) {
                        Jurnal j = (Jurnal) k;
                        view.getTxtAtributKhusus1().setText("-".equals(j.getIssn()) ? "" : j.getIssn());
                        view.getSpnAtributKhusus2().setValue(j.getVolume());
                    } else if (k instanceof Skripsi) {
                        view.getTxtAtributKhusus1().setText(((Skripsi) k).getDosenPembimbing());
                    }
                } else if (jenis.equalsIgnoreCase("Buku") || jenis.equalsIgnoreCase("Skripsi")) {
                    view.getTxtAtributKhusus1().setText(atributKhusus);
                } else if (jenis.equalsIgnoreCase("Jurnal")) {
                    try {
                        view.getSpnAtributKhusus2().setValue(Integer.parseInt(atributKhusus));
                    } catch (Exception ex) {
                    }
                }

            }
        });
    }

    public void muatDataTabel() {
        DefaultTableModel model = view.getModelTabel();
        model.setRowCount(0);
        try {
            List<Koleksi> listKoleksi = dao.ambilSemuaKoleksi();
            daftarKoleksiTampil = listKoleksi;
            for (Koleksi k : listKoleksi) {
                // Konversi atribut khusus untuk ditampilkan di kolom tabel
                String atributKhusus = "-";
                if (k instanceof Buku)
                    atributKhusus = ((Buku) k).getIsbn();
                else if (k instanceof Jurnal) {
                    Jurnal j = (Jurnal) k;
                    atributKhusus = "-".equals(j.getIssn())
                            ? "Vol. " + j.getVolume()
                            : "ISSN: " + j.getIssn() + " (Vol. " + j.getVolume() + ")";
                } else if (k instanceof Skripsi)
                    atributKhusus = ((Skripsi) k).getDosenPembimbing();

                model.addRow(new Object[] {
                        k.getId(),
                        k.getJenis(),
                        k.getJudul(),
                        k.getPengarang(),
                        k.getTahun(),
                        k.getStok(),
                        k.isReferensi() ? "Referensi" : k.getStatus(),
                        atributKhusus,
                        k.isReferensi() ? "Ya" : "Tidak"
                });
            }
        } catch (SQLException ex) {
            view.tampilkanPesanError("Gagal memuat data dari database: " + ex.getMessage());
        }
    }

    private void validasiInputKoleksi(String judul, String pengarang, String jenis, String atributKhusus1,
            int idSaatIni)
            throws ValidasiInputException, SQLException {
        if (judul == null || judul.trim().isEmpty() || pengarang == null || pengarang.trim().isEmpty()) {
            throw new ValidasiInputException("Judul dan Penulis tidak boleh kosong!");
        }

        if (dao.judulSudahAda(judul, idSaatIni)) {
            throw new ValidasiInputException(
                    "Judul koleksi \"" + judul.trim()
                            + "\" sudah terdaftar! Setiap buku, jurnal, atau skripsi hanya boleh memiliki 1 judul unik.");
        }

        String nilaiKhusus1 = atributKhusus1 == null ? "" : atributKhusus1.trim();
        if ("Buku".equalsIgnoreCase(jenis)) {
            if (nilaiKhusus1.isEmpty() || "-".equals(nilaiKhusus1)) {
                throw new ValidasiInputException("Nomor ISBN buku tidak boleh kosong!");
            }
            if (dao.isbnSudahAda(nilaiKhusus1, idSaatIni)) {
                throw new ValidasiInputException(
                        "Nomor ISBN \"" + nilaiKhusus1 + "\" sudah terdaftar! Setiap ISBN hanya boleh diinput 1 kali.");
            }
        } else if ("Jurnal".equalsIgnoreCase(jenis)) {
            if (nilaiKhusus1.isEmpty() || "-".equals(nilaiKhusus1)) {
                throw new ValidasiInputException("Nomor ISSN jurnal tidak boleh kosong!");
            }
            if (dao.issnSudahAda(nilaiKhusus1, idSaatIni)) {
                throw new ValidasiInputException(
                        "Nomor ISSN \"" + nilaiKhusus1 + "\" sudah terdaftar! Setiap ISSN hanya boleh diinput 1 kali.");
            }
        }
    }

    private void tambahDataKoleksi() {
        try {
            String judul = view.getTxtJudul().getText();
            String pengarang = view.getTxtPenulis().getText();
            int tahun = (int) view.getSpnTahunTerbit().getValue();
            int stok = (int) view.getSpnStok().getValue();
            String jenis = view.getCmbJenisKoleksi().getSelectedItem().toString();
            String atributKhusus1 = view.getTxtAtributKhusus1().getText();
            int atributKhusus2 = (int) view.getSpnAtributKhusus2().getValue();

            validasiInputKoleksi(judul, pengarang, jenis, atributKhusus1, -1);

            int id = dao.idBerikutnya(); // ID otomatis
            Koleksi koleksiBaru;
            if (jenis.equalsIgnoreCase("Buku")) {
                koleksiBaru = new Buku(id, judul, pengarang, tahun, new Date(), atributKhusus1);
            } else if (jenis.equalsIgnoreCase("Jurnal")) {
                koleksiBaru = new Jurnal(id, judul, pengarang, tahun, new Date(), atributKhusus2, atributKhusus1);
            } else {
                koleksiBaru = new Skripsi(id, judul, pengarang, tahun, new Date(), atributKhusus1);
            }
            koleksiBaru.setStok(stok);
            koleksiBaru.setReferensi(view.getChkReferensiSaja().isSelected());

            dao.tambahKoleksi(koleksiBaru);
            view.tampilkanPesanInfo("Data Koleksi berhasil ditambahkan dengan ID " + id + "!");
            view.bersihkanForm();
            muatDataTabel();
            tampilkanIdBerikutnya();

        } catch (ValidasiInputException | SQLException | IllegalArgumentException ex) {
            view.tampilkanPesanError(ex.getMessage());
        }
    }

    private void ubahDataKoleksi() {
        try {
            if (view.getTxtIdKoleksi().getText().isEmpty()) {
                throw new ValidasiInputException("Pilih baris di tabel yang ingin diubah terlebih dahulu!");
            }

            int id = Integer.parseInt(view.getTxtIdKoleksi().getText());
            String judul = view.getTxtJudul().getText();
            String pengarang = view.getTxtPenulis().getText(); 
            int tahun = (int) view.getSpnTahunTerbit().getValue(); 
            int stok = (int) view.getSpnStok().getValue();
            String jenis = view.getCmbJenisKoleksi().getSelectedItem().toString(); 
            String atributKhusus1 = view.getTxtAtributKhusus1().getText(); 
            int atributKhusus2 = (int) view.getSpnAtributKhusus2().getValue(); 

            validasiInputKoleksi(judul, pengarang, jenis, atributKhusus1, id);

            Koleksi koleksiEdit;
            if (jenis.equalsIgnoreCase("Buku")) {
                koleksiEdit = new Buku(id, judul, pengarang, tahun, new Date(), atributKhusus1);
            } else if (jenis.equalsIgnoreCase("Jurnal")) {
                koleksiEdit = new Jurnal(id, judul, pengarang, tahun, new Date(), atributKhusus2, atributKhusus1); 
            } else {
                koleksiEdit = new Skripsi(id, judul, pengarang, tahun, new Date(), atributKhusus1); 
            }
            koleksiEdit.setStok(stok);
            koleksiEdit.setReferensi(view.getChkReferensiSaja().isSelected());

            dao.ubahKoleksi(koleksiEdit);
            view.tampilkanPesanInfo("Data Koleksi berhasil diperbarui!");
            view.bersihkanForm();
            muatDataTabel();
            tampilkanIdBerikutnya();

        } catch (ValidasiInputException | SQLException | IllegalArgumentException ex) {
            view.tampilkanPesanError(ex.getMessage());
        }
    }

    private void hapusDataKoleksi() {
        int barisTerpilih = view.getTabelKoleksi().getSelectedRow();
        if (barisTerpilih == -1) {
            view.tampilkanPesanError("Pilih baris di tabel terlebih dahulu untuk dihapus!");
            return;
        }

        int idKoleksi = (int) view.getModelTabel().getValueAt(barisTerpilih, 0);
        String judul = (String) view.getModelTabel().getValueAt(barisTerpilih, 2);

        if (view.konfirmasiHapus(String.valueOf(idKoleksi), judul)) {
            try {
                dao.hapusKoleksi(idKoleksi);
                view.tampilkanPesanInfo("Koleksi berhasil dihapus.");
                view.bersihkanForm();
                muatDataTabel();
                tampilkanIdBerikutnya();
            } catch (SQLException ex) {
                view.tampilkanPesanError("Gagal menghapus dari database: " + ex.getMessage());
            }
        }
    }
}