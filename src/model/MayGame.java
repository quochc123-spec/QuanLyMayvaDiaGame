/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author hovin
 */
public class MayGame {
    private String maMay;
    private String tenMay;
    private String loaiMay;
    private double giaThueGio;
    private String trangThai;

    public MayGame() {
    }

    public MayGame(String maMay, String tenMay, String loaiMay, double giaThueGio, String trangThai) {
        this.maMay = maMay;
        this.tenMay = tenMay;
        this.loaiMay = loaiMay;
        this.giaThueGio = giaThueGio;
        this.trangThai = trangThai;
    }

    public String getMaMay() { return maMay; }
    public void setMaMay(String maMay) { this.maMay = maMay; }

    public String getTenMay() { return tenMay; }
    public void setTenMay(String tenMay) { this.tenMay = tenMay; }

    public String getLoaiMay() { return loaiMay; }
    public void setLoaiMay(String loaiMay) { this.loaiMay = loaiMay; }

    public double getGiaThueGio() { return giaThueGio; }
    public void setGiaThueGio(double giaThueGio) { this.giaThueGio = giaThueGio; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenMay + " (" + loaiMay + ")";
    }
}
