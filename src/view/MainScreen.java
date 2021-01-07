package view;

import controllers.ClientDAO;
import controllers.ProductDAO;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import models.AuxClient;
import models.Client;
import models.Product;

public class MainScreen extends javax.swing.JFrame {

    //DECIMAL FORMATTER
    DecimalFormat df = new DecimalFormat("#.00");

    boolean isClientUpdate = false;
    boolean isPedidoUpdate = false;

    public MainScreen() {
        initComponents();
        jPanel_product_info.setVisible(true);
        jPanel_product_add.setVisible(false);
        jPanel3.setVisible(false);
        jPanel4.setVisible(false);
        jPanel5.setVisible(false);
        jPanel_client_add.setVisible(false);
        jPanel7.setVisible(false);
        jPanel_client_info.setVisible(true);
        jPanel10.setVisible(false);

        //TABLES INITIALIZATION
        productTableBuilder(jtable_pedidos, ProductDAO.read());
        clientTableBuilder(jtable_clientes, ClientDAO.read());

        //ESPECIAL TEXTFIELDS INITIALIZATION
        jtf_addClient_id.setEditable(false);
        jtf_product_id.setEditable(false);
    }

    String dateBuilder(LocalDateTime localDate) {
        String dia = "" + localDate.getDayOfMonth();
        if (dia.length() == 1) {
            dia = "0" + dia;
        }
        String mes = "" + localDate.getMonthValue();
        if (mes.length() == 1) {
            mes = "0" + mes;
        }
        String ano = "" + localDate.getYear();
        ano = ano.substring(2);
        return dia + "/" + mes + "/" + ano;
    }

    String hourBuilder(LocalDateTime localDate) {
        String hora = "" + localDate.getHour();
        if (hora.length() == 1) {
            hora = "0" + hora;
        }
        String min = "" + localDate.getMinute();
        if (min.length() == 1) {
            min = "0" + min;
        }
        return (hora + ":" + min);
    }

    void productTableBuilder(JTable jtable, ArrayList<Product> productList) {
        double faturamento = 0;
        DefaultTableModel tableRows1;
        LocalDateTime lastProductTime = null;
        Product lastProduct = null;
        ArrayList<AuxClient> auxClientList = new ArrayList();
        tableRows1 = new DefaultTableModel(new String[]{"Nº", "ID Produto", "Descrição", "Valor", "Data", "Hora", "ID Cliente", "Nome Cliente"}, 0);
        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);
            Client c = ClientDAO.searchClientById(p.getFk_client_id());
            faturamento += p.getPrice();
            if (i == 0) {
                lastProduct = p;
                lastProductTime = p.getCreationDateTime();
            } else if (p.getCreationDateTime().isAfter(lastProductTime)) {
                lastProductTime = p.getCreationDateTime();
                lastProduct = p;
            }
            tableRows1.addRow(new Object[]{(i + 1), p.getProdutctID(), p.getDescription(),
                df.format(p.getPrice()), dateBuilder(p.getCreationDateTime()), hourBuilder(p.getCreationDateTime()), p.getFk_client_id(), c.getName()});

