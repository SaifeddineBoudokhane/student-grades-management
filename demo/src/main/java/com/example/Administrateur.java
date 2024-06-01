package com.example;
public class Administrateur extends Personne {
    
    public Administrateur(String First_name,String Last_name,String Birth_Date,int Code,String Password){
        super(First_name, Last_name, Birth_Date, Code,Password);
    }
    public String toString() {
        return "Administrateur{" +
                "First name='" + getFirst_name() + '\'' +
                ", Last name='" + getLast_name() + '\'' +
                ", Birth Date='" + getBirth_Date() + '\'' +
                ", Code=" + getCode() +
                ", Password='" + getPassword() + '\'' +
                '}';
    }
}
