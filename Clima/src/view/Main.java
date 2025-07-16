package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.Cidade;
import model.ListaCidades;
import model.Previsao;
import model.PrevisaoCidade;
import model.parse.XStreamParser;
import model.service.WeatherForecastService;

public class Main {

    // Mapeamento de siglas para descrições completas
    private static final Map<String, String> DESCRICOES_TEMPO = new HashMap<>();
    static {
        DESCRICOES_TEMPO.put("ec", "Encoberto com Chuvas Isoladas");
        DESCRICOES_TEMPO.put("ci", "Chuvas Isoladas");
        DESCRICOES_TEMPO.put("c", "Chuva");
        DESCRICOES_TEMPO.put("in", "Instável");
        DESCRICOES_TEMPO.put("pp", "Poss. de Pancadas de Chuva");
        DESCRICOES_TEMPO.put("cm", "Chuva pela Manhã");
        DESCRICOES_TEMPO.put("cn", "Chuva a Noite");
        DESCRICOES_TEMPO.put("pt", "Pancadas de Chuva a Tarde");
        DESCRICOES_TEMPO.put("pm", "Pancadas de Chuva pela Manhã");
        DESCRICOES_TEMPO.put("np", "Nublado e Pancadas de Chuva");
        DESCRICOES_TEMPO.put("pc", "Pancadas de Chuva");
        DESCRICOES_TEMPO.put("pn", "Parcialmente Nublado");
        DESCRICOES_TEMPO.put("cv", "Chuvisco");
        DESCRICOES_TEMPO.put("ch", "Chuvoso");
        DESCRICOES_TEMPO.put("t", "Tempestade");
        DESCRICOES_TEMPO.put("ps", "Predomínio de Sol");
        DESCRICOES_TEMPO.put("e", "Encoberto");
        DESCRICOES_TEMPO.put("n", "Nublado");
        DESCRICOES_TEMPO.put("cl", "Céu Claro");
        DESCRICOES_TEMPO.put("nv", "Nevoeiro");
        DESCRICOES_TEMPO.put("g", "Geada");
        DESCRICOES_TEMPO.put("ne", "Neve");
        DESCRICOES_TEMPO.put("nd", "Não Definido");
        DESCRICOES_TEMPO.put("pnt", "Pancadas de Chuva a Noite");
        DESCRICOES_TEMPO.put("psc", "Possibilidade de Chuva");
        DESCRICOES_TEMPO.put("pcm", "Possibilidade de Chuva pela Manhã");
        DESCRICOES_TEMPO.put("pct", "Possibilidade de Chuva a Tarde");
        DESCRICOES_TEMPO.put("pcn", "Possibilidade de Chuva a Noite");
        DESCRICOES_TEMPO.put("npt", "Nublado com Pancadas a Tarde");
        DESCRICOES_TEMPO.put("npn", "Nublado com Pancadas a Noite");
        DESCRICOES_TEMPO.put("ncn", "Nublado com Poss. de Chuva a Noite");
        DESCRICOES_TEMPO.put("nct", "Nublado com Poss. de Chuva a Tarde");
        DESCRICOES_TEMPO.put("ncm", "Nublado com Poss. de Chuva pela Manhã");
        DESCRICOES_TEMPO.put("npm", "Nublado com Pancadas pela Manhã");
        DESCRICOES_TEMPO.put("npp", "Nublado com Possibilidade de Chuva");
        DESCRICOES_TEMPO.put("vn", "Variação de Nebulosidade");
        DESCRICOES_TEMPO.put("ct", "Chuva a Tarde");
        DESCRICOES_TEMPO.put("ppn", "Poss. de Panc. de Chuva a Noite");
        DESCRICOES_TEMPO.put("ppt", "Poss. de Panc. de Chuva a Tarde");
        DESCRICOES_TEMPO.put("ppm", "Poss. de Panc. de Chuva pela Manhã");
    }

    private JFrame frame;
    private JTextField cidadeTextField;
    private JButton buscarButton;
    private JList<Cidade> listaCidades;
    private DefaultListModel<Cidade> listModel;
    private JPanel previsaoPanel;
    private JLabel statusLabel;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new Main().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Previsão do Tempo - INPE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel buscaPanel = new JPanel(new BorderLayout(5, 5));
        buscaPanel.setBorder(BorderFactory.createTitledBorder("Buscar Cidade"));
        cidadeTextField = new JTextField(20);
        buscarButton = new JButton("Buscar");
        buscaPanel.add(cidadeTextField, BorderLayout.CENTER);
        buscaPanel.add(buscarButton, BorderLayout.EAST);
        cidadeTextField.addActionListener(e -> buscarButton.doClick());

        listModel = new DefaultListModel<>();
        listaCidades = new JList<>(listModel);
        listaCidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCidades.setCellRenderer(new CidadeListCellRenderer());

        JScrollPane listScrollPane = new JScrollPane(listaCidades);
        listScrollPane.setPreferredSize(new Dimension(200, 100));
        
        JPanel resultadosPanel = new JPanel(new BorderLayout());
        resultadosPanel.setBorder(BorderFactory.createTitledBorder("Resultados da Busca"));
        resultadosPanel.add(listScrollPane, BorderLayout.CENTER);

