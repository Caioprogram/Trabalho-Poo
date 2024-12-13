
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class DocumentManager extends JFrame {
    private JTextField titleField;
    private JTextField authorField;
    private JTextField dateField;
    private JTable documentTable;
    private DefaultTableModel tableModel;

    public DocumentManager() {
        // Configuração da Janela
        setTitle("Gerenciador de Documentos");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de Cadastro
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Título (Máx 100 caracteres):"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Autor (Máx 50 caracteres):"));
        authorField = new JTextField();
        inputPanel.add(authorField);

        inputPanel.add(new JLabel("Data de Criação (DD/MM/AAAA):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(new SaveButtonListener());
        inputPanel.add(saveButton);

        add(inputPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(new String[]{"Título", "Autor", "Data de Criação"}, 0);
        documentTable = new JTable(tableModel);
        add(new JScrollPane(documentTable), BorderLayout.CENTER);

        // Botões
        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Carregar Dados");
        loadButton.addActionListener(new LoadButtonListener());
        buttonPanel.add(loadButton);

        JButton deleteButton = new JButton("Excluir");
        deleteButton.addActionListener(new DeleteButtonListener());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText();
            String author = authorField.getText();
            String creationDate = dateField.getText();

            // Validação de Campos
            if (title.isEmpty() || title.length() > 100) {
                JOptionPane.showMessageDialog(null, "Título não pode estar vazio e deve ter no máximo 100 caracteres.");
                return;
            }
            if (author.isEmpty() || author.length() > 50) {
                JOptionPane.showMessageDialog(null, "Autor não pode estar vazio e deve ter no máximo 50 caracteres.");
                return;
            }
            if (!creationDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
                JOptionPane.showMessageDialog(null, "Data deve estar no formato DD/MM/AAAA.");
                return;
            }

            // Salvar o documento
            Document document = new Document(title, author, creationDate);
            saveDocumentToFile(document);
            clearFields();
        }
    }

    private class LoadButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            loadDocumentsFromFile();
        }
    }

    private class DeleteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = documentTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                updateFile();
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um documento para excluir.");
            }
        }
    }

    private void saveDocumentToFile(Document document) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/documents.csv", true))) {
            writer.write(document.toString());
            writer.newLine();
            tableModel.addRow(new Object[]{document.getTitle(), document.getAuthor(), document.getCreationDate()});
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar documento: " + ex.getMessage());
        }
    }

    private void loadDocumentsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/documents.csv"))) {
            String line;
            tableModel.setRowCount(0); // Limpa a tabela antes de carregar novos dados
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                tableModel.addRow(data);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar documentos: " + ex.getMessage());
        }
    }

    private void clearFields() {
        titleField.setText("");
        authorField.setText("");
        dateField.setText("");
    }

    private void updateFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/documents.csv"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String title = (String) tableModel.getValueAt(i, 0);
                String author = (String) tableModel.getValueAt(i, 1);
                String creationDate = (String) tableModel.getValueAt(i, 2);
                Document document = new Document(title, author, creationDate);
                writer.write(document.toString());
                writer.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar o arquivo: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DocumentManager manager = new DocumentManager();
            manager.setVisible(true);
        });
    }
}

