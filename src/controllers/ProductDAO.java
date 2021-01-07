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
import models.Product;

public class ProductDAO {

    static Connection connection = DatabaseConnection.getConexao();

    public static String create(Product product) {
        String erro = null;
        PreparedStatement state;
        String msgSQL = "insert into product(price, p_description, fk_client_id, creationDateTime)"
                + "values (?, ?, ?, ?)";
        try {
            state = connection.prepareStatement(msgSQL);
            state.setDouble(1, product.getPrice());
            state.setString(2, product.getDescription());
            state.setInt(3, product.getFk_client_id());
            state.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            state.execute();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
            erro = e.toString();
        }
        if (erro == null) {
            System.out.println("\n Produto adicionado com sucesso!");
        }

        return erro;
    }

    public static ArrayList<Product> read() {
        PreparedStatement state;
        ArrayList<Product> productList = new ArrayList();
        String msgSQL;

        msgSQL = "Select * from product";

        try {
            state = connection.prepareStatement(msgSQL);
            ResultSet res = state.executeQuery();
            productList = resultSetToArrayList(res);
            res.close();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
        }
        return productList;
    }

    private static ArrayList<Product> resultSetToArrayList(ResultSet res) {
        ArrayList<Product> productList = new ArrayList();
        try {
            while (res.next()) {
                Product newProduct = new Product();
                newProduct.setProdutctID(res.getInt("product_id"));
                newProduct.setPrice(res.getDouble("price"));
                newProduct.setDescription(res.getString("p_description"));
                newProduct.setFk_client_id(res.getInt("fk_client_id"));
                Timestamp timestampFromDataBase = res.getTimestamp("creationDateTime");
                newProduct.setCreationDateTime(timestampFromDataBase.toLocalDateTime());
                productList.add(newProduct);
            }
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
        }
        return productList;
    }

    public static String update(Product product) {
        String erro = null;
        PreparedStatement state;
        String msgSQL = "update product set price=?, p_description=?, "
                + "fk_client_id=? where product_id=?";
        try {
            state = connection.prepareStatement(msgSQL);
            state.setDouble(1, product.getPrice());
            state.setString(2, product.getDescription());
            state.setInt(3, product.getFk_client_id());
            state.setInt(4, product.getProdutctID());
            state.execute();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
            erro = e.toString();
        }
        if (erro == null) {
            System.out.println("\n Produto Atualizado com sucesso!");
        }
        return erro;
    }

    public static String delete(Product product) {
        String erro = null;
        PreparedStatement state;
        String msgSQL = "delete from product where product_id=?";
        try {
            state = connection.prepareStatement(msgSQL);
            state.setInt(1, product.getProdutctID());
            state.execute();
            state.close();
        } catch (SQLException e) {
            System.out.println("\n Erro Encontrado: " + e.toString());
            erro = e.toString();
        }
        if (erro == null) {
            System.out.println("\n Produto Removido com sucesso!");
        }
        return erro;
    }
}
