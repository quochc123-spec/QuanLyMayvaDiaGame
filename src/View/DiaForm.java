/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import DAO.DiaGameDAO;
import model.DiaGame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
/**
 *
 * @author hovin
 */
public class DiaForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DiaForm.class.getName());
    
    private DiaGameDAO diaGameDAO;
    private DefaultTableModel tableModel;
    /**
     * Creates new form DiaForm
     */
    public DiaForm() {
        initComponents();
        
        diaGameDAO = new DiaGameDAO();
        setLocationRelativeTo(null); // Căn giữa màn hình
        
        // Khởi tạo model cho JTable
        tableModel = (DefaultTableModel) tblDiaGame.getModel();
        
        // Nạp dữ liệu từ CSDL lên bảng
        loadDataToTable();

        // Đăng ký các sự kiện nút bấm
        initEvents();
    }
    
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<DiaGame> list = diaGameDAO.selectAll();
        for (DiaGame dg : list) {
            tableModel.addRow(new Object[]{
                dg.getMaDia(),
                dg.getTenGame(),
                dg.getTheLoai(),
                String.format("%,.0f VNĐ", dg.getGiaThueNgay()),
                dg.getSoLuongTon()
            });
        }
    }
    
    private void initEvents() {
        // Sự kiện click dòng trên bảng đưa dữ liệu lên ô nhập
        tblDiaGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblDiaGame.getSelectedRow();
                if (row >= 0) {
                    txtMaDia.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaDia.setEditable(false); // Khóa mã đĩa khi đang chọn sửa
                    txtTenGame.setText(tableModel.getValueAt(row, 1).toString());
                    cbTheLoai.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    
                    String giaStr = tableModel.getValueAt(row, 3).toString().replaceAll("[^0-9]", "");
                    txtGiaThue.setText(giaStr);
                    
                    txtSoLuongTon.setText(tableModel.getValueAt(row, 4).toString());
                }
            }
        });
    }
    //Clear dữ liệu trong Form
    private void clearForm() {
        txtMaDia.setText("");
        txtMaDia.setEditable(true);
        txtTenGame.setText("");
        txtGiaThue.setText("");
        txtSoLuongTon.setText("");
        cbTheLoai.setSelectedIndex(0);
        tblDiaGame.clearSelection();
    }
    //Thêm đĩa game
    private void btnThemActionPerformed() {
        try {
            DiaGame dg = new DiaGame(
                txtMaDia.getText().trim(),
                txtTenGame.getText().trim(),
                cbTheLoai.getSelectedItem().toString(),
                Double.parseDouble(txtGiaThue.getText().trim()),
                Integer.parseInt(txtSoLuongTon.getText().trim())
            );

            if (diaGameDAO.insert(dg)) {
                JOptionPane.showMessageDialog(this, "Thêm đĩa game thành công!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã đĩa)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá thuê và số lượng tồn phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Sửa đĩa game
    private void btnSuaActionPerformed() {
    int selectedRow = tblDiaGame.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn đĩa game cần sửa từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        String maDia = txtMaDia.getText().trim();
        String tenGame = txtTenGame.getText().trim();
        String theLoai = cbTheLoai.getSelectedItem().toString();
        double giaThue = Double.parseDouble(txtGiaThue.getText().trim());
        int soLuongTon = Integer.parseInt(txtSoLuongTon.getText().trim());

        if (tenGame.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên game không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tạo đối tượng DiaGame cập nhật
        DiaGame dg = new DiaGame(maDia, tenGame, theLoai, giaThue, soLuongTon);

        // Gọi DAO để cập nhật vào SQL Server
        if (diaGameDAO.update(dg)) {
            JOptionPane.showMessageDialog(this, "Cập nhật đĩa game thành công!");
            loadDataToTable(); // Tải lại bảng
            clearForm();       // Xóa trắng form
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Giá thuê và số lượng tồn phải là định dạng số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
    //Xóa đĩa game
    private void btnXoaActionPerformed() {
    int selectedRow = tblDiaGame.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn đĩa game cần xóa từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maDia = tableModel.getValueAt(selectedRow, 0).toString();
    String tenGame = tableModel.getValueAt(selectedRow, 1).toString();

    // Hộp thoại xác nhận xóa
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Bạn có chắc chắn muốn xóa đĩa game '" + tenGame + "' (Mã: " + maDia + ") không?", 
        "Xác nhận xóa", 
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (confirm == JOptionPane.YES_OPTION) {
        if (diaGameDAO.delete(maDia)) {
            JOptionPane.showMessageDialog(this, "Xóa đĩa game thành công!");
            loadDataToTable(); // Tải lại bảng
            clearForm();       // Xóa trắng form
        } else {
            JOptionPane.showMessageDialog(
                this, 
                "Xóa thất bại! Đĩa game này có thể đang nằm trong danh sách Phiếu Thuê chưa thanh toán.", 
                "Lỗi ràng buộc CSDL", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
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
        txtMaDia = new javax.swing.JTextField();
        txtTenGame = new javax.swing.JTextField();
        cbTheLoai = new javax.swing.JComboBox<>();
        txtGiaThue = new javax.swing.JTextField();
        txtSoLuongTon = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDiaGame = new javax.swing.JTable();
        btnSua = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QUẢN LÝ ĐĨA GAME");

        btnQuayLai.setText("Quay lại");
        btnQuayLai.addActionListener(this::btnQuayLaiActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Thông tin đĩa game");

        jLabel2.setText("Mã Đĩa:");

        jLabel3.setText("Tên Game:");

        jLabel4.setText("Thể Loại:");

        jLabel5.setText("Giá thuê:");

        jLabel6.setText("Số lượng:");

        cbTheLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hành động", "Sinh tồn ", "Giả lập", "Phiêu lưu", "Thế giới mở", "Góc nhìn thứ nhất", "Góc nhìn thứ ba", "Bắn súng", "Kinh dị", "Platformer", "Cổ điển", "Metroidvania", "Nhiều người chơi", "Nhịp điệu", "Thẻ bài", " " }));
        cbTheLoai.addActionListener(this::cbTheLoaiActionPerformed);

        tblDiaGame.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Đĩa", "Tên Game", "Thể Loại", "Giá Thuê", "Số Lượng"
            }
        ));
        jScrollPane1.setViewportView(tblDiaGame);

        btnSua.setText("Sửa");
        btnSua.addActionListener(this::btnSuaActionPerformed);

        btnThem.setText("Thêm");
        btnThem.addActionListener(this::btnThemActionPerformed);

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(this::btnLamMoiActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnThem)
                        .addGap(42, 42, 42)
                        .addComponent(btnSua)
                        .addGap(27, 27, 27)
                        .addComponent(btnXoa)
                        .addGap(41, 41, 41)
                        .addComponent(btnLamMoi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnQuayLai))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSoLuongTon, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtMaDia)
                                .addComponent(txtTenGame)
                                .addComponent(cbTheLoai, 0, 213, Short.MAX_VALUE))
                            .addComponent(txtGiaThue, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(279, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(268, 268, 268))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTenGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbTheLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGiaThue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSoLuongTon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnThem)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnQuayLai)
                        .addComponent(btnLamMoi)
                        .addComponent(btnSua)
                        .addComponent(btnXoa)))
                .addGap(55, 55, 55))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnQuayLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuayLaiActionPerformed
        btnQuayLai.addActionListener(e -> {
    new MayVaDiaForm().setVisible(true); 
    });
    }//GEN-LAST:event_btnQuayLaiActionPerformed

    private void cbTheLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTheLoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTheLoaiActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
       btnSuaActionPerformed(); 
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        btnThemActionPerformed(); 
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        btnXoaActionPerformed(); 
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        btnLamMoi.addActionListener(e -> clearForm());
    }//GEN-LAST:event_btnLamMoiActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new DiaForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnQuayLai;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cbTheLoai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblDiaGame;
    private javax.swing.JTextField txtGiaThue;
    private javax.swing.JTextField txtMaDia;
    private javax.swing.JTextField txtSoLuongTon;
    private javax.swing.JTextField txtTenGame;
    // End of variables declaration//GEN-END:variables

}
