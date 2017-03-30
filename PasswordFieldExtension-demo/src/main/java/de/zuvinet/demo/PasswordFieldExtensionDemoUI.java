package de.zuvinet.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import de.zuvinet.PasswordFieldExtension;

import javax.servlet.annotation.WebServlet;

@Theme("demo")
@Title("PasswordFieldExtension Add-on Demo")
public class PasswordFieldExtensionDemoUI extends UI
{
  @WebServlet(value = "/*", asyncSupported = true)
  @VaadinServletConfiguration(productionMode = false, ui = PasswordFieldExtensionDemoUI.class)
  public static class Servlet extends VaadinServlet
  {
  }

  @Override
  protected void init(VaadinRequest request)
  {
    PasswordFieldExtension passwordFieldExtension = new PasswordFieldExtension("New password");

    final TextField email = new TextField("E-mail");
    final PasswordField passwordFieldOld = new PasswordField("Old password");
    final PasswordField passwordFieldRepeat = new PasswordField("Confirm new password");
    final VerticalLayout layout = new VerticalLayout();
    layout.setStyleName("demoContentLayout");
    layout.setSizeFull();
    Panel panel = new Panel("Change password");
    panel.setSizeUndefined();
    VerticalLayout panelVerticalLayout = new VerticalLayout();
    panelVerticalLayout.setWidth("400px");
    panelVerticalLayout.setMargin(true);
    panelVerticalLayout.setSpacing(true);
    panelVerticalLayout.addComponent(email);
    panelVerticalLayout.setComponentAlignment(email, Alignment.MIDDLE_LEFT);
    panelVerticalLayout.setExpandRatio(email, 0.0f);
    panelVerticalLayout.addComponent(passwordFieldOld);
    panelVerticalLayout.setComponentAlignment(passwordFieldOld, Alignment.MIDDLE_LEFT);
    panelVerticalLayout.setExpandRatio(passwordFieldOld, 1.0f);
    panelVerticalLayout.addComponent(passwordFieldExtension);
    panelVerticalLayout.setComponentAlignment(passwordFieldExtension, Alignment.MIDDLE_LEFT);
    panelVerticalLayout.setExpandRatio(passwordFieldExtension, 1.0f);
    panelVerticalLayout.addComponent(passwordFieldRepeat);
    panelVerticalLayout.setComponentAlignment(passwordFieldRepeat, Alignment.MIDDLE_LEFT);
    panelVerticalLayout.setExpandRatio(passwordFieldRepeat, 1.0f);
    Label label = new Label();
    panelVerticalLayout.addComponent(label);
    Button button = new Button("Change password", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent clickEvent)
      {
        if (passwordFieldExtension.getValue().equals(passwordFieldRepeat.getValue()))
          label.setValue("Password changed!");
        else label.setValue("Password not the same!");
      }
    });
    panelVerticalLayout.addComponent(button);
    panel.setContent(panelVerticalLayout);
    layout.addComponent(panel);
    layout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
    setContent(layout);
  }
}
