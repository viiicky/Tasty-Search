package home.searchengine.util;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class IndexerUtils {

	public static List<String> getWordsFromString(String inputString){
		List<String> tokens = new ArrayList<>();

		BreakIterator iterator = BreakIterator.getWordInstance();
		iterator.setText(inputString);

		int start = iterator.first();
		for(int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()){
			tokens.add(inputString.substring(start, end));
		}

		return tokens;
	}
}