            //SEARCH FOR COSTUMER WITH MORE PRODUCTS AND AMOUNTSPENT
            boolean achou2 = false;
            if (!auxClientList.isEmpty()) {
                for (int j = 0; j < auxClientList.size(); j++) {
                    if (auxClientList.get(j).getClientID() == p.getFk_client_id()) {
                        auxClientList.get(j).countOneMoreProduct();
                        auxClientList.get(j).increaseAmountSpent(p.getPrice());
                        achou2 = true;
                    }
                }
            }
            if (achou2 == false) {
                AuxClient newAuxClient = new AuxClient(p.getFk_client_id(), c.getName());
                newAuxClient.countOneMoreProduct();
                newAuxClient.increaseAmountSpent(p.getPrice());
                auxClientList.add(newAuxClient);
            }
        }
        //SHOW AuxClientList INFORMATION
        AuxClient maisComprou = null;
        AuxClient maisGastou = null;
        if (auxClientList.size() > 0) {
            for (int j = 0; j < auxClientList.size(); j++) {
                if (j == 0) {
                    maisGastou = maisComprou = auxClientList.get(0);
                } else {
                    if (auxClientList.get(j).getAmountSpent() > maisGastou.getAmountSpent()) {
                        maisGastou = auxClientList.get(j);
                    }
                    if (auxClientList.get(j).getProductQt() > maisComprou.getProductQt()) {
                        maisComprou = auxClientList.get(j);
                    }
                }
            }
            jLabel_client_maior_numero_pedidos.setText(maisComprou.getClientID() + "- "
                    + maisComprou.getClientName() + " - " + maisComprou.getProductQt() + " produtos");
            jLabel_client_mais_gastou.setText(maisGastou.getClientID() + "- "
                    + maisGastou.getClientName() + " - " + " R$ " + df.format(maisGastou.getAmountSpent()));
        } else {
            jLabel_client_maior_numero_pedidos.setText("Produtos ainda não foram Cadastrados.");
            jLabel_client_mais_gastou.setText("Produtos ainda não foram Cadastrados.");
        }

        jtable.setModel(tableRows1);
        jLabel_product_total_produtos.setText("" + productList.size());
        jLabel_product_faturamento.setText(df.format(faturamento));
        if (lastProduct != null) {
            jLabel_product_last_product_name.setText(lastProduct.getProdutctID() + " - "
                    + lastProduct.getDescription());
            jLabel_product_last_product_datetime.setText(dateBuilder(lastProductTime) + " - "
                    + hourBuilder(lastProductTime));
        } else {
            jLabel_product_last_product_name.setText("Produtos ainda não foram Cadastrados.");
            jLabel_product_last_product_datetime.setText("");
        }
    }

    void clientTableBuilder(JTable jtable, ArrayList<Client> clientList) {
        DefaultTableModel tableRows;
        LocalDateTime lastClientTime = null;
        Client lastClient = null;
        tableRows = new DefaultTableModel(new String[]{"Nº", "ID", "Nome", "Email", "Data Cadastro", "Hora Cadastro"}, 0);
        for (int i = 0; i < clientList.size(); i++) {
            Client c = clientList.get(i);
            if (i == 0) {
                lastClient = c;
                lastClientTime = c.getCreationDateTime();
            } else if (c.getCreationDateTime().isAfter(lastClientTime)) {
                lastClientTime = c.getCreationDateTime();
                lastClient = c;
            }
            tableRows.addRow(new Object[]{(i + 1), c.getClientID(), c.getName(),
                c.getEmail(), dateBuilder(c.getCreationDateTime()), hourBuilder(c.getCreationDateTime())});
        }
        jtable.setModel(tableRows);
        jLabel_client_total_cadastrados.setText("" + clientList.size());
        if (lastClient != null) {
            jLabel_client_last_client_name.setText(lastClient.getClientID() + " - "
                    + lastClient.getName());
            jLabel_client_last_client_datetime.setText(dateBuilder(lastClientTime) + " - "
                    + hourBuilder(lastClientTime));
        } else {
            jLabel_client_last_client_name.setText("Clientes não Cadastrados.");
            jLabel_client_last_client_datetime.setText("");
        }
    }

    boolean newClientValidation() {
        boolean valido = true;
        String erro = "";
        if (jtf_addClient_name.getText().isEmpty()) {
            erro = erro + "\nNome do Cliente Não Preenchido";
            valido = false;
        }
        if (jtf_addClient_email.getText().isEmpty()) {
            erro = erro + "\nEmail do Cliente Não Preenchido";
            valido = false;
        }
        //SHOW ERROR MESSAGE
        if (erro != "") {
            JOptionPane.showMessageDialog(null, "Erro(s) Encontrados: " + erro,
                    "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
        }
        return valido;
    }

    void clientComboBoxBuilder() {
        ArrayList<Client> clientList = ClientDAO.read();
        jcb_product_client.removeAllItems();
        jcb_product_client.addItem("Selecionar...");
        jcb_product_client.setSelectedIndex(0);
        clientList.forEach(c -> {
            jcb_product_client.addItem(c.getClientID() + "- " + c.getName());
        });
    }

    boolean newPedidoValidation() {
        boolean valido = true;
        String erro = "";
        if (jtf_product_description.getText().isEmpty()) {
            valido = false;
            erro += "\nDescrição do Produto Vazia";
        }
        if (jtf_product_price.getText().isEmpty()) {
            valido = false;
            erro += "\nPreço do Produto Vazio";
        }
        if (jcb_product_client.getSelectedIndex() == 0) {
            valido = false;
            erro += "\nCliente Não Selecionado";
        }
        //SHOW ERROR MESSAGE
        if (!erro.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Erro(s) Encontrados: " + erro,
                    "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
        }
        return valido;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtable_pedidos = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtable_clientes = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel_product_info = new javax.swing.JPanel();
        jbutton_inserirProduct = new javax.swing.JButton();
        jbutton_alterarProduct = new javax.swing.JButton();
        jbutton_RemoverProduct = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel_product_faturamento = new javax.swing.JLabel();
        jLabel_product_total_produtos = new javax.swing.JLabel();
        jLabel_product_last_product_name = new javax.swing.JLabel();
        jLabel_product_last_product_datetime = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel_product_add = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jtf_product_id = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jtf_product_description = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jcb_product_client = new javax.swing.JComboBox<>();
        jlb_addProduct_title = new javax.swing.JLabel();
        jb_addProduct_create = new javax.swing.JButton();
        jb_addProduct_back = new javax.swing.JButton();
        jtf_product_price = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jPanel_client_info = new javax.swing.JPanel();
        jbutton_client_update = new javax.swing.JButton();
        jbutton_client_delete = new javax.swing.JButton();
        jbutton_client_add = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel_client_maior_numero_pedidos = new javax.swing.JLabel();
        jLabel_client_mais_gastou = new javax.swing.JLabel();
        jLabel_client_total_cadastrados = new javax.swing.JLabel();
        jLabel_client_last_client_datetime = new javax.swing.JLabel();
        jLabel_client_last_client_name = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel_client_add = new javax.swing.JPanel();
        jlabel_add_or_update_client = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jb_addCliente_create = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jtf_addClient_name = new javax.swing.JTextField();
        jtf_addClient_id = new javax.swing.JTextField();
        jtf_addClient_email = new javax.swing.JTextField();
        jb_addCliente_back = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Artesanato & Cia");
        setMaximumSize(new java.awt.Dimension(1450, 815));
        setMinimumSize(new java.awt.Dimension(1450, 815));
        setResizable(false);
        setSize(new java.awt.Dimension(1450, 815));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 32)); // NOI18N
        jLabel1.setText("Artesanato & Cia");

        jtable_pedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jtable_pedidos);

        jtable_clientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jtable_clientes);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("PRODUTOS");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("CLIENTES");

        jbutton_inserirProduct.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbutton_inserirProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_add_black_18dp.png"))); // NOI18N
        jbutton_inserirProduct.setText("Inserir Pedido");
        jbutton_inserirProduct.setMargin(new java.awt.Insets(4, 18, 4, 18));
        jbutton_inserirProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutton_inserirProductActionPerformed(evt);
            }
        });

        jbutton_alterarProduct.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbutton_alterarProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_edit_black_18dp.png"))); // NOI18N
        jbutton_alterarProduct.setText("Alterar Pedido");
        jbutton_alterarProduct.setMargin(new java.awt.Insets(4, 18, 4, 18));
        jbutton_alterarProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutton_alterarProductActionPerformed(evt);
            }
        });

        jbutton_RemoverProduct.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbutton_RemoverProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_clear_black_18dp.png"))); // NOI18N
        jbutton_RemoverProduct.setText("Remover Pedido");
        jbutton_RemoverProduct.setMargin(new java.awt.Insets(4, 18, 4, 18));
        jbutton_RemoverProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutton_RemoverProductActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Total de Produtos Cadastrados:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Último Produto Cadastrado:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setText("Faturamento Total");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("R$");

        jLabel_product_faturamento.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        jLabel_product_faturamento.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_product_faturamento.setText("0,00");

        jLabel_product_total_produtos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_product_total_produtos.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_product_total_produtos.setText("XXX");

        jLabel_product_last_product_name.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_product_last_product_name.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_product_last_product_name.setText("XX/XX/XX - XX:XX");

        jLabel_product_last_product_datetime.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_product_last_product_datetime.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_product_last_product_datetime.setText("XX/XX/XX - XX:XX");

        javax.swing.GroupLayout jPanel_product_infoLayout = new javax.swing.GroupLayout(jPanel_product_info);
        jPanel_product_info.setLayout(jPanel_product_infoLayout);
        jPanel_product_infoLayout.setHorizontalGroup(
            jPanel_product_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_product_infoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_product_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_product_infoLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel_product_total_produtos, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_product_infoLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel_product_last_product_name, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_product_infoLayout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(jLabel_product_last_product_datetime, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6)
                    .addGroup(jPanel_product_infoLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel_product_faturamento, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(jPanel_product_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbutton_inserirProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbutton_alterarProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbutton_RemoverProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel_product_infoLayout.setVerticalGroup(
            jPanel_product_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_product_infoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_product_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel_product_total_produtos))
                .addGap(13, 13, 13)
                .addGroup(jPanel_product_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel_product_last_product_name))
                .addGap(3, 3, 3)
                .addComponent(jLabel_product_last_product_datetime)
                .addGap(13, 13, 13)
                .addComponent(jLabel6)
                .addGap(1, 1, 1)
                .addGroup(jPanel_product_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel_product_faturamento)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_product_infoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbutton_inserirProduct)
                .addGap(18, 18, 18)
                .addComponent(jbutton_alterarProduct)
                .addGap(18, 18, 18)
                .addComponent(jbutton_RemoverProduct)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 173, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 173, Short.MAX_VALUE)
        );

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel8.setText("ID Produto:");

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel11.setText("Valor:");

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel12.setText("Descrição:");

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel13.setText("Cliente:");

        jcb_product_client.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jlb_addProduct_title.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jlb_addProduct_title.setText("Adicionar Produto");

        jb_addProduct_create.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_add_black_18dp.png"))); // NOI18N
        jb_addProduct_create.setText("Finalizar Cadastro");
        jb_addProduct_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_addProduct_createActionPerformed(evt);
            }
        });

        jb_addProduct_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_clear_black_18dp.png"))); // NOI18N
        jb_addProduct_back.setText("Cancelar");
        jb_addProduct_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_addProduct_backActionPerformed(evt);
            }
        });

        jtf_product_price.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtf_product_priceKeyTyped(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel17.setText("R$");

        javax.swing.GroupLayout jPanel_product_addLayout = new javax.swing.GroupLayout(jPanel_product_add);
        jPanel_product_add.setLayout(jPanel_product_addLayout);
        jPanel_product_addLayout.setHorizontalGroup(
            jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_product_addLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlb_addProduct_title)
                .addGap(262, 262, 262))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_product_addLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_product_addLayout.createSequentialGroup()
                        .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8)
                            .addGroup(jPanel_product_addLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel17)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtf_product_price)
                            .addComponent(jtf_product_id, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                        .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtf_product_description)
                            .addComponent(jcb_product_client, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_product_addLayout.createSequentialGroup()
                        .addComponent(jb_addProduct_back)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jb_addProduct_create)))
                .addGap(34, 34, 34))
        );
        jPanel_product_addLayout.setVerticalGroup(
            jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_product_addLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jlb_addProduct_title)
                .addGap(18, 18, 18)
                .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jtf_product_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jtf_product_description, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(jcb_product_client, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtf_product_price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_product_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jb_addProduct_back)
                    .addComponent(jb_addProduct_create))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jLayeredPane1.setLayer(jPanel_product_info, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel_product_add, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_product_info, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_product_add, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_product_info, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_product_add, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jbutton_client_update.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbutton_client_update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_edit_black_18dp.png"))); // NOI18N
        jbutton_client_update.setText("Alterar Cliente");
        jbutton_client_update.setMargin(new java.awt.Insets(4, 18, 4, 18));
        jbutton_client_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutton_client_updateActionPerformed(evt);
            }
        });

        jbutton_client_delete.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbutton_client_delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_clear_black_18dp.png"))); // NOI18N
        jbutton_client_delete.setText("Remover Cliente");
        jbutton_client_delete.setMargin(new java.awt.Insets(4, 18, 4, 18));
        jbutton_client_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutton_client_deleteActionPerformed(evt);
            }
        });

        jbutton_client_add.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbutton_client_add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_add_black_18dp.png"))); // NOI18N
        jbutton_client_add.setText("Inserir Cliente");
        jbutton_client_add.setMargin(new java.awt.Insets(4, 18, 4, 18));
        jbutton_client_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutton_client_addActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Total de Clientes Cadastrados:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Horário Último Cliente Cadastrado:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("Cliente com maior número de Pedidos:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Cliente que mais gastou na Loja:");

        jLabel_client_maior_numero_pedidos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_client_maior_numero_pedidos.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_client_maior_numero_pedidos.setText("Nome do Cliente - X pedidos");

        jLabel_client_mais_gastou.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_client_mais_gastou.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_client_mais_gastou.setText("Nome do Cliente - R$ YYYY,YY");

        jLabel_client_total_cadastrados.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_client_total_cadastrados.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_client_total_cadastrados.setText("XXX");

        jLabel_client_last_client_datetime.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_client_last_client_datetime.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_client_last_client_datetime.setText("XX/XX/XX - XX:XX");

        jLabel_client_last_client_name.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel_client_last_client_name.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_client_last_client_name.setText("XX/XX/XX - XX:XX");

        javax.swing.GroupLayout jPanel_client_infoLayout = new javax.swing.GroupLayout(jPanel_client_info);
        jPanel_client_info.setLayout(jPanel_client_infoLayout);
        jPanel_client_infoLayout.setHorizontalGroup(
            jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                        .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel_client_total_cadastrados, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel_client_last_client_name, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(jbutton_client_add, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                        .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                                .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel15)
                                    .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                                        .addGap(250, 250, 250)
                                        .addComponent(jLabel_client_last_client_datetime, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE))
                            .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                                .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel_client_mais_gastou, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel_client_maior_numero_pedidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(29, 29, 29)))
                        .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbutton_client_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutton_client_update, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(21, 21, 21))
        );
        jPanel_client_infoLayout.setVerticalGroup(
            jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                        .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel_client_total_cadastrados))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel_client_last_client_name)))
                    .addComponent(jbutton_client_add))
                .addGroup(jPanel_client_infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel_client_last_client_datetime)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_client_maior_numero_pedidos)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel15)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel_client_mais_gastou))
                    .addGroup(jPanel_client_infoLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jbutton_client_update)
                        .addGap(18, 18, 18)
                        .addComponent(jbutton_client_delete))))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 173, Short.MAX_VALUE)
        );

        jlabel_add_or_update_client.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jlabel_add_or_update_client.setText("Adicionar Cliente");

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel18.setText("ID Cliente:");

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel19.setText("Nome:");

        jb_addCliente_create.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_add_black_18dp.png"))); // NOI18N
        jb_addCliente_create.setText("Finalizar Cadastro");
        jb_addCliente_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_addCliente_createActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel20.setText("Email:");

        jb_addCliente_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_clear_black_18dp.png"))); // NOI18N
        jb_addCliente_back.setText("Cancelar");
        jb_addCliente_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_addCliente_backActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_client_addLayout = new javax.swing.GroupLayout(jPanel_client_add);
        jPanel_client_add.setLayout(jPanel_client_addLayout);
        jPanel_client_addLayout.setHorizontalGroup(
            jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_client_addLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_client_addLayout.createSequentialGroup()
                        .addComponent(jlabel_add_or_update_client)
                        .addGap(261, 261, 261))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_client_addLayout.createSequentialGroup()
                        .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel_client_addLayout.createSequentialGroup()
                                .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel19))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtf_addClient_name, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                    .addComponent(jtf_addClient_id))
                                .addGap(38, 38, 38)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jtf_addClient_email))
                            .addGroup(jPanel_client_addLayout.createSequentialGroup()
                                .addComponent(jb_addCliente_back)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 399, Short.MAX_VALUE)
                                .addComponent(jb_addCliente_create)))
                        .addGap(16, 16, 16))))
        );
        jPanel_client_addLayout.setVerticalGroup(
            jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_client_addLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jlabel_add_or_update_client)
                .addGap(18, 18, 18)
                .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20)
                        .addComponent(jtf_addClient_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_client_addLayout.createSequentialGroup()
                        .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jtf_addClient_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jtf_addClient_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel_client_addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jb_addCliente_create)
                    .addComponent(jb_addCliente_back))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 176, Short.MAX_VALUE)
        );

        jLayeredPane2.setLayer(jPanel_client_info, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jPanel5, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jPanel_client_add, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jPanel7, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jPanel10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_client_add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_client_info, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_client_add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_client_info, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addGap(1, 1, 1)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(2, 2, 2)))
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/icons/baseline_info_black_18dp.png"))); // NOI18N
        jButton1.setText("Sobre");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(290, 290, 290))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(289, 289, 289)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)))
                        .addGap(231, 231, 231)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1)
                    .addComponent(jLayeredPane2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //CHANGE TO ADD CLIENT SCREEN
    private void jbutton_client_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutton_client_addActionPerformed
        // TODO add your handling code here:
        jtf_addClient_name.setText("");
        jtf_addClient_id.setText("Gerado Pelo Sistema");
        jtf_addClient_email.setText("");
        jlabel_add_or_update_client.setText("Adicionar Cliente");
        jb_addCliente_create.setText("Finalizar Cadastro");
        isClientUpdate = false;
        jPanel_client_add.setVisible(true);
        jPanel_client_info.setVisible(false);
    }//GEN-LAST:event_jbutton_client_addActionPerformed

    //CHANGE TO ADD CLIENT SCREEN WITH A CLIENT DATA TO UPDATE
    private void jbutton_client_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutton_client_updateActionPerformed
        // TODO add your handling code here:
        //ASK WHAT OBJECT USER WANTS TO UPDATE
        String idUpdate = JOptionPane.showInputDialog("Por favor digite o ID do Cliente para modificar: ");
        if (idUpdate != null) {
            //CREATE AUXILIARY VARS
            ArrayList<Client> clientList = ClientDAO.read();
            Client clientModify = new Client();
            boolean achou = false;
            //SEARCH FOR THE OBJECT
            for (int i = 0; i < clientList.size() && achou == false; i++) {
                clientModify = clientList.get(i);
                if (idUpdate.equals("" + clientModify.getClientID())) {
                    achou = true;
                }
            }
            //PREPARE NEW SCREEN OR SHOW ERROR MESSAGE
            if (achou == true) {
                //PREPARE NEW SCREEN
                jtf_addClient_id.setText("" + clientModify.getClientID());
                jtf_addClient_name.setText(clientModify.getName());
                jtf_addClient_email.setText(clientModify.getEmail());
                //MAKE THE SCREEN CHANGES
                isClientUpdate = true;
                jlabel_add_or_update_client.setText("Alterar Cliente");
                jb_addCliente_create.setText("Finalizar Alteração");
                jPanel_client_add.setVisible(true);
                jPanel_client_info.setVisible(false);

            } else {
                JOptionPane.showMessageDialog(null, "ID do Cliente não Encontrado", "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jbutton_client_updateActionPerformed

    private void jbutton_alterarProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutton_alterarProductActionPerformed
        // TODO add your handling code here:
        //ASK WHAT OBJECT USER WANTS TO UPDATE
        String idUpdate = JOptionPane.showInputDialog("Por favor digite o ID do Produto para modificar: ");
        if (idUpdate != null) {
            //CREATE AUXILIARY VARS
            ArrayList<Product> productList = ProductDAO.read();
            Product productModify = new Product();
            boolean achou = false;
            //SEARCH FOR THE OBJECT
            for (int i = 0; i < productList.size() && achou == false; i++) {
                productModify = productList.get(i);
                if (idUpdate.equals("" + productModify.getProdutctID())) {
                    achou = true;
                }
            }
            //PREPARE NEW SCREEN OR SHOW ERROR MESSAGE
            if (achou == true) {
                //PREPARE NEW SCREEN
                jtf_product_id.setText("" + productModify.getProdutctID());
                jtf_product_description.setText(productModify.getDescription());
                jtf_product_price.setText("" + productModify.getPrice());
                clientComboBoxBuilder();
                for (int i = 1; i < jcb_product_client.getItemCount(); i++) {
                    int idComboBox = Integer.parseInt(new StringTokenizer(jcb_product_client.getItemAt(i).toString(), "-").nextToken());
                    if (idComboBox == productModify.getFk_client_id()) {
                        jcb_product_client.setSelectedIndex(i);
                    }
                }
                jcb_product_client.getItemAt(WIDTH);
                //MAKE THE SCREEN CHANGES
                isPedidoUpdate = true;
                jlb_addProduct_title.setText("Alterar Produto");

                jPanel_product_add.setVisible(true);
                jPanel_product_info.setVisible(false);

            } else {
                JOptionPane.showMessageDialog(null, "ID do Produto não Encontrado", "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbutton_alterarProductActionPerformed

    private void jb_addProduct_createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_addProduct_createActionPerformed
        // TODO add your handling code here:
        if (newPedidoValidation() == true) {
            int clientId = Integer.parseInt((new StringTokenizer(jcb_product_client.getSelectedItem().toString(), "-")).nextToken());
            double price = -1;
            boolean hasError = false;
            String erro = null;
            try {
                price = Double.parseDouble(jtf_product_price.getText().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Erro Encontrado: " + e.toString());
                hasError = true;
            }
            if (hasError == false) {
                if (isPedidoUpdate == true) {
                    erro = ProductDAO.update(new Product(Integer.parseInt(jtf_product_id.getText()), price, jtf_product_description.getText(), clientId, null));
                } else {
                    erro = ProductDAO.create(new Product(0, price, jtf_product_description.getText(), clientId, null));
                }
                //SHOW CREATION RESULT
                JOptionPane.showMessageDialog(null, (erro == null)
                        ? "Dados do Produto salvos com sucesso!"
                        : "Erro Encontado: \n" + erro, "Resultado da operação",
                        (erro == null) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (erro == null) {
                    //CHANGE SUBPAGE
                    productTableBuilder(jtable_pedidos, ProductDAO.read());
                    jPanel_product_add.setVisible(false);
                    jPanel_product_info.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Erro(s) Encontrados: " + "\nPreço Inválido",
                        "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jb_addProduct_createActionPerformed

    private void jb_addProduct_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_addProduct_backActionPerformed
        // TODO add your handling code here:
        jPanel_product_info.setVisible(true);
        jPanel_product_add.setVisible(false);
    }//GEN-LAST:event_jb_addProduct_backActionPerformed

    //CHANGE TO ADD PEDIDO SCREEN
    private void jbutton_inserirProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutton_inserirProductActionPerformed
        // TODO add your handling code here:
        jtf_product_id.setText("Gerado pelo Sistema");
        jtf_product_price.setText("");
        jtf_product_description.setText("");
        jlb_addProduct_title.setText("Adicionar Produto");
        clientComboBoxBuilder();
        isPedidoUpdate = false;
        jPanel_product_info.setVisible(false);
        jPanel_product_add.setVisible(true);
    }//GEN-LAST:event_jbutton_inserirProductActionPerformed

    private void jbutton_RemoverProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutton_RemoverProductActionPerformed
        // TODO add your handling code here:
        String idProductDelete = JOptionPane.showInputDialog("Por favor digite o ID do Produto para remover: ");
        if (idProductDelete != null) {
            ArrayList<Product> productList = ProductDAO.read();
            Product productDelete = new Product();
            boolean achou = false;
            for (int i = 0; i < productList.size() && achou == false; i++) {
                productDelete = productList.get(i);
                if (idProductDelete.equals("" + productDelete.getProdutctID())) {
                    achou = true;
                }
            }
            //ASK DELETE CONFIRMATION AND EXECUTE DELETE. THEN SHOW THE OPERATION RESULTS
            if (achou == true) {
                int delete = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir "
                        + "o Produto:\nID: " + productDelete.getProdutctID() + ", Nome: " + productDelete.getDescription() + " ?",
                        "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

                if (delete == 0) {
                    String erro = ProductDAO.delete(productDelete);
                    JOptionPane.showMessageDialog(null, (erro == null)
                            ? "Produto Removido com Sucesso!"
                            : "Erro Encontado: \n" + erro, "Resultado da operação",
                            (erro == null) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                    //REFRESH TABLE
                    productTableBuilder(jtable_pedidos, ProductDAO.read());
                } else {
                    JOptionPane.showMessageDialog(null, "Operação Cancelada!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "ID do Produto não Encontrado", "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbutton_RemoverProductActionPerformed

    //SAVE THE NEW CLIENT IN DATABASE / UPDATE DE CLIENT INFORMATION IN DATABASE
    private void jb_addCliente_createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_addCliente_createActionPerformed
        // TODO add your handling code here:
        if (newClientValidation()) {
            String erro;
            if (isClientUpdate == true) {
                erro = ClientDAO.update(new Client(Integer.parseInt(jtf_addClient_id.getText()), jtf_addClient_name.getText(), jtf_addClient_email.getText(), null));
            } else {
                erro = ClientDAO.create(new Client(0, jtf_addClient_name.getText(), jtf_addClient_email.getText(), null));
            }
            clientTableBuilder(jtable_clientes, ClientDAO.read());
            //SHOW CREATION RESULT
            JOptionPane.showMessageDialog(null, (erro == null)
                    ? "Dados do Cliente salvos com sucesso!"
                    : "Erro Encontado: \n" + erro, "Resultado da operação",
                    (erro == null) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (erro == null) {
                //CHANGE SUBPAGE
                jPanel_client_add.setVisible(false);
                jPanel_client_info.setVisible(true);
            }
        }
    }//GEN-LAST:event_jb_addCliente_createActionPerformed

    private void jb_addCliente_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_addCliente_backActionPerformed
        // TODO add your handling code here:
        jPanel_client_add.setVisible(false);
        jPanel_client_info.setVisible(true);
    }//GEN-LAST:event_jb_addCliente_backActionPerformed

    private void jbutton_client_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutton_client_deleteActionPerformed
        // TODO add your handling code here:
        String idClientDelete = JOptionPane.showInputDialog("Por favor digite o ID do Cliente para remover: ");
        if (idClientDelete != null) {
            ArrayList<Client> clientsList = ClientDAO.read();
            Client clientDelete = new Client();
            boolean achou = false;
            for (int i = 0; i < clientsList.size() && achou == false; i++) {
                clientDelete = clientsList.get(i);
                if (idClientDelete.equals("" + clientDelete.getClientID())) {
                    achou = true;
                }
            }
            //ASK DELETE CONFIRMATION AND EXECUTE DELETE. THEN SHOW THE OPERATION RESULTS
            if (achou == true) {
                int delete = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir "
                        + "o Cliente:\nID: " + clientDelete.getClientID() + ", Nome: " + clientDelete.getName() + " ?"
                        + "\nIsso também irá excluir os pedidos do\nCliente e Não poderá ser desfeito.",
                        "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                int productDeleteError = 0;
                if (delete == 0) {
                    ArrayList<Product> productList = ProductDAO.read();
                    for (int i = 0; i < productList.size(); i++) {
                        Product p = productList.get(i);
                        if (p.getFk_client_id() == clientDelete.getClientID()) {
                            String erro = ProductDAO.delete(p);
                            if (erro != null) {
                                productDeleteError++;
                            }
                        }
                    }
                    if (productDeleteError == 0) {
                        String erro = ClientDAO.delete(clientDelete);
                        JOptionPane.showMessageDialog(null, (erro == null)
                                ? "Cliente Removido com Sucesso!"
                                : "Erro Encontado: \n" + erro, "Resultado da operação",
                                (erro == null) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                        //REFRESH TABLE
                        clientTableBuilder(jtable_clientes, ClientDAO.read());
                        productTableBuilder(jtable_pedidos, ProductDAO.read());
                    } else {
                        JOptionPane.showMessageDialog(null, "Não foi possivel excluir o cliente, pois ainda"
                                + "\nhá produtos cadastrados em seu nome.", "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Operação Cancelada!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "ID não Encontrado", "Erro ao Realizar Operação", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbutton_client_deleteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null, ""
                + "\nSistema Desktop Desenvolvido na Disciplina de           "
                + "\nProgramação Orientada a Objetos e Visual"
                + "\ncomo Atividade Avaliativa 2 - 07/01/2021.\n"
                + "\nAluno: Guilherme Rodrigues de Melo, 4ºPeríodo"
                + "\nProf.: Jefferson Beethoven Martins");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jtf_product_priceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_product_priceKeyTyped
        // TODO add your handling code here:
        String caracteres = "0987654321.,";
        String aux = ",.";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        } else {
            if (aux.contains(evt.getKeyChar() + "")) {
                if (jtf_product_price.getText().contains(".") || jtf_product_price.getText().contains(",")) {
                    evt.consume();
                }
            }
        }
    }//GEN-LAST:event_jtf_product_priceKeyTyped

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainScreen().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_client_last_client_datetime;
    private javax.swing.JLabel jLabel_client_last_client_name;
    private javax.swing.JLabel jLabel_client_maior_numero_pedidos;
    private javax.swing.JLabel jLabel_client_mais_gastou;
    private javax.swing.JLabel jLabel_client_total_cadastrados;
    private javax.swing.JLabel jLabel_product_faturamento;
    private javax.swing.JLabel jLabel_product_last_product_datetime;
    private javax.swing.JLabel jLabel_product_last_product_name;
    private javax.swing.JLabel jLabel_product_total_produtos;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel_client_add;
    private javax.swing.JPanel jPanel_client_info;
    private javax.swing.JPanel jPanel_product_add;
    private javax.swing.JPanel jPanel_product_info;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jb_addCliente_back;
    private javax.swing.JButton jb_addCliente_create;
    private javax.swing.JButton jb_addProduct_back;
    private javax.swing.JButton jb_addProduct_create;
    private javax.swing.JButton jbutton_RemoverProduct;
    private javax.swing.JButton jbutton_alterarProduct;
    private javax.swing.JButton jbutton_client_add;
    private javax.swing.JButton jbutton_client_delete;
    private javax.swing.JButton jbutton_client_update;
    private javax.swing.JButton jbutton_inserirProduct;
    private javax.swing.JComboBox<String> jcb_product_client;
    private javax.swing.JLabel jlabel_add_or_update_client;
    private javax.swing.JLabel jlb_addProduct_title;
    private javax.swing.JTable jtable_clientes;
    private javax.swing.JTable jtable_pedidos;
    private javax.swing.JTextField jtf_addClient_email;
    private javax.swing.JTextField jtf_addClient_id;
    private javax.swing.JTextField jtf_addClient_name;
    private javax.swing.JTextField jtf_product_description;
    private javax.swing.JTextField jtf_product_id;
    private javax.swing.JTextField jtf_product_price;
    // End of variables declaration//GEN-END:variables
}
