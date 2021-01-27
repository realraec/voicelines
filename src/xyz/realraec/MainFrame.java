package xyz.realraec;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainFrame extends JFrame {

  String url = "";
  private static final JPanel mainPanel = new JPanel(new BorderLayout());
  private static JPanel rightPanel = null;
  private static JPanel leftPanel = null;


  public MainFrame(String url) throws IOException {
    this.url = url;

    this.setSize(1100, 410);
    this.setLocationRelativeTo(null);
    this.setLocation(100, 150);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Document
    Document inputDocument = initDocument(url);

    // Title
    initTitle(inputDocument);

    // Buttons and text
    leftPanel = LeftPanel.initLeftPanel(this, inputDocument);

    // Card and search
    rightPanel = RightPanel.initRightPanel(this, inputDocument);

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
}
