//AVALIAÇÃO 2 - PROGRAMAÇÃO ORIENTADA A OBJETOS E VISUAL
//ALUNO: GUILHERME RODRIGUES DE MELO
//DATA: 07/01/21, PROF: JEFFERSON BEETHOVEN MARTINS
 
package main;

import controllers.ClientDAO;
import controllers.DatabaseConnection;
import controllers.ProductDAO;
import java.sql.Connection;
import java.time.LocalDateTime;
import models.Client;
import models.Product;
import view.MainScreen;

public class Main {

    public static void main(String[] args) {
        
        MainScreen tela = new MainScreen();
        tela.setVisible(true);
        tela.setLocationRelativeTo(null);
        
        System.out.println("Desenvolvido por Guilherme Rodrigues de Melo");
    }
}
