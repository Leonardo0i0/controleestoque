package conexao;

import telaEstoque.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe CRUD para tabela produto
public class CRUD {

    private static final String URL = "jdbc:mysql://localhost:3306/controleestoque";
    private static final String USER = "root"; // troque se seu usuário for diferente
    private static final String PASSWORD = ""; // troque pela sua senha

    // Método para abrir conexão
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // CREATE - Inserir produto
    public void adicionarProduto(int id, String nome, String tipo, int quantidade, double preco, int qtdAVencer) {
        String sql = "INSERT INTO produto (IdProduto, nomeProduto, tipoProduto, quantidadeProduto, precoProduto, qtdAVencer) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, nome);
            stmt.setString(3, tipo);
            stmt.setInt(4, quantidade);
            stmt.setDouble(5, preco);
            stmt.setInt(6, qtdAVencer);
            stmt.executeUpdate();
            System.out.println("Produto inserido com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ - Listar todos os produtos
    // READ - Listar todos os produtos (retorna objetos Produto)
    public List<Produto> listarProdutosDB() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto produto = new Produto(
                        String.valueOf(rs.getInt("IdProduto")),   // codigo
                        rs.getString("nomeProduto"),              // nome
                        rs.getInt("quantidadeProduto"),           // quantidade
                        rs.getString("tipoProduto"),              // tipo
                        rs.getDouble("precoProduto"),             // preco
                        String.valueOf(rs.getInt("qtdAVencer"))   // vencimentoMes
                );
                produtos.add(produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }


    // UPDATE - Atualizar preço e quantidade
    public void atualizarProduto(int id, int novaQtd, double novoPreco) {
        String sql = "UPDATE produto SET quantidadeProduto = ?, precoProduto = ? WHERE IdProduto = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novaQtd);
            stmt.setDouble(2, novoPreco);
            stmt.setInt(3, id);
            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Produto atualizado com sucesso!");
            } else {
                System.out.println("Produto não encontrado!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE - Remover produto
    public void deletarProduto(int id) {
        String sql = "DELETE FROM produto WHERE IdProduto = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Produto deletado com sucesso!");
            } else {
                System.out.println("Produto não encontrado!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validarUsuario(String nome, String senha) {
        String sql = "SELECT * FROM usuario WHERE nomeusuario = ? AND senhausuario = ?";
        try (Connection conn = getConnection(); // aqui usa o mesmo método já criado
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true se encontrou usuário
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
