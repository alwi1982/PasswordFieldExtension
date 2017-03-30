package de.zuvinet;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * This component is an extension of the password field.
 *
 * @author Alexander Witt (created on 08.09.2016)
 * @version 1.0
 */
public class PasswordFieldExtension extends CustomComponent {
    private VerticalLayout verticalLayout;
    private HorizontalLayout horizontalLayout;
    private PasswordField passwordField;
    private VerticalLayout minimumRequirementVerticalLayout;
    private Label minimumRequirementLabel;
    private boolean isMinimumRequirement = false;
    private Label passwordStrengthTextL;
    private String passwordWeakText;
    private String passwordMiddleText;
    private String passwordStrongText;
    private String passwordStrongestText;
    private boolean isPasswordStrengthText = true;
    private Resource passwordWeakResource;
    private Resource passwordMiddleResource;
    private Resource passwordStrongResource;
    private Resource passwordStrongestResource;
    private boolean isPasswordStrengthResource = true;
    private Image passwordStrengthImage;
    private ProgressBar progressBar;
    private boolean isProgressBar = false;
    private String specialCharacters;

    public PasswordFieldExtension() {
        initializeComponent(null);
    }

    /**
     * Constructor that creates the component with caption.
     *
     * @param caption caption of the component
     */
    public PasswordFieldExtension(String caption) {
        initializeComponent(caption);
    }

