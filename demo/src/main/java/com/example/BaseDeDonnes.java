package com.example;
import java.sql.*;
import java.util.*;
public class BaseDeDonnes {
    private static Vector<Etudiant> Students=new Vector<>();
    private static Vector<Administrateur> Admins=new Vector<>();
    public static Vector<String> SubjectsColumnNames = new Vector<>();
    public static Vector<String> GradesCoefColumnNames=new Vector<>();
    private static Connection conn = BaseDeDonnes.connectToStudentsDB("students", "postgres", "0000");
    
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
                Students.add(new Etudiant(rs.getString("prenom"), rs.getString("nom"), rs.getString("date_de_naissance"), rs.getInt("code_etudiant"),rs.getString("pwd"),getStudentGrades(rs.getInt("code_etudiant"))));
            }

            Etudiant.Subjects.clear();
            Etudiant.Subjects=getSubjectsNames();
            GradesCoefColumnNames.clear();
            GradesCoefColumnNames=getGradesCoefNames();
            Etudiant.GradesCoef.clear();
            Etudiant.GradesCoef=getGradesCoef();
            
            for(Etudiant student : BaseDeDonnes.getStudents()){
                student.Grades.clear();
                student.Grades=BaseDeDonnes.getStudentGrades(student.getCode());
            }

            query="SELECT * FROM admins_list";
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                Admins.add(new Administrateur(rs.getString("prenom"), rs.getString("nom"), rs.getString("date_de_naissance"), rs.getInt("code_admin"),rs.getString("pwd")));
            }
        }catch (Exception e){
            System.out.println("updateDB");
            System.out.println(e);
        }
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
            System.out.println("connectToStudentsDB");
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
            System.out.println("insertStudentsRow");
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
            System.out.println("deleteStudentByCode");
            System.out.println(e);
        }
    }

    public static void update_student_info(int Code,String newFirstName,String newLastName,String newBirthDate){
        updateStudentInfo(Code,newFirstName, newLastName, newBirthDate);
        updateDB();
    }
    private static void updateStudentInfo(int Code,String newFirstName,String newLastName,String newBirthDate){
        Statement statement;
        try{
            String query="UPDATE students_list set prenom='" + newFirstName + "', nom='" + newLastName + "', date_de_naissance='" + newBirthDate + "' where code_etudiant='"+Code+"'";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data updated");
        }catch (Exception e){
            System.out.println("updateStudentsInfo");
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
            System.out.println("insertAdminsRow");
            System.out.println(e);
        }
    }

    
    public static void initDataBase() {
        String[] firstNames = {"Mohamed", "Aya", "Youssef", "Fatma", "Ahmed", "Mariem", "Anis", "Ines", "Hassan", "Amel", "Mehdi", "Safa", "Oussama", "Rania", "Ali", "Amina", "Maher", "Samar", "Marwen", "Wafa", "Nizar", "Hiba", "Rami", "Nour", "Mounir", "Sabrine", "Tarek", "Nadia", "Bilel", "Amani", "Hamza", "Asma", "Walid", "Sonia", "Fares", "Rym"};
        String[] lastNames = {"Ben Ali", "Ben Youssef", "Khelifa", "Gharbi", "Saidi", "Gabsi", "Mabrouk", "Khadhraoui", "Ghazi", "Nasri", "Ammar", "Belhaj", "Salem", "Nouira", "Bouazizi", "Masmoudi", "Ben Amor", "Trabelsi", "El Abed", "Hajji", "Ben Mansour", "Ayadi", "Ben Mahmoud", "Mekki", "Hajjem", "Kefi", "Slimani", "Letaief", "Guediche", "Bouslama", "Bouzid", "Mejri", "Bchir", "Boujelbane", "Boujelbene", "Lahmar", "Tlili", "Khazri", "Makhloufi", "Jouini", "Rahmani", "Ben Hassine", "Kallel", "Brahmi", "Farhat"};
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String birthDate = String.format("%02d", random.nextInt(31) + 1) + "/" + String.format("%02d", random.nextInt(12) + 1) + "/" + (random.nextInt(26) + 1980);
            add_student(firstName, lastName, birthDate);
        }
        insertAdminsRow("Driss", "Guezguez", "01/01/1950");
        insertAdminsRow("Alice", "Smith", "02/02/1945");
    }

    public static void insertColumn(String label,Float fixed) {
        Statement statement;
        try {
            String query = "ALTER TABLE students_list ADD COLUMN " + replaceSpacesWithUnderscores(label) + " DOUBLE PRECISION DEFAULT "+fixed;
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Column " + replaceSpacesWithUnderscores(label) + " added to students_list table.");
        } catch (Exception e) {
            System.out.println("insertColumn");
            System.out.println(e);
        }
        updateDB();
    }
    public static String replaceSpacesWithUnderscores(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ') {
                result.append('_');
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    public static void listColumns(String tableName) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

            System.out.println("Columns of table " + tableName + ":");
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                System.out.println(columnName);
            }
        } catch (Exception e) {
            System.out.println("listColumns");
            System.out.println(e);
        }
    }
    public static void deleteColumn(String columnName) {
        Statement statement;
        try {
            String query = "ALTER TABLE students_list DROP COLUMN " + columnName;
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Column " + columnName + " deleted from students_list table.");
        } catch (Exception e) {
            System.out.println("deleteColumn");
            System.out.println(e);
        }
        updateDB();
    }
    public static Vector<Float> getStudentGrades(int Code){
        Vector<Float> Grades = new Vector<>();
        Statement statement;
        ResultSet rs=null;
        try{
            String query="SELECT * FROM students_list WHERE code_etudiant='"+Code+"'";
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                for (String element : SubjectsColumnNames) {
                    Grades.add(rs.getFloat(element));
                }
            }
        }catch (Exception e) {
            System.out.println("getStudentGrades ERROR:");
            System.out.println(e);
        }
        return Grades;
    }
    public static Vector<String> getSubjectsNames(){
        Vector<String> Subjects = new Vector<>();
        try{
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, "students_list", null);

            int currentColumn = 1;
            while (resultSet.next()) {
                if (currentColumn >= 6) {
                    Subjects.add(resultSet.getString("COLUMN_NAME"));
                    resultSet.next();
                }
                currentColumn++;
            }
        }catch (Exception e) {
            System.out.println("getSubjectsName ERROR:");
            System.out.println(e);
        }
        return Subjects;
    }
    public static Vector<String> getGradesCoefNames(){
        Vector<String> GradesCoefNames = new Vector<>();
        try{
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, "students_list", null);

            int currentColumn = 1;
            while (resultSet.next()) {
                if (currentColumn >= 7) {
                    GradesCoefNames.add(resultSet.getString("COLUMN_NAME"));
                    resultSet.next();
                }
                currentColumn++;
            }
        }catch (Exception e) {
            System.out.println("getSubjectsName ERROR:");
            System.out.println(e);
        }
        return GradesCoefNames;
    }

    public static Vector<Float> getGradesCoef(){
        Vector<Float> defaultValues = new Vector<>();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            for (String label : GradesCoefColumnNames) {
                ResultSet resultSet = metaData.getColumns(null, null, "students_list", label);

                if (resultSet.next()) {
                    String defaultValue = resultSet.getString("COLUMN_DEF");
                    if (defaultValue != null) {
                        defaultValues.add(Float.parseFloat(defaultValue));
                    } else {
                        defaultValues.add(null);
                    }
                } else {
                    defaultValues.add(null);
                }
            }
        } catch (SQLException e) {
            System.out.println("getDefaultValues");
            System.out.println(e);
        }
        return defaultValues;
    }
    
    public static void updateGrade(int Code,String Subject,float Grade){
        Statement statement;
        try{
            String query="UPDATE students_list set "+ Subject+"='"+Grade+"' where code_etudiant='"+Code+"'";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    /*private static Vector<Etudiant> Students=new Vector<>();
    private static Vector<Administrateur> Admins=new Vector<>();
    private static int StudentCodeIndex=1000000;
    private static int AdminsCodeIndex=9000000;

    public static void initDataBase() {
        String[] firstNames = {"Mohamed", "Aya", "Youssef", "Fatma", "Ahmed", "Mariem", "Anis", "Ines", "Hassan", "Amel", "Mehdi", "Safa", "Oussama", "Rania", "Ali", "Amina", "Maher", "Samar", "Marwen", "Wafa", "Nizar", "Hiba", "Rami", "Nour", "Mounir", "Sabrine", "Tarek", "Nadia", "Bilel", "Amani", "Hamza", "Asma", "Walid", "Sonia", "Fares", "Rym"};
        String[] lastNames = {"Ben Ali", "Ben Youssef", "Khelifa", "Gharbi", "Saidi", "Gabsi", "Mabrouk", "Khadhraoui", "Ghazi", "Nasri", "Ammar", "Belhaj", "Salem", "Nouira", "Bouazizi", "Masmoudi", "Ben Amor", "Trabelsi", "El Abed", "Hajji", "Ben Mansour", "Ayadi", "Ben Mahmoud", "Mekki", "Hajjem", "Kefi", "Slimani", "Letaief", "Guediche", "Bouslama", "Bouzid", "Mejri", "Bchir", "Boujelbane", "Boujelbene", "Lahmar", "Tlili", "Khazri", "Makhloufi", "Jouini", "Rahmani", "Ben Hassine", "Kallel", "Brahmi", "Farhat"};
        Students.add(new Etudiant("Seif", "Boudokhane", "01/01/2000", 1234567));
        Students.get(0).setPassword("user");
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String birthDate = String.format("%02d", random.nextInt(31) + 1) + "/" + String.format("%02d", random.nextInt(12) + 1) + "/" + (random.nextInt(26) + 1980);
            Students.add(new Etudiant(firstName, lastName, birthDate, StudentCodeIndex));
            StudentCodeIndex++;
        }
        Admins.add(new Administrateur("Driss", "Guezguez", "01/01/1950", AdminsCodeIndex));
        AdminsCodeIndex++;
        Admins.get(0).setPassword("admin");
        Admins.add(new Administrateur("Alice", "Smith", "02/02/1945", AdminsCodeIndex));
        AdminsCodeIndex++;
        Admins.get(1).setPassword("1");
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
    
    public static void add_student(String firstName,String lastName,String birthDate){
        Students.add(new Etudiant(capitalizeName(firstName), capitalizeName(lastName), birthDate, StudentCodeIndex));
            StudentCodeIndex++;
    }
    public static void delete_student(int studentCode){
        for (int i = 0; i < Students.size(); i++) {
            if (Students.get(i).getCode() == studentCode) {
                Students.remove(i);
                break;
            }
        }
    }
    

    private static String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }*/
}
