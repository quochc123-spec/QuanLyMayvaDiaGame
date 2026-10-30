/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author hovin
 */
public class PhieuThue {
    private String maPhieu;
    private String maKH;
    private Date ngayThue;
    private Date ngayTraDuKien;
    private Date ngayTraThucTe;
    private double tongTien;
    private String trangThai;

    public PhieuThue() {
    }

    public PhieuThue(String maPhieu, String maKH, Date ngayThue, Date ngayTraDuKien, Date ngayTraThucTe, double tongTien, String trangThai) {
        this.maPhieu = maPhieu;
        this.maKH = maKH;
        this.ngayThue = ngayThue;
        this.ngayTraDuKien = ngayTraDuKien;
        this.ngayTraThucTe = ngayTraThucTe;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public Date getNgayThue() { return ngayThue; }
    public void setNgayThue(Date ngayThue) { this.ngayThue = ngayThue; }

    public Date getNgayTraDuKien() { return ngayTraDuKien; }
    public void setNgayTraDuKien(Date ngayTraDuKien) { this.ngayTraDuKien = ngayTraDuKien; }

    public Date getNgayTraThucTe() { return ngayTraThucTe; }
    public void setNgayTraThucTe(Date ngayTraThucTe) { this.ngayTraThucTe = ngayTraThucTe; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
