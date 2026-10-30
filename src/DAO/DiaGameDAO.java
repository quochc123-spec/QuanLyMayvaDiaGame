/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import model.DiaGame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author hovin
 */
public class DiaGameDAO {

    public List<DiaGame> selectAll() {
        List<DiaGame> list = new ArrayList<>();
        String sql = "SELECT * FROM DiaGame";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                DiaGame dg = new DiaGame(
                    rs.getString("MaDia"),
                    rs.getString("TenGame"),
                    rs.getString("TheLoai"),
                    rs.getDouble("GiaThueNgay"),
                    rs.getInt("SoLuongTon")
                );
                list.add(dg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public DiaGame selectById(String maDia) {
        String sql = "SELECT * FROM DiaGame WHERE MaDia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maDia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DiaGame(
                        rs.getString("MaDia"),
                        rs.getString("TenGame"),
                        rs.getString("TheLoai"),
                        rs.getDouble("GiaThueNgay"),
                        rs.getInt("SoLuongTon")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(DiaGame dg) {
        String sql = "INSERT INTO DiaGame (MaDia, TenGame, TheLoai, GiaThueNgay, SoLuongTon) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dg.getMaDia());
            ps.setString(2, dg.getTenGame());
            ps.setString(3, dg.getTheLoai());
            ps.setDouble(4, dg.getGiaThueNgay());
            ps.setInt(5, dg.getSoLuongTon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(DiaGame dg) {
        String sql = "UPDATE DiaGame SET TenGame = ?, TheLoai = ?, GiaThueNgay = ?, SoLuongTon = ? WHERE MaDia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dg.getTenGame());
            ps.setString(2, dg.getTheLoai());
            ps.setDouble(3, dg.getGiaThueNgay());
            ps.setInt(4, dg.getSoLuongTon());
            ps.setString(5, dg.getMaDia());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maDia) {
        String sql = "DELETE FROM DiaGame WHERE MaDia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maDia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
