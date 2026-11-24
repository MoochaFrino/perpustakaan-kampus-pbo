-- Opsional: aplikasi sudah menjalankan ini otomatis saat dibuka pertama kali.
-- Jalankan manual di Supabase SQL Editor jika ingin memastikan sendiri.
ALTER TABLE koleksi ADD COLUMN IF NOT EXISTS stok integer NOT NULL DEFAULT 1;

-- Koleksi yang saat ini sedang dipinjam (tersedia = false) diset stok 0.
-- Jalankan HANYA SEKALI, tepat setelah kolom stok pertama kali dibuat.
UPDATE koleksi SET stok = CASE WHEN tersedia THEN 1 ELSE 0 END;
