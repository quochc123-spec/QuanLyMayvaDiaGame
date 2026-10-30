/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author hovin
 */
public class DbConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QLGameDB;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Chưa thêm file thư viện Driver JDBC vào Projects -> Libraries!");
            e.printStackTrace();
            return null;
        }
    }

    // --- HÀM MAIN CHẠY THỬ KẾT NỐI ---
    public static void main(String[] args) {
        System.out.println("Đang kiểm tra kết nối đến CSDL SQL Server...");
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("==========================================");
                System.out.println("=> KẾT NỐI CSDL THÀNH CÔNG!");
                System.out.println("Đang kết nối tới Database: " + conn.getCatalog());
                System.out.println("==========================================");
            } else {
                System.out.println("=> KẾT NỐI THẤT BẠI: Không thể khởi tạo Connection.");
            }
        } catch (Exception e) {
            System.err.println("=> LỖI KẾT NỐI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}