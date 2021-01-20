package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

public class RightPanel extends JPanel {

  protected static JPanel initRightPanel(MainFrame frame, Document inputDocument) {

    // Reset to make the previous page disappear if there is one
    if (frame.getRightPanel() != null) {
      frame.removeRightPanel();
    }

    // CARD (IMAGE)
    Elements images = inputDocument.select("img[src~=(_Card)\\.(png)]");
    String urlCard = images.attr("src");
    urlCard = urlCard.substring(0, urlCard.lastIndexOf("/revision/"));

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

    // SEARCH
    JPanel searchPanel = new JPanel(new BorderLayout());
    JTextField searchField = new JTextField();
    JButton searchButton = new JButton("Link");
    searchField.setText(inputDocument.location());
    searchPanel.add(searchField, BorderLayout.CENTER);
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String userInput = searchField.getText().trim();
        if (userInput.startsWith("https://smite.gamepedia.com/")
            && userInput.endsWith("_voicelines")
            && !userInput.equals(inputDocument.location())
        ) {

          int code = 0;
          try {
            URL possibleUrl = new URL(userInput);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) possibleUrl
                .openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();
            code = httpsURLConnection.getResponseCode();
          } catch (IOException malformedURLException) {
            malformedURLException.printStackTrace();
          }

          if (code == 200) {
            try {
              Document newDocument = MainFrame.initDocument(userInput);
              System.out.println("Fetching " + userInput);
              frame.initTitle(newDocument);
              MainFrame.setRightPanel(RightPanel.initRightPanel(frame, newDocument));
              MainFrame.setLeftPanel(LeftPanel.initLeftPanel(frame,newDocument));
              MainFrame.getMainPanel().revalidate();
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          } else {
            JOptionPane.showMessageDialog(null, "Make sure you've entered a valid link.");
          }
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

    MainFrame.getMainPanel().add(rightPanel, BorderLayout.EAST);
    return rightPanel;
  }


}
