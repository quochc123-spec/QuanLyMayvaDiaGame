/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import DAO.MayGameDAO;
import DAO.DiaGameDAO;
import DAO.PhieuThueDAO;
import model.ChiTietPhieuThue;
import model.PhieuThue;
import model.MayGame;
import model.DiaGame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 *
 * @author hovin
 */
public class PhieuThueForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PhieuThueForm.class.getName());
    private PhieuThueDAO phieuThueDAO;
    private MayGameDAO mayGameDAO;
    private DiaGameDAO diaGameDAO;
    private DefaultTableModel tableModel;
    private DefaultTableModel tableModelChiTiet;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    /**
     * Creates new form PhieuThueForm
     */
    public PhieuThueForm() {
       initComponents();
        phieuThueDAO = new PhieuThueDAO();
        mayGameDAO = new MayGameDAO();
        diaGameDAO = new DiaGameDAO();
        setLocationRelativeTo(null); 
        
        // Sửa lại tiêu đề cột cho khớp với hàm loadDataToTable
        String[] columnNames = {"Mã Phiếu", "Mã Khách Hàng", "Ngày Thuê", "Ngày Trả", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblPhieuThue.setModel(tableModel);
        
        // Setup Bảng 2: Chi Tiết Phiếu Thuê
        String[] columnNamesChiTiet = {"Mã Phiếu", "Loại Thuê", "Mã Thiết Bị", "Tên Máy/Đĩa", "Giờ thuê", "Thành Tiền"};
        tableModelChiTiet = new DefaultTableModel(columnNamesChiTiet, 0);
        tblChiTiet.setModel(tableModelChiTiet);
        loadDataToTable();
        initEvents();
    }
    private void autoResizeColumn(JTable table) {
        // Tắt ép viền để cho phép bảng tràn ra ngoài và hiện thanh cuộn ngang
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Độ rộng tối thiểu
            
            // 1. Lấy độ rộng của nội dung Tiêu đề cột (Header)
            javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
            javax.swing.table.TableCellRenderer headerRenderer = columnModel.getColumn(column).getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = table.getTableHeader().getDefaultRenderer();
            }
            java.awt.Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, columnModel.getColumn(column).getHeaderValue(), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width);

            // 2. Lấy độ rộng lớn nhất của dữ liệu các hàng trong cột đó
            for (int row = 0; row < table.getRowCount(); row++) {
                javax.swing.table.TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                java.awt.Component cellComp = table.prepareRenderer(cellRenderer, row, column);
                width = Math.max(width, cellComp.getPreferredSize().width);
            }

            // Cộng thêm khoảng trống (margin) 20 pixel hai bên cho thoáng mắt
            width += 20;
            
            // Set lại độ rộng đã tính toán cho cột
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
    private void loadDataToTable() {
        DefaultTableModel model = (DefaultTableModel) tblPhieuThue.getModel();
        model.setRowCount(0); // Xoá dữ liệu cũ
        
        PhieuThueDAO dao = new PhieuThueDAO();
        List<PhieuThue> list = dao.getAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (PhieuThue pt : list) {
            // Kiểm tra ngày trả
            String ngayTraHienThi = "Chưa đặt";
            if (pt.getNgayTraDuKien() != null) {
                ngayTraHienThi = sdf.format(pt.getNgayTraDuKien());
            }
            
            // Kiểm tra ngày thuê tránh NullPointerException
            String ngayThueHienThi = "";
            if (pt.getNgayThue() != null) {
                ngayThueHienThi = sdf.format(pt.getNgayThue());
            } else {
                ngayThueHienThi = "Trống/Lỗi DB"; // Cảnh báo nếu DB không có ngày
            }
            
            model.addRow(new Object[]{
                pt.getMaPhieu(),
                pt.getMaKH(),
                ngayThueHienThi, 
                ngayTraHienThi, 
                
                // --- SỬA Ở ĐÂY: Định dạng Tổng tiền VNĐ ---
                String.format("%,.0f VNĐ", pt.getTongTien()),
                
                pt.getTrangThai()
            });
        }
        autoResizeColumn(tblPhieuThue);
    }
   private void loadChiTietToTable(String maPhieu) {
        tableModelChiTiet.setRowCount(0);
        List<ChiTietPhieuThue> list = phieuThueDAO.selectChiTietByMaPhieu(maPhieu);
        for (ChiTietPhieuThue ct : list) {
            String tenThietBi = "Không xác định";
            if ("May".equalsIgnoreCase(ct.getLoaiThue()) || "Máy".equalsIgnoreCase(ct.getLoaiThue())) {
                MayGame mg = mayGameDAO.selectById(ct.getMaThietBi());
                if (mg != null) tenThietBi = mg.getTenMay();
            } else if ("Dia".equalsIgnoreCase(ct.getLoaiThue()) || "Đĩa".equalsIgnoreCase(ct.getLoaiThue())) {
                DiaGame dg = diaGameDAO.selectById(ct.getMaThietBi());
                if (dg != null) tenThietBi = dg.getTenGame();
            }

            tableModelChiTiet.addRow(new Object[]{
                ct.getMaPhieu(),
                ct.getLoaiThue(),
                ct.getMaThietBi(),
                tenThietBi, // Hiển thị tên thiết bị vào cột mới
                ct.getSoLuongHoacGio(),
                String.format("%,.0f VNĐ", ct.getThanhTien())
            });
        }
        autoResizeColumn(tblChiTiet);
    }
  private void initEvents() {
    tblPhieuThue.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = tblPhieuThue.getSelectedRow();
            if (row >= 0) {
                String maPhieu = tableModel.getValueAt(row, 0).toString();
                txtMaPhieu.setText(maPhieu);
                txtMaPhieu.setEditable(false); 
                txtMaKH.setText(tableModel.getValueAt(row, 1).toString());
                String loaiThue = cbLoaiThue.getSelectedItem().toString();
                txtMaMay.setText("");
                txtSoGioMay.setText("");
                txtMaDia.setText("");
                txtSoLuongDia.setText("");

                loadChiTietToTable(maPhieu);
                SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                txtNgayTra.setText(sdfDate.format(new Date()));
                tinhTongTien();
            }
        }
    });
    txtNgayTra.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            tinhTongTien();
        }
    });
  }
  // --- HÀM TỰ ĐỘNG TÍNH TỔNG TIỀN ---
