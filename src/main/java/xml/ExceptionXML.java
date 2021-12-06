package xml;

import java.io.Serial;

/**
 * Exception raised when XML parsing fails.
 */
public class ExceptionXML extends Exception {

	@Serial
    private static final long serialVersionUID = 1L;

	/**
	 * Constructor. Call constructor of Exception using message parameter.
	 * @param message message to print
	 */
	public ExceptionXML(String message) {
		super(message);
	}

}
