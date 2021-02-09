package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainFrame extends JFrame {

  public MainFrame(String url) throws IOException {

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
    initLeftPanel(inputDocument);

    // Card and search
    initRightPanel(inputDocument);

    // Menu bar
    this.setJMenuBar(initMenuBar());

    this.setVisible(true);
  }

  public Document initDocument(String url) throws IOException {
    return Jsoup.connect(url).get();
  }

  public void initTitle(Document inputDocument) {
    String titleRaw = inputDocument.title();
    this.setTitle(titleRaw.substring(0, titleRaw.lastIndexOf(" - ")));
  }

  public void initLeftPanel(Document inputDocument) {
    // Check and reset to make the previous page disappear if there is one
    BorderLayout layout = (BorderLayout)this.getContentPane().getLayout();
    if (layout.getLayoutComponent(BorderLayout.CENTER) != null) {
      this.getContentPane().remove(layout.getLayoutComponent(BorderLayout.CENTER));
    }

    LeftPanel leftPanel = new LeftPanel(inputDocument);
    this.getContentPane().add(leftPanel, BorderLayout.CENTER);
  }

  public void initRightPanel(Document inputDocument) {
    // Check and reset to make the previous page disappear if there is one
    BorderLayout layout = (BorderLayout)this.getContentPane().getLayout();
    if (layout.getLayoutComponent(BorderLayout.EAST) != null) {
      this.getContentPane().remove(layout.getLayoutComponent(BorderLayout.EAST));
    }

    RightPanel rightPanel = new RightPanel(this, inputDocument);
    this.getContentPane().add(rightPanel, BorderLayout.EAST);
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
