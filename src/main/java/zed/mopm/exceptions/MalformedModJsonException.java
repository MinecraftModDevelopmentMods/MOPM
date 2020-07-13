package zed.mopm.exceptions;

public class MalformedModJsonException extends Exception {
	public MalformedModJsonException() {
	}

	public MalformedModJsonException(String message) {
		super(message);
	}

	public MalformedModJsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedModJsonException(Throwable cause) {
		super(cause);
	}
}
