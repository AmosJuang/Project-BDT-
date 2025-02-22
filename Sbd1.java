import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sbd1 {
    
    static String url = "jdbc:sqlserver://localhost:1433;databaseName=TestDB;encrypt=true;trustServerCertificate=true";
    static String userName = "sa";
    static String password = "123";

    public static void main(String[] args) {
        try (Connection conn = getConnection(url, userName, password)) {
            createTables(conn);
            System.out.println("Before Insert");
            displayData(conn);
            insertMahasiswa(conn);
            insertNilai(conn);
            System.out.println("\nAfter Insert");
            displayData(conn); 
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection(String url, String userName, String passwd)
            throws SQLException, ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, userName, passwd);
    }
    public static void createTables(Connection conn) throws SQLException {
        String createMahasiswaTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Mahasiswa' AND xtype='U') " +
                                      "CREATE TABLE Mahasiswa (" +
                                      "NIM VARCHAR(10) PRIMARY KEY, " +
                                      "Nama VARCHAR(50) NOT NULL)";

        String createNilaiTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Nilai' AND xtype='U') " +
                                  "CREATE TABLE Nilai (" +
                                  "NIM VARCHAR(10), " +
                                  "Nilai FLOAT NOT NULL, " +
                                  "FOREIGN KEY (NIM) REFERENCES Mahasiswa(NIM))";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createMahasiswaTable);
            stmt.executeUpdate(createNilaiTable);
            System.out.println("Tabel berhasil dibuat atau sudah ada.");
        }
    }
    public static void insertMahasiswa(Connection conn) throws SQLException {
        String insertQuery = "INSERT INTO Mahasiswa(NIM, Nama) VALUES (?, ?), (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, "001");
            ps.setString(2, "Amos");
            ps.setString(3, "002");
            ps.setString(4, "Yuli");
            ps.executeUpdate();
        }
    }
    public static void insertNilai(Connection conn) throws SQLException {
        String insertQuery = "INSERT INTO Nilai(NIM, Nilai) VALUES (?, ?), (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, "001");
            ps.setFloat(2, 95);
            ps.setString(3, "002");
            ps.setFloat(4, 87);
            ps.executeUpdate();
        }
    }
    public static void displayData(Connection conn) throws SQLException {
        String query = "SELECT Mahasiswa.NIM, Mahasiswa.Nama, Nilai.Nilai " +
                       "FROM Mahasiswa " +
                       "JOIN Nilai ON Mahasiswa.NIM = Nilai.NIM";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("==========================================");
            System.out.printf("| %-4s | %-10s | %-5s |\n", "NIM", "NAMA", "NILAI");
            System.out.println("==========================================");
            while (rs.next()) {
                String nim = rs.getString("NIM");
                String nama = rs.getString("Nama");
                float nilai = rs.getFloat("Nilai");
                System.out.printf("| %-4s | %-10s | %-5.1f |\n", nim, nama, nilai);
            }
        }
    }
}
