package xyz.realraec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LeftPanel extends JPanel {

  JPanel buttonsPanel;
  Elements audios;
  Element audioKindLiTd;
  Element sectionElement;
  Element audioLine;
  List<String> audioNamesList = null;
  String audioSource;
  String audioName = "";
  String audioLineString;
  String sectionName = "";
  String sectionNameComparison = "";
  String previousSectionName = "";
  String previousSectionNameComparison = "";
  String VGSCode = "";
  JLabel sectionLabel;
  JTextField textField;
  int numberColumns = 0;
  int elementsCounter = 0;
  int extraCounterTemp = 0;
  int numberStances = 0;
  int loopCounter = 0;
  int fixedBrCount = 0;
  boolean firstTdCheck = true;
  boolean firstSectionCheck = true;
  boolean firstElementCheck = true;
  boolean brokenAudio = false;
  // Technically all labels, only the font used differs
  Font sectionFont = new Font("Arial", Font.BOLD, 14);
  Font subSectionFont = new Font("Arial", Font.PLAIN, 12);
  Font subSubSectionFont = new Font("Arial", Font.ITALIC, 11);


  public LeftPanel(MainFrame frame, Document inputDocument) {
    initLeftPanel(frame, inputDocument);
  }

  private void initLeftPanel(MainFrame frame, Document inputDocument) {
    this.setLayout(new BorderLayout());
    audioSource = null;
    String pageAddress = inputDocument.location();

    // COMPONENTS AND BASE
    textField = new JTextField();
    textField.setEditable(false);
    textField.setHorizontalAlignment(0);

    // EITHER LIST OR EXPLORER
    if (pageAddress.equals("https://smite.gamepedia.com/God_voicelines")
        || pageAddress.equals("https://smite.gamepedia.com/Skin_voicelines")
        || pageAddress.equals("https://smite.gamepedia.com/Announcer_packs")) {
      initList(frame, inputDocument, pageAddress);
    } else {
      numberColumns = 3;
      initExplorer(inputDocument, pageAddress);
    }

    // COMPONENTS
    buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    JScrollPane scrollButtonPane = new JScrollPane(buttonsPanel);
    scrollButtonPane.getVerticalScrollBar().setUnitIncrement(20);
    //JPanel leftPanel = new JPanel(new BorderLayout());
    this.add(scrollButtonPane, BorderLayout.CENTER);
    this.add(textField, BorderLayout.NORTH);
  }

  private void initList(MainFrame frame, Document inputDocument, String pageAddress) {
    switch (pageAddress) {
      case "https://smite.gamepedia.com/God_voicelines":
        textField.setText(
            "Here are all the default voicepacks. Click on a button to load the god's default voicepack.");
        break;
      case "https://smite.gamepedia.com/Skin_voicelines":
        textField.setText(
            "Here are all the non-default voicepacks. Click on a button to load the skin's voicepack.");
        break;
      default:
        textField.setText(
            "Here are all the announcer packs. Click on a button to load the announcer pack.");
    }

    // Fixed number of columns, as many rows as needed
    GridLayout layout;
    if (pageAddress.contains("Skin")) {
      layout = new GridLayout(0, 4);
      numberColumns = 4;
    } else {
      layout = new GridLayout(0, 5);
      numberColumns = 5;
    }
    buttonsPanel = new JPanel(layout);
    // Base: <img> tags
    // Excluding one type of image that is not an icon
    Elements images = inputDocument.select("img[src~=\\.(png)]:not([alt*='Gems.png'])");

    // New thread for the list to start displaying and loading early and not only at the end
    startThreadList(frame, pageAddress, images);
  }

  private void startThreadList(MainFrame frame, String pageAddress, Elements images) {
    int substring;
    // If the announcer packs list is loaded, remove 15 chars (" Announcer pack")
    if (pageAddress.contains("packs")) {
      substring = 15;
      // If the gods or skins list is loaded, remove 11 (" voicelines")
    } else {
      substring = 11;
    }
    int xxx = 0;
    //pageAddress.contains("Skin") ? 700 : 0;
    new Thread(new Runnable() {
      @Override
      public void run() {
        // Skipping the first and last two on purpose since they are not icons of interest
        for (int i = 1; i < images.size() - 2 - xxx; i++) {
          // SOURCE
          String imgSource = images.get(i).attr("src");
          Icon imgIcon = null;
          try {
            imgIcon = new ImageIcon(new URL(imgSource));
          } catch (MalformedURLException e) {
            e.printStackTrace();
          }

          // TEXT
          String imgTextString = images.get(i).parent().parent().text().trim();
          imgTextString = imgTextString.substring(0, imgTextString.length() - substring);
          JLabel imgIconLabel = new JLabel(imgIcon);
          JLabel imgTextLabel = new JLabel(imgTextString);
          imgTextLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
          JButton imgButton = new JButton();
          imgButton.setLayout(new BorderLayout());
          imgButton.add(imgIconLabel, BorderLayout.WEST);
          imgButton.add(imgTextLabel, BorderLayout.CENTER);
          imgButton.setPreferredSize(new Dimension(0, 42));
          //imgButton.setUI(new BasicButtonUI());

          // SECTION
          if (pageAddress.equals("https://smite.gamepedia.com/Skin_voicelines")) {
            sectionName = images.get(i).parent().parent().parent().previousElementSibling().text();
            sectionName = sectionName.substring(0, sectionName.indexOf("["));
          } else if (!imgTextString.equals("Default")) {
            sectionName = imgTextString.substring(0, 1);
          } else {
            sectionName = "Default";
          }

          // If it is not the same label as the one added just before
          if (!sectionName.equals(sectionNameComparison)) {
        /*if (!sectionNameComparison.equals("")) {
          newLine(buttonsPanel, elementsCounter);
        }*/

            endLine(buttonsPanel);
            sectionLabel = new JLabel(sectionName);
            buttonsPanel.add(sectionLabel);
            elementsCounter++;
            endLine(buttonsPanel);
          }

          // LINK
          String imgLink = images.get(i).parent().nextElementSibling().attr("href");
          if (imgLink.contains("?action=")) {
            imgButton.setEnabled(false);
          }
          String finalImgLink = "https://smite.gamepedia.com" + imgLink;
          imgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              frame.performSearch(finalImgLink);
            }
          });
          buttonsPanel.add(imgButton);
          elementsCounter++;
          sectionNameComparison = sectionName;
          // For the new button to appear as soon as it is made
          frame.revalidate();
        }
      }
    }).start();
  }

  private void initExplorer(Document inputDocument, String pageAddress) {
    // 3 fixed columns, as many rows as needed
    buttonsPanel = new JPanel(new GridLayout(0, 3));
    // Base: <audio> tags
    audios = inputDocument.select("audio,a[data-state=\"error\"]");

    for (int i = 0; i < audios.size(); i++) {
      try {
        audioSource = audios.get(i).child(0).attr("src");
        brokenAudio = false;
      } catch (IndexOutOfBoundsException e) {
        audioSource = null;
        brokenAudio = true;
      }

      if (!brokenAudio) {
        audioKindLiTd = audios.get(i).parent().parent();
      } else {
        audioKindLiTd = audios.get(i).parent();
      }

      try {

        // 2ND KIND (MULTIPLE LINES): <td>
        if (audioKindLiTd.normalName().equals("td")) {
          tdAnalysis(i);

          // 1ST KIND (SAME LINE): <li>
        } else {
          liAnalysis(i);
        }

        // ADDING TO PANEL
        addToPanel();
        if (pageAddress.contains("Announcer_pack")) {
          textField.setText(
              "Here are all the voicelines for the announcer pack. Click on a button to play the voiceline.");
        } else {
          textField.setText(
              "Here are all the voicelines for the voicepack. Click on a button to play the voiceline.");
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
  }

  private void tdAnalysis(int i) {
    if (!brokenAudio) {
      sectionElement = audios.get(i).parent().parent().parent().parent().parent()
          .previousElementSibling();
    } else {
      sectionElement = audios.get(i).parent().parent().parent().parent().previousElementSibling();
    }
    // Whole line with section name and beginning VGS code
    String sectionElementText = sectionElement.text();
    sectionName = sectionElementText.substring(sectionElementText.indexOf("-") + 2);
    sectionName = properName(sectionName);
    VGSCode = sectionElementText.substring(0, sectionElementText.indexOf("-") - 1);

    // Checking the number of stances only once
    if (firstTdCheck) {
      String tableForStances;
      try {
        // Before simple h2
        if (!brokenAudio) {
          tableForStances = audios.get(i).parent().parent().parent().previousElementSibling()
              .previousElementSibling().toString();
        } else {
          tableForStances = audios.get(i).parent().parent().previousElementSibling()
              .previousElementSibling().toString();
        }
      } catch (NullPointerException e) {
        // Before h2 AND h4: special skins with stances
        if (!brokenAudio) {
          tableForStances = audios.get(i).parent().parent().parent().previousElementSibling()
              .toString();
        } else {
          tableForStances = audios.get(i).parent().parent().previousElementSibling()
              .toString();
        }
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
    // If ▶ character found in name, the location is not right (non-broken audio)
    // If empty name, the location is not right (broken audio)
    while (audioName.contains("▶") || audioName.equals("")) {
      audioLine = audioLine.previousElementSibling();
      audioName = audioLine.text().trim();
    }

    // Last letter
    String VGSLetter = audioLine.previousElementSibling().text().trim();

          /* -----------------------------------------------------
          ----------------------------------------------------- */

    // NON-DEFAULT
    audioLineString = audioLine.toString();
    audioLineString = audioLineString.substring(audioLineString.indexOf(">") + 1,
        audioLineString.lastIndexOf("<")).trim();
    // Several voicelines on the same line
    if (audioLineString.contains("<br>")) {
      nonDefaultTdAnalysis();
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
  }

  private void liAnalysis(int i) {
    // DEFAULT / STARTING POINT
    if (!brokenAudio) {
      audioName = audioKindLiTd.text().substring(7);
      sectionElement = audios.get(i).parent().parent().parent().previousElementSibling();
    } else {
      audioName = audioKindLiTd.text();
      sectionElement = audios.get(i).parent().parent().previousElementSibling();
    }
    sectionName = sectionElement.text();

    try {
      sectionName = properName(sectionName);
      // Sections in which the buttons should have a VGS
      switch (sectionName) {
        case "Jokes":
          VGSCode = "VEJ";
          break;
        case "Taunts":
          VGSCode = "VET";
          break;
        case "Special Interaction with Merlin":
        case "Special Interaction with King Arthur":
        case "Directed Taunts":
          // Needed since the VGS code was added to the wrong type of voicelines
          VGSCode = "";
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
          previousSectionName = properName(previousSectionName);
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

    // NON-DEFAULT
    // If ▶ character found in name, the location is right but the name must be split into several sections
    if (audioName.contains("▶")) {
      nonDefaultLiAnalysis();
    }
  }

  private void nonDefaultTdAnalysis() {
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

  private void nonDefaultLiAnalysis() {
    // Effectively counting the number of voicelines in the same tag
    int forbiddenCharCount = 0;
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

  private void addToPanel() {
    // For voicelines that have an associated VGS code
    if (!VGSCode.equals("")) {
      audioName = audioName + " (" + VGSCode + ")";
    }

    // Special case where there is no h3 between a <h4> and a <h2>
    if (sectionElement.normalName().equals("h4")) {
      try {
        String problematicSectionName = sectionElement.previousElementSibling().text();
        // Not using the properName() method since we deliberately want it to fail
        // if there is no bracket, since it means that the name is wrong, so we skip
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

        previousSectionName = previousSectionCheck.text();
        previousSectionName = properName(previousSectionName);
        if (elementsCounter != 0) {
          newLine(buttonsPanel);
        }
        addSubLabelAfterLabel(buttonsPanel, previousSectionName, sectionFont);

      } else if (previousSectionCheck.normalName().equals("h3")) {

        // Same logic
        Element previousPreviousSectionCheck = previousSectionCheck.previousElementSibling();
        if (previousPreviousSectionCheck.normalName().equals("h2")) {
          String tempPreviousPreviousSectionName = previousPreviousSectionCheck.text();
          tempPreviousPreviousSectionName = properName(tempPreviousPreviousSectionName);
          newLine(buttonsPanel);
          addSubLabelAfterLabel(buttonsPanel, tempPreviousPreviousSectionName, sectionFont);
        }

        previousSectionName = previousSectionCheck.text();
        previousSectionName = properName(previousSectionName);
        addSubLabelAfterLabel(buttonsPanel, previousSectionName, subSectionFont);
      }

      // When it's not at the very beginning
      // When it's big sections, not smaller ones
      if (elementsCounter != 0 && sectionLabel.getFont().equals(sectionFont)) {
        newLine(buttonsPanel);
      }
      endLine(buttonsPanel);
      buttonsPanel.add(sectionLabel);
      elementsCounter++;
      endLine(buttonsPanel);
    }

    // To be used in the following loops to check if the label is already there
    sectionNameComparison = sectionName;
    previousSectionNameComparison = previousSectionName;

    // AUDIO BUTTON
    createAudioButton(audioName);
  }

  private void createAudioButton(String audioName) {
    JButton audioButton = new JButton(audioName);
    // Fixes top/bottom margin issue and sets panel height and width
    audioButton.setPreferredSize(new Dimension(0, 23));
    audioButton.setFocusPainted(false);
    if (audioSource != null) {
      final String finalAudioUrl = audioSource;
      final String finalAudioName = audioName;
      audioButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          textField.setText(finalAudioName);
          // New Thread and instance so several voicelines can be played simultaneously
          new AudioFilePlayer(finalAudioUrl);
        }
      });
    } else {
      audioButton.setEnabled(false);
    }
    buttonsPanel.add(audioButton);
    elementsCounter++;

    // Automatically plays the first voiceline (the god's name) when a page is loaded
    if (firstElementCheck) {
      audioButton.doClick();
      firstElementCheck = false;
    }
  }

  private void addSubLabelAfterLabel(JPanel buttonsPanel, String labelName, Font font) {
    JLabel tempPrevious = new JLabel(labelName);
    tempPrevious.setFont(font);
    endLine(buttonsPanel);
    buttonsPanel.add(tempPrevious);
    elementsCounter++;
  }

  private String properName(String sectionName) {
    if (sectionName.contains("[")) {
      sectionName = sectionName.substring(0, sectionName.indexOf("["));
    }
    return sectionName;
  }

  private void endLine(JPanel buttonsPanel) {
    while (elementsCounter % numberColumns != 0) {
      elementsCounter++;
      buttonsPanel.add(new JLabel(" "));
    }
  }

  private void newLine(JPanel buttonsPanel) {
    for (int j = 1; j <= numberColumns; j++) {
      elementsCounter++;
      buttonsPanel.add(new JLabel(" "));
    }
  }

}
