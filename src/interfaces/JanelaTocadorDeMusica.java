package interfaces;
import entidade.Musica;
import controle.TocadorDeMusica;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class JanelaTocadorDeMusica extends JFrame {

    public static final Color FRAME_COLOR = Color.black;
    public static final Color TEXT_COLOR = Color.white;

    private final TocadorDeMusica tocadorDeMusica;

    private final JFileChooser jFileChooser;

    private JLabel tituloLabel, artistaLabel;

    private JPanel botoesPanel;

    private JSlider musicaSlider;

    public JanelaTocadorDeMusica(){
        // constrói a janela com título "Tocador de Música"
        super("MP3 Player");

        setSize(400,600);

        // termina o processo ao fechar
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // programa abre centralizado na tela
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);

        tocadorDeMusica = new TocadorDeMusica(this);

        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/assets"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        adicionarComponentes();
    }
    private void adicionarComponentes(){
        adicionarToolBar();
        String caminho = "src/assets/icon.png";
        JLabel imagem = new JLabel(carregarImagem(caminho));
        imagem.setBounds(0, 50, getWidth() - 20, 225);
        add(imagem);

        tituloLabel = new JLabel("Título");
        tituloLabel.setBounds(0, 285, getWidth() - 10, 30);
        tituloLabel.setFont(new Font("Dialog",Font.BOLD, 24));
        tituloLabel.setForeground(TEXT_COLOR);
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(tituloLabel);

        artistaLabel = new JLabel("Artista");
        artistaLabel.setBounds(0, 315, getWidth() - 10, 30);
        artistaLabel.setFont(new Font("Dialog",Font.PLAIN, 24));
        artistaLabel.setForeground(TEXT_COLOR);
        artistaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(artistaLabel);

        musicaSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        musicaSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        musicaSlider.setBackground(null);
        musicaSlider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                tocadorDeMusica.pausarMusica();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JSlider source = (JSlider) e.getSource();
                int frame = source.getValue();
                tocadorDeMusica.setFrameAtual(frame);
                int tempoEmMilli = (int) (frame / (2 * tocadorDeMusica.getMusicaAtual().getFramesPorMilissegundo()));
                tocadorDeMusica.setTempoAtualMilissegundos(tempoEmMilli);
                tocadorDeMusica.tocarMusicaAtual();
                mostrarBotaoPause();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        add(musicaSlider);

        adicionarItensInteracao();
    }

    private void adicionarToolBar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(),20);

        // previne a ToolBar de ser movida
        toolBar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        JMenu musicasMenu = new JMenu("Musicas");
        menuBar.add(musicasMenu);

        JMenuItem carregarMusicaMenuItem = new JMenuItem("Carregar Música");
        carregarMusicaMenuItem.addActionListener(_ -> {
            jFileChooser.setDialogTitle("Abrir Música");
            int resultado = jFileChooser.showOpenDialog(JanelaTocadorDeMusica.this);
            File arquivoSelecionado = jFileChooser.getSelectedFile();

            if(resultado == JFileChooser.APPROVE_OPTION && arquivoSelecionado != null){
                Musica musica = new Musica(arquivoSelecionado.getPath());
                tocadorDeMusica.carregarMusica(musica);
                atualizarTituloArtista(musica);

                atualizarSlider(musica);
                mostrarBotaoPause();
            }
        });
        musicasMenu.add(carregarMusicaMenuItem);

        JMenu playlistMenu = new JMenu("Paylists");
        menuBar.add(playlistMenu);

        JMenuItem criarPlaylistMenuItem = new JMenuItem("Criar Playlist");
        criarPlaylistMenuItem.addActionListener(_ -> new PlaylistDialog(JanelaTocadorDeMusica.this).setVisible(true));
        playlistMenu.add(criarPlaylistMenuItem);

        JMenuItem carregarPlaylistMenuItem = new JMenuItem("Carregar Playlist");
        carregarPlaylistMenuItem.addActionListener(_ -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Abrir Playlist");
            jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
            jFileChooser.setCurrentDirectory(new File("src/assets"));

            int resultado = jFileChooser.showOpenDialog(JanelaTocadorDeMusica.this);
            File arquivoSelecionado = jFileChooser.getSelectedFile();

            if (resultado == JFileChooser.APPROVE_OPTION && arquivoSelecionado != null){
                tocadorDeMusica.pausarMusica();
            }
            tocadorDeMusica.carregarPlaylist(arquivoSelecionado);
        });
        playlistMenu.add(carregarPlaylistMenuItem);

        add(toolBar);
    }

    private void adicionarItensInteracao(){
        botoesPanel = new JPanel();
        botoesPanel.setBounds(13, 435, getWidth(), 80);
        botoesPanel.setBackground(null);

        String caminhoImagemAnterior = "src/assets/previous.png";
        String caminhoImagemPlay = "src/assets/play.png";
        String caminhoImagemPause = "src/assets/pause.png";
        String caminhoImagemProximo = "src/assets/next.png";
        String caminhoImagemLoop = "src/assets/loop.png";

        JButton anteriorButton = new JButton(carregarImagem(caminhoImagemAnterior));
        anteriorButton.setBorderPainted(false);
        anteriorButton.setBackground(null);
        anteriorButton.addActionListener(_ -> tocadorDeMusica.musicaAnterior());
        botoesPanel.add(anteriorButton);

        JButton playButton = new JButton(carregarImagem(caminhoImagemPlay));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(_ -> {
            mostrarBotaoPause();
            tocadorDeMusica.tocarMusicaAtual();
        });
        botoesPanel.add(playButton);

        JButton pauseButton = new JButton(carregarImagem(caminhoImagemPause));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(_ -> {
            mostrarBotaoPlay();
            tocadorDeMusica.pausarMusica();
        });
        botoesPanel.add(pauseButton);

        JButton proximoButton = new JButton(carregarImagem(caminhoImagemProximo));
        proximoButton.setBorderPainted(false);
        proximoButton.setBackground(null);
        proximoButton.addActionListener(_ -> tocadorDeMusica.proximaMusica());
        botoesPanel.add(proximoButton);

        JToggleButton loopToggleButton = new JToggleButton(carregarImagem(caminhoImagemLoop));
        loopToggleButton.setBorderPainted(false);
        loopToggleButton.setBackground(null);
        loopToggleButton.addActionListener(_ -> tocadorDeMusica.setLoop(loopToggleButton.isSelected()));
        loopToggleButton.setPreferredSize(new Dimension(40,40));
        botoesPanel.add(loopToggleButton);

        add(botoesPanel);
    }

    public void setValorDoSlider(int frame){
        musicaSlider.setValue(frame);
    }

    public void atualizarTituloArtista(Musica musica){
        tituloLabel.setText(musica.getTitulo());
        artistaLabel.setText(musica.getArtista());
    }

    public void atualizarSlider(Musica musica){
        musicaSlider.setMaximum(musica.getMp3File().getFrameCount());

        Hashtable<Integer, JLabel> tabelaDeLabels = new Hashtable<>();

        JLabel comecoLabel = new JLabel("00:00");
        comecoLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        comecoLabel.setForeground(TEXT_COLOR);

        JLabel fimLabel = new JLabel(musica.getDuracao());
        fimLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        fimLabel.setForeground(TEXT_COLOR);

        tabelaDeLabels.put(0, comecoLabel);
        tabelaDeLabels.put(musica.getMp3File().getFrameCount(), fimLabel);

        musicaSlider.setLabelTable(tabelaDeLabels);
        musicaSlider.setPaintLabels(true);
    }

    public void mostrarBotaoPause(){
        JButton playButton = (JButton) botoesPanel.getComponent(1);
        JButton pauseButton = (JButton) botoesPanel.getComponent(2);
        playButton.setVisible(false);
        playButton.setEnabled(false);
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void mostrarBotaoPlay(){
        JButton playButton = (JButton) botoesPanel.getComponent(1);
        JButton pauseButton = (JButton) botoesPanel.getComponent(2);
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
        playButton.setVisible(true);
        playButton.setEnabled(true);
    }
    private ImageIcon carregarImagem(String caminho){
        try{
            BufferedImage imagem = ImageIO.read(new File(caminho));
            return new ImageIcon(imagem);
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}

