package br.edu.ifsuldeminas.mch.sd.chat.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import br.edu.ifsuldeminas.mch.sd.chat.ChatException;
import br.edu.ifsuldeminas.mch.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.mch.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.mch.sd.chat.Sender;

public class ChatGUI extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L;
    private JTextField nameField;
    private JTextField localPortField;
    private JTextField remoteIpField;
    private JTextField remotePortField;
    private JCheckBox connectionOrientedCheckBox;
    private JButton connectButton;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private Sender sender;

    public ChatGUI() {
        super("Chat Application");

        // --- Painel de configuração ---
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        configPanel.add(new JLabel("Nome:"));
        nameField = new JTextField(10);
        configPanel.add(nameField);
        configPanel.add(new JLabel("Porta Local:"));
        localPortField = new JTextField(5);
        configPanel.add(localPortField);
        configPanel.add(new JLabel("IP Remoto:"));
        remoteIpField = new JTextField(10);
        remoteIpField.setText("localhost");
        configPanel.add(remoteIpField);
        configPanel.add(new JLabel("Porta Remota:"));
        remotePortField = new JTextField(5);
        configPanel.add(remotePortField);
        connectionOrientedCheckBox = new JCheckBox("TCP");
        connectionOrientedCheckBox.setSelected(true);
        configPanel.add(connectionOrientedCheckBox);
        connectButton = new JButton("Conectar");
        configPanel.add(connectButton);

        // --- Área de Chat ---
        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // --- Painel de envio de mensagem ---
        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        // Estado inicial dos componentes
        messageField.setEnabled(false);
        sendButton.setEnabled(false);

        // Adicionando componentes ao frame
        add(configPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);

        // Action Listeners
        connectButton.addActionListener(e -> connect());
        ActionListener sendMessageAction = e -> sendMessage();
        sendButton.addActionListener(sendMessageAction);
        messageField.addActionListener(sendMessageAction);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connect() {
        try {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira seu nome.", "Nome Requerido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int localPort = Integer.parseInt(localPortField.getText());
            String remoteIp = remoteIpField.getText();
            int remotePort = Integer.parseInt(remotePortField.getText());
            boolean isConnectionOriented = connectionOrientedCheckBox.isSelected();

            // Se for TCP, mostra a caixa "conectando" em uma thread separada.
            if (isConnectionOriented) {
                JDialog connectingDialog = new JDialog(this, "Conexão TCP", true);
                connectingDialog.add(new JLabel("Conectando ao servidor..."));
                connectingDialog.pack();
                connectingDialog.setLocationRelativeTo(this);

                SwingWorker<Sender, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Sender doInBackground() throws Exception {
                        return ChatFactory.build(isConnectionOriented, remoteIp, remotePort, localPort, ChatGUI.this);
                    }

                    @Override
                    protected void done() {
                        connectingDialog.dispose();
                        try {
                            Sender connectedSender = get();
                            onConnectionSuccess(connectedSender);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ChatGUI.this, "Erro ao conectar: " + ex.getCause().getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
                connectingDialog.setVisible(true); // Bloqueia até o worker terminar.
            } else {
                // Para UDP, a "conexão" é instantânea.
                Sender connectedSender = ChatFactory.build(isConnectionOriented, remoteIp, remotePort, localPort, this);
                onConnectionSuccess(connectedSender);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Portas devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar chat: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onConnectionSuccess(Sender connectedSender) {
        this.sender = connectedSender;
        
        // Desabilita painel de configuração
        nameField.setEnabled(false);
        localPortField.setEnabled(false);
        remoteIpField.setEnabled(false);
        remotePortField.setEnabled(false);
        connectionOrientedCheckBox.setEnabled(false);
        connectButton.setEnabled(false);
        
        // Habilita painel de mensagem
        messageField.setEnabled(true);
        sendButton.setEnabled(true);
        messageField.requestFocusInWindow();
        
        chatArea.append("Conexão estabelecida. Você já pode enviar mensagens.\n");
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (message.trim().isEmpty()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String formattedMessage = name + ": " + message;
            sender.send(formattedMessage);
            chatArea.append("Eu: " + message + "\n");
            messageField.setText("");
        } catch (ChatException e) {
            JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + e.getCause().getMessage(), "Erro de Envio", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void newMessage(String message) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            chatArea.append(message.trim() + "\n");
        });
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(ChatGUI::new);
    }
}