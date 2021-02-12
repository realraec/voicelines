package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class RightPanel extends JPanel {

  public RightPanel(MainFrame frame, Document inputDocument) {
    initRightPanel(frame, inputDocument);
  }

  protected void initRightPanel(MainFrame frame, Document inputDocument) {

    // NECESSITIES
    this.setLayout(new BorderLayout());
    Elements images = inputDocument.select("img[src~=\\.(png)]");
    String pageName = inputDocument.title();
    pageName = pageName.substring(0, inputDocument.title().indexOf("- Official") - 1).trim();
    String pageAddress = inputDocument.location();
    String urlCard;

    // CARD (IMAGE)
    switch (pageAddress) {
      // Exceptions for evolving skins (and therefore cards)
      case "https://smite.gamepedia.com/Demonic_Pact_Anubis_voicelines":
        urlCard = "https://static.wikia.nocookie.net/smite_gamepedia/images/b/b1/T_Anubis_DemonicPact_Stage3.png";
        break;
      case "https://smite.gamepedia.com/Stellar_Demise_Baron_Samedi_voicelines":
        urlCard = "https://static.wikia.nocookie.net/smite_gamepedia/images/6/6e/T_BaronSamedi_T5_Form3_Card.png";
        break;
      case "https://smite.gamepedia.com/Ragnarok_Force_X_Thor_voicelines":
        urlCard = "https://static.wikia.nocookie.net/smite_gamepedia/images/5/50/T_Thor_T5_Mech_Card.png";
        break;
      default:
        urlCard = images.attr("src");
        try {
          urlCard = urlCard.substring(0, urlCard.lastIndexOf("/revision/"));
          // Will fail if the image is not a .PNG
        } catch (StringIndexOutOfBoundsException e) {
          images = inputDocument.select("img[src~=\\.(jpg)]");
          urlCard = images.attr("src");
          urlCard = urlCard.substring(0, urlCard.lastIndexOf("/revision/"));
        }
    }
    initCard(urlCard, pageAddress);

    // SEARCH PANEL
    initSearch(frame, pageAddress, pageName);
  }

  private void initCard(String urlCard, String pageAddress) {
    this.setPreferredSize(new Dimension(264, 0));
    this.setBackground(Color.BLACK);
    try {
      URL url = new URL(urlCard);
      BufferedImage bufferedCard = ImageIO.read(url);
      ImageIcon cardImage;
      if (pageAddress.equals("https://smite.gamepedia.com/God_voicelines")
          || pageAddress.equals("https://smite.gamepedia.com/Skin_voicelines")
          || pageAddress.equals("https://smite.gamepedia.com/Announcer_packs")
          || pageAddress.contains("Announcer_pack")) {
        cardImage = new ImageIcon(bufferedCard);
      } else {
        Image bufferedCard2 = bufferedCard.getScaledInstance(250, 330, Image.SCALE_SMOOTH);
        cardImage = new ImageIcon(bufferedCard2);
      }
      JLabel cardLabel = new JLabel(cardImage);
      this.add(cardLabel, BorderLayout.CENTER);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initSearch(MainFrame frame, String pageAddress, String pageName) {
    JPanel searchPanel = new JPanel(new BorderLayout());

    // OPEN IN NEW BUTTON
    initOpenInNewButton(searchPanel, pageAddress);

    // SEARCH FIELD
    JTextField searchField = initField(frame, searchPanel, pageAddress, pageName);

    // SEARCH BUTTON
    initLoadButton(frame, searchPanel, searchField, pageAddress, pageName);

    this.add(searchPanel, BorderLayout.SOUTH);
  }

  private JTextField initField(MainFrame frame, JPanel searchPanel, String pageAddress,
      String pageName) {
    JTextField searchField = new JTextField();
    searchField.setHorizontalAlignment(0);
    if (pageName.contains("voicelines")) {
      searchField.setText(pageName.substring(0, pageName.length() - 11));
    } else {
      searchField.setText(pageName);
    }
    searchField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // If you hit enter, effectively click on the search button
        if (e.getKeyCode() == 10) {
          String userInput = checkUserInput(searchField, pageName);
          if (!userInput.equals(pageAddress)) {
            frame.performSearch(userInput);
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });
    searchPanel.add(searchField, BorderLayout.CENTER);

    return searchField;
  }

  private void initOpenInNewButton(JPanel searchPanel, String pageAddress) {
    JButton openInNewButton = new JButton("⚶");
    openInNewButton.setFocusPainted(false);
    openInNewButton.setContentAreaFilled(false);
    openInNewButton.setBorder(null);
    openInNewButton.setPreferredSize(new Dimension(25, 0));
    openInNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop()
            .isSupported(Desktop.Action.BROWSE)) {
          try {
            Desktop.getDesktop().browse(new URI(pageAddress));
          } catch (IOException | URISyntaxException ioException) {
            ioException.printStackTrace();
          }
        }
      }
    });
    searchPanel.add(openInNewButton, BorderLayout.WEST);
  }

  private void initLoadButton(MainFrame frame, JPanel searchPanel, JTextField searchField,
      String pageAddress, String pageName) {
    JButton searchButton = new JButton("Load");
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String userInput = checkUserInput(searchField, pageName);
        if (!userInput.equals(pageAddress)) {
          frame.performSearch(userInput);
        }
      }
    });
    searchPanel.add(searchButton, BorderLayout.EAST);
  }

  private String checkUserInput(JTextField searchField, String pageName) {
    String userInput = searchField.getText().trim();
    boolean isAnnouncerPack = false;

    // If given an address: extract name
    if (userInput.startsWith("https://smite.gamepedia.com/")
        && userInput.endsWith("_voicelines")
        || userInput.endsWith("_Announcer_pack")
        || userInput.endsWith("Announcer_packs")) {
      if (userInput.endsWith("_voicelines")) {
        userInput = userInput.substring(0, userInput.length() - 11);
      } else if (userInput.endsWith("_Announcer_pack")) {
        userInput = userInput.substring(0, userInput.length() - 15);
        isAnnouncerPack = true;
      }

      userInput = userInput.substring(28);
      userInput = userInput.replaceAll("_", " ");
    }

    // First letter capitalized
    char[] userInputArray = userInput.toLowerCase().toCharArray();
    try {
      userInputArray[0] = (char) (userInputArray[0] - 32);
    } catch (Exception ignored) {
    }
    userInput = String.valueOf(userInputArray);

    // Special cases 1/2
    if (userInput.contains(".bat")) {
      userInput = userInput.replace(".bat", ".BAT");
    }
    if (userInput.contains(".dmg")) {
      userInput = userInput.replace(".dmg", ".DMG");
    }
    if (userInput.contains(".exe")) {
      userInput = userInput.replace(".exe", ".EXE");
    }
    if (userInput.contains(".god")) {
      userInput = userInput.replace(".god", ".GOD");
    }
    if (userInput.contains(".rar")) {
      userInput = userInput.replace(".rar", ".RAR");
    }

    // Any letter after a blank space capitalized
    while (userInput.contains(" ")) {
      int indexToBeChanged = userInput.indexOf(" ");
      String toBeChanged = userInput.substring(indexToBeChanged, indexToBeChanged + 2);
      userInput = userInput.replace(toBeChanged, toBeChanged.toUpperCase().replace(" ", "_"));
    }
    while (pageName.contains(" ")) {
      int indexToBeChanged = pageName.indexOf(" ");
      String toBeChanged = pageName.substring(indexToBeChanged, indexToBeChanged + 2);
      pageName = pageName.replace(toBeChanged, toBeChanged.toUpperCase().replace(" ", "_"));
    }

    // Special cases 2/2
    if (userInput.contains("_The_") && !userInput.contains("Morrigan")) {
      userInput = userInput.replace("_The_", "_the_");
    }
    if (userInput.contains("_Of_")) {
      userInput = userInput.replace("_Of_", "_of_");
    }
    if (userInput.contains("_And_")) {
      userInput = userInput.replace("_And_", "_and_");
    }
    if (userInput.contains("_For_")) {
      userInput = userInput.replace("_For_", "_for_");
    }
    if (userInput.contains("_To_")) {
      userInput = userInput.replace("_To_", "_to_");
    }
    if (userInput.contains("_A_")) {
      userInput = userInput.replace("_A_", "_a_");
    }

    if (userInput.contains("G.o.a.t")) {
      userInput = userInput.replace("G.o.a.t", "G.O.A.T");
    }
    if (userInput.contains("M.o.u.s.e")) {
      userInput = userInput.replace("M.o.u.s.e", "M.O.U.S.E");
    }
    if (userInput.contains("G.e.b")) {
      userInput = userInput.replace("G.e.b", "G.E.B");
    }
    if (userInput.contains("G.i.z")) {
      userInput = userInput.replace("G.i.z", "G.I.Z");
    }

    if (userInput.contains("Dj")) {
      userInput = userInput.replace("Dj", "DJ");
    }
    if (userInput.contains("Ba5s")) {
      userInput = userInput.replace("Ba5s", "BA5S");
    }
    if (userInput.contains("Jpf")) {
      userInput = userInput.replace("Jpf", "JPF");
    }
    if (userInput.contains("Spf")) {
      userInput = userInput.replace("Spf", "SPF");
    }
    if (userInput.contains("Iii")) {
      userInput = userInput.replace("Iii", "III");
    }
    if (userInput.contains("_Iv")) {
      userInput = userInput.replace("_Iv", "_IV");
    }
    if (userInput.contains("Swc")) {
      userInput = userInput.replace("Swc", "SWC");
    }
    if (userInput.contains("Nrg")) {
      userInput = userInput.replace("Nrg", "NRG");
    }
    if (userInput.contains("Eunited")) {
      userInput = userInput.replace("Eunited", "EUnited");
    }
    if (userInput.contains("Sk_")) {
      userInput = userInput.replace("Sk_", "SK_");
    }
    if (userInput.contains("Idusa")) {
      userInput = userInput.replace("Idusa", "iDusa");
    }
    if (userInput.contains("aneith")) {
      userInput = userInput.replace("aneith", "aNeith");
    }
    if (userInput.contains("nbun")) {
      userInput = userInput.replace("nbun", "nBun");
    }
    if (userInput.contains("yodin")) {
      userInput = userInput.replace("yodin", "yOdin");
    }
    if (userInput.contains("Necr")) {
      userInput = userInput.replace("Necr", "NecR");
    }
    if (userInput.contains("atyr")) {
      userInput = userInput.replace("atyr", "aTyr");
    }
    if (userInput.contains("etyr")) {
      userInput = userInput.replace("etyr", "eTyr");
    }

    if (userInput.contains("-a-b")) {
      userInput = userInput.replace("-a-b", "-A-B");
    }
    if (userInput.contains("-a")) {
      userInput = userInput.replace("-a", "-A");
    }
    if (userInput.contains("-f")) {
      userInput = userInput.replace("-f", "-F");
    }
    if (userInput.contains("-lord")) {
      userInput = userInput.replace("-lord", "-Lord");
    }
    if (userInput.contains("-s")) {
      userInput = userInput.replace("-s", "-S");
    }
    if (userInput.contains("-t")) {
      userInput = userInput.replace("-t", "-T");
    }
    if (userInput.contains("-u")) {
      userInput = userInput.replace("-u", "-U");
    }

    // To distinguish between voicelines (by default) and announcer packs
    if (userInput.endsWith("_Voicelines")) {
      userInput = userInput.substring(0, userInput.length() - 11);
    }
    if (userInput.endsWith("_Announcer_Pack")) {
      userInput = userInput.substring(0, userInput.length() - 15);
      isAnnouncerPack = true;
    }
    if (userInput.endsWith("*")) {
      userInput = userInput.substring(0, userInput.length() - 1);
      isAnnouncerPack = true;
    }

    // Necessary for special characters and to make sure the URL will not have special characters
    if (pageName.endsWith("_Voicelines")) {
      pageName = pageName.substring(0, pageName.length() - 11) + "_voicelines";
    }
    String userInputFinal = "";
    String pageNameFinal = "";
    try {
      URI userInputURI = new URI(userInput);
      userInputFinal = userInputURI.toASCIIString();
      URI pageNameURI = new URI(pageName);
      pageNameFinal = pageNameURI.toASCIIString();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }


    // If not address, logically given a name
    if (!userInputFinal.equals("")) {
      if (userInputFinal.equals("Announcer_Packs")) {
        userInputFinal = "https://smite.gamepedia.com/Announcer_packs";
      } else if (!isAnnouncerPack && !userInputFinal.concat("_voicelines").equals(pageNameFinal)) {
        userInputFinal = "https://smite.gamepedia.com/" + userInputFinal + "_voicelines";
      } else if (isAnnouncerPack && !userInputFinal.concat(" Announcer pack")
          .equals(pageNameFinal)) {
        userInputFinal = "https://smite.gamepedia.com/" + userInputFinal + "_Announcer_pack";
      }
    }

    return userInputFinal;
  }


}
