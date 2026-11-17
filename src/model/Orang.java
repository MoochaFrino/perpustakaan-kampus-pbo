package model;

public abstract class Orang implements Identifiable {
    private int id;
    private String nama;

    protected Orang(int id, String nama) {
        this.id = id;
        setNama(nama);
    }

    public abstract String getPeran();

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty())
            throw new IllegalArgumentException("Nama tidak boleh kosong.");
        this.nama = nama.trim();
    }

    @Override
    public String toString() { return getPeran() + " #" + id + ": " + nama; }
}