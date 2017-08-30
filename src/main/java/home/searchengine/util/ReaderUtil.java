package home.searchengine.util;

public class ReaderUtil {

	public static float parseFloat(String inputString){
		if(inputString.contains("/")){
			String[] ratio = inputString.split("/");
			return Float.parseFloat(ratio[0])/Float.parseFloat(ratio[1]);
		} else{
			return Float.parseFloat(inputString);
		}
	}
}
