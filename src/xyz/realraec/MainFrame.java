package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainFrame extends JFrame {

  public MainFrame(String url) throws IOException {

    this.setSize(1100, 435);
    this.setMinimumSize(new Dimension(850, 435));
    this.setLocationRelativeTo(null);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Document
    Document inputDocument = initDocument(url);

    // Title
    initTitle(inputDocument);

    // Buttons and text
    initLeft(inputDocument);

    // Card and search
    initRight(inputDocument);

    // Menu bar
    initMenuBar();

    this.setVisible(true);
  }

  public Document initDocument(String url) throws IOException {
    return Jsoup.connect(url).get();
  }

  public void initTitle(Document inputDocument) {
    String titleRaw = inputDocument.title();
    this.setTitle(titleRaw.substring(0, titleRaw.lastIndexOf(" - ")));
  }

  public void initLeft(Document inputDocument) {
    // Check and reset to make the previous page disappear if there is one
    BorderLayout layout = (BorderLayout)this.getContentPane().getLayout();
    if (layout.getLayoutComponent(BorderLayout.CENTER) != null) {
      this.getContentPane().remove(layout.getLayoutComponent(BorderLayout.CENTER));
    }

    LeftPanel leftPanel = new LeftPanel(this, inputDocument);
    this.getContentPane().add(leftPanel, BorderLayout.CENTER);
  }

  public void initRight(Document inputDocument) {
    // Check and reset to make the previous page disappear if there is one
    BorderLayout layout = (BorderLayout)this.getContentPane().getLayout();
    if (layout.getLayoutComponent(BorderLayout.EAST) != null) {
      this.getContentPane().remove(layout.getLayoutComponent(BorderLayout.EAST));
    }

    RightPanel rightPanel = new RightPanel(this, inputDocument);
    this.getContentPane().add(rightPanel, BorderLayout.EAST);
  }

  private void initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenuItem godsListItem = new JMenuItem("God voicelines");
    godsListItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performSearch("https://smite.gamepedia.com/God_voicelines");
      }
    });
    JMenuItem skinsListItem = new JMenuItem("Skin voicelines");
    skinsListItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performSearch("https://smite.gamepedia.com/Skin_voicelines");
      }
    });
    JMenuItem announcerPacksItem = new JMenuItem("Announcer packs");
    announcerPacksItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performSearch("https://smite.gamepedia.com/Announcer_packs");
      }
    });
    JMenu listsMenu = new JMenu("Lists");
    listsMenu.add(godsListItem);
    listsMenu.add(skinsListItem);
    listsMenu.add(announcerPacksItem);
    JMenuItem explorerItem = new JMenuItem("Explorer");
    JMenuItem favoritesItem = new JMenuItem("Favorites");
    favoritesItem.setEnabled(false);
    JMenu singlesMenu = new JMenu("Viewer");
    singlesMenu.add(explorerItem);
    singlesMenu.add(favoritesItem);
    menuBar.add(listsMenu);
    menuBar.add(singlesMenu);
    this.setJMenuBar(menuBar);
  }

  public void performSearch(String userInput) {
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
      try {
        Document newDocument = this.initDocument(userInput);
        System.out.println("Fetching " + userInput);
        this.initTitle(newDocument);
        this.initLeft(newDocument);
        this.initRight(newDocument);
        this.getContentPane().revalidate();

      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else {
      JOptionPane.showMessageDialog(null, "Make sure you've entered a valid link or name.");
    }
  }
}
