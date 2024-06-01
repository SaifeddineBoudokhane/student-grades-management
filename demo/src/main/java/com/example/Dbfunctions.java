package com.example;
import java.sql.*;
import java.util.*;

public class Dbfunctions {
    private static Vector<Etudiant> Students=new Vector<>();
    private static Vector<Administrateur> Admins=new Vector<>();
    private static Connection conn = Dbfunctions.connectToStudentsDB("students", "postgres", "0000");
    
    public static void updateDB(){
        Students.clear();
        Admins.clear();
        Statement statement;
        ResultSet rs=null;
        try {
            String query="SELECT * FROM students_list";
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                Students.add(new Etudiant(rs.getString("prenom"), rs.getString("nom"), rs.getString("date_de_naissance"), rs.getInt("code_etudiant"),rs.getString("pwd")));
            }
            query="SELECT * FROM admins_list";
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                Admins.add(new Administrateur(rs.getString("prenom"), rs.getString("nom"), rs.getString("date_de_naissance"), rs.getInt("code_etudiant"),rs.getString("pwd")));
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
    
    public Dbfunctions(){
        updateDB();
    }
    
    public static Etudiant getStudent(int index){
        return Students.get(index);
    }
    public static Etudiant getStudentByCode(int studentCode) {
        for (Etudiant student : Students) {
            if (student.getCode() == studentCode) {
                return student;
            }
        }
        return null;
    }
    public static Administrateur getAdmin(int index){
        return Admins.get(index);
    }
    public static Vector<Etudiant> getStudents(){
        return Students;
    }
    public static Vector<Etudiant> getStudentsSortedByName() {
        Vector<Etudiant> sortedStudents = new Vector<>(Students);
        sortedStudents.sort((student1, student2) -> {
            int lastNameComparison = student1.getLast_name().compareTo(student2.getLast_name());
            if (lastNameComparison != 0) {
                return lastNameComparison;
            }
            return student1.getFirst_name().compareTo(student2.getFirst_name());
        });
        return sortedStudents;
    }

    public static Vector<Etudiant> getStudentsOrderedByOverallAverage() {
        Vector<Etudiant> students = BaseDeDonnes.getStudents();
        Collections.sort(students, new Comparator<Etudiant>() {
            @Override
            public int compare(Etudiant student1, Etudiant student2) {
                return Double.compare(student1.get_overall_average(), student2.get_overall_average());
            }
        });
        Collections.reverse(students);
        return students;
    }
    public static Vector<Administrateur> getAdmins(){
        return Admins;
    }
    public static int Check_login_student(int Code,String student_PWD){
        int index=-1;
        for(int i=0;i<Students.size();i++){
            if((Students.get(i)).getCode()==Code){
                index=i;
                break;
            }
        }
        if (index!=-1 && student_PWD.equals(Students.get(index).getPassword())) {
            return index;
        }
        return (-1);
    }

    public static int Check_login_admin(int Code,String admin_PWD){
        int index=-1;
        for(int i=0;i<Admins.size();i++){
            if((Admins.get(i)).getCode()==Code){
                index=i;
                break;
            }
        }
        if (index!=-1 && admin_PWD.equals(Admins.get(index).getPassword())) {
            return index;
        }
        return (-1);
    }
    
    public static Connection connectToStudentsDB(String dbname, String user, String pass){
        Connection conn=null;
        try{
            Class.forName("org.postgresql.Driver");
            conn=DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("connected");
            }else{
            System.out.println("Not connected");
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return conn;
    }
    
    public static void add_student(String firstName,String lastName,String birthDate){
        insertStudentsRow(capitalizeName(firstName), capitalizeName(lastName), birthDate);
        updateDB();
    }
    private static String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
    private static void insertStudentsRow(String firstName,String lastName,String birthDate){
        Statement statement;
        try {
            String query = "INSERT INTO students_list (nom, prenom, date_de_naissance, pwd) VALUES ('"+ lastName + "', '" + firstName + "', '" + birthDate + "', '" + Personne.generateRandomPassword() + "')";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row student inserted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void delete_student(int studentCode){
        deleteStudentByCode(studentCode);
        updateDB();
    }
    private static void deleteStudentByCode(int Code){
        Statement statement;
        try{
            String query="DELETE FROM students_list WHERE code_etudiant='"+Code+"'";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Student Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void update_student_info(String newFirstName,String newLastName,String newBirthDate){
        update_student_info(newFirstName, newLastName, newBirthDate);
        updateDB();
    }
    public void updateStudentInfo(int Code,String newFirstName,String newLastName,String newBirthDate){
        Statement statement;
        try{
            String query="UPDATE students_list set prenom='" + newFirstName + "', nom='" + newLastName + "', date_de_naissance='" + newBirthDate + "' where code_etudiant='"+Code+"'";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    
    public static void add_admin(String firstName,String lastName,String birthDate){
        insertAdminsRow(capitalizeName(firstName), capitalizeName(lastName), birthDate);
        updateDB();
    }
    private static void insertAdminsRow(String firstName,String lastName,String birthDate){
        Statement statement;
        try {
            String query = "INSERT INTO admins_list (nom, prenom, date_de_naissance, pwd) VALUES ('"+ lastName + "', '" + firstName + "', '" + birthDate + "', '" + Personne.generateRandomPassword() + "')";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row admin inserted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /*public void readStudentsData(){
        Statement statement;
        ResultSet rs=null;
        try {
            String query="SELECT * FROM students_list";
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                System.out.println(rs.getString("code_etudiant")+" ");
                System.out.println(rs.getString("nom")+" ");
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void updatePrenom(int Code,String prenom){
        Statement statement;
        try{
            String query="UPDATE students_list set prenom='"+prenom+"' where code_etudiant='"+Code+"'";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void searchByCode(int Code){
        Statement statement;
        ResultSet rs=null;
        try{
            String query="SELECT * FROM students_list WHERE code_etudiant='"+Code+"'";
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                System.out.println(rs.getInt("code_etudiant")+" ");
                System.out.println(rs.getString("nom")+" ");
                System.out.println(rs.getString("prenom")+" ");
                System.out.println(rs.getString("date_de_naissance")+" ");
                System.out.println(rs.getString("pwd")+" ");
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }*/
}