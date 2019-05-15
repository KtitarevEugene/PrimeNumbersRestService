package web_app.messaging.exceptions;

public class JMSConfigurationException extends Exception {
    public JMSConfigurationException() { super(); }

    public JMSConfigurationException(String what) { super(what); }

    public JMSConfigurationException(Throwable cause) { super(cause); }

    public JMSConfigurationException(String what, Throwable cause) { super(what, cause); }
}
