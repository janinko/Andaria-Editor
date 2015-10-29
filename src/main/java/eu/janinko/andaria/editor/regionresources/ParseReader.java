
package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class ParseReader {
	private BufferedReader r;
	private static final int MARK = 50000;
	private String oline;

	public ParseReader(BufferedReader r) {
		this.r = r;
	}

	private final Matcher mComment = Pattern.compile("//.*").matcher("");
	public String readLine() throws IOException{
		oline = r.readLine();
		if (oline == null) return null;

		return mComment.reset(oline).replaceFirst("").trim().toLowerCase();
	}

	public String getCurrentOriginalLine(){
		return oline;
	}

	void mark() throws IOException {
		r.mark(MARK);
	}

	void reset() throws IOException {
		r.reset();
	}
}
