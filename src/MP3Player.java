import interfaces.JanelaTocadorDeMusica;

import javax.swing.*;

public class MP3Player {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JanelaTocadorDeMusica().setVisible(true);
            }
        });
    }
}
