package org.javastart.main.Database;

public class ModelUser {
    String Username;
    Integer Waktu;

    public ModelUser(String Username, Integer Waktu){
        this.Username=Username;
        this.Waktu=Waktu;
    }
    public String getUsername() {
        return Username;
    }
    public Integer getWaktu() {
        return Waktu;
    }
}
