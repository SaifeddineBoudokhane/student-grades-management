package com.example;
import java.text.*;
import java.util.*;

public class Personne {
    private String First_name;
    private String Last_name;
    private String Birth_Date;
    private int Code;
    private String Password;
    
    public Personne(String First_name,String Last_name,String Birth_Date,int Code,String Password){
        this.First_name=First_name;
        this.Last_name=Last_name;
        this.Birth_Date=Birth_Date;
        this.Code=Code;
        this.Password=Password;
    }
    public Personne(String First_name,String Last_name,String Birth_Date,int Code){
        this.First_name=First_name;
        this.Last_name=Last_name;
        this.Birth_Date=Birth_Date;
        this.Code=Code;
        this.Password=generateRandomPassword();
    }
    public void setFirst_name(String First_name) {
        this.First_name = First_name;
    }
    public void setLast_name(String Last_name) {
        this.Last_name = Last_name;
    }
    public void setBirth_Date(String Birth_Date) {
        this.Birth_Date = Birth_Date;
    }
    public void setCode(int Code) {
        this.Code = Code;
    }
    public void setPassword(String Password){
        this.Password=Password;
    }
    public String getFirst_name() {
        return First_name;
    }
    public String getLast_name() {
        return Last_name;
    }
    public String getBirth_Date() {
        return Birth_Date;
    }
    public int getCode() {
        return Code;
    }
    public String getPassword() {
        return Password;
    }
    public static String generateRandomPassword() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(allowedChars.length());
            password.append(allowedChars.charAt(index));
        }
        return password.toString();
    }
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (char ch : name.toCharArray()) {
            if (!Character.isLetter(ch) && ch != ' ' && ch != '-') {
                return false;
            }
        }
        return true;
    }
    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); 

        try {Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
    
            int year = cal.get(Calendar.YEAR);
            if (year < 1900 || year > 2100) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