        previsaoPanel = new JPanel();
        previsaoPanel.setLayout(new GridLayout(1, 0, 10, 10)); 
        previsaoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        statusLabel = new JLabel("Digite o nome de uma cidade e clique em buscar.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel topPanel = new JPanel(new BorderLayout(10,10));
        topPanel.add(buscaPanel, BorderLayout.NORTH);
        topPanel.add(resultadosPanel, BorderLayout.CENTER);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(previsaoPanel, BorderLayout.CENTER);
        
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(mainContentPanel), BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        buscarButton.addActionListener(e -> buscarCidades());
        listaCidades.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaCidades.getSelectedValue() != null) {
                buscarPrevisao(listaCidades.getSelectedValue());
            }
        });
        
        frame.pack();
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    private void buscarCidades() {
        String nomeCidade = cidadeTextField.getText().trim();
        if (nomeCidade.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor, digite o nome de uma cidade.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        statusLabel.setText("Buscando cidades...");
        listModel.clear();
        previsaoPanel.removeAll();
        previsaoPanel.revalidate();
        previsaoPanel.repaint();

        new Thread(() -> {
            try {
                String cidadesXML = WeatherForecastService.cidades(nomeCidade);
                XStreamParser<PrevisaoCidade, ListaCidades> xspCidades = new XStreamParser<>();
                ListaCidades lista = (ListaCidades) xspCidades.cidades(cidadesXML);

                SwingUtilities.invokeLater(() -> {
                    if (lista.getCidades() == null || lista.getCidades().isEmpty()) {
                        statusLabel.setText("Nenhuma cidade encontrada com o nome '" + nomeCidade + "'.");
                        return;
                    }
                    lista.getCidades().forEach(listModel::addElement);
                    statusLabel.setText(lista.getCidades().size() + " cidade(s) encontrada(s). Selecione uma da lista.");
                });

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                     JOptionPane.showMessageDialog(frame, "Erro de rede ao buscar cidades: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                     statusLabel.setText("Erro ao buscar cidades.");
                });
            }
        }).start();
    }
    
    private void buscarPrevisao(Cidade cidade) {
        statusLabel.setText("Buscando previsão para " + cidade.getNome() + "...");
        previsaoPanel.removeAll();

        new Thread(() -> {
            try {
                String previsaoXML = WeatherForecastService.previsoesParaSeteDias(cidade.getId());
                XStreamParser<PrevisaoCidade, ListaCidades> xspPrevisoes = new XStreamParser<>();
                PrevisaoCidade pc = (PrevisaoCidade) xspPrevisoes.previsao(previsaoXML);

                SwingUtilities.invokeLater(() -> {
                    exibirPrevisoes(pc);
                });

            } catch (IOException ex) {
                 SwingUtilities.invokeLater(() -> {
                     JOptionPane.showMessageDialog(frame, "Erro de rede ao buscar previsão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                     statusLabel.setText("Erro ao buscar previsão do tempo.");
                });
            }
        }).start();
    }

    private void exibirPrevisoes(PrevisaoCidade previsaoCidade) {
        previsaoPanel.removeAll();
        statusLabel.setText("Previsão para " + previsaoCidade.getNome() + " - " + previsaoCidade.getUf());

        SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM (EEE)");
        String hoje = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        for (Previsao p : previsaoCidade.getPrevisoes()) {
            JPanel diaPanel = new JPanel(new BorderLayout(5, 5));
            diaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            String dataFormatada;
            try {
                Date data = sdfEntrada.parse(p.getDia());
                dataFormatada = sdfSaida.format(data).toLowerCase();
            } catch (ParseException e) {
                dataFormatada = p.getDia();
            }
            
            JLabel dataLabel = new JLabel(dataFormatada, SwingConstants.CENTER);
            dataLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            ImageIcon icon = getIconeTempo(p.getTempo());
            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
            
            String descricao = DESCRICOES_TEMPO.getOrDefault(p.getTempo(), "Não definido");
            JLabel descLabel = new JLabel("<html><div style='text-align: center; width: 100px;'>" + descricao + "</div></html>", SwingConstants.CENTER);
            
            JLabel tempLabel = new JLabel(p.getMinima() + "°C - " + p.getMaxima() + "°C", SwingConstants.CENTER);
            tempLabel.setFont(new Font("Arial", Font.BOLD, 12));
            
            JPanel infoPanel = new JPanel(new BorderLayout(5,5));
            infoPanel.add(descLabel, BorderLayout.CENTER);
            infoPanel.add(tempLabel, BorderLayout.SOUTH);
            
            diaPanel.add(dataLabel, BorderLayout.NORTH);
            diaPanel.add(iconLabel, BorderLayout.CENTER);
            diaPanel.add(infoPanel, BorderLayout.SOUTH);
            
            if (p.getDia().equals(hoje)) {
                diaPanel.setBackground(new Color(225, 240, 255));
                infoPanel.setBackground(new Color(225, 240, 255));
            } else {
                 diaPanel.setBackground(Color.WHITE);
                 infoPanel.setBackground(Color.WHITE);
            }
            
            previsaoPanel.add(diaPanel);
        }
        previsaoPanel.revalidate();
        previsaoPanel.repaint();
    }
    
   
    private ImageIcon getIconeTempo(String sigla) {
        try {
            // Cria uma URL para o recurso DENTRO do projeto
            URL url = getClass().getResource("/icons/" + sigla + ".png");
            
            // Verifica se o recurso foi encontrado
            if (url == null) {
                System.err.println("Ícone local não encontrado: /icons/" + sigla + ".png");
                return null;
            }
            return new ImageIcon(url);
        } catch (Exception e) {
            System.err.println("Erro ao carregar o ícone local: " + sigla + ".png");
            e.printStackTrace();
            return null;
        }
    }

    
    private static class CidadeListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Cidade) {
                Cidade c = (Cidade) value;
                label.setText(c.getNome() + " - " + c.getUf());
            }
            return label;
        }
    }
}