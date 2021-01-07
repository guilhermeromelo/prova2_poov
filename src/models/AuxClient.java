//AVALIAÇÃO 2 - PROGRAMAÇÃO ORIENTADA A OBJETOS E VISUAL
//ALUNO: GUILHERME RODRIGUES DE MELO
//DATA: 07/01/21, PROF: JEFFERSON BEETHOVEN MARTINS
 
package models;

public class AuxClient {

    //HELPS CLIENT INFO FUNCTIONS 
    //(CLIENTE MAIOR NUMERO PEDIDOS E QUE MAIS GASTOU)
    private int clientID;
    private String clientName;
    private int productQt;
    private double amountSpent;

    public AuxClient(int clientID, String clientName) {
        this.clientID = clientID;
        this.clientName = clientName;
        this.productQt = 0;
        this.amountSpent = 0;
    }

    public void countOneMoreProduct() {
        this.productQt = this.productQt + 1;
    }

    public void increaseAmountSpent(double newPrice) {
        this.amountSpent = this.amountSpent + newPrice;
    }

    public int getClientID() {
        return clientID;
    }

    public int getProductQt() {
        return productQt;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public String getClientName() {
        return clientName;
    }
    
}
