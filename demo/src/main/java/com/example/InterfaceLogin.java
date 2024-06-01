package com.example;
import java.awt.event.*;
import java.text.NumberFormat;

import javax.swing.text.*;
import javax.swing.*;

public class InterfaceLogin implements ActionListener {
    private JFrame frame = new JFrame("LOGIN PAGE");
    private JButton loginButton = new JButton("LOGIN");
    private JFormattedTextField  loginField;
    private JTextField passwordField = new JTextField();


    public InterfaceLogin(){
        addText("Code Étudiant/Administrateur :", 200, 20, 200, 30);
        initLoginField();
        addText("Mot De Pass :", 200, 100, 200, 30);
        initPasswordField();
        initButton();
        displayFrame();
    }
    private void addText(String text,int x,int y,int width,int height){
        JLabel label = new JLabel(text);
        label.setBounds(x,y,width,height);
        frame.add(label);
    }
    private void initLoginField(){
        
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMaximum(9999999); 
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);
        loginField = new JFormattedTextField(formatter);
        loginField.setBounds(200, 50, 200, 30);
        frame.add(loginField);
    }

    private void initPasswordField(){
        
        passwordField.setBounds(200, 130, 200, 30);

        frame.add(passwordField);
    }

    private void initButton(){
        loginButton.setBounds(260,200,80,40);
        loginButton.setFocusable(false);
        loginButton.addActionListener(this);

        frame.add(loginButton);
    }

    private void displayFrame(){
        ImageIcon image=new ImageIcon("issatso.png");
        frame.setIconImage(image.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,700);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void displayError(String errorTitle,String errorText){
        JOptionPane.showMessageDialog(null, errorText, errorTitle, JOptionPane.ERROR_MESSAGE);
    }
    public void actionPerformed(ActionEvent event){
        if(event.getSource()==loginButton){
            if (loginField.getText().length()==7&&passwordField.getText().length()>0){
                String login=loginField.getText();
                String pwd=passwordField.getText();
                if(BaseDeDonnes.Check_login_admin(Integer.parseInt(login),pwd)!=-1){
                    frame.dispose();
                    new InterfaceAdministrateur(BaseDeDonnes.getAdmin(BaseDeDonnes.Check_login_admin(Integer.parseInt(login),pwd)));
                }else if(BaseDeDonnes.Check_login_student(Integer.parseInt(login),pwd)!=-1){
                    frame.dispose();
                    new InterfaceUtilisateur(BaseDeDonnes.getStudent(BaseDeDonnes.Check_login_student(Integer.parseInt(login),pwd)),false);
                }else{
                    displayError("ERREUR", "Identifiants incorrects. Veuillez vérifier votre code et mot de passe.");
                }
            }else{
            displayError("ERREUR", "Identifiants incorrects. Veuillez vérifier votre code et mot de passe.");
            }
        }
    }
}
