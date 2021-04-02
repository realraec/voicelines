package xyz.realraec;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class VoicelineBundle {

  String name;
  JButton button;
  JComponent firstSection;
  JComponent secondSection;
  JComponent thirdSection;

  public VoicelineBundle() {

  }

  public VoicelineBundle(JButton button, JComponent firstSection,
      JComponent secondSection, JComponent thirdSection) {
    this.button = button;
    this.firstSection = firstSection;
    this.secondSection = secondSection;
    this.thirdSection = thirdSection;
    this.name = button.getText();
  }

  public void setButton(JButton button) {
    this.button = button;
    this.name = button.getText();
  }

  public void setFirstSection(JComponent firstSection) {
    this.firstSection = firstSection;
  }

  public void setSecondSection(JComponent secondSection) {
    this.secondSection = secondSection;
  }

  public void setThirdSection(JComponent thirdSection) {
    this.thirdSection = thirdSection;
  }

  @Override
  public String toString() {
    return "VoicelineBundle{" +
        "name='" + name + '\'' +
        ", lowerTitle=" + ((firstSection !=null) ?((JLabel) firstSection).getText() : "null") +
        ", middleTitle=" + ((secondSection !=null) ? ((JLabel) secondSection).getText() : "null") +
        ", upperTitle=" + ((thirdSection !=null) ? ((JLabel) thirdSection).getText() : "null") +
        '}';
  }
}
