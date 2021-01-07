//AVALIAÇÃO 2 - PROGRAMAÇÃO ORIENTADA A OBJETOS E VISUAL
//ALUNO: GUILHERME RODRIGUES DE MELO
//DATA: 07/01/21, PROF: JEFFERSON BEETHOVEN MARTINS
 
package models;

import java.time.LocalDateTime;

public class Client {
    private int clientID;
    private String name;
    private String email;
    private LocalDateTime creationDateTime;

    public Client(){
        this.clientID = 0;
        this.name = "Sem Nome";
        this.email = "Sem Email";
        this.creationDateTime = null;
    }
    
    public Client(int clientID, String name, String email, LocalDateTime creationDateTime) {
        this.clientID = clientID;
        this.name = name;
        this.email = email;
        this.creationDateTime = creationDateTime;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    @Override
    public String toString() {
        return "Client{" + "clientID=" + clientID + ", name=" + name + ", email=" + email + '}';
    }
}