private void tinhTongTien() {
        String maPhieu = txtMaPhieu.getText().trim();
        if (maPhieu.isEmpty()) return;

        try {
            double tongTien = 0;
            List<ChiTietPhieuThue> list = phieuThueDAO.selectChiTietByMaPhieu(maPhieu);
            for (ChiTietPhieuThue ct : list) {
                tongTien += ct.getThanhTien();
            }

            String ngayTraStr = txtNgayTra.getText().trim();
            if (!ngayTraStr.isEmpty()) {
                SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                Date ngayTra = sdfDate.parse(ngayTraStr);
                
                int row = tblPhieuThue.getSelectedRow();
                if (row >= 0) {
                    Object objNgayDuKien = tableModel.getValueAt(row, 3);
                    if (objNgayDuKien != null && !objNgayDuKien.toString().equals("Chưa đặt") && !objNgayDuKien.toString().isEmpty()) {
                        Date ngayDuKien = sdfDate.parse(objNgayDuKien.toString());
                        if (ngayTra.after(ngayDuKien)) {
                            long diffInMillies = Math.abs(ngayTra.getTime() - ngayDuKien.getTime());
                            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
                            tongTien += (diffInDays * 20000); 
                        }
                    }
                }
            }
            txtTongTien.setText(String.valueOf(tongTien));
        } catch (Exception e) {
            // Khai báo ngoại lệ khi đang nhập dở ngày tháng
        }
    }
  private void btnThemActionPerformed() {                                        
        String maPhieu = txtMaPhieu.getText().trim();
        String maKH = txtMaKH.getText().trim();

        if (maPhieu.isEmpty() || maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Phiếu và Mã Khách Hàng!");
            return;
        }

        PhieuThue pt = new PhieuThue();
        pt.setMaPhieu(maPhieu);
        pt.setMaKH(maKH);
        
        Date ngayThue = new Date();
        pt.setNgayThue(ngayThue);
        pt.setTrangThai("Đang thuê");
        pt.setTongTien(0);
        
        String ngayTraStr = txtNgayTra.getText().trim();
        if (!ngayTraStr.isEmpty() && !ngayTraStr.equals("Chưa đặt")) {
            try {
                SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                Date ngayTra = sdfDate.parse(ngayTraStr);
                Date ngayThueChuanHoa = sdfDate.parse(sdfDate.format(ngayThue));
                
                if (ngayTra.before(ngayThueChuanHoa)) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Ngày trả không được diễn ra trước Ngày thuê!");
                    return; 
                }
                pt.setNgayTraDuKien(ngayTra);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: Ngày trả phải có định dạng dd/MM/yyyy!");
                return;
            }
        }
        
        if (phieuThueDAO.insert(pt)) {
            boolean coChiTiet = false;
            double tongTienPhieu = 0;

            // 1. Thêm Chi Tiết Máy (Lấy đơn giá từ CSDL)
            String maMay = txtMaMay.getText().trim();
            if (!maMay.isEmpty() && !txtSoGioMay.getText().trim().isEmpty()) {
                try {
                    double soGio = Double.parseDouble(txtSoGioMay.getText().trim());
                    MayGame mg = mayGameDAO.selectById(maMay);
                    if (mg != null) {
                        double giaThue = mg.getGiaThueGio();
                        double thanhTien = soGio * giaThue;
                        
                        ChiTietPhieuThue ctMay = new ChiTietPhieuThue();
                        ctMay.setMaPhieu(maPhieu);
                        ctMay.setLoaiThue("May"); 
                        ctMay.setMaThietBi(maMay);
                        ctMay.setSoLuongHoacGio(soGio);
                        ctMay.setThanhTien(thanhTien);
                        
                        if (phieuThueDAO.insertChiTiet(ctMay)) {
                            coChiTiet = true;
                            tongTienPhieu += thanhTien;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy Mã Máy: " + maMay);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Số giờ máy phải là số!");
                }
            }

            // 2. Thêm Chi Tiết Đĩa (Lấy đơn giá từ CSDL)
            String maDia = txtMaDia.getText().trim();
            if (!maDia.isEmpty() && !txtSoLuongDia.getText().trim().isEmpty()) {
                try {
                    double soLuong = Double.parseDouble(txtSoLuongDia.getText().trim());
                    DiaGame dg = diaGameDAO.selectById(maDia);
                    if (dg != null) {
                        double giaThue = dg.getGiaThueNgay();
                        double thanhTien = soLuong * giaThue;
                        
                        ChiTietPhieuThue ctDia = new ChiTietPhieuThue();
                        ctDia.setMaPhieu(maPhieu);
                        ctDia.setLoaiThue("Dia"); 
                        ctDia.setMaThietBi(maDia);
                        ctDia.setSoLuongHoacGio(soLuong);
                        ctDia.setThanhTien(thanhTien);
                        
                        if (phieuThueDAO.insertChiTiet(ctDia)) {
                            coChiTiet = true;
                            tongTienPhieu += thanhTien;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy Mã Đĩa: " + maDia);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Số lượng đĩa phải là số!");
                }
            }

            // Cập nhật lại tổng tiền vào bảng PhieuThue trong CSDL
            phieuThueDAO.updateTongTien(maPhieu, tongTienPhieu);

            if (coChiTiet) {
                JOptionPane.showMessageDialog(this, "Thêm phiếu thuê và chi tiết thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Đã tạo phiếu nhưng chưa gắn chi tiết thuê.");
            }
            
            loadDataToTable();
            loadChiTietToTable(maPhieu); 
        } else {
            JOptionPane.showMessageDialog(this, "Thêm phiếu thuê thất bại! Mã phiếu có thể đã tồn tại.");
        }
    }

    private void btnSuaActionPerformed() {
        String maPhieu = txtMaPhieu.getText().trim();
        String maKH = txtMaKH.getText().trim();

        if (maPhieu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần sửa!");
            return;
        }

        int row = tblPhieuThue.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu trên bảng!");
            return;
        }

        PhieuThue pt = new PhieuThue();
        pt.setMaPhieu(maPhieu);
        pt.setMaKH(maKH);
        
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            Date ngayThue = new Date(); 
            String ngayThueStr = tblPhieuThue.getValueAt(row, 2).toString();
            if (!ngayThueStr.isEmpty() && !ngayThueStr.equals("Trống/Lỗi DB")) {
                ngayThue = sdfDate.parse(ngayThueStr);
            }
            pt.setNgayThue(ngayThue);
            
            String ngayTraStr = txtNgayTra.getText().trim();
            if (!ngayTraStr.isEmpty() && !ngayTraStr.equals("Chưa đặt")) {
                Date ngayTra = sdfDate.parse(ngayTraStr);
                Date ngayThueChuanHoa = sdfDate.parse(sdfDate.format(ngayThue));
                
                if (ngayTra.before(ngayThueChuanHoa)) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Ngày trả không được diễn ra trước Ngày thuê!");
                    return;
                }
                pt.setNgayTraDuKien(ngayTra);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày tháng (định dạng dd/MM/yyyy)!");
            return;
        }
        
        if (phieuThueDAO.update(pt)) {
            
            // --- SỬA Ở ĐÂY: XỬ LÝ LƯU CHI TIẾT (MÁY/ĐĨA) KHI BẤM SỬA ---
            boolean hasChiTietChanged = false;
            double tongTienHienTai = 0;
            
            // Lấy lại danh sách chi tiết cũ và tính tổng tiền hiện tại
            List<ChiTietPhieuThue> listCT = phieuThueDAO.selectChiTietByMaPhieu(maPhieu);
            for(ChiTietPhieuThue c : listCT) tongTienHienTai += c.getThanhTien();

            // 1. Nếu có nhập Máy -> Lưu chi tiết máy
            String maMay = txtMaMay.getText().trim();
            if (!maMay.isEmpty() && !txtSoGioMay.getText().trim().isEmpty()) {
                try {
                    double soGio = Double.parseDouble(txtSoGioMay.getText().trim());
                    MayGame mg = mayGameDAO.selectById(maMay);
                    if (mg != null) {
                        double thanhTien = soGio * mg.getGiaThueGio();
                        ChiTietPhieuThue ctMay = new ChiTietPhieuThue();
                        ctMay.setMaPhieu(maPhieu); ctMay.setLoaiThue("May"); 
                        ctMay.setMaThietBi(maMay); ctMay.setSoLuongHoacGio(soGio); 
                        ctMay.setThanhTien(thanhTien);
                        
                        if (phieuThueDAO.checkTonTaiChiTiet(maPhieu, maMay)) {
                            // Khấu trừ tiền cũ, cập nhật cái mới
                            for(ChiTietPhieuThue oldCt : listCT) {
                                if(oldCt.getMaThietBi().equals(maMay)) tongTienHienTai -= oldCt.getThanhTien();
                            }
                            phieuThueDAO.updateChiTiet(ctMay);
                        } else {
                            // Thêm chi tiết hoàn toàn mới
                            phieuThueDAO.insertChiTiet(ctMay);
                        }
                        tongTienHienTai += thanhTien;
                        hasChiTietChanged = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy Mã Máy: " + maMay);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Số giờ máy phải là số!");
                }
            }

            // 2. Nếu có nhập Đĩa -> Lưu chi tiết đĩa
            String maDia = txtMaDia.getText().trim();
            if (!maDia.isEmpty() && !txtSoLuongDia.getText().trim().isEmpty()) {
                try {
                    double soLuong = Double.parseDouble(txtSoLuongDia.getText().trim());
                    DiaGame dg = diaGameDAO.selectById(maDia);
                    if (dg != null) {
                        double thanhTien = soLuong * dg.getGiaThueNgay();
                        ChiTietPhieuThue ctDia = new ChiTietPhieuThue();
                        ctDia.setMaPhieu(maPhieu); ctDia.setLoaiThue("Dia"); 
                        ctDia.setMaThietBi(maDia); ctDia.setSoLuongHoacGio(soLuong); 
                        ctDia.setThanhTien(thanhTien);
                        
                        if (phieuThueDAO.checkTonTaiChiTiet(maPhieu, maDia)) {
                            for(ChiTietPhieuThue oldCt : listCT) {
                                if(oldCt.getMaThietBi().equals(maDia)) tongTienHienTai -= oldCt.getThanhTien();
                            }
                            phieuThueDAO.updateChiTiet(ctDia);
                        } else {
                            phieuThueDAO.insertChiTiet(ctDia);
                        }
                        tongTienHienTai += thanhTien;
                        hasChiTietChanged = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy Mã Đĩa: " + maDia);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Số lượng đĩa phải là số!");
                }
            }

            // Lưu lại tổng tiền cập nhật vào bảng PhieuThue
            if (hasChiTietChanged) {
                phieuThueDAO.updateTongTien(maPhieu, tongTienHienTai);
            }

            JOptionPane.showMessageDialog(this, "Sửa thông tin phiếu thuê thành công!");
            btnLamMoiActionPerformed();
            loadDataToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Sửa thất bại! Vui lòng kiểm tra lại dữ liệu.");
        }
    }
    private void btnXoaActionPerformed() {
        String maPhieu = txtMaPhieu.getText().trim();
        if (maPhieu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc nhập Mã Phiếu cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phiếu này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (phieuThueDAO.delete(maPhieu)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                btnLamMoiActionPerformed();
                loadDataToTable();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }
   private void btnThanhToanActionPerformed() {
    String maPhieu = txtMaPhieu.getText().trim();
    if (maPhieu.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu thuê cần thanh toán!");
        return;
    }
    
    String ngayTraStr = txtNgayTra.getText().trim();
    if (ngayTraStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày Trả (dd/MM/yyyy)!");
        return;
    }

    try {
        tinhTongTien(); 
        
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        Date ngayTra = sdfDate.parse(ngayTraStr);
        double tongTien = Double.parseDouble(txtTongTien.getText().trim());

        // HIỂN THỊ POPUP XÁC NHẬN TRƯỚC KHI THANH TOÁN
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận thanh toán phiếu " + maPhieu + " với số tiền: " + String.format("%,.0f VNĐ", tongTien) + "?", 
            "Xác nhận thanh toán", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Người dùng chọn YES -> Tiến hành lưu CSDL
            if (phieuThueDAO.thanhToanPhieuThue(maPhieu, ngayTra, tongTien)) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công phiếu: " + maPhieu);
                
                btnLamMoiActionPerformed(null); 
                loadDataToTable(); 
                tableModelChiTiet.setRowCount(0);
                
                // (Tùy chọn) Mở luôn form Báo cáo doanh thu sau khi thanh toán xong
                // new BaoCaoDoanhThuForm().setVisible(true); 
                
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại! Lỗi hệ thống.");
            }
        }
    } catch (Exception e) {
         JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày Trả đúng định dạng dd/MM/yyyy.");
    }
}
    private void btnLamMoiActionPerformed() {
        txtMaPhieu.setText("");
        txtMaPhieu.setEditable(true);
        txtMaKH.setText("");
        String loaiThue = cbLoaiThue.getSelectedItem().toString();
        txtMaMay.setText("");
        txtSoGioMay.setText("");
        txtMaDia.setText("");
        txtSoLuongDia.setText("");
        tblPhieuThue.clearSelection();
        tableModelChiTiet.setRowCount(0);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnQuayLai = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMaPhieu = new javax.swing.JTextField();
        txtMaKH = new javax.swing.JTextField();
        txtSoLuongDia = new javax.swing.JTextField();
        txtMaMay = new javax.swing.JTextField();
        txtSoGioMay = new javax.swing.JTextField();
        txtMaDia = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPhieuThue = new javax.swing.JTable();
        btnLamMoi = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChiTiet = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtNgayTra = new javax.swing.JTextField();
        btnThanhToan = new javax.swing.JButton();
        txtTongTien = new javax.swing.JTextField();
        cbLoaiThue = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QUẢN LÝ PHIẾU THUÊ");

        btnQuayLai.setText("Quay lại");
        btnQuayLai.addActionListener(this::btnQuayLaiActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Thông tin phiếu thuê");

        jLabel2.setText("Mã Phiếu");

        jLabel3.setText("Mã Khách Hàng");

        jLabel4.setText("Mã Máy Thuê");

        jLabel5.setText("Số Giờ Thuê Máy");

        jLabel6.setText("Mã Đĩa Thuê");

        jLabel7.setText("Số Giờ Thuê Đĩa");

        txtSoGioMay.addActionListener(this::txtSoGioMayActionPerformed);

        txtMaDia.addActionListener(this::txtMaDiaActionPerformed);

        tblPhieuThue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Phiếu", "Mã Khách Hàng", "Ngày Thuê", "Ngày Trả", "Tổng Tiền", "Trạng Thái"
            }
        ));
        jScrollPane1.setViewportView(tblPhieuThue);

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(this::btnLamMoiActionPerformed);

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        btnSua.setText("Sửa");
        btnSua.addActionListener(this::btnSuaActionPerformed);

        tblChiTiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Phiếu", "Loại Thuê", "Mã Thiết Bị", "Tên Máy/Game", "Giờ thuê", "Thành Tiền"
            }
        ));
        jScrollPane2.setViewportView(tblChiTiet);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Danh sách phiếu thuê");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Danh sách chi tiết");

        jLabel10.setText("Loại Thuê");

        btnThem.setText("Thêm");
        btnThem.addActionListener(this::btnThemActionPerformed);

        jLabel11.setText("Ngày trả");

        btnThanhToan.setText("Thanh toán");
        btnThanhToan.addActionListener(this::btnThanhToanActionPerformed);

        cbLoaiThue.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Máy và Đĩa", "Máy", "Đĩa", " " }));
        cbLoaiThue.addActionListener(this::cbLoaiThueActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMaPhieu, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                            .addComponent(txtMaKH))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMaMay, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                            .addComponent(cbLoaiThue, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSoGioMay, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addComponent(txtMaDia, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addComponent(txtSoLuongDia, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addComponent(txtNgayTra)
                    .addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnThem)
                .addGap(18, 18, 18)
                .addComponent(btnSua)
                .addGap(18, 18, 18)
                .addComponent(btnXoa)
                .addGap(18, 18, 18)
                .addComponent(btnThanhToan)
                .addGap(18, 18, 18)
                .addComponent(btnLamMoi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnQuayLai))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(232, 232, 232)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(223, 223, 223)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSoGioMay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6)
                    .addComponent(txtMaDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSoLuongDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(cbLoaiThue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtNgayTra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaMay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa)
                    .addComponent(btnLamMoi)
                    .addComponent(btnQuayLai)
                    .addComponent(btnThanhToan))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnQuayLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuayLaiActionPerformed
        btnQuayLai.addActionListener(e -> {
    new TrangChuForm().setVisible(true); // Mở lại Trang Chủ
    this.dispose(); // Đóng Form Máy Game hiện tại
});
    }//GEN-LAST:event_btnQuayLaiActionPerformed

    private void txtSoGioMayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoGioMayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoGioMayActionPerformed

    private void txtMaDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaDiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaDiaActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        btnSuaActionPerformed();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        btnXoaActionPerformed();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        btnLamMoiActionPerformed();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
       btnThemActionPerformed(); 
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        btnThanhToanActionPerformed(); 
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void cbLoaiThueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLoaiThueActionPerformed
        String selected = cbLoaiThue.getSelectedItem().toString().trim();
    
    if (selected.equals("Máy")) {
        // Chỉ mở ô Máy, khóa ô Đĩa
        txtMaMay.setEnabled(true);
        txtSoGioMay.setEnabled(true);
        txtMaDia.setEnabled(false);
        txtSoLuongDia.setEnabled(false);
        txtMaDia.setText(""); // Xóa dữ liệu tồn dư
        txtSoLuongDia.setText("");
    } 
    else if (selected.equals("Đĩa")) {
        // Chỉ mở ô Đĩa, khóa ô Máy
        txtMaMay.setEnabled(false);
        txtSoGioMay.setEnabled(false);
        txtMaMay.setText("");
        txtSoGioMay.setText("");
        txtMaDia.setEnabled(true);
        txtSoLuongDia.setEnabled(true);
    } 
    else {
        // Mở cả 2 khi chọn "Máy và Đĩa"
        txtMaMay.setEnabled(true);
        txtSoGioMay.setEnabled(true);
        txtMaDia.setEnabled(true);
        txtSoLuongDia.setEnabled(true);
    }
    }//GEN-LAST:event_cbLoaiThueActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new PhieuThueForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnQuayLai;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cbLoaiThue;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblChiTiet;
    private javax.swing.JTable tblPhieuThue;
    private javax.swing.JTextField txtMaDia;
    private javax.swing.JTextField txtMaKH;
    private javax.swing.JTextField txtMaMay;
    private javax.swing.JTextField txtMaPhieu;
    private javax.swing.JTextField txtNgayTra;
    private javax.swing.JTextField txtSoGioMay;
    private javax.swing.JTextField txtSoLuongDia;
    private javax.swing.JTextField txtTongTien;
    // End of variables declaration//GEN-END:variables
}
