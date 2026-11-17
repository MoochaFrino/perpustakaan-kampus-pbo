package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DaftarObjek<T extends Identifiable> {
    private final Map<Integer, T> data = new LinkedHashMap<>();
    private int idBerikutnya = 1;

    public T tambah(T item) {
        if (item == null) throw new IllegalArgumentException("Objek tidak boleh null.");
        if (item.getId() <= 0) item.setId(idBerikutnya);
        if (data.containsKey(item.getId()))
            throw new IllegalArgumentException("ID " + item.getId() + " sudah dipakai.");
        data.put(item.getId(), item);
        idBerikutnya = Math.max(idBerikutnya, item.getId() + 1);
        return item;
    }

    public T cari(int id) { return data.get(id); }

    public boolean ubah(T item) {
        if (item == null || !data.containsKey(item.getId())) return false;
        data.put(item.getId(), item);
        return true;
    }

    public boolean hapus(int id) { return data.remove(id) != null; }

    public List<T> semua() { return new ArrayList<>(data.values()); }

    public List<T> filter(Predicate<T> syarat) {
        List<T> hasil = new ArrayList<>();
        for (T item : data.values()) {
            if (syarat.test(item)) hasil.add(item);
        }
        return hasil;
    }

    public int jumlah() { return data.size(); }
}