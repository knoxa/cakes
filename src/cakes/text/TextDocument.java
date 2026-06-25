package cakes.text;
import java.util.Map;
import java.util.Set;

public interface TextDocument {

	public String getTextContent();
	public String getUri();
	public Map<String, Set<String>> getDictionaryMap();

}
