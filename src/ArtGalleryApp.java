import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

// Interface untuk CRUD
interface GalleryOperations {
    void createPainting(Painting painting);
    void updatePainting(int id, Painting painting);
    void deletePainting(int id);
    List<Painting> getAllPaintings();
    Painting getPaintingById(int id);
}

// Kelas induk
class Artwork {
    protected int id;
    protected String name;
    protected String artist;
    protected String dateAdded;

    // Konstruktor untuk menginisialisasi atribut karya seni
    public Artwork(int id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        // Mengatur tanggal
        this.dateAdded = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Method untuk menampilkan detail karya seni
    public String getDetails() {
        return "ID: " + id + ", Name: " + name + ", Artist: " + artist + ", Date Added: " + dateAdded;
    }
}

// Subclass
class Painting extends Artwork {
    // Konstruktor untuk lukisan yang memanggil konstruktor superclass
    public Painting(int id, String name, String artist) {
        super(id, name, artist);
    }
}

// Implementasi interface
class Gallery implements GalleryOperations {
    private Connection conn;

     // Konstruktor untuk mengatur koneksi ke database
    public Gallery() {
        try {
            //Penggunaan JDBC
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DBUAS", "postgres", "miGacoan1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    // Method untuk menambahkan lukisan baru ke database
    public void createPainting(Painting painting) {
        try {
            // Periksa apakah ID sudah ada di database
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM paintings WHERE id = ?");
            checkStmt.setInt(1, painting.id);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                throw new SQLException("ID lukisan sudah ada di database.");
            }

            // Jika ID belum ada, tambahkan lukisan baru
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO paintings VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, painting.id);
            pstmt.setString(2, painting.name);
            pstmt.setString(3, painting.artist);
            pstmt.setDate(4, java.sql.Date.valueOf(painting.dateAdded));
            pstmt.executeUpdate();

            System.out.println("");
            System.out.println("Lukisan berhasil ditambahkan.");
            // Menampilkan hasil 
            Date tanggal = new Date(); // Mengambil waktu saat ini
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//penggunaan date
            System.out.println("Tanggal dan Waktu : " + formatter.format(tanggal));
            System.out.println("");
            System.out.println("+-----------------------------------------------+");
            System.out.println("Id lukisan      : " + painting.id);
            System.out.println("Nama Lukisan    : " + painting.name);
            System.out.println("Artis           : " + painting.artist);
            System.out.println("+-----------------------------------------------+");

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan lukisan: " + e.getMessage());//salah satu exception handling
        }
    }

    // Method untuk memperbarui data lukisan di database
    @Override
    public void updatePainting(int id, Painting painting) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE paintings SET name = ?, artist = ? WHERE id = ?");
            pstmt.setString(1, painting.name);
            pstmt.setString(2, painting.artist);
            pstmt.setInt(3, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {// salah satu percabangan
                System.out.println("Lukisan berhasil diperbarui.");
            } else {
                System.out.println("Lukisan dengan ID tersebut tidak ditemukan.");
            }
        } catch (SQLException e) {//salah satu exception handling
            e.printStackTrace();
        }    }

     // Method untuk menghapus lukisan dari database
    @Override
    public void deletePainting(int id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM paintings WHERE id = ?");
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Lukisan berhasil dihapus.");
            } else {
                System.out.println("Lukisan dengan ID tersebut tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk mengambil semua lukisan dari database
    @Override
    public List<Painting> getAllPaintings() {//penggunaan collection framework
        List<Painting> paintings = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM paintings");
            while (rs.next()) {
                paintings.add(new Painting(rs.getInt("id"), rs.getString("name"), rs.getString("artist")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paintings;
    }

    // Method untuk mengambil lukisan berdasarkan ID
    @Override
    public Painting getPaintingById(int id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM paintings WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Painting(rs.getInt("id"), rs.getString("name"), rs.getString("artist"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

public class ArtGalleryApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gallery gallery = new Gallery();
        boolean loginBerhasil = false;

        while (!loginBerhasil) {
            try {
                System.out.println("+-----------------------------------------------+");
                System.out.println("Log in");
                System.out.println("+-----------------------------------------------+");

                System.out.print("Username : ");
                String username = scanner.nextLine().trim(); // Method String: trim()

                System.out.print("Password : ");
                String password = scanner.nextLine();

                // Membuat captcha sederhana
                String captcha = generateCaptcha();
                System.out.println("Captcha : " + captcha);
                System.out.print("Masukkan Captcha : ");
                String inputCaptcha = scanner.nextLine();

                // Validasi login
                if (username.isEmpty() || password.isEmpty() || inputCaptcha.isEmpty()) {
                    throw new IllegalArgumentException("\nInput tidak boleh kosong! Silakan coba lagi.");
                }

                if (username.equalsIgnoreCase("aldi") && password.equals("1234") && inputCaptcha.equals(captcha)) {
                    System.out.println("\nLogin berhasil!\n");
                    loginBerhasil = true;
                } else {
                    throw new IllegalArgumentException("\nLogin gagal, silakan coba lagi.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("\nTerjadi kesalahan tak terduga. Silakan coba lagi.");
                scanner.nextLine(); // Membersihkan buffer
            }
        }


        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Input Lukisan");
            System.out.println("2. Ubah Data Lukisan");
            System.out.println("3. Hapus Lukisan");
            System.out.println("4. Tampilkan Semua Lukisan");
            System.out.println("5. Cari Lukisan");
            System.out.println("6. Keluar");
            System.out.print("Pilih opsi: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Masukkan ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Masukkan Nama Lukisan: ");
                    String name = scanner.nextLine();
                    System.out.print("Masukkan Nama Pelukis: ");
                    String artist = scanner.nextLine();
                    gallery.createPainting(new Painting(id, name, artist));
                    break;

                case 2:
                    System.out.print("Masukkan ID yang akan diubah: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Masukkan Nama Baru: ");
                    String newName = scanner.nextLine();
                    System.out.print("Masukkan Nama Pelukis Baru: ");
                    String newArtist = scanner.nextLine();
                    gallery.updatePainting(updateId, new Painting(updateId, newName, newArtist));
                    break;

                case 3:
                    System.out.print("Masukkan ID yang akan dihapus: ");
                    int deleteId = scanner.nextInt();
                    gallery.deletePainting(deleteId);
                    break;

                case 4:
                    List<Painting> paintings = gallery.getAllPaintings();
                    System.out.println("Daftar Lukisan:");
                    for (Painting painting : paintings) {
                        System.out.println(painting.getDetails());
                    }
                    System.out.println("Total Lukisan: " + paintings.size());//perhitungan matematika
                    break;

                case 5:
                    System.out.print("Masukkan ID yang dicari: ");
                    int searchId = scanner.nextInt();
                    Painting painting = gallery.getPaintingById(searchId);
                    if (painting != null) {
                        System.out.println(painting.getDetails());
                    } else {
                        System.out.println("Lukisan tidak ditemukan.");
                    }
                    break;

                case 6:
                    System.out.println("Keluar dari program.");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Method untuk membuat captcha
    public static String generateCaptcha() {
        String karakter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captcha = new StringBuilder(); // StringBuilder untuk efisiensi manipulasi string
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * karakter.length());
            captcha.append(karakter.charAt(index)); // Method String: charAt
        }
        return captcha.toString();
    }
    

}
