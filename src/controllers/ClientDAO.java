//AVALIAÇÃO 2 - PROGRAMAÇÃO ORIENTADA A OBJETOS E VISUAL
//ALUNO: GUILHERME RODRIGUES DE MELO
//DATA: 07/01/21, PROF: JEFFERSON BEETHOVEN MARTINS

package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import models.Client;

public class ClientDAO {

    static Connection connection = DatabaseConnection.getConexao();

    public static String create(Client newClient) {
        String erro = null;
        PreparedStatement state;
        String msgSQL = "insert into client (name, email, creationDateTime) values (?,?,?)";
        try {
            state = connection.prepareStatement(msgSQL);
            state.setString(1, newClient.getName());
            state.setString(2, newClient.getEmail());
            state.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            state.execute();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
            erro = e.toString();
        }
        if (erro == null) {
            System.out.println("\n Cliente adicionado com Sucesso!");
        }
        return erro;
    }

    public static ArrayList<Client> read() {
        ArrayList<Client> clientList = new ArrayList();
        PreparedStatement state;
        ResultSet res;
        String msgSQL;
        msgSQL = "Select * from client";

        try {
            state = connection.prepareStatement(msgSQL);
            res = state.executeQuery();
            clientList = resultSetToArrayListClient(res);
            res.close();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
        }

        return clientList;
    }

    private static ArrayList<Client> resultSetToArrayListClient(ResultSet res) {
        ArrayList<Client> clientList = new ArrayList();
        try {
            while (res.next()) {
                Client newClient = new Client();
                newClient.setClientID(res.getInt("client_id"));
                newClient.setName(res.getString("name"));
                newClient.setEmail(res.getString("email"));
                Timestamp timestampFromDataBase = res.getTimestamp("creationDateTime");
                newClient.setCreationDateTime(timestampFromDataBase.toLocalDateTime());
                clientList.add(newClient);
            }
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
        }

        return clientList;
    }

    public static String update(Client client) {
        String erro = null;
        PreparedStatement state;
        String msgSQL = "update client set name=?, email=? where client_id=?";
        try {
            state = connection.prepareStatement(msgSQL);
            state.setString(1, client.getName());
            state.setString(2, client.getEmail());
            state.setInt(3, client.getClientID());
            state.execute();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
            erro = e.toString();
        }
        if (erro == null) {
            System.out.println("\n Cliente atualizado com sucesso!");
        }
        return erro;
    }

    public static String delete(Client client) {
        String erro = null;
        PreparedStatement state;
        String msgSQL = "delete from client where client_id=?";
        try {
            state = connection.prepareStatement(msgSQL);
            state.setInt(1, client.getClientID());
            state.execute();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
            erro = e.toString();
        }
        if (erro == null) {
            System.out.println("\n Cliente Excluído com sucesso!");
        }
        return erro;
    }
    
    public static Client searchClientById(int id) {
        ArrayList<Client> clientList = new ArrayList();
        PreparedStatement state;
        ResultSet res;
        String msgSQL;
        msgSQL = "select * from client where client_id = ?";

        try {
            state = connection.prepareStatement(msgSQL);
            state.setInt(1, id);
            res = state.executeQuery();
            clientList = resultSetToArrayListClient(res);
            res.close();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
        }

        return clientList.get(0);
    }
}
