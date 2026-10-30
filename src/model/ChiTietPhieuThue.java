/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author hovin
 */
public class ChiTietPhieuThue {
    private int maChiTiet;
    private String maPhieu;
    private String loaiThue; // "MAY" hoặc "DIA"
    private String maThietBi; // Lưu MaMay hoặc MaDia
    private double soLuongHoacGio;
    private double thanhTien;

    public ChiTietPhieuThue() {
    }

    public ChiTietPhieuThue(int maChiTiet, String maPhieu, String loaiThue, String maThietBi, double soLuongHoacGio, double thanhTien) {
        this.maChiTiet = maChiTiet;
        this.maPhieu = maPhieu;
        this.loaiThue = loaiThue;
        this.maThietBi = maThietBi;
        this.soLuongHoacGio = soLuongHoacGio;
        this.thanhTien = thanhTien;
    }

    public int getMaChiTiet() { return maChiTiet; }
    public void setMaChiTiet(int maChiTiet) { this.maChiTiet = maChiTiet; }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public String getLoaiThue() { return loaiThue; }
    public void setLoaiThue(String loaiThue) { this.loaiThue = loaiThue; }

    public String getMaThietBi() { return maThietBi; }
    public void setMaThietBi(String maThietBi) { this.maThietBi = maThietBi; }

    public double getSoLuongHoacGio() { return soLuongHoacGio; }
    public void setSoLuongHoacGio(double soLuongHoacGio) { this.soLuongHoacGio = soLuongHoacGio; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    
}
