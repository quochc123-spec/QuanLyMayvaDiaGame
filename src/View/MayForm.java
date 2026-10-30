/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import DAO.MayGameDAO;
import model.MayGame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
/**
 *
 * @author hovin
 */
public class MayForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MayForm.class.getName());
    private MayGameDAO mayGameDAO;
    private DefaultTableModel tableModel;
    /**
     * Creates new form MayForm
     */
    public MayForm() {
        initComponents();
        mayGameDAO = new MayGameDAO();
        setLocationRelativeTo(null); // Căn giữa màn hình
        
        // Lấy model của JTable đã kéo thả
        tableModel = (DefaultTableModel) tblMayGame.getModel();
        
        // Nạp dữ liệu từ SQL Server
        loadDataToTable();

        // Đăng ký toàn bộ sự kiện click
        initEvents();
    }
    // Nạp danh sách máy game lên JTable
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<MayGame> list = mayGameDAO.selectAll();
        for (MayGame mg : list) {
            tableModel.addRow(new Object[]{
                mg.getMaMay(),
                mg.getTenMay(),
                mg.getLoaiMay(),
                String.format("%,.0f VNĐ", mg.getGiaThueGio()),
                mg.getTrangThai()
            });
        }
    }
    // Đăng ký xử lý sự kiện
    private void initEvents() {
        // Click dòng trên bảng -> Đưa dữ liệu lên ô nhập
        tblMayGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblMayGame.getSelectedRow();
                if (row >= 0) {
                    txtMaMay.setText(tableModel.getValueAt(row, 0).toString());
                    txtMaMay.setEditable(false); // Khóa không cho sửa Mã Máy
                    txtTenMay.setText(tableModel.getValueAt(row, 1).toString());
                    cbLoaiMay.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    
                    String giaStr = tableModel.getValueAt(row, 3).toString().replaceAll("[^0-9]", "");
                    txtGiaThue.setText(giaStr);
                    
                    cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });
        }
    // Logic Thêm Máy Game
    private void btnThemActionPerformed() {
        try {
            String maMay = txtMaMay.getText().trim();
            String tenMay = txtTenMay.getText().trim();
            String loaiMay = cbLoaiMay.getSelectedItem().toString();
            String giaStr = txtGiaThue.getText().trim();
            String trangThai = cbTrangThai.getSelectedItem().toString();

            if (maMay.isEmpty() || tenMay.isEmpty() || giaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin máy!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            MayGame mg = new MayGame(maMay, tenMay, loaiMay, Double.parseDouble(giaStr), trangThai);

            if (mayGameDAO.insert(mg)) {
                JOptionPane.showMessageDialog(this, "Thêm máy game thành công!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Mã máy đã tồn tại trong CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá thuê giờ phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Sửa máy
    private void btnSuaActionPerformed() {
        int selectedRow = tblMayGame.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn máy game cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            MayGame mg = new MayGame(
                txtMaMay.getText().trim(),
                txtTenMay.getText().trim(),
                cbLoaiMay.getSelectedItem().toString(),
                Double.parseDouble(txtGiaThue.getText().trim()),
                cbTrangThai.getSelectedItem().toString()
            );

            if (mayGameDAO.update(mg)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá thuê giờ phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Logic Xóa Máy Game
    private void btnXoaActionPerformed() {
        int selectedRow = tblMayGame.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn máy game cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maMay = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
            this, "Bạn có chắc chắn muốn xóa máy " + maMay + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (mayGameDAO.delete(maMay)) {
                JOptionPane.showMessageDialog(this, "Xóa máy thành công!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại! (Máy này đang nằm trong Phiếu Thuê)", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void clearForm() {
        txtMaMay.setText("");
        txtMaMay.setEditable(true);
        txtTenMay.setText("");
        txtGiaThue.setText("");
        cbLoaiMay.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        tblMayGame.clearSelection();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMaMay = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTenMay = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtGiaThue = new javax.swing.JTextField();
        cbLoaiMay = new javax.swing.JComboBox<>();
        cbTrangThai = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMayGame = new javax.swing.JTable();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnQuayLai = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QUẢN LÝ MÁY GAME");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Thông tin máy game");

        jLabel2.setText("Mã Máy");

        jLabel3.setText("Tên Máy");

        jLabel4.setText("Loại Máy");

        jLabel5.setText("Trạng Thái");

        jLabel6.setText("Giá Thuê");

        cbLoaiMay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Console", "Handheld", "VR" }));

        cbTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rảnh", "Đang thuê", "Bảo trì" }));

        tblMayGame.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Máy", "Tên Máy", "Loại Máy", "Giá thuê", "Tình trạng"
            }
        ));
        jScrollPane1.setViewportView(tblMayGame);

        btnThem.setText("Thêm");
        btnThem.addActionListener(this::btnThemActionPerformed);

        btnSua.setText("Sửa");
        btnSua.addActionListener(this::btnSuaActionPerformed);

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(this::btnLamMoiActionPerformed);

        btnQuayLai.setText("Quay lại");
        btnQuayLai.addActionListener(this::btnQuayLaiActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(208, 208, 208))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btnThem)
                            .addGap(28, 28, 28)
                            .addComponent(btnSua)
                            .addGap(28, 28, 28)
                            .addComponent(btnXoa)
                            .addGap(37, 37, 37)
                            .addComponent(btnLamMoi)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnQuayLai))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 691, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtMaMay, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtTenMay, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbLoaiMay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)
                            .addComponent(txtGiaThue, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)
                            .addComponent(cbTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaMay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTenMay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbLoaiMay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGiaThue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa)
                    .addComponent(btnLamMoi)
                    .addComponent(btnQuayLai))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        btnThemActionPerformed(); 
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        btnSuaActionPerformed(); 
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        btnXoaActionPerformed(); 
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        btnLamMoi.addActionListener(e -> clearForm());
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnQuayLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuayLaiActionPerformed
        btnQuayLai.addActionListener(e -> {
    new MayVaDiaForm().setVisible(true); 
    });
    }//GEN-LAST:event_btnQuayLaiActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new MayForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnQuayLai;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cbLoaiMay;
    private javax.swing.JComboBox<String> cbTrangThai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMayGame;
    private javax.swing.JTextField txtGiaThue;
    private javax.swing.JTextField txtMaMay;
    private javax.swing.JTextField txtTenMay;
    // End of variables declaration//GEN-END:variables
}
