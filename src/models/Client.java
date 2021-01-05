package models;

public class Client {
    private int clientID;
    private String name;
    private String email;

    public Client(){
        this.clientID = 0;
        this.name = "Sem Nome";
        this.email = "Sem Email";
    }
    
    public Client(int clientID, String name, String email) {
        this.clientID = clientID;
        this.name = name;
        this.email = email;
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

    @Override
    public String toString() {
        return "Client{" + "clientID=" + clientID + ", name=" + name + ", email=" + email + '}';
    }
}
