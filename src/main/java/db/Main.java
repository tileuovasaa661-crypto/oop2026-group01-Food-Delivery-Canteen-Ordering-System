package db;

public class Main {
    public static void main(String[] args) {
        try (var conn = db.DatabaseConnection.getConnection()) {
            System.out.println("Connected OK: " + (conn != null && !conn.isClosed()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
