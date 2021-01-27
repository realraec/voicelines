package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LeftPanel {

  protected static JPanel initLeftPanel(MainFrame frame, Document inputDocument) {

    // Reset to make the previous page disappear if there is one
    if (frame.getLeftPanel() != null) {
      frame.removeLeftPanel();
    }

    // COMPONENTS
    JTextField textField = new JTextField();
    textField.setEditable(false);
    textField.setHorizontalAlignment(0);
    // 3 fixed columns, as many rows as needed
    GridLayout gridlayout = new GridLayout(0, 3);
    JPanel buttonsPanel = new JPanel(gridlayout);

    // NECESSARY VARIABLES
    // Base: <audio> tags
    Elements audios = inputDocument.select("audio");
    Element sectionElement;
    Element audioLine;
    List<String> audioNamesList = null;
    String audioName = "";
    String audioLineString;
    String sectionName = "";
    String sectionNameComparison = "";
    String previousSectionName = "";
    String previousSectionNameComparison = "";
    String VGSCode = "";
    int elementsCounter = 0;
    int extraCounterTemp = 0;
    int numberStances = 0;
    int loopCounter = 0;
    int fixedBrCount = 0;
    boolean firstTdCheck = true;
    boolean firstSectionCheck = true;
    boolean firstElementCheck = true;
    // Technically all labels, only the font used differs
    Font sectionFont = new Font("Arial", Font.BOLD, 14);
    Font subSectionFont = new Font("Arial", Font.PLAIN, 12);
    Font subSubSectionFont = new Font("Arial", Font.ITALIC, 11);

    /* -----------------------------------------------------
    ----------------------------------------------------- */

    // Similar to
    // for (org.jsoup.nodes.Element audio : audios)
    for (int i = 0; i < audios.size(); i++) {

      JLabel sectionLabel;
      String audioSource = audios.get(i).child(0).attr("src");
      Element audioKindLiTd = audios.get(i).parent().parent();

      try {

        // 2ND KIND (MULTIPLE LINES): <td>
        if (audioKindLiTd.normalName().equals("td")) {

          sectionElement = audios.get(i).parent().parent().parent().parent().parent()
              .previousElementSibling();
          // Whole line with section name and beginning VGS code
          String sectionElementText = sectionElement.text();
          sectionName = sectionElementText.substring(sectionElementText.indexOf("-") + 2,
              sectionElementText.indexOf("["));
          VGSCode = sectionElementText.substring(0, sectionElementText.indexOf("-") - 1);

          // Checking the number of stances only once
          if (firstTdCheck) {
            String tableForStances;
            try {
              // Before simple h2
              tableForStances = audios.get(i).parent().parent().parent().previousElementSibling()
                  .previousElementSibling().toString();
            } catch (NullPointerException e) {
              // Before h2 AND h4: special skins with stances
              tableForStances = audios.get(i).parent().parent().parent().previousElementSibling()
                  .toString();
            }
            // Effectively counting the number of stances
            int count2 = 0, fromIndex2 = 0;
            while ((fromIndex2 = tableForStances.indexOf("<th", fromIndex2)) != -1) {
              count2++;
              fromIndex2++;
            }
            numberStances = count2 - 2;
            firstTdCheck = false;
          }

          /* -----------------------------------------------------
          ----------------------------------------------------- */

          // DEFAULT / STARTING POINT
          // Text location
          audioLine = audioKindLiTd.previousElementSibling();
          // Button name
          audioName = audioLine.text().trim();
          // If ▶ character found in name, the location is not right
          while (audioName.contains("▶")) {
            audioLine = audioLine.previousElementSibling();
            audioName = audioLine.text().trim();
          }

          // Last letter
          String VGSLetter = audioLine.previousElementSibling().text().trim();

          /* -----------------------------------------------------
          ----------------------------------------------------- */

          // NON-DEFAULT
          audioLineString = audioLine.toString();
          audioLineString = audioLineString
              .substring(audioLineString.indexOf(">") + 1, audioLineString.lastIndexOf("<"))
              .trim();

          // Several voicelines on the same line
          if (audioLineString.contains("<br>")) {

            // First loop: creating the list and using it
            if (loopCounter == 0) {
              int brCount = 0, fromIndex2 = 0;
              while ((fromIndex2 = audioLineString.indexOf("<br>", fromIndex2)) != -1) {
                brCount++;
                fromIndex2++;
              }
              fixedBrCount = brCount;
              audioNamesList = Arrays.asList(audioLineString.split("\\s*<br>\\s*"));
              audioName = audioNamesList.get(0);

              // Next loop(s): using the previously-created list
            } else {
              switch (fixedBrCount) {

                case 1:
                  if (numberStances == 2 || audioLineString.contains("tower")) {
                    audioName = audioNamesList.get(1);
                    loopCounter = -1;

                    // No numberStances == 3 since only case is with tower/phoenix lines
                  } else if (numberStances == 4) {
                    switch (loopCounter % 4) {
                      case 1:
                        audioName = audioNamesList.get(0);
                        break;
                      case 3:
                        loopCounter = -1;
                      case 2:
                        audioName = audioNamesList.get(1);
                    }
                  }
                  break;

                case 2:
                  // Special case Hel
                  if (numberStances == 2) {
                    if (loopCounter % 2 == 1) {
                      if (extraCounterTemp % 2 == 0) {
                        audioName = audioNamesList.get(1);
                      } else {
                        audioName = audioNamesList.get(2);
                        extraCounterTemp = -1;
                      }
                      loopCounter = -1;
                      extraCounterTemp++;
                    }

                    // e.g. Divine Dragon Bellona
                  } else if (numberStances == 3) {
                    switch (loopCounter % 3) {
                      case 1:
                        audioName = audioNamesList.get(1);
                        break;
                      case 2:
                        audioName = audioNamesList.get(2);
                        loopCounter = -1;
                    }
                  }
              }
            }
            loopCounter++;
          }

          // VGS code (beginning + last letter)
          VGSCode += VGSLetter;

          // Different label style depending on title rank
          sectionLabel = new JLabel(sectionName);
          if (sectionElement.normalName().equals("h3")) {
            sectionLabel.setFont(sectionFont);
          } else {
            sectionLabel.setFont(subSectionFont);
          }

          /* -----------------------------------------------------
          ----------------------------------------------------- */

          // 1ST KIND (SAME LINE): <li>
        } else {

          // DEFAULT
          audioName = audioKindLiTd.text().substring(7);
          sectionElement = audios.get(i).parent().parent().parent().previousElementSibling();
          sectionName = sectionElement.text();

          try {
            sectionName = sectionName.substring(0, sectionName.lastIndexOf("["));
            // Sections in which the buttons should have a VGS
            switch (sectionName) {
              case "Jokes":
                VGSCode = "VEJ";
                break;
              case "Taunts":
                VGSCode = "VET";
                break;
              case "Laughs":
                VGSCode = "VEL";
                break;
            }
            // If the substring operation fails, the previous "section" is simple text in a <p> tag
            // Case handled further down
          } catch (StringIndexOutOfBoundsException ignored) {
          }

          // Different (different) label style depending on title rank
          sectionLabel = new JLabel(sectionName);
          switch (sectionElement.normalName()) {
            case "h2":
              sectionLabel.setFont(sectionFont);
              break;
            case "h3":
              sectionLabel.setFont(subSectionFont);
              break;
            case "h4":
              sectionLabel.setFont(subSubSectionFont);
              break;

            // Extra text either turned into subsection or ignored
            case "p":
              // Actual text (e.g. Cthulhu)
              if (!sectionName.equals("") && firstSectionCheck) {
                firstSectionCheck = false;
                previousSectionName = sectionElement.previousElementSibling().text();
                previousSectionName = previousSectionName
                    .substring(0, previousSectionName.indexOf("["));
                previousSectionNameComparison = previousSectionName;
                // The original text being too long and stretching the cell
                // Changing its content (since only happens once), and treating it as a subsection
                sectionLabel.setText("Sound effects played when afflicted by Insanity:");
                sectionLabel.setFont(subSubSectionFont);

                // Technically text but empty
                // In case there are no subsections where there should be (e.g. Merlin/King Arthur)
                // No subsection so just all part of the same section
              } else {
                sectionName = sectionNameComparison;
              }
              break;
          }

          /* -----------------------------------------------------
          ----------------------------------------------------- */

          // NON-DEFAULT
          int forbiddenCharCount = 0;
          // If ▶ character found in name, the location is right but the name must be split into several sections
          if (audioName.contains("▶")) {
            // Effectively counting the number of voicelines in the same tag
            int fromIndex = 0;
            while ((fromIndex = audioName.indexOf("▶", fromIndex)) != -1) {
              forbiddenCharCount++;
              fromIndex++;
            }

            switch (forbiddenCharCount) {

              case 1:
                if (loopCounter % 2 == 0) {
                  audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
                } else {
                  audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                  loopCounter = -1;
                  sectionLabel = null;
                }
                break;

              case 2:
                if (loopCounter % 3 == 0) {
                  audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
                } else if (loopCounter % 3 == 1) {
                  audioName = audioName
                      .substring(audioName.indexOf("▶") + 3, audioName.lastIndexOf("▶") - 5);
                  sectionLabel = null;
                } else {
                  audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                  loopCounter = -1;
                  sectionLabel = null;
                }
                break;

              case 3:
                if (loopCounter % 4 == 0) {
                  audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
                } else if (loopCounter % 4 == 1) {
                  audioName = audioName.substring(audioName.indexOf("▶") + 3, audioName.indexOf("▶",
                      audioName.indexOf("▶") + 3) - 5);
                  sectionLabel = null;
                } else if (loopCounter % 4 == 2) {
                  audioName = audioName.substring(audioName.lastIndexOf("▶",
                      audioName.lastIndexOf("▶") - 5) + 3, audioName.lastIndexOf("▶") - 5);
                  sectionLabel = null;
                } else {
                  audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                  loopCounter = -1;
                  sectionLabel = null;
                }
                break;

              case 4:
                if (loopCounter % 5 == 0) {
                  audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
                } else if (loopCounter % 5 == 1) {
                  audioName = audioName.substring(audioName.indexOf("▶") + 3, audioName.indexOf("▶",
                      audioName.indexOf("▶") + 3) - 5);
                  sectionLabel = null;
                } else if (loopCounter % 5 == 2) {
                  audioName = audioName.substring(audioName.indexOf("▶",
                      audioName.indexOf("▶") + 3) + 3, audioName.lastIndexOf("▶",
                      audioName.lastIndexOf("▶") - 5) - 5);
                  sectionLabel = null;
                } else if (loopCounter % 5 == 3) {
                  audioName = audioName.substring(audioName.lastIndexOf("▶",
                      audioName.lastIndexOf("▶") - 5) + 3, audioName.lastIndexOf("▶") - 5);
                  sectionLabel = null;
                } else {
                  audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                  loopCounter = -1;
                  sectionLabel = null;
                }
            }
            loopCounter++;
          }
        }

        /* -----------------------------------------------------
        ----------------------------------------------------- */

        // ADDING TO PANEL
        // For voicelines that have an associated VGS code
        if (!VGSCode.equals("")) {
          audioName = audioName + " (" + VGSCode + ")";
        }

        // Special case where there is no h3 between a <h4> and a <h2>
        if (sectionElement.normalName().equals("h4")) {
          try {
            String problematicSectionName = sectionElement.previousElementSibling().text();
            problematicSectionName = problematicSectionName
                .substring(0, problematicSectionName.indexOf("["));
            // Forcing the next loop for the labels
            if (!problematicSectionName.equals(previousSectionNameComparison)) {
              sectionNameComparison = "";
            }
          } catch (StringIndexOutOfBoundsException ignored) {
          }
        }

        // If there is a label to add,
        // and if it is not the same as the one added just before
        if (sectionLabel != null && !sectionName.equals(sectionNameComparison)) {

          // Before handling the current (possibly smaller) section,
          // Need to check if there is a bigger section before that
          Element previousSectionCheck = sectionElement.previousElementSibling();
          if (previousSectionCheck.normalName().equals("h2")) {
            previousSectionName = previousSectionCheck.text()
                .substring(0, previousSectionCheck.text().indexOf("["));
            if (elementsCounter != 0) {
              elementsCounter = newLine(buttonsPanel, elementsCounter);
            }
            elementsCounter = addSubLabelAfterLabel(buttonsPanel, previousSectionName,
                elementsCounter,
                sectionFont);

          } else if (previousSectionCheck.normalName().equals("h3")) {

            // Same logic
            Element previousPreviousSectionCheck = previousSectionCheck.previousElementSibling();
            if (previousPreviousSectionCheck.normalName().equals("h2")) {
              String tempPreviousPreviousSectionName = previousPreviousSectionCheck.text();
              tempPreviousPreviousSectionName = tempPreviousPreviousSectionName
                  .substring(0, tempPreviousPreviousSectionName.indexOf("["));
              elementsCounter = newLine(buttonsPanel, elementsCounter);
              elementsCounter = addSubLabelAfterLabel(buttonsPanel, tempPreviousPreviousSectionName,
                  elementsCounter,
                  sectionFont);
            }

            previousSectionName = previousSectionCheck.text()
                .substring(0, previousSectionCheck.text().indexOf("["));
            elementsCounter = addSubLabelAfterLabel(buttonsPanel, previousSectionName,
                elementsCounter,
                subSectionFont);
          }

          // When it's not at the very beginning
          // When it's big sections, not smaller ones
          if (elementsCounter != 0 && sectionLabel.getFont().equals(sectionFont)) {
            elementsCounter = newLine(buttonsPanel, elementsCounter);
          }
          elementsCounter = endLine(buttonsPanel, elementsCounter);
          buttonsPanel.add(sectionLabel);
          elementsCounter++;
          elementsCounter = endLine(buttonsPanel, elementsCounter);
        }

        // To be used in the following loops to check if the label is already there
        sectionNameComparison = sectionName;
        previousSectionNameComparison = previousSectionName;

        /* -----------------------------------------------------
        ----------------------------------------------------- */

        JButton temp = new JButton(audioName);
        // Fixes top/bottom margin issue and sets panel height and width
        temp.setPreferredSize(new Dimension(0, 23));
        final String finalAudioUrl = audioSource;
        final String finalAudioName = audioName;
        temp.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            textField.setText(finalAudioName);
            // New Thread and instance so several voicelines can be played simultaneously
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

        // Automatically plays the first voiceline (the god's name) when a page is loaded
        if (firstElementCheck) {
          temp.doClick();
          firstElementCheck = false;
        }
        // Visual cue to help notice when something is wrong instead of just skipping it
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

    // COMPONENTS
    buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    JScrollPane scrollButtonPane = new JScrollPane(buttonsPanel);
    scrollButtonPane.getVerticalScrollBar().setUnitIncrement(20);
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(scrollButtonPane, BorderLayout.CENTER);
    leftPanel.add(textField, BorderLayout.NORTH);
    MainFrame.getMainPanel().add(leftPanel, BorderLayout.CENTER);

    return leftPanel;
  }

  private static int endLine(JPanel buttonsPanel, int elementsCounter) {
    while (elementsCounter % 3 != 0) {
      elementsCounter++;
      buttonsPanel.add(new JLabel(" "));
    }
    // Need to return it so the new value doesn't get lost
    return elementsCounter;
  }

  private static int newLine(JPanel buttonsPanel, int elementsCounter) {
    for (int j = 1; j <= 3; j++) {
      elementsCounter++;
      buttonsPanel.add(new JLabel(" "));
    }
    // Need to return it so the new value doesn't get lost
    return elementsCounter;
  }

  private static int addSubLabelAfterLabel(JPanel buttonsPanel, String labelName,
      int elementsCounter, Font font) {
    JLabel tempPrevious = new JLabel(labelName);
    tempPrevious.setFont(font);
    elementsCounter = endLine(buttonsPanel, elementsCounter);
    buttonsPanel.add(tempPrevious);
    elementsCounter++;
    // Need to return it so the new value doesn't get lost
    return elementsCounter;
  }

}