    /**
     * Initializing method for the component.
     *
     * @param caption caption of the component
     */
    private void initializeComponent(String caption) {
        passwordWeakResource = getStreamResource("images/ball_red.png");
        passwordMiddleResource = getStreamResource("images/ball_yellow.png");
        passwordStrongResource = getStreamResource("images/ball_green.png");
        passwordStrongestResource = getStreamResource("images/ball_light_green.png");
        specialCharacters = getSpecialCharacters(Charset.defaultCharset().displayName());
        passwordStrengthImage = new Image();
        passwordStrengthImage.setWidth("60%");
        passwordStrengthImage.setHeight("60%");
        passwordStrengthImage.setImmediate(true);
        verticalLayout = new VerticalLayout();
        horizontalLayout = new HorizontalLayout();
        passwordField = new PasswordField();
        minimumRequirementVerticalLayout = new VerticalLayout();
        minimumRequirementLabel = new Label();
        passwordStrengthTextL = new Label();
        passwordWeakText = new String("password weak");
        passwordMiddleText = new String("password middle");
        passwordStrongText = new String("password strong");
        passwordStrongestText = new String("password strongest");
        progressBar = new ProgressBar(0.0f);
        verticalLayout.setId("password-strength-verticallayout");
        verticalLayout.setStyleName("password-strength-verticallayout");
        horizontalLayout.setId("password-strength-horizontallayout");
        horizontalLayout.setStyleName("password-strength-horizontallayout");
        horizontalLayout.setSpacing(true);
        passwordField.setId("password-strength-passwordfield");
        passwordField.setStyleName("password-strength-passwordfield");
        minimumRequirementLabel.setId("password-strength-minimumrequirement-label");
        minimumRequirementLabel.setStyleName("password-strength-minimumrequirement-label");
        minimumRequirementLabel.setContentMode(ContentMode.HTML);
        minimumRequirementLabel.setCaption("Password minimum requirement");
        String minimumRequirementTextHtml = "<ul><li>8 character</li><li>1 capital letter</li><li>1 small letter</li><li>1 number</li><li>1 special character</li></ul>";
        minimumRequirementLabel.setValue(minimumRequirementTextHtml);
        minimumRequirementVerticalLayout.setId("password-strength-minimumrequirement-verticallayout");
        minimumRequirementVerticalLayout.setStyleName("password-strength-minimumrequirement-verticallayout");
        minimumRequirementVerticalLayout.addComponent(new Label());
        minimumRequirementVerticalLayout.addComponent(minimumRequirementLabel);
        minimumRequirementVerticalLayout.addComponent(new Label());
        passwordStrengthTextL.setId("password-strength-text");
        passwordStrengthTextL.setStyleName("password-strength-text");
        passwordStrengthTextL.setImmediate(true);
        passwordStrengthImage.setId("password-strength-image");
        passwordStrengthImage.setStyleName("password-strength-image");
        passwordStrengthImage.setImmediate(true);
        progressBar.setCaption("");
        progressBar.setId("password-strength-progressbar");
        progressBar.setStyleName("password-strength-progressbar");

        passwordField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                updateComponents(valueChangeEvent);
            }
        });
        passwordField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
                updateComponents(textChangeEvent);
            }
        });

        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add("#password-strength-image {margin-top:5px;}");
        styles.add("#password-strength-text {margin-top:5px;}");

        horizontalLayout.addComponent(passwordField);
        horizontalLayout.addComponent(passwordStrengthImage);
        horizontalLayout.addComponent(passwordStrengthTextL);
        horizontalLayout.setComponentAlignment(passwordField, Alignment.MIDDLE_CENTER);
        horizontalLayout.setComponentAlignment(passwordStrengthImage, Alignment.MIDDLE_CENTER);
        horizontalLayout.setComponentAlignment(passwordStrengthTextL, Alignment.MIDDLE_CENTER);
        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setExpandRatio(horizontalLayout, 0.0f);

        super.setSizeUndefined();
        if (caption != null) super.setCaption(caption);
        super.setCompositionRoot(verticalLayout);
    }

    /**
     * Updates the password strength components.
     *
     * @param event valueChangeEvent or textChangeEvent of the component
     */
    private void updateComponents(Object event) {
        float passwordStrength = 0.0f;

        if (event != null) {
            if (event instanceof Property.ValueChangeEvent) {
                Property.ValueChangeEvent valueChangeEvent = (Property.ValueChangeEvent) event;
                passwordStrength = checkPasswordStrength((String) valueChangeEvent.getProperty().getValue());
            } else if (event instanceof FieldEvents.TextChangeEvent) {
                FieldEvents.TextChangeEvent textChangeEvent = (FieldEvents.TextChangeEvent) event;
                passwordStrength = checkPasswordStrength(textChangeEvent.getText());
            } else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            passwordStrength = checkPasswordStrength(this.getValue());
        }
        progressBar.setValue(passwordStrength);
        if (passwordStrength >= 0.1f && passwordStrength <= 0.3f) {
            if (isPasswordStrengthText) passwordStrengthTextL.setValue(passwordWeakText);
            if (isPasswordStrengthResource) passwordStrengthImage.setSource(passwordWeakResource);
            progressBar.setCaption(passwordWeakText);
        } else if (passwordStrength >= 0.4f && passwordStrength <= 0.6f) {
            if (isPasswordStrengthText) passwordStrengthTextL.setValue(passwordMiddleText);
            if (isPasswordStrengthResource) passwordStrengthImage.setSource(passwordMiddleResource);
            progressBar.setCaption(passwordMiddleText);
        } else if (passwordStrength >= 0.7f && passwordStrength <= 0.9f) {
            if (isPasswordStrengthText) passwordStrengthTextL.setValue(passwordStrongText);
            if (isPasswordStrengthResource) passwordStrengthImage.setSource(passwordStrongResource);
            progressBar.setCaption(passwordStrongText);
        } else if (passwordStrength >= 1.0f) {
            if (isPasswordStrengthText) passwordStrengthTextL.setValue(passwordStrongestText);
            if (isPasswordStrengthResource) passwordStrengthImage.setSource(passwordStrongestResource);
            progressBar.setCaption(passwordStrongestText);
        } else {
            passwordStrengthTextL.setValue(null);
            passwordStrengthImage.setSource(null);
            progressBar.setCaption(null);
        }
    }

    /**
     * Checks the strength of the password.
     *
     * @param password the typed in password
     * @return the password strength
     */
    private float checkPasswordStrength(String password) {
        float strength = 0.0f;
        int countUpperCase = 0;
        int countLowerCase = 0;
        int countDigit = 0;
        int countSpecialChar = 0;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) countUpperCase++;
            if (Character.isLowerCase(ch)) countLowerCase++;
            if (Character.isDigit(ch)) countDigit++;
            for (char specialChar : specialCharacters.toCharArray())
                if (ch == specialChar) countSpecialChar++;
        }

        if (password.length() >= 8) strength += 0.1f;
        if (countUpperCase >= 1) strength += 0.1f;
        if (countLowerCase >= 1) strength += 0.1f;
        if (countDigit >= 1) strength += 0.1f;
        if (countSpecialChar >= 1) strength += 0.1f;

        if (password.length() >= 10) strength += 0.1f;
        if (countUpperCase >= 2) strength += 0.1f;
        if (countLowerCase >= 2) strength += 0.1f;
        if (countDigit >= 2) strength += 0.1f;
        if (countSpecialChar >= 2) strength += 0.1f;

        strength = strength * 10;
        float strengthRound = Math.round(strength);
        strength = strengthRound / 10;

        return strength;
    }

    /**
     * Finds out the special characters of the charset.
     *
     * @param charset the charset
     * @return the special characters of the charset
     * @see String
     */
    private String getSpecialCharacters(String charset) {
        CharsetEncoder charsetEncoder = Charset.forName(charset).newEncoder();
        StringBuilder specialCharBuilder = new StringBuilder();

        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (charsetEncoder.canEncode(c)) {
                if (!Character.isLetter(c) && !Character.isDigit(c) && !Character.isSpaceChar(c) && !Character.isWhitespace(c)) {
                    specialCharBuilder.append(c);
                }
            }
        }

        return specialCharBuilder.toString();
    }

    /**
     * Returns a stream resource.
     *
     * @param pathToResource the path to the resource
     * @return the stream resource
     * @see StreamResource
     */
    public StreamResource getStreamResource(String pathToResource) {
        ClassLoader classLoader = PasswordFieldExtension.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(pathToResource);

        StreamResource.StreamSource streamSource = new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return inputStream;
            }
        };

        StreamResource streamResource = new StreamResource(streamSource, String.valueOf(Math.random()));
        streamResource.setCacheTime(0);

        return streamResource;
    }

    /**
     * Returns true if the progress bar is enabled, otherwise false.
     *
     * @return true if progress bar enabled, otherwise false
     */
    public boolean isProgressBar() {
        return isProgressBar;
    }

    /**
     * Sets the progress bar instead the resource.
     *
     * @param isProgressBar If true, the progress bar is set. If false, the progress bar is not set.
     */
    public void setProgressBar(boolean isProgressBar) {
        this.isProgressBar = isProgressBar;
        if (isProgressBar) {
            horizontalLayout.removeAllComponents();
            horizontalLayout.addComponent(passwordField);
            horizontalLayout.addComponent(progressBar);
        } else {
            horizontalLayout.removeAllComponents();
            horizontalLayout.addComponent(passwordField);
            horizontalLayout.addComponent(passwordStrengthImage);
            horizontalLayout.addComponent(passwordStrengthTextL);
        }
    }

    /**
     * Returns true if the minimum requirement text is enabled, otherwise false.
     *
     * @return true if minimum requirement text enabled, otherwise false
     */
    public boolean isMinimumRequirement() {
        return isMinimumRequirement;
    }

    /**
     * Sets the minimum requirement.
     *
     * @param isMinimumRequirement If true, the minimum requirement is set. If false, the minimum requirement is not set.
     */
    public void setMinimumRequirement(boolean isMinimumRequirement) {
        this.isMinimumRequirement = isMinimumRequirement;
        if (isMinimumRequirement) {
            verticalLayout.addComponent(minimumRequirementVerticalLayout);
            verticalLayout.setComponentAlignment(minimumRequirementVerticalLayout, Alignment.TOP_LEFT);
            verticalLayout.setExpandRatio(minimumRequirementVerticalLayout, 1.0f);
        } else {
            verticalLayout.removeComponent(minimumRequirementVerticalLayout);
        }
    }

    /**
     * Returns the minimum requirement caption.
     *
     * @return the minimum requirement caption
     */
    public String getMinimumRequirementCaption() {
        return minimumRequirementLabel.getCaption();
    }

    /**
     * Sets the minimum requirement caption.
     *
     * @param caption the caption to set
     */
    public void setMinimumRequirementCaption(String caption) {
        minimumRequirementLabel.setCaption(caption);
    }

    /**
     * Returns the minimum requirement text.
     *
     * @return the minimum requirement text
     */
    public String getMinimumRequirementText() {
        return minimumRequirementLabel.getValue();
    }

    /**
     * Sets the minimum requirement text.
     *
     * @param textHtml the text to set (HTML is allowed)
     */
    public void setMinimumRequirementText(String textHtml) {
        minimumRequirementVerticalLayout.removeAllComponents();
        if (this.isMinimumRequirement) {
            minimumRequirementLabel.setContentMode(ContentMode.HTML);
            minimumRequirementLabel.setValue(textHtml);

            minimumRequirementVerticalLayout.addComponent(new Label());
            minimumRequirementVerticalLayout.addComponent(minimumRequirementLabel);
            minimumRequirementVerticalLayout.addComponent(new Label());
        } else {
            minimumRequirementVerticalLayout.removeAllComponents();
        }
    }

    /**
     * Sets the minimum requirement caption and text.
     *
     * @param caption  the caption to set
     * @param textHtml the text to set (HTML is allowed)
     */
    public void setMinimumRequirement(String caption, String textHtml) {
        minimumRequirementVerticalLayout.removeAllComponents();
        if (this.isMinimumRequirement) {
            minimumRequirementLabel.setContentMode(ContentMode.HTML);
            minimumRequirementLabel.setCaption(caption);
            minimumRequirementLabel.setValue(textHtml);

            minimumRequirementVerticalLayout.addComponent(new Label());
            minimumRequirementVerticalLayout.addComponent(minimumRequirementLabel);
            minimumRequirementVerticalLayout.addComponent(new Label());
        } else {
            minimumRequirementVerticalLayout.removeAllComponents();
        }
    }

    /**
     * Returns the value of the password field.
     *
     * @return the password field value
     */
    public String getValue() {
        return passwordField.getValue();
    }

    /**
     * Sets the password field value.
     *
     * @param value the password field value to set
     */
    public void setValue(String value) {
        passwordField.setValue(value);
    }

    /**
     * Returns the text for a weak password.
     *
     * @return the password weak text
     */
    public String getPasswordWeakText() {
        return passwordWeakText;
    }

    /**
     * Sets the text for a weak password.
     *
     * @param passwordWeakText the password weak text to set
     */
    public void setPasswordWeakText(String passwordWeakText) {
        this.passwordWeakText = passwordWeakText;
    }

    /**
     * Returns the text for a middle password.
     *
     * @return the password middle text
     */
    public String getPasswordMiddleText() {
        return passwordMiddleText;
    }

    /**
     * Sets the text for a middle password.
     *
     * @param passwordMiddleText the password middle text to set
     */
    public void setPasswordMiddleText(String passwordMiddleText) {
        this.passwordMiddleText = passwordMiddleText;
    }

    /**
     * Returns the text for a strong password.
     *
     * @return the password strong text
     */
    public String getPasswordStrongText() {
        return passwordStrongText;
    }

    /**
     * Sets the text for a strong password.
     *
     * @param passwordStrongText the password strong text to set
     */
    public void setPasswordStrongText(String passwordStrongText) {
        this.passwordStrongText = passwordStrongText;
    }

    /**
     * Returns the text for a very strong password.
     *
     * @return the password strongest text
     */
    public String getPasswordStrongestText() {
        return passwordStrongestText;
    }

    /**
     * Sets the text for a very strong password.
     *
     * @param passwordStrongestText the password strongest text to set
     */
    public void setPasswordStrongestText(String passwordStrongestText) {
        this.passwordStrongestText = passwordStrongestText;
    }

    /**
     * Returns true if the password strength text is enabled, otherwise false.
     *
     * @return true if password strength text enabled, otherwise false
     */
    public boolean isPasswordStrengthText() {
        return isPasswordStrengthText;
    }

    /**
     * Sets the password strength text.
     *
     * @param isPasswordStrengthText If true, the password strength text is set. If false, the password strength text is not set.
     */
    public void setPasswordStrengthText(boolean isPasswordStrengthText) {
        this.isPasswordStrengthText = isPasswordStrengthText;
        if (isPasswordStrengthText) {
            updateComponents(null);
        } else {
            passwordStrengthTextL.setValue(null);
        }
    }

    /**
     * Returns the resource for a weak password.
     *
     * @return the password weak resource
     */
    public Resource getPasswordWeakResource() {
        return passwordWeakResource;
    }

    /**
     * Sets the resource for a weak password.
     *
     * @param passwordWeakResourcePath the path to the resource to set
     */
    public void setPasswordWeakResource(String passwordWeakResourcePath) {
        this.passwordWeakResource = new FileResource(new File(passwordWeakResourcePath));
        updateComponents(null);
    }

    /**
     * Sets the resource for a weak password.
     *
     * @param passwordWeakResourceFile the file to the resource to set
     */
    public void setPasswordWeakResource(File passwordWeakResourceFile) {
        this.passwordWeakResource = new FileResource(passwordWeakResourceFile);
        updateComponents(null);
    }

    /**
     * Sets the resource for a weak password.
     *
     * @param passwordWeakResource the password weak resource to set
     */
    public void setPasswordWeakResource(Resource passwordWeakResource) {
        this.passwordWeakResource = passwordWeakResource;
        updateComponents(null);
    }

    /**
     * Returns the resource for a middle password.
     *
     * @return the password middle resource
     */
    public Resource getPasswordMiddleResource() {
        return passwordMiddleResource;
    }

    /**
     * Sets the resource for a middle password.
     *
     * @param passwordMiddleResourcePath the path to the resource to set
     */
    public void setPasswordMiddleResource(String passwordMiddleResourcePath) {
        this.passwordMiddleResource = new FileResource(new File(passwordMiddleResourcePath));
        updateComponents(null);
    }

    /**
     * Sets the resource for a middle password.
     *
     * @param passwordMiddleResourceFile the file to the resource to set
     */
    public void setPasswordMiddleResource(File passwordMiddleResourceFile) {
        this.passwordMiddleResource = new FileResource(passwordMiddleResourceFile);
        updateComponents(null);
    }

    /**
     * Sets the resource for a middle password.
     *
     * @param passwordMiddleResource the password middle resource to set
     */
    public void setPasswordMiddleResource(Resource passwordMiddleResource) {
        this.passwordMiddleResource = passwordMiddleResource;
        updateComponents(null);
    }

    /**
     * Returns the resource for a strong password.
     *
     * @return the password strong resource
     */
    public Resource getPasswordStrongResource() {
        return passwordStrongResource;
    }

    /**
     * Sets the resource for a strong password.
     *
     * @param passwordStrongResourcePath the path to the resource to set
     */
    public void setPasswordStrongResource(String passwordStrongResourcePath) {
        this.passwordStrongResource = new FileResource(new File(passwordStrongResourcePath));
        updateComponents(null);
    }

    /**
     * Sets the resource for a strong password.
     *
     * @param passwordStrongResourceFile the file to the resource to set
     */
    public void setPasswordStrongResource(File passwordStrongResourceFile) {
        this.passwordStrongResource = new FileResource(passwordStrongResourceFile);
        updateComponents(null);
    }

    /**
     * Sets the resource for a strong password.
     *
     * @param passwordStrongResource the password strong resource to set
     */
    public void setPasswordStrongResource(Resource passwordStrongResource) {
        this.passwordStrongResource = passwordStrongResource;
        updateComponents(null);
    }

    /**
     * Returns the resource for a very strong password.
     *
     * @return the password strongest resource
     */
    public Resource getPasswordStrongestResource() {
        return passwordStrongestResource;
    }

    /**
     * Sets the resource for a very strong password.
     *
     * @param passwordStrongestResourcePath the path to the resource to set
     */
    public void setPasswordStrongestResource(String passwordStrongestResourcePath) {
        this.passwordStrongestResource = new FileResource(new File(passwordStrongestResourcePath));
        updateComponents(null);
    }

    /**
     * Sets the resource for a very strong password.
     *
     * @param passwordStrongestResourceFile the file to the resource to set
     */
    public void setPasswordStrongestResource(File passwordStrongestResourceFile) {
        this.passwordStrongestResource = new FileResource(passwordStrongestResourceFile);
        updateComponents(null);
    }

    /**
     * Sets the resource for a very strong password.
     *
     * @param passwordStrongestResource the password strongest resource to set
     */
    public void setPasswordStrongestResource(Resource passwordStrongestResource) {
        this.passwordStrongestResource = passwordStrongestResource;
        updateComponents(null);
    }

    /**
     * Returns true if the password strength resource is enabled, otherwise false.
     *
     * @return true if password strength resource enabled, otherwise false
     */
    public boolean isPasswordStrengthResource() {
        return isPasswordStrengthResource;
    }

    /**
     * Sets the password strength resource.
     *
     * @param isPasswordStrengthResource If true, the password strength resource is set. If false, the password strength resource is not set.
     */
    public void setPasswordStrengthResource(boolean isPasswordStrengthResource) {
        this.isPasswordStrengthResource = isPasswordStrengthResource;
        if (isPasswordStrengthResource) {
            updateComponents(null);
        } else {
            passwordStrengthImage.setSource(null);
        }
    }

    /**
     * Returns the total width of the component.
     *
     * @return the total width
     */
    @Override
    public float getWidth() {
        return verticalLayout.getWidth();
    }

    /**
     * Sets the total width of the component.
     *
     * @param width the total width to set
     */
    @Override
    public void setWidth(String width) {
        verticalLayout.setWidth(width);
    }

    /**
     * Returns the total height of the component.
     *
     * @return the total height
     */
    @Override
    public float getHeight() {
        return verticalLayout.getHeight();
    }

    /**
     * Sets the total height of the component.
     *
     * @param height the total height to set
     */
    @Override
    public void setHeight(String height) {
        verticalLayout.setHeight(height);
    }

    /**
     * Returns the unit of the total width.
     *
     * @return the unit of the total width
     */
    @Override
    public Unit getWidthUnits() {
        return verticalLayout.getWidthUnits();
    }

    /**
     * Sets the total width with unit of the component.
     *
     * @param width the total width to set
     * @param unit  the unit to set
     */
    @Override
    public void setWidth(float width, Sizeable.Unit unit) {
        if (verticalLayout != null)
            verticalLayout.setWidth(width, unit);
    }

    /**
     * Returns the unit of the total height.
     *
     * @return the unit of the total height
     */
    @Override
    public Unit getHeightUnits() {
        return verticalLayout.getHeightUnits();
    }

    /**
     * Sets the total height with unit of the component.
     *
     * @param height the total height to set
     * @param unit   the unit to set
     */
    @Override
    public void setHeight(float height, Sizeable.Unit unit) {
        verticalLayout.setHeight(height, unit);
    }

    /**
     * Returns the width of the password field.
     *
     * @return the width of the password field
     */
    public float getPasswordFieldWidth() {
        return passwordField.getWidth();
    }

    /**
     * Sets the width of the password field.
     *
     * @param width the width of the password field to set
     */
    public void setPasswordFieldWidth(String width) {
        passwordField.setWidth(width);
    }

    /**
     * Returns the height of the password field.
     *
     * @return the height of the password field
     */
    public float getPasswordFieldHeight() {
        return passwordField.getHeight();
    }

    /**
     * Sets the height of the password field.
     *
     * @param height the height of the password field to set
     */
    public void setPasswordFieldHeight(String height) {
        passwordField.setHeight(height);
    }

    /**
     * Returns the unit of the password field width.
     *
     * @return the unit of the password field width
     */
    public Unit getPasswordFieldWidthUnits() {
        return passwordField.getWidthUnits();
    }

    /**
     * Sets the width of the password field.
     *
     * @param width the width of the password field to set
     * @param unit  the unit to set
     */
    public void setPasswordFieldWidth(float width, Sizeable.Unit unit) {
        passwordField.setWidth(width, unit);
    }

    /**
     * Returns the unit of the password field height.
     *
     * @return the unit of the password field height
     */
    public Unit getPasswordFieldHeightUnits() {
        return passwordField.getHeightUnits();
    }

    /**
     * Sets the height of the password field.
     *
     * @param height the height of the password field to set
     * @param unit   the unit to set
     */
    public void setPasswordFieldHeight(float height, Sizeable.Unit unit) {
        passwordField.setHeight(height, unit);
    }

    /**
     * Returns true if the password field is read only, otherwise false.
     *
     * @return true if password field is read only, otherwise false
     */
    @Override
    public boolean isReadOnly() {
        return passwordField.isReadOnly();
    }

    /**
     * Sets the read only mode for the password field.
     *
     * @param isReadOnly If true, the password field is read only. If false, the password field is not read only.
     */
    @Override
    public void setReadOnly(boolean isReadOnly) {
        passwordField.setReadOnly(isReadOnly);
    }
}
