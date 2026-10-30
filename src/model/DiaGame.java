/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author hovin
 */
public class DiaGame {
    private String maDia;
    private String tenGame;
    private String theLoai;
    private double giaThueNgay;
    private int soLuongTon;

    public DiaGame() {
    }

    public DiaGame(String maDia, String tenGame, String theLoai, double giaThueNgay, int soLuongTon) {
        this.maDia = maDia;
        this.tenGame = tenGame;
        this.theLoai = theLoai;
        this.giaThueNgay = giaThueNgay;
        this.soLuongTon = soLuongTon;
    }

    public String getMaDia() { return maDia; }
    public void setMaDia(String maDia) { this.maDia = maDia; }

    public String getTenGame() { return tenGame; }
    public void setTenGame(String tenGame) { this.tenGame = tenGame; }

    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }

    public double getGiaThueNgay() { return giaThueNgay; }
    public void setGiaThueNgay(double giaThueNgay) { this.giaThueNgay = giaThueNgay; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    @Override
    public String toString() {
        return tenGame;
    }
}
