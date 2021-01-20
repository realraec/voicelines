package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LeftPanel {

  protected static JPanel initLeftPanel(MainFrame frame, Document inputDocument) {

    // Reset to make the previous page disappear if there is one
    if (frame.getLeftPanel() != null) {
      frame.removeLeftPanel();
    }

    // TEXT
    JTextField textField = new JTextField();
    textField.setEditable(false);
    textField.setHorizontalAlignment(0);

    // BUTTONS
    // 3 fixed columns, as many rows as needed
    GridLayout gridlayout = new GridLayout(0, 3);
    JPanel buttonsPanel = new JPanel(gridlayout);
    //System.out.println("\t\t" + buttonsPanel.getHeight());

    // Base: <audio> tags
    Elements audios = inputDocument.select("audio");

    String audioName;
    String sectionName = "";
    String sectionNameComparison = "";
    String subSectionName;
    String wholeText;
    String wholeTextComparison = "";
    String VGSCode = "";
    int elementsCounter = 0;
    int counterTemp = 0;
    Font sectionFont = new Font("Arial", Font.BOLD, 14);
    //Font subsectionFont = new Font("Arial", Font.ITALIC, 12);

    // Similar to
    // for (org.jsoup.nodes.Element audio : audios)
    for (int i = 0; i < audios.size(); i++) {

      JLabel sectionLabel = null;
      JLabel subSectionLabel = null;
      boolean firstCheck = true;
      String childSource = audios.get(i).child(0).attr("src");

      try {
        //System.out.println(childSource);
        //System.out.println(childSource.substring(0, childSource.lastIndexOf("/revision/")));

        // AUDIO NAMES & MISC
        // 2nd kind (different lines)
        if (!childSource.toLowerCase().contains("_select")
            && !childSource.toLowerCase().contains("_intro")
            && !childSource.toLowerCase().contains("_passive")
            && !childSource.toLowerCase().contains("_ability")
            && !childSource.toLowerCase().contains("_health_low")
            && !childSource.toLowerCase().contains("_ward_placed")
            && !childSource.toLowerCase().contains("_purchase")
            && !childSource.toLowerCase().contains("_kill")
            && !childSource.toLowerCase().contains("_death")
            && !childSource.toLowerCase().contains("_taunt")
            && !childSource.toLowerCase().contains("_joke")
            && !childSource.toLowerCase().contains("_laugh")
            && !childSource.toLowerCase().contains("_move")
            && !childSource.toLowerCase().contains("_exert")
            && !childSource.toLowerCase().contains("_jungleboss")
            && !childSource.toLowerCase().contains("_extra")
            && !childSource.toLowerCase().contains("_special")
        ) {

          String parentParentSibling = audios.get(i).parent().parent().previousElementSibling()
              .toString();

          if (childSource.toLowerCase().contains("_tower")) {
            audioName = parentParentSibling.substring(17, parentParentSibling.lastIndexOf("<br>"));
            //System.out.println(parentParentSibling.substring(17, parentParentSibling.lastIndexOf("<br>")));
          } else if (childSource.toLowerCase().contains("_phoenix")) {
            audioName = parentParentSibling.substring(parentParentSibling.lastIndexOf("<br>") + 5,
                parentParentSibling.length() - 5);
            //System.out.println(parentParentSibling.substring(parentParentSibling.lastIndexOf("<br>") + 5,parentParentSibling.length() - 5));
          } else {
            audioName = audios.get(i).parent().parent().previousElementSibling().text();
            //System.out.println(audios.get(i).parent().parent().previousElementSibling().text());
          }

          // Last letter
          String VGSLetter = audios.get(i).parent().parent().previousElementSibling()
              .previousElementSibling().text().trim();

          // Whole line with section name and beginning VGS code
          wholeText = audios.get(i).parent().parent().parent().parent().parent()
              .previousElementSibling().text();

          // VGS code (beginning + last letter)
          sectionName = wholeText.substring(wholeText.indexOf('-') + 2, wholeText.lastIndexOf('['));
          VGSCode = wholeText.substring(0, wholeText.indexOf('-') - 1).concat(VGSLetter);
          //System.out.println("\t\t" + VGSCode);

        } else {

          // 1st kind (same line)
          audioName = audios.get(i).parent().parent().text().substring(7);
          wholeText = audios.get(i).parent().parent().parent().previousElementSibling().text();
          //System.out.println("\t\t\t\t" + wholeText);

          // In case there are two buttons on the same line (cf. Chang'e)
          if (audioName.contains("▶️")) {
            if (counterTemp % 2 == 0) {
              audioName = audioName.substring(0, audioName.lastIndexOf("▶") - 5).trim();
            } else {
              audioName = audioName.substring(audioName.indexOf("▶") + 2).trim();
            }
            counterTemp++;
          }

          // In case there are no subsections where there should be (cf. Merlin/King Arthur)
          if (!audios.get(i).parent().parent().parent().previousElementSibling().toString()
              .startsWith("<p>")) {
            sectionName = wholeText.substring(0, wholeText.lastIndexOf("["));
          } else {
            // For special interactions: no subsection so just all part of the same section
            firstCheck = false;
          }
        }

        // SECTIONS
        if (wholeTextComparison.equals("") || !wholeText.equals(wholeTextComparison)) {
          wholeTextComparison = wholeText;
        /*System.out.println(wholeText);
        System.out.println("\t" + wholeTextComparison);
        System.out.println("---------------");*/

          // SUBSECTIONS
          // 1st kind (same line)
          if (wholeText.startsWith("(") || wholeText.startsWith("When p")
              || wholeText.startsWith("When b") || wholeText.startsWith("When i")
              || wholeText.startsWith("When k") || wholeText.startsWith("When d")) {

            subSectionName = audios.get(i).parent().parent().parent().previousElementSibling()
                .previousElementSibling().text();
            // Technically sectionName, so need to switch
            String temp = subSectionName;
            subSectionName = sectionName;
            sectionName = temp;

            // Only non-firsts (the incorrect ones) start with "Link"
            if (!sectionName.startsWith("Link")) {
              sectionNameComparison = sectionName;
            } else {
              firstCheck = false;
              sectionName = sectionNameComparison;
            }

            subSectionLabel = new JLabel(subSectionName);

            sectionName = sectionName.substring(0, sectionName.lastIndexOf("["));
            //System.out.println("" + sectionName);
            //System.out.println("\t" + subSectionName);
            //System.out.println("\t\t" + sectionNameComparison);

            // 2nd kind (different lines)
          } else if (childSource.toLowerCase().contains("_enemy_jungle")
              || childSource.toLowerCase().contains("_attack_t")
              || childSource.toLowerCase().contains("_self_attack")
              || childSource.toLowerCase().contains("_self_b")
              || childSource.toLowerCase().contains("_jungle_buff")
              || childSource.toLowerCase().contains("_self_defend")
              || childSource.toLowerCase().contains("_self_gank")
              || childSource.toLowerCase().contains("_self_ward")
              || childSource.toLowerCase().contains("_self_returned")
              || childSource.toLowerCase().contains("_other_g")
              || childSource.toLowerCase().contains("_other_v")
              || childSource.toLowerCase().contains("emote_f")
              || childSource.toLowerCase().contains("nicejob")
              || childSource.toLowerCase().contains("ward_t")
              && !childSource.toLowerCase().contains("_other_g_m")
              && !childSource.toLowerCase().contains("other_s")
              && !childSource.toLowerCase().contains("other_w")) {

            subSectionName = audios.get(i).parent().parent().parent().previousElementSibling()
                .parent().parent().previousElementSibling().text();
            //System.out.println(subSectionName);

            String temp = subSectionName;
            subSectionName = sectionName;
            sectionName = temp;

            //System.out.println("" + sectionName);
            //System.out.println("\t" + subSectionName);
            //System.out.println("\t\t" + sectionNameComparison);
            subSectionLabel = new JLabel(subSectionName);
            if (sectionName.contains(subSectionName)) {
              firstCheck = false;
            }
          }

          sectionLabel = new JLabel(sectionName);
        }

        // ADDING LABELS TO PANEL
        // Section
        // Add only the section label the first time
        if (sectionLabel != null && firstCheck) {
          // When it's not at the very beginning
          if (elementsCounter != 0) {
            // 1st part empty line after button before section: empty line
            for (int j = 1; j <= 3; j++) {
              elementsCounter++;
              buttonsPanel.add(new JLabel(" "));
            }
          }

          // 2nd part empty line after button before section: new line
          elementsCounter = newLine(buttonsPanel, elementsCounter);

          sectionLabel.setFont(sectionFont);
          buttonsPanel.add(sectionLabel);
          elementsCounter++;

          // New line after section
          elementsCounter = newLine(buttonsPanel, elementsCounter);
        }

        // Subsection
        if (subSectionLabel != null) {

          // New line after button before subsection
          elementsCounter = newLine(buttonsPanel, elementsCounter);
          buttonsPanel.add(subSectionLabel);
          elementsCounter++;

          // New line after subsection
          elementsCounter = newLine(buttonsPanel, elementsCounter);
        }

        // Button
        // Always a button to add, but section/subsection is optional
        if (!VGSCode.equals("")) {
          audioName = audioName + " (" + VGSCode + ")";
        }

        JButton temp = new JButton(audioName);
        // Fixes top/bottom margin issue and sets panel height and width
        temp.setPreferredSize(new Dimension(0, 23));
        final String finalAudioUrl = childSource;
        final String finalAudioName = audioName;
        temp.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            textField.setText(finalAudioName);
            new Thread(new Runnable() {
              @Override
              public void run() {
                AudioFilePlayer.getInstance().play(finalAudioUrl);
              }
            }).start();
          }
        });
        buttonsPanel.add(temp);

        elementsCounter++;
        if (elementsCounter == 4) {
          //System.out.println(elementsCounter + temp.getText());
          temp.doClick();
        }

        // Handling error in case a file or (sub)section does not have the right name
      } catch (NullPointerException e) {
        buttonsPanel.add(new JLabel("ERROR 1: NULL POINTER ######"));
        elementsCounter++;
        e.printStackTrace();
      } catch (StringIndexOutOfBoundsException e) {
        buttonsPanel.add(new JLabel("ERROR 2: INDEX OUT ######"));
        elementsCounter++;
        e.printStackTrace();
      }
    }

    buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    JScrollPane scrollButtonPane = new JScrollPane(buttonsPanel);
    scrollButtonPane.getVerticalScrollBar().setUnitIncrement(20);

    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(scrollButtonPane, BorderLayout.CENTER);
    leftPanel.add(textField, BorderLayout.NORTH);
    MainFrame.getMainPanel().add(leftPanel, BorderLayout.CENTER);

    return leftPanel;
  }

  private static int newLine(JPanel buttonsPanel, int elementsCounter) {
    while (elementsCounter % 3 != 0) {
      elementsCounter++;
      buttonsPanel.add(new JLabel(" "));
    }
    // Need to return it so the new value doesn't get lost
    return elementsCounter;
  }

}
