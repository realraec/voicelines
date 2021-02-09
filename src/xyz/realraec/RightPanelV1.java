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
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class RightPanelV1 extends JPanel {

  protected static JPanel initRightPanel(MainFrame frame, Document inputDocument) {

    // Reset to make the previous page disappear if there is one
    /*if (frame.getRightPanel() != null) {
      frame.removeRightPanel();
    }*/

    // CARD (IMAGE)
    /*
    Elements images = inputDocument.select("img[src~=(_Card)\\.(png)]");
    */
    Elements images = inputDocument.select("img[src~=\\.(png)]");
    String skinName = inputDocument.title()
        .substring(0, inputDocument.title().indexOf("voicelines") - 1).trim();
    String skinAddress = inputDocument.location();

    String urlCard = images.attr("src");
    urlCard = urlCard.substring(0, urlCard.lastIndexOf("/revision/"));
    /*String urlIcon = inputDocument.select("link[href~=\\.(ico)]").attr("href");
    System.out.println(urlIcon);
    urlIcon = urlIcon.substring(0, urlIcon.lastIndexOf("/revision/"));*/

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setPreferredSize(new Dimension(264, 0));
    rightPanel.setBackground(Color.BLACK);
    try {
      URL url = new URL(urlCard);
      BufferedImage bufferedCard = ImageIO.read(url);
      Image bufferedCard2 = bufferedCard.getScaledInstance(250, 330, Image.SCALE_SMOOTH);
      ImageIcon cardImage = new ImageIcon(bufferedCard2);
      JLabel cardLabel = new JLabel(cardImage);
      rightPanel.add(cardLabel, BorderLayout.CENTER);
    } catch (IOException e) {
      e.printStackTrace();
    }

    /* -----------------------------------------------------
    ----------------------------------------------------- */

    // SEARCH PANEL
    JPanel searchPanel = new JPanel(new BorderLayout());

    // OPEN IN NEW BUTTON
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
            Desktop.getDesktop().browse(new URI(skinAddress));
          } catch (IOException | URISyntaxException ioException) {
            ioException.printStackTrace();
          }
        }
      }
    });
    searchPanel.add(openInNewButton, BorderLayout.WEST);

    // SEARCH FIELD
    JTextField searchField = new JTextField();
    searchField.setHorizontalAlignment(0);
    searchField.setText(skinName);
    searchPanel.add(searchField, BorderLayout.CENTER);

    // SEARCH BUTTON
    JButton searchButton = new JButton("Load");
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String userInput = searchField.getText().trim();

        // If given an address: extract name
        if (userInput.startsWith("https://smite.gamepedia.com/")
            && userInput.endsWith("_voicelines")) {
          userInput = userInput.substring(28);
          userInput = userInput.substring(0, userInput.length() - 11);
          userInput = userInput.replaceAll("_", " ");
        }

        // First letter capitalized
        char[] userInputArray = userInput.toLowerCase().toCharArray();
        userInputArray[0] = (char) (userInputArray[0] - 32);
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

        // Special cases 2/2
        if (userInput.contains("_Of_")) {
          userInput = userInput.replace("_Of_", "_of_");
        }
        if (userInput.contains("_The_")) {
          userInput = userInput.replace("_The_", "_the_");
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
        //System.out.println("\t\t\t\t\t finalUserInput: " + userInput);

        // If not address, logically given a name
        if (!userInput.equals("") && !userInput.equals(skinName)) {
          userInput = "https://smite.gamepedia.com/" + userInput + "_voicelines";
        }
        if (!userInput.equals(skinAddress)) {
          performSearch(frame, userInput);
        }
      }
    });
    searchPanel.add(searchButton, BorderLayout.EAST);
    searchField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // If you hit enter, effectively click on the search button
        if (e.getKeyCode() == 10) {
          searchButton.doClick();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });
    rightPanel.add(searchPanel, BorderLayout.SOUTH);
    //MainFrame.getMainPanel().add(rightPanel, BorderLayout.EAST);

    return rightPanel;
  }

  private static void performSearch(MainFrame frame, String userInput) {
    int code;
    try {
      URL possibleUrl = new URL(userInput);
      HttpsURLConnection httpsURLConnection = (HttpsURLConnection) possibleUrl
          .openConnection();
      httpsURLConnection.setRequestMethod("GET");
      httpsURLConnection.connect();
      code = httpsURLConnection.getResponseCode();
    } catch (IOException ignored) {
      return;
    }
    //System.out.println(code);

    if (code == 200) {
      /*try {
        Document newDocument = MainFrame.initDocument(userInput);
        System.out.println("Fetching " + userInput);
        frame.initTitle(newDocument);
        *//*MainFrame.setRightPanel(RightPanelV1.initRightPanel(frame, newDocument));
        MainFrame.setLeftPanel(LeftPanel.initLeftPanel(frame, newDocument));
        MainFrame.getMainPanel().revalidate();*//*
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }*/
    } else {
      JOptionPane.showMessageDialog(null, "Make sure you've entered a valid link or name.");
    }
  }
}
