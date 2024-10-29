package entidade;

import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;


public class Musica {
    private String titulo;
    private String artista;
    private String duracao;
    private final String caminhoArquivo;
    private Mp3File mp3File;
    private double framesPorMilissegundo;

    public Musica(String caminho){
        this.caminhoArquivo = caminho;
        try{
            mp3File = new Mp3File(caminho);
            framesPorMilissegundo = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            AudioFile audioFile = AudioFileIO.read(new File(caminho));
            duracao = converterDuracaoDaMusica();

            Tag tag = audioFile.getTag();
            if(tag != null){
                titulo = tag.getFirst(FieldKey.TITLE);
                artista = tag.getFirst(FieldKey.ARTIST);
            }else{
                titulo = "N/A";
                artista = "N/A";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String converterDuracaoDaMusica(){
        //usado para mostrar o tempo abaixo do slider
        long minutos = mp3File.getLengthInSeconds() / 60;
        long segundos = mp3File.getLengthInSeconds() % 60;

        return String.format("%02d:%02d", minutos, segundos);
    }
    public String getTitulo() {
        return titulo;
    }

    public String getArtista() {
        return artista;
    }

    public String getDuracao() {
        return duracao;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public double getFramesPorMilissegundo() {
        return framesPorMilissegundo;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }
}
