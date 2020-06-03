package zed.mopm.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 *     Special characters can be found at: <a href="https://www.w3schools.com/charsets/ref_html_utf8.asp">here</a><br></br>
 *     Box drawing characters: <a href="https://www.w3schools.com/charsets/ref_utf_box.asp">here</a><br></br>
 *     <ul>
 *         <li><p><b>Hex:</b> \u252C = ┬</p></li>
 *         <li><p><b>Hex:</b> \u2502 = │</p></li>
 *         <li><p><b>Hex:</b> \u251C = ├</p></li>
 *         <li><p><b>Hex:</b> \u2500 = ─</p></li>
 *         <li><p><b>Hex:</b> \u2514 = └</p></li>
 *     </ul>
 * </p>
 */
public class PrintUtils {
	public static final Logger LOG = LogManager.getLogger(PrintUtils.class.getName());

	private PrintUtils() {}

	public static void println(final String message, final String charSet) {
		StringBuilder msg = new StringBuilder(message).append('\n');
		PrintUtils.println(msg.toString(), charSet);
	}

	public static void print(final String message, final String charSet) {
		try (final PrintStream stream = new PrintStream(System.out, true, charSet)) {
			String str = new String(message.getBytes(charSet), charSet);
			stream.print(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void main(final String[] args) {
		LOG.debug("Hi: \u2514");
	}
}
