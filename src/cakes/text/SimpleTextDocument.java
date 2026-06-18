package cakes.text;

public class SimpleTextDocument implements TextDocument {

	private String text = null;
	private String uri = null;
	
	@Override
	public String getTextContent() {
		return text;
	}

	@Override
	public String getUri() {
		return uri;
	}

	public SimpleTextDocument(String text, String uri) {
		
		this.text = text;
		this.uri = uri;
	}

	
}
