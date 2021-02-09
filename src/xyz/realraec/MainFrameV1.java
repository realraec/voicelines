package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainFrameV1 extends JFrame {

  String url = "";
  private static final JPanel mainPanel = new JPanel(new BorderLayout());
  private static JPanel rightPanel = null;
  private static JPanel leftPanel = null;


  public MainFrameV1(String url) throws IOException {
    this.url = url;

    this.setSize(1100, 435);
    this.setMinimumSize(new Dimension(850, 435));
    this.setLocationRelativeTo(null);
    this.setLocation(100, 150);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Document
    Document inputDocument = initDocument(url);

    // Title
    initTitle(inputDocument);

    // Buttons and text
    //leftPanel = LeftPanel.initLeftPanel(this, inputDocument);

    // Card and search
    //rightPanel = RightPanel.initRightPanel(this, inputDocument);

    // Menu bar
    this.setJMenuBar(initMenuBar());

    this.setContentPane(mainPanel);
    this.setVisible(true);
  }

  static Document initDocument(String url) throws IOException {
    return Jsoup.connect(url).get();
  }

  void initTitle(Document inputDocument) {
    String titleRaw = inputDocument.title();
    this.setTitle(titleRaw.substring(0, titleRaw.lastIndexOf(" - ")));
  }

  public JPanel getLeftPanel() {
    return leftPanel;
  }

  public static void setLeftPanel(JPanel panel) {
    leftPanel = panel;
  }

  public void removeLeftPanel() {
    mainPanel.remove(leftPanel);
  }

  public JPanel getRightPanel() {
    return rightPanel;
  }

  public static void setRightPanel(JPanel panel) {
    rightPanel = panel;
  }

  public void removeRightPanel() {
    mainPanel.remove(rightPanel);
  }

  public static JPanel getMainPanel() {
    return mainPanel;
  }

  private JMenuBar initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenuItem godsListItem = new JMenuItem("Gods list");
    JMenuItem skinsListItem = new JMenuItem("Skins list");
    JMenu listsMenu = new JMenu("Lists");
    listsMenu.add(godsListItem);
    listsMenu.add(skinsListItem);
    JMenuItem explorerItem = new JMenuItem("Explorer");
    JMenuItem favoritesItem = new JMenuItem("Favorites");
    JMenu singlesMenu = new JMenu("Viewer");
    singlesMenu.add(explorerItem);
    singlesMenu.add(favoritesItem);
    menuBar.add(listsMenu);
    menuBar.add(singlesMenu);

    return menuBar;
  }
}
