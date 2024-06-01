package com.example;
import java.util.*;
public class Etudiant extends Personne{
    public static Vector<String> Subjects = new Vector<>();
    public static Vector<Float> GradesCoef=new Vector<>();
    public Vector<Float> Grades=new Vector<>();

    public Etudiant(String First_name,String Last_name,String Birth_Date,int Code,String Password,Vector<Float> Grades){
        super(First_name, Last_name, Birth_Date, Code,Password);
        this.Grades=Grades;
    }
    public Etudiant(String First_name,String Last_name,String Birth_Date,int Code,String Password){
        super(First_name, Last_name, Birth_Date, Code,Password);
    }
    public String toString() {
        return "Etudiant{" +
                "First name='" + getFirst_name() + '\'' +
                ", Last name='" + getLast_name() + '\'' +
                ", Birth Date='" + getBirth_Date() + '\'' +
                ", Code=" + getCode() +
                ", Password='" + getPassword() + '\'' +
                '}';
    }
    
    public static void addSubject(String subject,float coef){
        if(!Subjects.contains(subject)){
            Subjects.add(subject);
            GradesCoef.add(coef);
            BaseDeDonnes.insertColumn(subject, 0.0f);
            BaseDeDonnes.insertColumn(subject+"_coef", coef);
        }
    }
    public static void removeSubject(String subject){
        if(Subjects.contains(subject)){
            BaseDeDonnes.deleteColumn(subject);
            BaseDeDonnes.deleteColumn(subject+"_coef");
        }
    }

    private void updateGradesSize() {
        int newSize = Subjects.size();
        while (Grades.size() < newSize) {
            Grades.add(0.0f);
        }
    }
    public void setGrade(int index,float grade){
        updateGradesSize();
        Grades.set(index, grade);
        BaseDeDonnes.updateGrade(getCode(), Etudiant.getSubject(index), grade);

    }
    public float getGrade(String subject){
        updateGradesSize();
        return Grades.get(Subjects.indexOf(subject));
    }
    public static String getSubject(int index) {
        return Subjects.get(index);
    }

    public static Vector<String> getSubjects() {
        return Subjects;
    }
    public Vector<Float> getGrades() {
        return Grades;
    }
    public float get_total_coefficients(){
        float totalCoef=0;
        for(int i=0;i<GradesCoef.size();i++){
            totalCoef+=GradesCoef.get(i);
        }
        return totalCoef;
    }
    public float get_overall_average(){
        float average=0;
        for(int i=0;i<Grades.size();i++){
            average+=(Grades.get(i)*GradesCoef.get(i));
        }
        average=average/get_total_coefficients();
        return(average);
    }
}