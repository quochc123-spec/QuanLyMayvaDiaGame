/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import model.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author hovin
 */
public class KhachHangDAO {

    public List<KhachHang> selectAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                    rs.getString("MaKH"),
                    rs.getString("HoTen"),
                    rs.getString("SoDienThoai"),
                    rs.getString("CCCD")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public KhachHang selectById(String maKH) {
    String sql = "SELECT * FROM KhachHang WHERE MaKH = ?";
    try (Connection conn = DbConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, maKH);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new KhachHang(
                    rs.getString("MaKH"),
                    rs.getString("HoTen"),
                    rs.getString("SoDienThoai"),
                    rs.getString("CCCD")
                );
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null; // Trả về null nếu KHÔNG tìm thấy khách hàng
}
    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (MaKH, HoTen, SoDienThoai, CCCD) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getCccd());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhachHang kh) {
        String sql = "UPDATE KhachHang SET HoTen = ?, SoDienThoai = ?, CCCD = ? WHERE MaKH = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getCccd());
            ps.setString(4, kh.getMaKH());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE MaKH = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
