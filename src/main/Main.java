
package main;

import controllers.ClientDAO;
import controllers.DatabaseConnection;
import controllers.ProductDAO;
import java.sql.Connection;
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
