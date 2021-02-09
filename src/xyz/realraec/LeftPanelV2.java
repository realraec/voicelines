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

public class LeftPanelV2 {

  protected static JPanel initLeftPanel(MainFrame frame, Document inputDocument) {

    // Reset to make the previous page disappear if there is one
    /*if (frame.getLeftPanel() != null) {
      frame.removeLeftPanel();
    }*/

    // TEXT
    JTextField textField = new JTextField();
    textField.setEditable(false);
    textField.setHorizontalAlignment(0);

    // BUTTONS
    // 3 fixed columns, as many rows as needed
    GridLayout gridlayout = new GridLayout(0, 3);
    JPanel buttonsPanel = new JPanel(gridlayout);

    // Base: <audio> tags
    Elements audios = inputDocument.select("audio");

    String audioName = "";
    String sectionName = "";
    String sectionNameComparison = "";
    String previousSectionName = "";
    String previousSectionNameComparison = "";
    Element sectionElement;
    String VGSCode = "";
    int elementsCounter = 0;
    int counterTempTd = 0;
    int counterTemp2 = 0;
    int counterTempLi = 0;
    int numberStances = 0;
    int firstTdCheck = 0;
    Font sectionFont = new Font("Arial", Font.BOLD, 14);
    Font subSectionFont = new Font("Arial", Font.PLAIN, 12);
    Font subSubSectionFont = new Font("Arial", Font.ITALIC, 11);
    boolean firstCheck = true;
    boolean firstElementCheck = true;

    // Similar to
    // for (org.jsoup.nodes.Element audio : audios)
    for (int i = 0; i < audios.size(); i++) {

      JLabel sectionLabel = null;
      String childSource = audios.get(i).child(0).attr("src");
      Element audioKindLiTd = audios.get(i).parent().parent();

      try {
        // AUDIO NAMES & MISC
        // 2nd kind (different lines): td
        if (audioKindLiTd.normalName().equals("td")) {

          sectionElement = audios.get(i).parent().parent().parent().parent().parent()
              .previousElementSibling();

          // Whole line with section name and beginning VGS code
          String rawLine = sectionElement.text();
          sectionName = rawLine.substring(rawLine.indexOf("-") + 2, rawLine.indexOf("["));
          VGSCode = rawLine.substring(0, rawLine.indexOf("-") - 1);

          // Checking the number of stances only once
          if (firstTdCheck == 0) {
            String tableForStances;
            try {
              tableForStances = audios.get(i).parent().parent().parent().previousElementSibling()
                  .previousElementSibling().toString();
            } catch (NullPointerException e) {
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
            firstTdCheck++;
          }

          // DEFAULT
          // Button name
          audioName = audioKindLiTd.previousElementSibling().text().trim();
          // Last letter
          String VGSLetter = audioKindLiTd.previousElementSibling().previousElementSibling().text()
              .trim();


          // NON-DEFAULT
          // If ▶ character found, the name is not right:
          // Either only remove the part before, or fetch the right one (and its VGS letter) higher up
          // Looped for skins with 4 stages -- only treating the last 3 if 4, or last 1 if 2
          int forbiddenCharCount = 0;
          while (audioName.contains("▶")) {
            forbiddenCharCount++;

            if (forbiddenCharCount == 1) {
              audioName = audioName.substring(audioName.lastIndexOf("▶") + 1).trim();
              if (!audioName.contains("\"")) {
                audioName = audioKindLiTd.previousElementSibling().previousElementSibling().text()
                    .trim();
                VGSLetter = audioKindLiTd.previousElementSibling().previousElementSibling()
                    .previousElementSibling().text().trim();
              }
            } else if (forbiddenCharCount == 2) {
              audioName = audioName.substring(audioName.lastIndexOf("▶") + 1).trim();
              if (!audioName.contains("\"")) {
                audioName = audioKindLiTd.previousElementSibling().previousElementSibling()
                    .previousElementSibling().text().trim();
                VGSLetter = audioKindLiTd.previousElementSibling().previousElementSibling()
                    .previousElementSibling().previousElementSibling().text().trim();
              }
            } else {
              audioName = audioName.substring(audioName.lastIndexOf("▶") + 1).trim();
              if (!audioName.contains("\"")) {
                audioName = audioKindLiTd.previousElementSibling().previousElementSibling()
                    .previousElementSibling().previousElementSibling().text().trim();
                VGSLetter = audioKindLiTd.previousElementSibling().previousElementSibling()
                    .previousElementSibling().previousElementSibling()
                    .previousElementSibling().text().trim();
              }
            }
          }

          // Effectively counting the number of voicelines in the same tag
          int DQCount = 0, fromIndex = 0;
          while ((fromIndex = audioName.indexOf("\"", fromIndex)) != -1) {
            DQCount++;
            fromIndex++;
          }

          // 2 voicelines in the same tag
          if (DQCount == 4) {

            // Special case: 1 stance, 2 voicelines, so not using the regular counter
            // e.g Sylvanus
            if (numberStances == 1) {
              if (counterTemp2 % 2 == 0) {
                audioName = audioName
                    .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
              } else {
                audioName = audioName.substring(audioName
                    .lastIndexOf("\"", audioName.lastIndexOf("\"",
                        audioName.lastIndexOf("\"") - 1) - 1) + 2);
              }
              counterTemp2++;

              // e.g. Cu Chulainn
            } else if (numberStances == 2) {

              if (counterTempTd % 2 == 0) {
                audioName = audioName
                    .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
              } else {
                audioName = audioName.substring(audioName
                    .lastIndexOf("\"", audioName.lastIndexOf("\"",
                        audioName.lastIndexOf("\"") - 1) - 1) + 2);
              }

              // e.g. Divine Dragon Bellona
            } else if (numberStances == 3) {

              if (counterTempTd % 2 == 0) {
                audioName = audioName
                    .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
              } else {
                audioName = audioName.substring(audioName
                    .lastIndexOf("\"",
                        audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 2);
              }

              // e.g. Anubis Demonic Pact
            } else if (numberStances == 4) {

              // For tower and phoenix lines
              if (audioName.toLowerCase().contains("tower")) {
                if (counterTempTd % 4 == 0 || counterTempTd % 4 == 2) {
                  audioName = audioName
                      .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
                } else {
                  audioName = audioName.substring(audioName
                      .lastIndexOf("\"",
                          audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 2);
                }

                // For non-tower and non-phoenix lines
              } else {
                if (counterTempTd % 4 == 0 || counterTempTd % 4 == 1) {
                  audioName = audioName
                      .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
                } else {
                  audioName = audioName.substring(audioName
                      .lastIndexOf("\"",
                          audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 2);
                }
              }
            }

            // 3 voicelines in the same tag
          } else if (DQCount == 6) {

            // Particular case: 3 voicelines in the same tag but only 2 stances
            // e.g. Hel
            if (numberStances == 2) {
              if (counterTempTd % 2 == 0) {
                audioName = audioName
                    .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
              } else {
                if (counterTemp2 % 2 == 0) {
                  audioName = audioName
                      .substring(audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 2,
                          audioName.lastIndexOf("\"",
                              audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1)
                              + 1);
                } else {
                  audioName = audioName.substring(audioName
                      .lastIndexOf("\"",
                          audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 2);
                }
                counterTemp2++;
              }

              // e.g. Winds of Change Kukulkan
            } else if (numberStances == 3) {

              if (counterTempTd % 3 == 0) {
                audioName = audioName
                    .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
              } else if (counterTempTd % 3 == 1) {
                audioName = audioName
                    .substring(audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 2,
                        audioName.lastIndexOf("\"",
                            audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 1);
              } else {
                audioName = audioName
                    .substring(audioName.lastIndexOf("\"",
                        audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 2,
                        audioName.lastIndexOf("\"", audioName.lastIndexOf("\"")) + 1);
              }

            }

            // e.g. Stellar Demise Baron Samedi
          } else if (DQCount == 8) {
            if (numberStances == 3) {

              if (counterTempTd % 3 == 0) {
                audioName = audioName
                    .substring(0, audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 1);
              } else if (counterTempTd % 3 == 1) {
                audioName = audioName
                    .substring(audioName.indexOf("\"", audioName.indexOf("\"") + 1) + 2,
                        audioName.lastIndexOf("\"",
                            audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 1);
              } else {
                audioName = audioName
                    .substring(audioName.lastIndexOf("\"",
                        audioName.lastIndexOf("\"", audioName.lastIndexOf("\"") - 1) - 1) + 2,
                        audioName.lastIndexOf("\"", audioName.lastIndexOf("\"")) + 1);
              }
            }
          }

          counterTempTd++;

          // VGS code (beginning + last letter)
          VGSCode += VGSLetter;

          // Different label style depending on title rank
          sectionLabel = new JLabel(sectionName);
          if (sectionElement.normalName().equals("h3")) {
            sectionLabel.setFont(sectionFont);
          } else {
            sectionLabel.setFont(subSectionFont);
          }

        } else {

          // 1st kind (same line): li
          audioName = audioKindLiTd.text().substring(7);
          sectionElement = audios.get(i).parent().parent().parent().previousElementSibling();
          sectionName = sectionElement.text();

          try {
            sectionName = sectionName.substring(0, sectionName.lastIndexOf("["));
          } catch (StringIndexOutOfBoundsException ignored) {
          }

          // Different (different) label style depending on title rank
          sectionLabel = new JLabel(sectionName);
          if (sectionElement.normalName().equals("h2")) {
            sectionLabel.setFont(sectionFont);
          } else if (sectionElement.normalName().equals("h3")) {
            sectionLabel.setFont(subSectionFont);
          } else if (sectionElement.normalName().equals("h4")) {
            sectionLabel.setFont(subSubSectionFont);

            // Extra text or absent subsection
          } else if (sectionElement.normalName().equals("p")) {

            // Text (e.g. Cthulhu)
            if (!sectionElement.text().equals("") && firstCheck) {
              previousSectionName = sectionElement.previousElementSibling().text();
              previousSectionName = previousSectionName
                  .substring(0, previousSectionName.indexOf("["));
              previousSectionNameComparison = previousSectionName;
              firstCheck = false;
              sectionLabel.setText("Sound effects played when afflicted by Insanity:");
              sectionLabel.setFont(subSubSectionFont);

              // In case there are no subsections where there should be (e.g. Merlin/King Arthur)
              // No subsection so just all part of the same section
            } else {
              sectionName = sectionNameComparison;
            }
          }

          // Effectively counting the number of voicelines in the same tag
          int DQCount = 0, fromIndex = 0;
          while ((fromIndex = audioName.indexOf("\"", fromIndex)) != -1) {
            DQCount++;
            fromIndex++;
          }

          if (audioName.contains("▶")) {

            // 2 on the same line
            if (DQCount == 4) {

              if (counterTempLi % 2 == 0) {
                audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
              } else {
                /*audioName = audioName.substring(audioName.indexOf("▶") + 2).trim();*/
                audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                counterTempLi = -1;
                sectionLabel = null;
              }

              // 3 on the same line
            } else if (DQCount == 6) {

              if (counterTempLi % 3 == 0) {
                audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
              } else if (counterTempLi % 3 == 1) {
                audioName = audioName
                    .substring(audioName.indexOf("▶") + 3, audioName.lastIndexOf("▶") - 5);
                sectionLabel = null;
              } else {
                audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                counterTempLi = -1;
                sectionLabel = null;
              }

              // 4 on the same line
            } else if (DQCount == 8) {

              if (counterTempLi % 4 == 0) {
                audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
              } else if (counterTempLi % 4 == 1) {
                audioName = audioName.substring(audioName.indexOf("▶") + 3, audioName.indexOf("▶",
                    audioName.indexOf("▶") + 3) - 5);
                sectionLabel = null;
              } else if (counterTempLi % 4 == 2) {
                audioName = audioName.substring(audioName.lastIndexOf("▶",
                    audioName.lastIndexOf("▶") - 5) + 3, audioName.lastIndexOf("▶") - 5);
                sectionLabel = null;
              } else {
                audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                counterTempLi = -1;
                sectionLabel = null;
              }

              // 5 on the same line
            } else if (DQCount == 10) {

              if (counterTempLi % 5 == 0) {
                audioName = audioName.substring(0, audioName.indexOf("▶") - 5);
              } else if (counterTempLi % 5 == 1) {
                audioName = audioName.substring(audioName.indexOf("▶") + 3, audioName.indexOf("▶",
                    audioName.indexOf("▶") + 3) - 5);
                sectionLabel = null;
              } else if (counterTempLi % 5 == 2) {
                audioName = audioName.substring(audioName.indexOf("▶",
                    audioName.indexOf("▶") + 3) + 3, audioName.lastIndexOf("▶",
                    audioName.lastIndexOf("▶") - 5) - 5);
                sectionLabel = null;
              } else if (counterTempLi % 5 == 3) {
                audioName = audioName.substring(audioName.lastIndexOf("▶",
                    audioName.lastIndexOf("▶") - 5) + 3, audioName.lastIndexOf("▶") - 5);
                sectionLabel = null;
              } else {
                audioName = audioName.substring(audioName.lastIndexOf("▶") + 3);
                counterTempLi = -1;
                sectionLabel = null;
              }
            }

            counterTempLi++;
          }

        }

        if (!VGSCode.equals("")) {
          audioName = audioName + " (" + VGSCode + ")";
        }

        // Button
        // Always a button to add, but section/subsection is optional

        if (sectionElement.normalName().equals("h4")) {

          try {
            //System.out.println(sectionElement.toString());
            String problematicSectionName = sectionElement.previousElementSibling().text();
            problematicSectionName = problematicSectionName
                .substring(0, problematicSectionName.indexOf("["));
            //System.out.println(problematicSectionName + " [vs] " + previousSectionNameComparison);
            //System.out.println(sectionElement.previousElementSibling().normalName() + " - " + sectionElement.previousElementSibling().previousElementSibling().normalName());

            if (!problematicSectionName.equals(previousSectionNameComparison)) {
              // Forcing the next loop for the labels
              sectionNameComparison = "";
            }
          } catch (StringIndexOutOfBoundsException ignored) {
            // If you have an alternative for this bullshit, I'm all ears
          }
        }

        if (sectionLabel != null && !sectionName.equals(sectionNameComparison)) {
          Element previousSectionCheck = sectionElement.previousElementSibling();

          if (previousSectionCheck.normalName().equals("h2")) {

            previousSectionName = previousSectionCheck.text()
                .substring(0, previousSectionCheck.text().indexOf("["));

            JLabel tempPrevious = new JLabel(previousSectionName);
            tempPrevious.setFont(sectionFont);
            if (elementsCounter != 0) {
              for (int j = 1; j <= 3; j++) {
                elementsCounter++;
                buttonsPanel.add(new JLabel(" "));
              }
            }
            elementsCounter = newLine(buttonsPanel, elementsCounter);
            buttonsPanel.add(tempPrevious);
            elementsCounter++;
            elementsCounter = newLine(buttonsPanel, elementsCounter);


          } else if (previousSectionCheck.normalName().equals("h3")) {
            Element previousPreviousSectionCheck = previousSectionCheck.previousElementSibling();

            if (previousPreviousSectionCheck.normalName().equals("h2")) {
              JLabel tempPreviousPrevious = new JLabel(previousPreviousSectionCheck.text()
                  .substring(0, previousPreviousSectionCheck.text().indexOf("[")));
              tempPreviousPrevious.setFont(sectionFont);
              for (int j = 1; j <= 3; j++) {
                elementsCounter++;
                buttonsPanel.add(new JLabel(" "));
              }
              elementsCounter = newLine(buttonsPanel, elementsCounter);
              buttonsPanel.add(tempPreviousPrevious);
              elementsCounter++;
              elementsCounter = newLine(buttonsPanel, elementsCounter);
            }

            previousSectionName = previousSectionCheck.text()
                .substring(0, previousSectionCheck.text().indexOf("["));
            JLabel tempPrevious = new JLabel(previousSectionName);
            tempPrevious.setFont(subSectionFont);
            elementsCounter = newLine(buttonsPanel, elementsCounter);
            buttonsPanel.add(tempPrevious);
            elementsCounter++;
            elementsCounter = newLine(buttonsPanel, elementsCounter);
          }

          // When it's not at the very beginning
          // When it's big sections, not small ones
          if (elementsCounter != 0 && sectionLabel.getFont().equals(sectionFont)) {
            for (int j = 1; j <= 3; j++) {
              elementsCounter++;
              buttonsPanel.add(new JLabel(" "));
            }
          }
          elementsCounter = newLine(buttonsPanel, elementsCounter);
          buttonsPanel.add(sectionLabel);
          elementsCounter++;
          elementsCounter = newLine(buttonsPanel, elementsCounter);
        }

        sectionNameComparison = sectionName;
        previousSectionNameComparison = previousSectionName;

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
                AudioFilePlayerV1.getInstance().play(finalAudioUrl);
              }
            }).start();
          }
        });
        buttonsPanel.add(temp);
        elementsCounter++;
        if (firstElementCheck) {
          temp.doClick();
          firstElementCheck = false;
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
    //MainFrame.getMainPanel().add(leftPanel, BorderLayout.CENTER);

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

/*  private static String stanceSwitcherName(String audioName, int counterTemp, int dqTagCount) {

    if (dqTagCount == 3) {
      System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
      //System.out.println(audioName);
      // Cu Chulainn
      if (counterTemp % 2 == 0) {
        audioName = audioName
            .substring(audioName.indexOf(">") - 1, audioName.indexOf("<br>") + 1);
      } else {
        audioName = audioName
            .substring(audioName.lastIndexOf("<br>") + 3, audioName.lastIndexOf("<") + 1);
      }


    } else if (dqTagCount == 5) {
      //System.out.println(audioName);
      // Hel
      if (counterTemp % 3 == 0) {
        //System.out.println(0);
        audioName = audioName
            .substring(audioName.indexOf(">"), audioName.indexOf("<br>") + 1);
      } else if (counterTemp % 3 == 1) {
        //System.out.println(1);
        audioName = audioName
            .substring(audioName.indexOf("<br>") + 3, audioName.lastIndexOf("<br>") + 1);
      } else {
        //System.out.println(2);
        audioName = audioName
            .substring(audioName.lastIndexOf("<br>") + 3, audioName.lastIndexOf("<") + 1);
      }
    }

    //System.out.println("\t"+audioName);
    return audioName;
  }*/

}
