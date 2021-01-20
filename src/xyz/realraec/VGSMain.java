package xyz.realraec;

import java.io.IOException;
import org.jsoup.helper.Validate;

public class VGSMain {


  public static void main(String[] args) throws IOException {

    String url = "";
    Validate.isTrue(args.length == 1, "Supply URL to fetch.");
    url = args[0];
    //url = https://smite.gamepedia.com/Ra%27merica_voicelines";
    //url = "https://smite.gamepedia.com/Baron_Samedi_voicelines";
    //url = "https://smite.gamepedia.com/Bacchus_voicelines";
    //url = "https://smite.gamepedia.com/King_Arthur_voicelines";
    url = "https://smite.gamepedia.com/Chang'e_voicelines";
    //url = "https://smite.gamepedia.com/Fafnir_voicelines";
    System.out.println("Fetching " + url);

    // Frame
    new MainFrame(url);

  }


}
