package remembrall.model.validation;

public class EmailExistsException extends Exception {
    public EmailExistsException(String s) {
        super(s);
    }
}
