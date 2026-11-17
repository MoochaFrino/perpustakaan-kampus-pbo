package controller;

import dao.AnggotaDAO;
import exception.ValidasiInputException;
import model.Anggota;
import view.PanelAnggota;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AnggotaController {
    private final PanelAnggota view;
    private final AnggotaDAO dao;

    public AnggotaController(PanelAnggota view) {
        this.view = view;
        this.dao = new AnggotaDAO();
        
        inisialisasiEvent();
        muatDataTabel();
    }

    private void inisialisasiEvent() {
        view.getBtnTambah().addActionListener(e -> tambahDataAnggota());
        view.getBtnUbah().addActionListener(e -> ubahDataAnggota());
        view.getBtnHapus().addActionListener(e -> hapusDataAnggota());
        
        // 2. Event Klik Baris Tabel (Mengisi form secara otomatis saat tabel diklik)
        view.getTabelAnggota().getSelectionModel().addListSelectionListener(e -> {
            int baris = view.getTabelAnggota().getSelectedRow();
            if (baris != -1 && !e.getValueIsAdjusting()) {
                view.getTxtIdAnggota().setText(view.getModelTabel().getValueAt(baris, 0).toString());
                view.getTxtIdAnggota().setEditable(false); // Kunci ID agar tidak diubah (Primary Key)
                
                view.getTxtNimNip().setText(view.getModelTabel().getValueAt(baris, 1).toString());
                view.getTxtNama().setText(view.getModelTabel().getValueAt(baris, 2).toString());
                view.getCmbKategori().setSelectedItem(view.getModelTabel().getValueAt(baris, 3).toString());
                view.getTxtJurusan().setText(view.getModelTabel().getValueAt(baris, 4).toString());
                
                String gender = view.getModelTabel().getValueAt(baris, 5).toString();
                if (gender.equalsIgnoreCase("Laki-laki")) {
                    view.getRbLakiLaki().setSelected(true);
                } else {
                    view.getRbPerempuan().setSelected(true);
                }
                
                String status = view.getModelTabel().getValueAt(baris, 6).toString();
                view.getChkStatusAktif().setSelected(status.equalsIgnoreCase("Aktif"));
            }
        });
    }

    private void muatDataTabel() {
        DefaultTableModel model = view.getModelTabel();
        model.setRowCount(0);
        try {
            List<Object[]> barisData = dao.ambilSemuaAnggota();
            for (Object[] baris : barisData) {
                model.addRow(baris);
            }
        } catch (SQLException ex) {
            view.tampilkanPesanError("Gagal memuat data database: " + ex.getMessage());
        }
    }

    private void tambahDataAnggota() {
        try {
            String idTeks = view.getTxtIdAnggota().getText();
            String nim = view.getTxtNimNip().getText();
            String nama = view.getTxtNama().getText();
            String prodi = view.getTxtJurusan().getText();
            String kategori = view.getCmbKategori().getSelectedItem().toString();
            String gender = view.getRbLakiLaki().isSelected() ? "Laki-laki" : "Perempuan";
            boolean aktif = view.getChkStatusAktif().isSelected();

            if (idTeks.isEmpty() || nama.isEmpty() || nim.isEmpty()) {
                throw new ValidasiInputException("ID, NIM, dan Nama tidak boleh kosong!");
            }
            int id = Integer.parseInt(idTeks);

            Anggota anggotaBaru = new Anggota(id, nama, nim, prodi, gender, new Date());
            anggotaBaru.setAktif(aktif);

            dao.tambahAnggota(anggotaBaru, kategori);
            
            view.tampilkanPesanInfo("Data Anggota berhasil ditambahkan!");
            view.bersihkanForm();
            muatDataTabel();

        } catch (NumberFormatException ex) {
            view.tampilkanPesanError("ID Anggota harus berupa angka bulat!");
        } catch (ValidasiInputException | SQLException ex) {
            view.tampilkanPesanError(ex.getMessage());
        }
    }

    private void ubahDataAnggota() {
        try {
            if (view.getTxtIdAnggota().getText().isEmpty()) {
                throw new ValidasiInputException("Pilih anggota di tabel yang ingin diubah terlebih dahulu!");
            }

            int id = Integer.parseInt(view.getTxtIdAnggota().getText());
            String nim = view.getTxtNimNip().getText();
            String nama = view.getTxtNama().getText();
            String prodi = view.getTxtJurusan().getText();
            String kategori = view.getCmbKategori().getSelectedItem().toString();
            String gender = view.getRbLakiLaki().isSelected() ? "Laki-laki" : "Perempuan";
            boolean aktif = view.getChkStatusAktif().isSelected();

            if (nama.isEmpty() || nim.isEmpty()) {
                throw new ValidasiInputException("NIM dan Nama tidak boleh kosong!");
            }

            Anggota anggotaEdit = new Anggota(id, nama, nim, prodi, gender, new Date());
            anggotaEdit.setAktif(aktif);

            dao.ubahAnggota(anggotaEdit, kategori);
            
            view.tampilkanPesanInfo("Data Anggota berhasil diperbarui!");
            view.bersihkanForm();
            muatDataTabel();

        } catch (ValidasiInputException | SQLException ex) {
            view.tampilkanPesanError(ex.getMessage());
        }
    }

    private void hapusDataAnggota() {
        int barisTerpilih = view.getTabelAnggota().getSelectedRow();
        if (barisTerpilih == -1) {
            view.tampilkanPesanError("Pilih baris di tabel terlebih dahulu untuk dihapus!");
            return;
        }

        int idAnggota = (int) view.getModelTabel().getValueAt(barisTerpilih, 0);
        String nama = (String) view.getModelTabel().getValueAt(barisTerpilih, 2);

        if (view.konfirmasiHapus(String.valueOf(idAnggota), nama)) {
            try {
                dao.hapusAnggota(idAnggota);
                view.tampilkanPesanInfo("Anggota berhasil dihapus.");
                view.bersihkanForm();
                muatDataTabel();
            } catch (SQLException ex) {
                view.tampilkanPesanError("Gagal menghapus dari database: " + ex.getMessage());
            }
        }
    }
}