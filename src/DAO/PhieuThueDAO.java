/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import model.PhieuThue;
import model.ChiTietPhieuThue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author hovin
 */
public class PhieuThueDAO {
    
    public List<PhieuThue> getAll() {
    List<PhieuThue> list = new ArrayList<>();
    String sql = "SELECT * FROM PhieuThue";
    
    // Sử dụng try-with-resources để tự động đóng kết nối
    try (Connection conn = DbConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
         
        while (rs.next()) {
            PhieuThue pt = new PhieuThue();
            
            // Gán dữ liệu từ ResultSet vào đối tượng
            pt.setMaPhieu(rs.getString("MaPhieu"));
            pt.setMaKH(rs.getString("MaKH"));
            
            // Xử lý Ngày Lập
            java.sql.Date ngayLap = rs.getDate("NgayThue");
            if (ngayLap != null) {
                pt.setNgayThue(new java.util.Date(ngayLap.getTime()));
            }
            
            // Xử lý Ngày Trả (Cột này có thể NULL trong CSDL nếu chưa trả)
            java.sql.Date ngayTra = rs.getDate("NgayTraDuKien");
            if (ngayTra != null) {
                pt.setNgayTraDuKien(new java.util.Date(ngayTra.getTime()));
            } else {
                pt.setNgayTraDuKien(null);
            }
            
            pt.setTrangThai(rs.getString("TrangThai"));
            pt.setTongTien(rs.getDouble("TongTien"));
            
            // Thêm đối tượng vào danh sách
            list.add(pt);
        }
    } catch (Exception e) {
        System.out.println("Lỗi khi lấy danh sách Phiếu Thuê: " + e.getMessage());
        e.printStackTrace();
    }
    
    return list;
}
    // Tạo phiếu thuê mới
    public boolean insert(PhieuThue pt) {
        String sql = "INSERT INTO PhieuThue (MaPhieu, MaKH, NgayThue, NgayTraDuKien, TongTien, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, pt.getMaPhieu());
            ps.setString(2, pt.getMaKH());
            ps.setTimestamp(3, new Timestamp(pt.getNgayThue().getTime()));
            ps.setTimestamp(4, pt.getNgayTraDuKien() != null ? new Timestamp(pt.getNgayTraDuKien().getTime()) : null);
            ps.setDouble(5, pt.getTongTien());
            ps.setString(6, pt.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Lấy danh sách Chi tiết phiếu thuê theo Mã Phiếu
    public List<ChiTietPhieuThue> selectChiTietByMaPhieu(String maPhieu) {
        List<ChiTietPhieuThue> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuThue WHERE MaPhieu = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maPhieu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietPhieuThue ct = new ChiTietPhieuThue();
                    ct.setMaPhieu(rs.getString("MaPhieu"));
                    ct.setLoaiThue(rs.getString("LoaiThue"));
                    ct.setMaThietBi(rs.getString("MaThietBi"));
                    ct.setSoLuongHoacGio(rs.getDouble("SoLuongHoacGio"));
                    ct.setThanhTien(rs.getDouble("ThanhTien"));
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // Thêm chi tiết phiếu thuê (Máy hoặc Đĩa)
    public boolean insertChiTiet(ChiTietPhieuThue ct) {
        String sql = "INSERT INTO ChiTietPhieuThue (MaPhieu, LoaiThue, MaThietBi, SoLuongHoacGio, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ct.getMaPhieu());
            ps.setString(2, ct.getLoaiThue());
            ps.setString(3, ct.getMaThietBi());
            ps.setDouble(4, ct.getSoLuongHoacGio());
            ps.setDouble(5, ct.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

     // --- CẬP NHẬT THÔNG TIN PHIẾU THUÊ ---
    public boolean update(PhieuThue pt) {
        String sql = "UPDATE PhieuThue SET MaKH = ?, NgayThue = ?, NgayTraDuKien = ? WHERE MaPhieu = ?";
    
    try (Connection con = DbConnection.getConnection(); // Hoặc class kết nối DB của bạn
         PreparedStatement ps = con.prepareStatement(sql)) {
         
         // Tham số 1
         ps.setString(1, pt.getMaKH());
         
         // Tham số 2
         ps.setDate(2, new java.sql.Date(pt.getNgayThue().getTime()));
         
         // Tham số 3 (Có kiểm tra Null cho Ngày trả)
         if (pt.getNgayTraDuKien() != null) {
             ps.setDate(3, new java.sql.Date(pt.getNgayTraDuKien().getTime()));
         } else {
             ps.setNull(3, java.sql.Types.DATE);
         }
         
         // THAM SỐ 4 THƯỜNG HAY BỊ QUÊN NHẤT (Điều kiện WHERE) -> Hãy thêm dòng này vào DAO của bạn
         ps.setString(4, pt.getMaPhieu()); 
         
         return ps.executeUpdate() > 0;
         
    } catch (Exception e) {
         e.printStackTrace();
    }
    return false;
    }

    // --- XÓA PHIẾU THUÊ VÀ CHI TIẾT PHIẾU THUÊ ---
public boolean delete(String maPhieu) {
    // Xóa chi tiết phiếu thuê trước (để tránh lỗi khóa ngoại FK), sau đó xóa phiếu thuê
    String sqlDeleteChiTiet = "DELETE FROM ChiTietPhieuThue WHERE MaPhieu = ?";
    String sqlDeletePhieu = "DELETE FROM PhieuThue WHERE MaPhieu = ?";
    
    try (Connection conn = DbConnection.getConnection()) {
        conn.setAutoCommit(false); // Bắt đầu Transaction
        
        try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteChiTiet);
             PreparedStatement ps2 = conn.prepareStatement(sqlDeletePhieu)) {
            
            ps1.setString(1, maPhieu);
            ps1.executeUpdate();
            
            ps2.setString(1, maPhieu);
            int rows = ps2.executeUpdate();
            
            conn.commit(); // Xác nhận xóa thành công
            return rows > 0;
        } catch (SQLException e) {
            conn.rollback(); // Hoàn tác nếu có lỗi
            e.printStackTrace();
            return false;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public boolean checkTonTaiChiTiet(String maPhieu, String maThietBi) {
        String sql = "SELECT 1 FROM ChiTietPhieuThue WHERE MaPhieu = ? AND MaThietBi = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            ps.setString(2, maThietBi);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true nếu đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
public boolean updateChiTiet(ChiTietPhieuThue ct) {
        String sql = "UPDATE ChiTietPhieuThue SET SoLuongHoacGio = ?, ThanhTien = ? WHERE MaPhieu = ? AND MaThietBi = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, ct.getSoLuongHoacGio());
            ps.setDouble(2, ct.getThanhTien());
            ps.setString(3, ct.getMaPhieu());
            ps.setString(4, ct.getMaThietBi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Xử lý Trả máy/đĩa và Thanh toán
    public boolean thanhToanPhieuThue(String maPhieu, java.util.Date ngayTraThucTe, double tongTien) {
        String sql = "UPDATE PhieuThue SET NgayTraThucTe = ?, TongTien = ?, TrangThai = N'Đã thanh toán' WHERE MaPhieu = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(ngayTraThucTe.getTime()));
            ps.setDouble(2, tongTien);
            ps.setString(3, maPhieu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách phiếu chưa thanh toán
    public List<PhieuThue> selectPhieuDangThue() {
        List<PhieuThue> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuThue WHERE TrangThai = N'Đang thuê'";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PhieuThue pt = new PhieuThue(
                    rs.getString("MaPhieu"),
                    rs.getString("MaKH"),
                    rs.getTimestamp("NgayThue"),
                    rs.getTimestamp("NgayTraDuKien"),
                    rs.getTimestamp("NgayTraThucTe"),
                    rs.getDouble("TongTien"),
                    rs.getString("TrangThai")
                );
                list.add(pt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<PhieuThue> selectPhieuDaThanhToan() {
    List<PhieuThue> list = new ArrayList<>();
    String sql = "SELECT * FROM PhieuThue WHERE TrangThai = N'Đã thanh toán'";
    try (Connection conn = DbConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            PhieuThue pt = new PhieuThue(
                rs.getString("MaPhieu"),
                rs.getString("MaKH"),
                rs.getTimestamp("NgayThue"),
                rs.getTimestamp("NgayTraDuKien"),
                rs.getTimestamp("NgayTraThucTe"), // Cột Ngày trả thực tế
                rs.getDouble("TongTien"),
                rs.getString("TrangThai")
            );
            list.add(pt);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
    public boolean updateTongTien(String maPhieu, double tongTien) {
        String sql = "UPDATE PhieuThue SET TongTien = ? WHERE MaPhieu = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tongTien);
            ps.setString(2, maPhieu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
