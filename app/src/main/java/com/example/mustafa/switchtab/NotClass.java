package com.example.mustafa.switchtab;

public class NotClass extends TakvimClass{
    private String notBaslik;
    private String notIcerik;
    private String notSahibi;
    private String notHedefi;
    private String notResmi;

    NotClass(){
        notBaslik=null;
        notIcerik=null;
        notSahibi=FirstActivity.autoLogin.getString("username",null);
        notHedefi=null;
        notResmi=null;
    }

    NotClass(String notBaslik,String notIcerik, String notSahibi, String notHedefi, String notResmi){
        this.notBaslik=notBaslik;
        this.notIcerik=notIcerik;
        this.notSahibi=notSahibi;
        this.notHedefi=notHedefi;
        this.notResmi=notResmi;
    }

    public void setNotBaslik(String notBaslik){
        this.notBaslik=notBaslik;
    }
    public String getNotBaslik(){return notBaslik;}

    public void setNotIcerik(String notIcerik){
        this.notIcerik=notIcerik;
    }
    public String getNotIcerik(){return notIcerik;}

    public void setNotSahibi(String notSahibi){
        this.notSahibi=notSahibi;
    }
    public String getNotSahibi(){return notSahibi;}

    public void setNotHedefi(String notHedefi){
        this.notHedefi=notHedefi;
    }
    public String getNotHedefi(){return notHedefi;}

    public void setNotResmi(String notResmi){
        this.notResmi=notResmi;
    }
    public String getNotResmi(){return notResmi;}
}
