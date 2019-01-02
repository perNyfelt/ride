package se.alipsa.ride.code.javatab;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.alipsa.ride.code.TextAreaTab;

import java.io.File;

public class JavaTab extends TextAreaTab {

  JavaTextArea javaTextArea;

  private Logger log = LoggerFactory.getLogger(JavaTab.class);

  public JavaTab(String title) {
    setTitle(title);
    javaTextArea = new JavaTextArea();
    VirtualizedScrollPane<JavaTextArea> pane = new VirtualizedScrollPane<>(javaTextArea);
    setContent(pane);
  }

  @Override
  public File getFile() {
    return javaTextArea.getFile();
  }

  @Override
  public void setFile(File file) {
    javaTextArea.setFile(file);
  }

  @Override
  public String getTextContent() {
    return javaTextArea.getTextContent();
  }

  @Override
  public String getAllTextContent() {
    return javaTextArea.getAllTextContent();
  }

  @Override
  public void replaceText(int start, int end, String content) {
    javaTextArea.replaceText(start, end, content);
  }
}
