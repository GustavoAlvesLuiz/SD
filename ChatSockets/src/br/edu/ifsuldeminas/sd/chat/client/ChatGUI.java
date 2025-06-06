package br.edu.ifsuldeminas.sd.chat.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

public class ChatGUI extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L;
    private JTextField localPortField, remotePortField, messageField, nameField;
    private JTextArea chatArea;
    private JButton connectButton, sendButton;
    private Sender sender;
    private String userName;

    public ChatGUI() {
        // Configurações da janela principal
        setTitle("Chat UDP");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

     // --- PAINEL SUPERIOR (CONEXÃO) ---
        // Usando GridLayout para organizar melhor os componentes
        JPanel topPanel = new JPanel(new java.awt.GridLayout(2, 4, 5, 5)); // 2 linhas, 4 colunas, com espaçamento
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Adiciona uma margem

        topPanel.add(new JLabel("Seu Nome:"));
        nameField = new JTextField();
        topPanel.add(nameField);

        topPanel.add(new JLabel("Porta Local:"));
        localPortField = new JTextField();
        topPanel.add(localPortField);

        topPanel.add(new JLabel("Porta Remota:"));
        remotePortField = new JTextField();
        topPanel.add(remotePortField);

        topPanel.add(new JPanel()); // Célula vazia para alinhar o botão

        connectButton = new JButton("Conectar");
        topPanel.add(connectButton);

        add(topPanel, BorderLayout.NORTH);

        // --- ÁREA CENTRAL (MENSAGENS) ---
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // --- PAINEL INFERIOR (ENVIO) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);
        sendButton = new JButton("Enviar");
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Desabilitar campos de envio até conectar
        messageField.setEnabled(false);
        sendButton.setEnabled(false);

        // --- LÓGICA DOS BOTÕES ---
        setupActionListeners();

        // Finaliza configuração e torna visível
        setLocationRelativeTo(null); // Centraliza na tela
        setVisible(true);
    }

    private void setupActionListeners() {
        // Ação para o botão Conectar
        connectButton.addActionListener(e -> connect());

        // Ação para o botão Enviar
        sendButton.addActionListener(e -> sendMessage());

        // Ação para a tecla 'Enter' no campo de mensagem
        messageField.addActionListener(e -> sendMessage());
    }
    
    private void connect() {
        try {
            userName = nameField.getText().trim();
            int localPort = Integer.parseInt(localPortField.getText().trim());
            int serverPort = Integer.parseInt(remotePortField.getText().trim());

            if (userName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe seu nome.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // A classe ChatGUI (this) implementa MessageContainer e será o container
            this.sender = ChatFactory.build("localhost", serverPort, localPort, this);

            // Habilita/desabilita campos após a conexão
            nameField.setEditable(false);
            localPortField.setEditable(false);
            remotePortField.setEditable(false);
            connectButton.setEnabled(false);
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            chatArea.append("Conectado com sucesso!\n");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "As portas devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (ChatException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + ex.getCause().getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty() && sender != null) {
            try {
                // Formata a mensagem com o nome do remetente
                String messageToSend = String.format("%s%s%s", messageText, MessageContainer.FROM, userName);
                sender.send(messageToSend);
                
                // Exibe a própria mensagem na área de chat
                chatArea.append(String.format("Eu> %s\n", messageText));
                messageField.setText("");
            } catch (ChatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + ex.getMessage(), "Erro de Envio", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void newMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        // Divide a mensagem para obter o conteúdo e o remetente
        String[] messageParts = message.split(MessageContainer.FROM);
        String content = messageParts[0];
        // O .trim() remove espaços e caracteres nulos que podem vir no buffer UDP
        String from = (messageParts.length > 1) ? messageParts[1].trim() : "Desconhecido";

        // IMPORTANTE: Atualizações de componentes Swing devem ocorrer na Event Dispatch Thread (EDT)
        // Usamos SwingUtilities.invokeLater para garantir isso, pois newMessage é chamada pela thread do Receiver.
        SwingUtilities.invokeLater(() -> {
            chatArea.append(String.format("%s> %s\n", from, content));
            // Auto-scroll para a última mensagem
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        // Inicia a GUI na Event Dispatch Thread
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}
