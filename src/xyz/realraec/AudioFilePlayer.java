package xyz.realraec;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFilePlayer {

  private volatile static AudioFilePlayer player = new AudioFilePlayer();

  public static AudioFilePlayer getInstance() {
    if (player == null) {
      synchronized (AudioFilePlayer.class) {
        player = new AudioFilePlayer();
      }
    }
    return player;
  }


/*  public static void main(String[] args) {

    String soundURL = "https://static.wikia.nocookie.net/smite_gamepedia/images/2/23/Ramerica_Taunt_Directed_Aphrodite.ogg";
    String soundPath = "C:/Users/Pierre/Desktop/Ramerica_Taunt_Directed_Aphrodite.oga";

    final AudioFilePlayer player = new AudioFilePlayer();
    player.play(soundURL);
  }*/

  public void play(String filePath) {

/*    final File file = new File(filePath);
    try (final AudioInputStream in = getAudioInputStream(file)) {*/

    try (final AudioInputStream in = getAudioInputStream(new URL(filePath))) {

      final AudioFormat outFormat = getOutFormat(in.getFormat());
      final Info info = new Info(SourceDataLine.class, outFormat);

      try (final SourceDataLine line =
          (SourceDataLine) AudioSystem.getLine(info)) {

        if (line != null) {
          line.open(outFormat);
          line.start();
          stream(getAudioInputStream(outFormat, in), line);
          line.drain();
          line.stop();
        }
      } catch (LineUnavailableException e) {
        e.printStackTrace();
      }

    } catch (UnsupportedAudioFileException
        | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private AudioFormat getOutFormat(AudioFormat inFormat) {
    final int ch = inFormat.getChannels();
    final float rate = inFormat.getSampleRate();
    return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
  }

  private void stream(AudioInputStream in, SourceDataLine line)
      throws IOException {
    final byte[] buffer = new byte[65536];
    for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
      line.write(buffer, 0, n);
    }
  }
}
