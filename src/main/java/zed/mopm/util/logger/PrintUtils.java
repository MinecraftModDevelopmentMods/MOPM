package zed.mopm.util.logger;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 *     Special characters can be found at: <a href="https://www.w3schools.com/charsets/ref_html_utf8.asp">here</a><br></br>
 *     Box drawing characters: <a href="https://www.w3schools.com/charsets/ref_utf_box.asp">here</a><br></br>
 *     <ul>
 *         <li><p><b>Hex:</b> {@literal \}u252C = ┬</p></li>
 *         <li><p><b>Hex:</b> {@literal \}u2502 = │</p></li>
 *         <li><p><b>Hex:</b> {@literal \}u251C = ├</p></li>
 *         <li><p><b>Hex:</b> {@literal \}u2500 = ─</p></li>
 *         <li><p><b>Hex:</b> {@literal \}u2514 = └</p></li>
 *     </ul>
 * </p>
 */
public class PrintUtils {
	private PrintUtils() {}

	/**
	 * Prints to the console using a specified character set and creates a new line.
	 * @param message The message to be printed.
	 * @param charSet The character set that should be printed.
	 */
	public static void println(final String message, final String charSet) {
		StringBuilder msg = new StringBuilder(message).append('\n');
		PrintUtils.print(msg.toString(), charSet);
	}

	/**
	 * Prints to the console using a specified character set.
	 * @param message The message to be printed.
	 * @param charSet The character set that should be printed.
	 */
	public static void print(final String message, final String charSet) {
		try {
			final PrintStream stream = new PrintStream(System.out, true, charSet);
			final String str = new String(message.getBytes(charSet), charSet);
			stream.print(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
