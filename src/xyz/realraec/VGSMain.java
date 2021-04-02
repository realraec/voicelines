package xyz.realraec;

import java.io.IOException;
import org.jsoup.helper.Validate;

public class VGSMain {

  public static void main(String[] args) throws IOException {

    String url = "";
    /*
    Either with jSoup
    Validate.isTrue(args.length == 1, "Supply URL to fetch.");
    url = args[0];

    or without
    assert args.length == 1;
    url = args[0];
    */

    //url = "https://smite.gamepedia.com/Baron_Samedi_voicelines";
    //url = "https://smite.gamepedia.com/Fafnir_voicelines";
    //url = "https://smite.gamepedia.com/Cu_Chulainn_voicelines";
    //url = "https://smite.gamepedia.com/Kukulkan_voicelines";
    //url = "https://smite.gamepedia.com/Ymir_voicelines";
    //url = "https://smite.gamepedia.com/Stellar_Demise_Baron_Samedi_voicelines";
    //url = "https://smite.gamepedia.com/Cu_Chulainn_voicelines";
    //url = "https://smite.gamepedia.com/Demonic_Pact_Anubis_voicelines";
    //url = "https://smite.gamepedia.com/Hel_voicelines";
    //url = "https://smite.gamepedia.com/Sylvanus_voicelines";
    //url = "https://smite.gamepedia.com/Cthulhu_voicelines";
    //url = "https://smite.gamepedia.com/Geb_voicelines";
    //url = "https://smite.gamepedia.com/Aggro_Announcer_pack";
    //url = "https://smite.gamepedia.com/Announcer_Packs";
    //url = "https://smite.gamepedia.com/God_voicelines";
    //url = "https://smite.gamepedia.com/Skin_voicelines";
    //url = "https://smite.gamepedia.com/Ma_Ch%C3%A9rie_Arachne_voicelines";
    //url = "https://smite.gamepedia.com/Unbreakable_Anhur_voicelines";
    //url = "https://smite.gamepedia.com/Winds_of_Change_Kukulkan_voicelines";
    //url = "https://smite.gamepedia.com/King_Arthur_voicelines";
    url = "https://smite.gamepedia.com/Chang'e_voicelines";
    url = "https://smite.gamepedia.com/Divine_Dragon_Bellona_voicelines";
    url = "https://smite.gamepedia.com/Ra%27merica_voicelines";

    System.out.println("Fetching " + url);

    // Frame
    new MainFrame(url);
  }
}
