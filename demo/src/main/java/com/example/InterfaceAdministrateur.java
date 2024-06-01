package com.example;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.*;


public class InterfaceAdministrateur {
    private Administrateur admin;
    private JFrame frame;
    private JMenuBar menuBar=new JMenuBar();
    private JButton selectAllButton;
    private boolean allSelected = false;
    private void updateSelectAllButtonText(JTable table) {
        int selectedRowCount = table.getSelectedRowCount();
        int totalRowCount = table.getRowCount();
        
        if (selectedRowCount == totalRowCount) {
            selectAllButton.setText("☑");
            allSelected = true;
        } else {
            selectAllButton.setText("◻");
            allSelected = false;
        }
    }
    
    
    public InterfaceAdministrateur(Administrateur admin){
        this.admin=admin;
        displayFrame(admin.getFirst_name(),admin.getLast_name());
        initMenuBar();
        panelHome();
    }
    private void displayFrame(String admin_First_Name,String admin_Last_Name){
        frame = new JFrame(admin_First_Name+" "+admin_Last_Name+" (Administrateur)");
        ImageIcon image=new ImageIcon("issatso.png");
        frame.setIconImage(image.getImage());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
        frame.setSize(600,700);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void confirmExit() {
        String[] optionsOuiNon = {"Oui", "Non"};
        int result = JOptionPane.showOptionDialog(frame,
            "Voulez-vous vraiment quitter ?",
            "Confirmer la sortie",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            optionsOuiNon,
            optionsOuiNon[1]);

        if (result == JOptionPane.YES_OPTION) {
            frame.dispose();
            System.exit(0);
        }
    }
    private MouseListener tableMouseListenerRight = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                JTable source = (JTable) e.getSource();
                int row = source.rowAtPoint(e.getPoint());
                int column = source.columnAtPoint(e.getPoint());
                Object value = source.getValueAt(row, column);
                String text = value != null ? value.toString() : "";
                StringSelection stringSelection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        }
    };
    
    private void panelSubjects() {
        frame.getContentPane().removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        Vector<String> subjects = Etudiant.getSubjects();
        Vector<Float> coefficients = Etudiant.GradesCoef;
        int nmbSubjects = subjects.size();
        String[] columnNames = {" ", "Nom Matière", "Coefficient"};
        Object[][] data = new Object[nmbSubjects][3];
        
        for (int i = 0; i < nmbSubjects; i++) {
            data[i][0] = i + 1;
            data[i][1] = subjects.get(i);
            data[i][2] = coefficients.get(i);
        }
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setMinWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.addMouseListener(tableMouseListenerRight);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton addSubjectButton = new JButton("Ajouter une matière");
        addSubjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(showAddSubjectDialog(frame)){
                    panelSubjects();
                }
            }
        });
        buttonPanel.add(addSubjectButton);
        JButton removeSubjectButton = new JButton("Supprimer une matière");
        removeSubjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    String[] optionsOuiNon = {"Oui", "Non"};
                    int confirmDelete = JOptionPane.showOptionDialog(frame,
                        "Voulez-vous vraiment supprimer les " + selectedRows.length + " matières sélectionnées ?",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        optionsOuiNon,
                        optionsOuiNon[1]);

                    if (confirmDelete == JOptionPane.YES_OPTION) {
                        for (int row : selectedRows) {
                            String subjectToRemove = (String) table.getValueAt(row, 1);
                            Etudiant.removeSubject(subjectToRemove);
                        }
                        panelSubjects();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Veuillez sélectionner une ou plusieurs matières à supprimer.", "Aucune matière sélectionnée", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(removeSubjectButton);

        selectAllButton = new JButton("◻");
        selectAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!allSelected) {
                    table.selectAll();
                } else {
                    table.clearSelection();
                }
                allSelected = !allSelected;
                updateSelectAllButtonText(table);
            }
        });
            
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateSelectAllButtonText(table);
            }
        });

        buttonPanel.add(selectAllButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }
    private boolean showAddSubjectDialog(JFrame parentFrame) {
        boolean inputError = false;
        do {
            JPanel dialogPanel = new JPanel(new GridLayout(2, 2));
            JTextField subjectNameField = new JTextField();
            JTextField coefficientField = new JTextField();
            dialogPanel.add(new JLabel("Nom de la matière:"));
            dialogPanel.add(subjectNameField);
            dialogPanel.add(new JLabel("Coefficient (min 0.25, max 5):"));
            dialogPanel.add(coefficientField);
            String[] optionsOKCancel = {"OK", "Annuler"};
int result = JOptionPane.showOptionDialog(parentFrame,
    dialogPanel,
    "Ajouter une nouvelle matière",
    JOptionPane.OK_CANCEL_OPTION,
    JOptionPane.PLAIN_MESSAGE,
    null,
    optionsOKCancel,
    optionsOKCancel[1]);

            if (result == JOptionPane.OK_OPTION) {
                String subjectName = subjectNameField.getText();
                String coefficientText = coefficientField.getText();
                if (subjectName.isEmpty()) {
                    displayError("Erreur de nom de la matière", "Le nom de la matière ne peut pas être vide.");
                    inputError = true;
                } else {
                    try {
                        float coefficient = Float.parseFloat(coefficientText);
                        if (coefficient >= 0.25 && coefficient <= 5) {
                            Etudiant.addSubject(subjectName, coefficient);
                            return true;
                        } else {
                            displayError("Erreur de coefficient", "Le coefficient doit être compris entre 0,25 et 5.");
                            inputError = true;
                        }
                    } catch (NumberFormatException ex) {
                        displayError("Erreur de coefficient", "Le coefficient doit être un nombre.");
                        inputError = true;
                    }
                }
            } else {
                return false;
            }
        } while (inputError);
        return false;
    }
    private void panelStudents() {
        frame.getContentPane().removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        Vector<Etudiant> sortedStudents = new Vector<>(BaseDeDonnes.getStudentsSortedByName());
        int nmbStudents=sortedStudents.size();
        String[] columnNames = {" ","Nom", "Prénom", "Date Naissance", "Code Étudiant","Mot de Pass", "Moyenne"};
        Object[][] data = new Object[nmbStudents][7];
        
        for (int i = 0; i < nmbStudents; i++) {
            data[i][0] = i+1;
            data[i][1] = sortedStudents.get(i).getLast_name();
            data[i][2] = sortedStudents.get(i).getFirst_name();
            data[i][3] = sortedStudents.get(i).getBirth_Date();
            data[i][4] = sortedStudents.get(i).getCode();
            data[i][5] = sortedStudents.get(i).getPassword();
            data[i][6] = sortedStudents.get(i).get_overall_average();
        }
            
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); 
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setMinWidth(80);
        table.getColumnModel().getColumn(6).setMaxWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.addMouseListener(tableMouseListenerRight);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, 80));

        selectAllButton = new JButton("◻");
        selectAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!allSelected) {
                    table.selectAll();
                } else {
                    table.clearSelection();
                }
                allSelected = !allSelected;
                updateSelectAllButtonText(table);
            }
        });
            
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateSelectAllButtonText(table);
            }
        });

        buttonPanel.add(selectAllButton);
        
        JButton removeStudentButton = new JButton("Supprimer un étudiant");
        removeStudentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    String[] optionsOuiNon = {"Oui", "Non"};
                    int confirmDelete = JOptionPane.showOptionDialog(frame,
                        "Voulez-vous vraiment supprimer les " + selectedRows.length + " étudiants sélectionnés ?",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        optionsOuiNon,
                        optionsOuiNon[1]);

                    if (confirmDelete == JOptionPane.YES_OPTION) {
                        for (int row : selectedRows) {
                            int studentToRemove = (int) table.getValueAt(row, 4);
                            BaseDeDonnes.delete_student(studentToRemove);
                        }
                        panelStudents();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un ou plusieurs étudiants à supprimer.", "Aucun étudiant sélectionné", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(removeStudentButton);

        JButton addStudentButton = new JButton("Ajouter un étudiant");
        addStudentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(showAddStudentDialog(frame)){
                    panelStudents();
                }
            }
        });
        buttonPanel.add(addStudentButton);
        
        JButton openStudentAccount = new JButton("Ouvrir compte étudiant");
        openStudentAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    String[] optionsOuiNon = {"Oui", "Non"};
                    int confirmOpen = JOptionPane.showOptionDialog(frame,
                        "Voulez-vous vraiment Ouvrir les compte de " + selectedRows.length + " étudiants sélectionnés ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        optionsOuiNon,
                        optionsOuiNon[1]);

                    if (confirmOpen == JOptionPane.YES_OPTION) {
                        for (int row : selectedRows) {
                            int studentCode = (int) table.getValueAt(row, 4);
                            new InterfaceUtilisateur(BaseDeDonnes.getStudentByCode(studentCode),true);
                        }
                        panelStudents();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un ou plusieurs étudiants à supprimer.", "Aucun étudiant sélectionné", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(openStudentAccount);

        JButton refresh = new JButton("⟳");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelStudents();
            }
        });
        buttonPanel.add(refresh);

        JButton exportButton = new JButton("Exporter au format CSV");
        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] options = {"données dans la table actuelle", "données personnelles des étudiants sélectionnés"};
                int choice = JOptionPane.showOptionDialog(frame,
                        "Quelles données souhaitez-vous exporter ?",
                        "Options d'exportation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (choice == JOptionPane.YES_OPTION) {
                    exportStudentsInfoToCSV(table);
                }else{
                    int[] selectedRows = table.getSelectedRows();
                    if (selectedRows.length > 0) {
                        Object[] optionsOuiNon = {"Oui", "Non"};
                        int confirmExport = JOptionPane.showOptionDialog(frame,
                            "Voulez-vous exporter l'information de " + selectedRows.length + " étudiants sélectionnés ?",
                            "Confirmation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            optionsOuiNon,
                            optionsOuiNon[0]);

                        if (confirmExport == JOptionPane.YES_OPTION) {
                            exportEachSelectedStudentToCSV(table);
                            panelStudents();
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un ou plusieurs étudiants à supprimer.", "Aucun étudiant sélectionné", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        buttonPanel.add(exportButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }
    private boolean showAddStudentDialog(JFrame parentFrame) {
        boolean inputError = false;
        do {
            JPanel dialogPanel = new JPanel(new GridLayout(3, 2));
            JTextField firstNameField = new JTextField();
            JTextField lastNameField = new JTextField();
            JTextField birthDayField = new JTextField();
            dialogPanel.add(new JLabel("Prénom:"));
            dialogPanel.add(firstNameField);
            dialogPanel.add(new JLabel("Nom:"));
            dialogPanel.add(lastNameField);
            dialogPanel.add(new JLabel("Date de Naissance:"));
            dialogPanel.add(birthDayField);
            String[] optionsOuiNon = {"OK", "Annuler"};
            int result = JOptionPane.showOptionDialog(parentFrame,
                dialogPanel,
                "Ajouter un étudiant",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                optionsOuiNon,
                optionsOuiNon[1]);

            if (result == JOptionPane.OK_OPTION) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String birthDay = birthDayField.getText();
                if (!Personne.isValidName(lastName)) {
                    displayError("Erreur de syntaxe dans le nom", "Nom invalide. Veuillez entrer un nom valide.");
                    inputError = true;
                } else if (!Personne.isValidName(firstName)) {
                    displayError("Erreur de syntaxe dans le prénom", "Prénom invalide. Veuillez entrer un prénom valide.");
                    inputError = true;
                } else if (!Personne.isValidDate(birthDay)) {
                    displayError("Erreur de syntaxe dans la date", "Date invalide. Veuillez entrer une date valide dans le format jj/MM/aaaa.");
                    inputError = true;
                } else {
                    BaseDeDonnes.add_student(firstName, lastName, birthDay);
                    return true;
                }
            } else {
                return false;
            }
        } while (inputError);
        return false;
    }
    private void exportStudentsInfoToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter au format CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("students_info.csv"));
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
                for (int i = 0; i < table.getColumnCount(); i++) {
                    writer.append(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) {
                        writer.append(",");
                    } else {
                        writer.append("\n");
                    }
                }
    
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        writer.append(table.getValueAt(i, j).toString());
                        if (j < table.getColumnCount() - 1) {
                            writer.append(",");
                        } else {
                            writer.append("\n");
                        }
                    }
                }
    
                writer.flush();
                JOptionPane.showMessageDialog(frame, "Informations sur les étudiants exportées avec succès vers un fichier CSV.", "Export réussi", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de l'exportation des informations sur les étudiants vers un fichier CSV.", "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void exportEachSelectedStudentToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter au format CSV");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(frame);
    
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File directoryToSave = fileChooser.getSelectedFile();
            
            int[] selectedRows = table.getSelectedRows();
    
            for (int row : selectedRows) {
                String fileName =  table.getValueAt(row, 4)+"-"+formatName((String)table.getValueAt(row, 1))+"-"+formatName((String)table.getValueAt(row, 2)) + ".csv";
                File fileToSave = new File(directoryToSave, fileName);
    
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writeStudentInfoToCSV(writer, BaseDeDonnes.getStudentByCode((int) table.getValueAt(row, 4)));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur lors de l'exportation des informations vers un fichier CSV.", "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
                }
            }
    
            JOptionPane.showMessageDialog(frame, "Toutes les lignes ont été exportées avec succès vers des fichiers CSV.", "Export réussi", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void writeStudentInfoToCSV(FileWriter writer,Etudiant student) throws IOException {
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
    }
    public static String formatName(String name) {
        String[] parts = name.split(" ");
        String formattedName = String.join("-", parts);
        return formattedName;
    }
    private void exportFinalResultsToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter au format CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("Affichage.csv"));
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
                for (int i = 0; i < table.getColumnCount(); i++) {
                    writer.append(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) {
                        writer.append(",");
                    } else {
                        writer.append("\n");
                    }
                }
    
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        writer.append(table.getValueAt(i, j).toString());
                        if (j < table.getColumnCount() - 1) {
                            writer.append(",");
                        } else {
                            writer.append("\n");
                        }
                    }
                }
    
                writer.flush();
                JOptionPane.showMessageDialog(frame, "Affichage des Notes exportées avec succès vers un fichier CSV.", "Export réussi", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de l'exportation d'affichage des Notes étudiants vers un fichier CSV.", "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    private void panelStudentsAverages() {
        frame.getContentPane().removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        Vector<Etudiant> sortedStudents = BaseDeDonnes.getStudentsOrderedByOverallAverage();
        int nmbStudents = sortedStudents.size();
        String[] columnNames = {" ", "Nom", "Prénom", "Moyenne"};
        Object[][] data = new Object[nmbStudents][4];
        for (int i = 0; i < nmbStudents; i++) {
            data[i][0] = i + 1;
            data[i][1] = sortedStudents.get(i).getLast_name();
            data[i][2] = sortedStudents.get(i).getFirst_name();
            data[i][3] = sortedStudents.get(i).get_overall_average();
        }
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setMinWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.addMouseListener(tableMouseListenerRight);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        JButton exportButton = new JButton("Exporter au format CSV");
        exportButton.addActionListener(e -> exportFinalResultsToCSV(table));
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }
    private void panelHome(){
        frame.getContentPane().removeAll();
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel nameLabel = createStyledLabel("Nom: " + admin.getLast_name());
        JLabel firstNameLabel = createStyledLabel("Prénom: " + admin.getFirst_name());
        JLabel birthDateLabel = createStyledLabel("Date de Naissance: " + admin.getBirth_Date());
        JLabel adminCodLabel = createStyledLabel("Code Administrateur: " + admin.getCode());
        panel.add(nameLabel);
        panel.add(firstNameLabel);
        panel.add(birthDateLabel);
        panel.add(adminCodLabel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.getContentPane().add(scrollPane);
        scrollPane.revalidate();
        scrollPane.repaint();
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint(); 
        frame.repaint();
    }
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return label;
    }
    private void initMenuBar(){
        JMenu menu=new JMenu("Menu");
        JMenuItem home=new JMenuItem("informations personnelles");
        JMenuItem subjects=new JMenuItem("Liste Matiére");
        JMenuItem studentsList=new JMenuItem("Liste Étudiants");
        JMenuItem overallAverageList=new JMenuItem("Liste Étudiants par Moyenne");
        JMenuItem logout=new JMenuItem("Se déconnecter");
        home.addActionListener(e->panelHome());
        subjects.addActionListener(e->panelSubjects());
        studentsList.addActionListener(e->panelStudents());
        overallAverageList.addActionListener(e->panelStudentsAverages());
        logout.addActionListener(e->endSession());
        menu.add(home);
        menu.add(subjects);
        menu.add(studentsList);
        menu.add(overallAverageList);
        menu.add(logout);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }
    private void endSession(){
        String[] optionsOuiNon = {"Ok", "Annuler"};
        int choice = JOptionPane.showOptionDialog(null,
            "Êtes-vous sûr de vouloir vous déconnecter",
            "DÉCONNEXION",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            optionsOuiNon,
            optionsOuiNon[1]);
                    if(choice==0){
                frame.dispose();
                new InterfaceLogin();
            }
    }
    private void displayError(String errorTitle,String errorText){
        JOptionPane.showMessageDialog(null, errorText, errorTitle, JOptionPane.ERROR_MESSAGE);
    }
}
