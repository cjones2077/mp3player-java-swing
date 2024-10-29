package interfaces;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class PlaylistDialog extends JDialog {
    private final ArrayList<String> caminhosMusicas;

    public PlaylistDialog(JanelaTocadorDeMusica janelaTocadorDeMusica){
        caminhosMusicas = new ArrayList<>();

        setTitle("Criar Playlist");
        setSize(400,400);
        setResizable(false);
        getContentPane().setBackground(JanelaTocadorDeMusica.FRAME_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(janelaTocadorDeMusica);

        adicionarComponentes();
    }

    private void adicionarComponentes(){
        JPanel musicasContainerPanel = new JPanel();
        musicasContainerPanel.setLayout(new BoxLayout(musicasContainerPanel, BoxLayout.Y_AXIS));
        musicasContainerPanel.setBounds((int)(getWidth() * 0.025), 10, (int)(getWidth() * 0.90), (int) (getHeight() * 0.75));
        add(musicasContainerPanel);

        JButton adicionarMusicaButton = new JButton("Adcionar");
        adicionarMusicaButton.setBounds(60, (int)(getHeight() * 0.80), 100, 25);
        adicionarMusicaButton.setFont(new Font("Dialog", Font.BOLD, 14));
        adicionarMusicaButton.addActionListener(_ -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
            jFileChooser.setCurrentDirectory(new File("src/assets"));
            int resultado = jFileChooser.showOpenDialog(PlaylistDialog.this);
            File arqSelecionado = jFileChooser.getSelectedFile();

            if(resultado == JFileChooser.APPROVE_OPTION && arqSelecionado != null){
                JLabel caminhoDoArquivoLabel = new JLabel(arqSelecionado.getPath());
                caminhoDoArquivoLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                caminhoDoArquivoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                caminhosMusicas.add(caminhoDoArquivoLabel.getText());
                musicasContainerPanel.add(caminhoDoArquivoLabel);
                musicasContainerPanel.revalidate();
            }
        });
        add(adicionarMusicaButton);

        JButton salvarPlaylistButton = new JButton("Salvar");
        salvarPlaylistButton.setBounds(215, (int)(getHeight() * 0.80), 100, 25);
        salvarPlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
        salvarPlaylistButton.addActionListener(_ -> {
            try{
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setCurrentDirectory(new File("src/assets"));
                jFileChooser.setDialogTitle("Salvar Playlist");
                int resultado = jFileChooser.showSaveDialog(PlaylistDialog.this);

                if (resultado == JFileChooser.APPROVE_OPTION){
                    File arquivoSelecionado = jFileChooser.getSelectedFile();

                    if (!arquivoSelecionado.getName().substring(arquivoSelecionado.getName().length() - 4).equalsIgnoreCase("txt")){
                        arquivoSelecionado = new File(arquivoSelecionado.getAbsoluteFile() + ".txt");
                    }

                    arquivoSelecionado.createNewFile();

                    FileWriter fileWriter = new FileWriter(arquivoSelecionado);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    for(String caminho : caminhosMusicas){
                        bufferedWriter.write(caminho + "\n");
                    }
                    bufferedWriter.close();

                    JOptionPane.showMessageDialog(PlaylistDialog.this, "Playlist Criada com Sucesso");
                    PlaylistDialog.this.dispose();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        add(salvarPlaylistButton);
    }
}
