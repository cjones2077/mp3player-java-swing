package controle;

import entidade.Musica;
import interfaces.JanelaTocadorDeMusica;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class TocadorDeMusica extends PlaybackListener {
    private static final Object playSignal = new Object();

    private final JanelaTocadorDeMusica janelaTocadorDeMusica;

    private Musica musicaAtual;

    private AdvancedPlayer advancedPlayer;

    private boolean musicaPausada;

    private boolean musicaAcabou;

    boolean loopAtivado;

    private boolean apertouProximo, apertouAnterior;

    private int frameAtual;

    private ArrayList<Musica> playlist;

    private int tempoAtualMilissegundos;

    private int playlistIndex;

    public Musica getMusicaAtual() {
        return musicaAtual;
    }

    public void setLoop(boolean loop) {
        this.loopAtivado = loop;
    }

    public void setFrameAtual(int frame){
        frameAtual = frame;
    }

    public void setTempoAtualMilissegundos(int tempo){
        tempoAtualMilissegundos = tempo;
    }

    public TocadorDeMusica(JanelaTocadorDeMusica janelaTocadorDeMusica){
        this.janelaTocadorDeMusica = janelaTocadorDeMusica;
    }

    public void carregarMusica(Musica musica){
        if (musica == null) return;
        musicaAtual = musica;
        playlist = null;
        if (!musicaAcabou)
            pararMusica();
        if(musicaAtual != null){
            frameAtual = 0;
            tempoAtualMilissegundos = 0;
            janelaTocadorDeMusica.setValorDoSlider(0);
            tocarMusicaAtual();
        }
    }

    public void carregarPlaylist(File arquivoPlaylist){
        if(arquivoPlaylist == null) return;

        playlist = new ArrayList<>();

        try{
            FileReader fileReader = new FileReader(arquivoPlaylist);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String caminhoMusica;
            while((caminhoMusica = bufferedReader.readLine()) != null){
                Musica musica = new Musica(caminhoMusica);
                playlist.add(musica);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!playlist.isEmpty()){
            janelaTocadorDeMusica.setValorDoSlider(0);
            tempoAtualMilissegundos = 0;
            musicaAtual = playlist.getFirst();
            frameAtual = 0;
            janelaTocadorDeMusica.mostrarBotaoPause();
            janelaTocadorDeMusica.atualizarTituloArtista(musicaAtual);
            janelaTocadorDeMusica.atualizarSlider(musicaAtual);
            tocarMusicaAtual();
        }
    }


    public void pausarMusica(){
        if(advancedPlayer != null){
            musicaPausada = true;
            pararMusica();
        }
    }

    public void pararMusica(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }
    public void tocarMusicaAtual(){
        if(musicaAtual == null) return;
        try{
            FileInputStream fileInputStream = new FileInputStream(musicaAtual.getCaminhoArquivo());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            iniciarThreadMusica();
            iniciarThreadSlider();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void proximaMusica(){
        if(playlist == null) return;
        if(playlistIndex + 1 > playlist.size() + 1) return;
        apertouProximo = true;
        if(!musicaAcabou)
            pararMusica();
        playlistIndex++;
        musicaAtual = playlist.get(playlistIndex);
        frameAtual = 0;
        tempoAtualMilissegundos = 0;
        janelaTocadorDeMusica.mostrarBotaoPause();
        janelaTocadorDeMusica.atualizarTituloArtista(musicaAtual);
        janelaTocadorDeMusica.atualizarSlider(musicaAtual);
        tocarMusicaAtual();
    }

    public void musicaAnterior(){
        if(playlist == null) return;
        if(playlistIndex - 1 < 0) return;
        apertouAnterior = true;
        if(!musicaAcabou)
            pararMusica();
        playlistIndex--;
        musicaAtual = playlist.get(playlistIndex);
        frameAtual = 0;
        tempoAtualMilissegundos = 0;
        janelaTocadorDeMusica.mostrarBotaoPause();
        janelaTocadorDeMusica.atualizarTituloArtista(musicaAtual);
        janelaTocadorDeMusica.atualizarSlider(musicaAtual);
        tocarMusicaAtual();
    }

    private void iniciarThreadMusica(){
        new Thread(() -> {
            try{
                if(musicaPausada){
                    synchronized (playSignal){
                        musicaPausada = false;
                        playSignal.notify();
                    }
                    advancedPlayer.play(frameAtual, Integer.MAX_VALUE);
                }else{
                    advancedPlayer.play();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    private void iniciarThreadSlider(){
        new Thread(() -> {
            if(musicaPausada){
                try{
                    synchronized (playSignal){
                        playSignal.wait();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            while(!musicaPausada && !musicaAcabou && !apertouAnterior && !apertouProximo){
                try{
                    tempoAtualMilissegundos++;

                    int frame = (int) ((double) tempoAtualMilissegundos * 2 * musicaAtual.getFramesPorMilissegundo());

                    janelaTocadorDeMusica.setValorDoSlider(frame);

                    Thread.sleep(1);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        musicaAcabou = false;
        apertouAnterior = false;
        apertouProximo = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        if(musicaPausada){
            frameAtual += (int) ((double) evt.getFrame() * musicaAtual.getFramesPorMilissegundo());
        }else{
            if(apertouProximo || apertouAnterior) return;
            if (loopAtivado) {
                tocarMusicaAtual();
                frameAtual = 0;
                tempoAtualMilissegundos = 0;
            }else{
                musicaAcabou = true;
                if (playlist == null){
                    janelaTocadorDeMusica.mostrarBotaoPlay();
                }else{
                    if(playlistIndex == playlist.size() + 1){
                        janelaTocadorDeMusica.mostrarBotaoPlay();
                    }else{
                        proximaMusica();
                    }
                }
            }
        }
    }
}
