package org.netbeans.modules.svg;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.swing.svg.SVGUserAgent;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;

public class ConsoleLoggingUserAgent implements SVGUserAgent {

    private static final Logger LOG = Logger.getLogger(ConsoleLoggingUserAgent.class.getName());

    @Override
    public void displayError(String message) {
        LOG.log(Level.SEVERE, "Error while rendering the svg: {0}", message);
    }

    @Override
    public void displayError(Exception ex) {
        this.displayError(ex.getMessage());
    }

    @Override
    public void displayMessage(String message) {
        LOG.log(Level.INFO, "Message: {0}", message);
    }

    @Override
    public void showAlert(String message) {
        LOG.log(Level.WARNING, "Warning: {0}", message);
    }

    @Override
    public String showPrompt(String message) {
        return "";
    }

    @Override
    public String showPrompt(String message, String defaultValue) {
        return defaultValue;
    }

    @Override
    public boolean showConfirm(String message) {
        return true;
    }

    @Override
    public float getPixelUnitToMillimeter() {
        return 0.264583f; // Standard for 96 DPI
    }

    @Override
    public float getPixelToMM() {
        return getPixelUnitToMillimeter();
    }

    @Override
    public String getDefaultFontFamily() {
        return "Arial, sans-serif";
    }

    @Override
    public float getMediumFontSize() {
        return 12f;
    }

    @Override
    public float getLighterFontWeight(float f) {
        return Math.max(f - 100, 100);
    }

    @Override
    public float getBolderFontWeight(float f) {
        return Math.min(f + 100, 900);
    }

    @Override
    public String getLanguages() {
        return "";
    }

    @Override
    public String getUserStyleSheetURI() {
        return null;
    }

    @Override
    public String getXMLParserClassName() {
        return "org.apache.xerces.parsers.SAXParser";
    }

    @Override
    public boolean isXMLParserValidating() {
        return true;
    }

    @Override
    public String getMedia() {
        return "screen";
    }

    @Override
    public String getAlternateStyleSheet() {
        return null;
    }

    @Override
    public void openLink(String uri, boolean newc) {
    }

    @Override
    public boolean supportExtension(String s) {
        return true;
    }

    @Override
    public void handleElement(Element elt, Object data) {
    }

    @Override
    public ScriptSecurity getScriptSecurity(String scriptType, ParsedURL scriptURL, ParsedURL docURL) {
        return null;
    }

    @Override
    public void checkLoadScript(String scriptType, ParsedURL scriptURL, ParsedURL docURL) throws SecurityException {
    }

    @Override
    public ExternalResourceSecurity getExternalResourceSecurity(ParsedURL resourceURL, ParsedURL docURL) {
        return null;
    }

    @Override
    public void checkLoadExternalResource(ParsedURL resourceURL, ParsedURL docURL) throws SecurityException {
    }

    @Override
    public float getSourceResolution() {
        return 96.0f; // Standard-Resolution in DPI
    }

    @Override
    public void setSourceResolution(float sourceResolution) {
    }

}
