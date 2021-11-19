package xml;

/**
 * Exception raised when XML parsing fails.
 */
public class ExceptionXML extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. Call constructor of Exception using message parameter.
	 * @param message message to print
	 */
	public ExceptionXML(String message) {
		super(message);
	}

}
