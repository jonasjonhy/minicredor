package fjn.edu.br.View;

import credicard.LeitorArquivoRemesa;
import fjn.edu.br.Connection.Conn;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import fjn.edu.br.View.CellRenderer;
import fjn.edu.br.View.ConectaBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Janela extends JFrame implements ActionListener {

    private JButton bt1, bt2, bt3;
    private JTextField tx;
    private JTable table;
    private DefaultTableModel modelo;
    private static String campo[] = new String[8];
    private static int intTotalRegistro, intNumRegistro, intRegistro, numSeg, qtdePar;
    private static double valorT;
    private String nomeArquivo = "gerado.txt";
    private String linha, mostra = "";
    private String[] dadosCompra = null;
    private String[] columnNames = {"Loja", "Nº do Cartão", "Cliente", "Validade", "Cod. segurança", "Valor Total", "Qtde. Parcelas", "Data Compra"};

    private final Connection conn;

    public Janela() {
        this.tx = new JTextField(50);
        this.bt1 = new JButton("Abrir");
        this.bt1.addActionListener(this);
        this.bt2 = new JButton("Gerar");
        this.bt2.addActionListener(this);
        this.bt3 = new JButton("Gerar Retorno");
        setLayout(new FlowLayout());
        this.conn = new Conn().getConnection();
        try {

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery("select * from solicitacao_compras");

            intTotalRegistro = 0;

            while (rs.next()) {
                modelo = new DefaultTableModel(columnNames, intNumRegistro);
                rs.beforeFirst();
                rs.next();
                intRegistro = rs.getInt("loja_id");
                numSeg = rs.getInt("num_seguranca");
                valorT = rs.getDouble("valor_total");
                qtdePar = rs.getInt("qtd_parcelas");
                campo[0] = Integer.toString(intRegistro);
                campo[1] = rs.getString("cartao_id");
                campo[2] = rs.getString("nome_cliente");
                campo[3] = rs.getString("data_validade");
                campo[4] = Integer.toString(numSeg);
                campo[5] = Double.toString(valorT);
                campo[6] = Integer.toString(qtdePar);
                campo[7] = rs.getString("data_compra");
                modelo.insertRow(0, campo);

                intNumRegistro = 1;

                while (rs.next()) {
                    intRegistro = rs.getInt("loja_id");
                    numSeg = rs.getInt("num_seguranca");
                    valorT = rs.getDouble("valor_total");
                    qtdePar = rs.getInt("qtd_parcelas");
                    campo[0] = Integer.toString(intRegistro);
                    campo[1] = rs.getString("cartao_id");
                    campo[2] = rs.getString("nome_cliente");
                    campo[3] = rs.getString("data_validade");
                    campo[4] = Integer.toString(numSeg);
                    campo[5] = Double.toString(valorT);
                    campo[6] = Integer.toString(qtdePar);
                    campo[7] = rs.getString("data_compra");
                    modelo.insertRow(intNumRegistro, campo);
                    intNumRegistro++;
                }

                rs.close();
                stmt.close();
                conn.close();

            }

        } catch (Exception e) {
            e.getMessage();
        }

        table = new JTable(modelo) {
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };
        table.setDefaultRenderer(Object.class, new CellRenderer());
        table.getTableHeader().setReorderingAllowed(false);
        table.setPreferredScrollableViewportSize(new Dimension(1000, 100));
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scroolPane = new JScrollPane(table);
        add(tx);
        add(bt1);
        add(bt2);
        add(scroolPane);
        add(bt3);

    }

    public static void main(String[] args) {
        JFrame gui = new Janela();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        gui.setSize(1200, 400);
        gui.setLocation(300, 300);
        gui.setTitle("Dados da Venda");
    }

    public void selecionar() {
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i = file.showSaveDialog(null);
        if (i == 1) {
            tx.setText("");
        } else {
            File arquivo = file.getSelectedFile();
            tx.setText(arquivo.getPath());
        }

    }

    public void gerar() {
        try {
            
            LeitorArquivoRemesa.LerArquivo(tx.getText());

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bt1) {
            selecionar();
        }
        if (e.getSource() == bt2) {
            System.out.println("Botão acionado");
            gerar();
        }

    }
}