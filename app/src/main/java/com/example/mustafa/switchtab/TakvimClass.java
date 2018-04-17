package com.example.mustafa.switchtab;

import java.util.Calendar;

public class TakvimClass {

    // !!! Aylar -1 Olarak Yazılıyor | Yani Ocak == 0 Şubak == 1 Mart == 2 ... Aralık == 11 Gibi
    // Alarm İşlemlerinde Buradaki "takvim" Değişkenini Kullanacağız
    private Calendar takvim;
    private Calendar anlikTarih;

    // Bu Değişkenleri Sadece Yazdırma İşlemlerinde Kullanacağız
    private int yil;
    private int ay;
    private int gun;
    private int saat;
    private int dakika;

    TakvimClass(){
        takvim=Calendar.getInstance();
        takvim.set(Calendar.SECOND, 0);
        takvim.set(Calendar.MILLISECOND, 0);

        yil=takvim.get(Calendar.YEAR);
        ay=takvim.get(Calendar.MONTH);
        gun=takvim.get(Calendar.DAY_OF_MONTH);
        saat=takvim.get(Calendar.HOUR_OF_DAY);
        dakika=takvim.get(Calendar.MINUTE);
    }

    public void setYil(int yil){
        this.yil=yil;
        takvim.set(Calendar.YEAR, yil);
    }
    public int getYil(){
        return yil;
    }

    public void setAy(int ay){
        this.ay=ay;
        takvim.set(Calendar.MONTH, ay);
    }
    public int getAy(){
        return ay;
    }

    public void setGun(int gun){
        this.gun=gun;
        takvim.set(Calendar.DAY_OF_MONTH, gun);
    }
    public int getGun(){
        return gun;
    }

    public void setSaat(int saat){
        this.saat=saat;
        takvim.set(Calendar.HOUR_OF_DAY, saat);
    }
    public int getSaat(){
        return saat;
    }

    public void setDakika(int dakika){
        this.dakika=dakika;
        takvim.set(Calendar.MINUTE, dakika);
    }
    public int getDakika(){
        return dakika;
    }

    public void setTakvim(int yil, int ay, int gun, int saat, int dakika){
        this.yil=yil;
        this.ay=ay;
        this.gun=gun;
        this.saat=saat;
        this.dakika=dakika;

        takvim.set(Calendar.YEAR, yil);
        takvim.set(Calendar.MONTH, ay);
        takvim.set(Calendar.DAY_OF_MONTH, gun);
        takvim.set(Calendar.HOUR_OF_DAY, saat);
        takvim.set(Calendar.MINUTE, dakika);
    }
    public Calendar getTakvim(){
        return takvim;
    }

    public String getStringEklendigiTarih(){
        anlikTarih = Calendar.getInstance();
        return anlikTarih.get(Calendar.YEAR)+"-"
                +(anlikTarih.get(Calendar.MONTH)+1)+"-"
                +anlikTarih.get(Calendar.DAY_OF_MONTH)
                +" "+anlikTarih.get(Calendar.HOUR_OF_DAY)+":"
                +anlikTarih.get(Calendar.MINUTE)+":"
                +anlikTarih.get(Calendar.SECOND)+":"
                +anlikTarih.get(Calendar.MILLISECOND);
    }

}
