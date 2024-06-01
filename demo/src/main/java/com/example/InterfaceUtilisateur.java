package com.example;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Vector;
import java.io.*;
import javax.swing.filechooser.*;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class InterfaceUtilisateur {
    private Etudiant student;
    private boolean sessionAdmin;
    private JFrame frame;
    private JMenuBar menuBar=new JMenuBar();
    private Vector<JFormattedTextField> Grades_Fields = new Vector<>();


    public InterfaceUtilisateur(Etudiant student,boolean sessionAdmin){
        this.student=student;
        this.sessionAdmin=sessionAdmin;
        displayFrame(student.getFirst_name(),student.getLast_name());
        initMenuBar();
        panelHome();
    }
    private void displayFrame(String student_First_Name,String student_Last_Name){
        frame = new JFrame(student_First_Name+" "+student_Last_Name+" (Étudiant)");
        ImageIcon image=new ImageIcon("issatso.png");
        frame.setIconImage(image.getImage());
        if(!sessionAdmin){
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    confirmExit();
                }
            });
        }
        frame.setSize(600,700);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(frame, "Voulez-vous vraiment quitter ?", "Confirmer la sortie",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            frame.dispose();
            System.exit(0);
        }
    }
    private void panelHome(){
        frame.getContentPane().removeAll();
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel nameLabel = createStyledLabel("Nom: " + student.getLast_name());
        JLabel firstNameLabel = createStyledLabel("Prénom: " + student.getFirst_name());
        JLabel birthDateLabel = createStyledLabel("Date de Naissance: " + student.getBirth_Date());
        JLabel studentCodeLabel = createStyledLabel("Code Etudiant: " + student.getCode());
        JLabel overallAverageLabel = createStyledLabel("Moyenne Générale: " + student.get_overall_average());
        panel.add(nameLabel);
        panel.add(firstNameLabel);
        panel.add(birthDateLabel);
        panel.add(studentCodeLabel);
        panel.add(overallAverageLabel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.getContentPane().add(scrollPane);
        
        JPanel buttonPanel = new JPanel();
        JButton exportButton = new JButton("Exporter au format CSV");
        exportButton.addActionListener(e -> exportGradesToCSV());
        buttonPanel.add(exportButton);
        if(sessionAdmin){
            JButton submitButton = new JButton("Modifier les informations");
            submitButton.setFocusable(false);
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(showEditDialog(frame)){
                        panelHome();
                    }
                }
            });
            buttonPanel.add(submitButton);
        }
            frame.add(buttonPanel, BorderLayout.SOUTH);
        scrollPane.revalidate();
        scrollPane.repaint();
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint(); 
        frame.repaint();
    }
    
    private boolean showEditDialog(JFrame parentFrame) {
        boolean inputError = false;
        do {
            JPanel dialogPanel = new JPanel(new GridLayout(3, 2));
            JTextField firstNameField = new JTextField(student.getFirst_name());
            JTextField lastNameField = new JTextField(student.getLast_name());
            JTextField birthDayField = new JTextField(student.getBirth_Date());
            dialogPanel.add(new JLabel("Prénom:"));
            dialogPanel.add(firstNameField);
            dialogPanel.add(new JLabel("Nom:"));
            dialogPanel.add(lastNameField);
            dialogPanel.add(new JLabel("Date de Naissance:"));
            dialogPanel.add(birthDayField);
            int result = JOptionPane.showConfirmDialog(parentFrame, dialogPanel, "Ajouter un étudiant",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String newFirstName = firstNameField.getText();
                String newLastName = lastNameField.getText();
                String newBirthDate = birthDayField.getText();
                if (!Personne.isValidName(newLastName)) {
                    displayError("Erreur de syntaxe dans le nom", "Nom invalide. Veuillez entrer un nom valide.");
                    inputError = true;
                } else if (!Personne.isValidName(newFirstName)) {
                    displayError("Erreur de syntaxe dans le prénom", "Prénom invalide. Veuillez entrer un prénom valide.");
                    inputError = true;
                } else if (!Personne.isValidDate(newBirthDate)) {
                    displayError("Erreur de syntaxe dans la date", "Date invalide. Veuillez entrer une date valide dans le format jj/MM/aaaa.");
                    inputError = true;
                } else {
                    student.setFirst_name(newFirstName);
                    student.setLast_name(newLastName);
                    student.setBirth_Date(newBirthDate);
                    BaseDeDonnes.update_student_info(student.getCode(),newFirstName, newLastName, newBirthDate);
                    panelHome();
                    return true;
                }
            } else {
                return false;
            }
        } while (inputError);
        return false;
    }
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT); 
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); 
        return label;
    }
    private void panelGrades() {
        frame.getContentPane().removeAll();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Grades_Fields.clear();
        Box verticalBox = Box.createVerticalBox();
        JLabel overallAverageLabel = createStyledLabel("Moyenne Générale: " + student.get_overall_average());
        verticalBox.add(overallAverageLabel);
        for (int i = 0; i < Etudiant.getSubjects().size(); i++) {
            JPanel subjectPanel = new JPanel();
            subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.X_AXIS));
            JLabel nameLabel = createStyledLabel(Etudiant.getSubject(i));
            subjectPanel.add(nameLabel);
            subjectPanel.add(Box.createHorizontalGlue());
            addGradeInput(subjectPanel, student.getGrade(Etudiant.getSubject(i)));
            subjectPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 100)); 
            verticalBox.add(subjectPanel);
        }
        JScrollPane scrollPane = new JScrollPane(verticalBox);
        panel.add(scrollPane);
        JButton exportButton = new JButton("Exporter au format CSV");
        exportButton.addActionListener(e -> exportGradesToCSV());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exportButton);
        frame.getContentPane().add(panel);
        if(sessionAdmin){
            JButton submitButton = new JButton("Soumettre");
            submitButton.setFocusable(false);
            submitButton.addActionListener(e -> updateGrades());
            buttonPanel.add(submitButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        panel.add(buttonPanel, BorderLayout.SOUTH);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(20);
        verticalScrollBar.setBlockIncrement(100);
        frame.revalidate();
        frame.repaint();
    }
    public static void exportInfoToCSV(Etudiant student){

    }
    private void exportGradesToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter au format CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(student.getFirst_name() + "-" + student.getLast_name() + "-Grades.csv"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setAcceptAllFileFilterUsed(false);
    
        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
    
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.append("Prénom:,").append(student.getFirst_name()).append("\n");
                writer.append("Nom:,").append(student.getLast_name()).append("\n");
                writer.append("Date de Naissance:,").append(student.getBirth_Date()).append("\n");
                writer.append("Code Etudiant:,").append(String.valueOf(student.getCode())).append("\n\n");
                writer.append("Subject,Grade\n");
                for (int i = 0; i < Etudiant.getSubjects().size(); i++) {
                    String subject = Etudiant.getSubject(i);
                    float grade = student.getGrade(Etudiant.getSubject(i));
                    writer.append(subject).append(",").append(String.valueOf(grade)).append("\n");
                }
                writer.append('\n');
                writer.append("Moyenne Générale:").append(",").append(String.valueOf(student.get_overall_average())).append("\n");
    
                writer.flush();
                JOptionPane.showMessageDialog(frame, "Informations sur l'étudiants exportées avec succès vers un fichier CSV.", "Export réussi", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de l'exportation des informations sur l'étudiants vers un fichier CSV.", "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void initMenuBar(){
        JMenu menu=new JMenu("Menu");
        JMenuItem home=new JMenuItem("informations personnelles");
        JMenuItem grades=new JMenuItem("Notes");
        JMenuItem logout=new JMenuItem("Se déconnecter");
        home.addActionListener(e->panelHome());
        grades.addActionListener(e->panelGrades());
        logout.addActionListener(e->endSession());

        menu.add(home);
        menu.add(grades);
        if(!sessionAdmin)menu.add(logout);

        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }
    

    private void addGradeInput(JPanel panel,float grade){
            DecimalFormat decimalFormat = new DecimalFormat("#0.000");
            decimalFormat.setMinimumFractionDigits(3);
            decimalFormat.setMaximumFractionDigits(3);
            NumberFormatter formatter = new NumberFormatter(decimalFormat);
            formatter.setValueClass(Float.class);
            formatter.setMinimum(0f);
            formatter.setMaximum(20f);
            formatter.setAllowsInvalid(false); 
            formatter.setCommitsOnValidEdit(true); 
            JFormattedTextField field = new JFormattedTextField(formatter);
            field.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    field.selectAll();
                }
            });
            field.setText(String.valueOf(grade));
            field.setColumns(10);
            field.setPreferredSize(new Dimension(100, 30));
            field.setMaximumSize(new Dimension(100, 30));
            field.setEnabled(sessionAdmin);
            
            panel.add(field);
            Grades_Fields.add(field);
    }
    private void endSession(){
        String[] optionsOKCancel = {"Ok", "Annuler"};
        int choice = JOptionPane.showOptionDialog(null,
            "Êtes-vous sûr de vouloir vous déconnecter",
            "DÉCONNEXION",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            optionsOKCancel,
            optionsOKCancel[1]); // Default option ("Non") selected

            if(choice==0){
                frame.dispose();
                new InterfaceLogin();
            }
    }
    private void updateGrades(){
        for (int i = 0; i < Grades_Fields.size(); i++) {
                    JFormattedTextField field = Grades_Fields.get(i);
                    String text = field.getText();
                    student.setGrade(i, Float.valueOf(text));
        }
        panelGrades();
    }
    private void displayError(String errorTitle,String errorText){
        JOptionPane.showMessageDialog(null, errorText, errorTitle, JOptionPane.ERROR_MESSAGE);
    }
}
