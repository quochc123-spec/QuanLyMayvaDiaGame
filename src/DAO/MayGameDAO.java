/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import model.MayGame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author hovin
 */
public class MayGameDAO {

    public List<MayGame> selectAll() {
        List<MayGame> list = new ArrayList<>();
        String sql = "SELECT * FROM MayGame";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MayGame mg = new MayGame(
                    rs.getString("MaMay"),
                    rs.getString("TenMay"),
                    rs.getString("LoaiMay"),
                    rs.getDouble("GiaThueGio"),
                    rs.getString("TrangThai")
                );
                list.add(mg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public MayGame selectById(String maMay) {
        String sql = "SELECT * FROM MayGame WHERE MaMay = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maMay);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MayGame(
                        rs.getString("MaMay"),
                        rs.getString("TenMay"),
                        rs.getString("LoaiMay"),
                        rs.getDouble("GiaThueGio"),
                        rs.getString("TrangThai")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean insert(MayGame mg) {
        String sql = "INSERT INTO MayGame (MaMay, TenMay, LoaiMay, GiaThueGio, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, mg.getMaMay());
            ps.setString(2, mg.getTenMay());
            ps.setString(3, mg.getLoaiMay());
            ps.setDouble(4, mg.getGiaThueGio());
            ps.setString(5, mg.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(MayGame mg) {
        String sql = "UPDATE MayGame SET TenMay = ?, LoaiMay = ?, GiaThueGio = ?, TrangThai = ? WHERE MaMay = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, mg.getTenMay());
            ps.setString(2, mg.getLoaiMay());
            ps.setDouble(3, mg.getGiaThueGio());
            ps.setString(4, mg.getTrangThai());
            ps.setString(5, mg.getMaMay());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(String maMay, String trangThai) {
        String sql = "UPDATE MayGame SET TrangThai = ? WHERE MaMay = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, trangThai);
            ps.setString(2, maMay);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maMay) {
        String sql = "DELETE FROM MayGame WHERE MaMay = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maMay);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
