package xyz.realraec;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class TableContentActionListener implements ActionListener {

  private final ArrayList<JComponent> componentsList;
  private final JScrollPane scrollButtonPane;
  private final Font subSubSectionFont;
  private final int numberColumns, componentHeight, h;

  TableContentActionListener(ArrayList<JComponent> componentsList, JScrollPane scrollButtonPane,
      Font subSubSectionFont, int numberColumns, int componentHeight, int h) {
    this.componentsList = componentsList;
    this.scrollButtonPane = scrollButtonPane;
    this.subSubSectionFont = subSubSectionFont;
    this.numberColumns = numberColumns;
    this.componentHeight = componentHeight;
    this.h = h;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton source = (JButton) e.getSource();

    // Necessities before loop but still in the Listener: table of contents
    JLabel basicLabel = new JLabel(" ");
    int i = 0;
    Font fontOfInterest1 = basicLabel.getFont();
    JComponent componentOfInterest1 = null;
    if (source.getFont().equals(subSubSectionFont)) {
      while (fontOfInterest1.equals(subSubSectionFont)
          || fontOfInterest1.equals(basicLabel.getFont())) {
        try {
          fontOfInterest1 = componentsList.get(h - i).getFont();
        } catch (ClassCastException e2) {
          i++;
        }
        i++;
      }
      componentOfInterest1 = componentsList.get(h - i + 1);
    }

    // Loop to check
    JComponent finalComponentOfInterest1 = componentOfInterest1;
    for (int j = 0; j < componentsList.size(); j++) {
      if (componentsList.get(j) instanceof JLabel
          && source.getText().equals(((JLabel) componentsList.get(j)).getText())
          && source.getFont().equals(componentsList.get(j).getFont())) {

        // Necessities in the loop: explorer
        int k = 0;
        Font fontOfInterest2 = basicLabel.getFont();
        JComponent componentOfInterest2 = null;
        if (componentsList.get(j).getFont().equals(subSubSectionFont)) {
          while (fontOfInterest2.equals(subSubSectionFont)
              || fontOfInterest2.equals(basicLabel.getFont())) {
            try {
              fontOfInterest2 = componentsList.get(j - k).getFont();
            } catch (ClassCastException e1) {
              k++;
            }
            k++;
          }
          componentOfInterest2 = componentsList.get(j - k + 1);
        }

        // Ultimate check
        // No need to loop if non-subsubsection
        if (!source.getFont().equals(subSubSectionFont)) {
          scrollButtonPane.getVerticalScrollBar().setValue((j + 1) / numberColumns * componentHeight);
          j = componentsList.size() - 1;
        } else {
          if (finalComponentOfInterest1.equals(componentOfInterest2)) {
            scrollButtonPane.getVerticalScrollBar().setValue((j + 1) / numberColumns * componentHeight);
            j = componentsList.size() - 1;
          }
        }
      }
    }
  }
}
