package cakes.nlp.parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import cakes.nlp.core.Lemma;
import cakes.nlp.core.Token;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

public class POSTagger {

	static POSTagger instance = null;
	
	private WordnetStemmer stemmer;	
	private POSTaggerME tagger;
	private IDictionary dict;
	private Hashtable<String, String> pennMap;
	
	private POSTagger () {
		
		try {
			InputStream modelIn = new FileInputStream("D:\\apache\\apache-opennlp-1.5.2-incubating\\en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			tagger = new POSTaggerME(model);
			
			URL url = new URL("file", null , "D:\\tools\\WordNet-3.0\\dict" );
			dict = new Dictionary (url);
			dict.open();
			stemmer = new WordnetStemmer(dict);
			
			pennMap = initializePennMap();
		}
		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (InvalidFormatException e) {

			e.printStackTrace();
		}
		catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	
	public void tag (List<Token> tokens) {
		
    	List<String> tokenStrings = new ArrayList<String>();
    	
    	for ( Token t: tokens ) {
    		
     		tokenStrings.add(t.getSurface().toLowerCase());
    	}
    	
    	String[] tokensArray = tokenStrings.toArray(new String[tokenStrings.size()]);
    	
    	String[] tags  = tagger.tag(tokensArray);
    	double[] probs = tagger.probs();
		
    	String posChars = "JRNV";
    	POS[] posTable = {POS.ADJECTIVE, POS.ADVERB, POS.NOUN, POS.VERB};

    	for (int i = 0; i < tags.length; i++) {
    		
    		String penn = Character.isLetterOrDigit(tokensArray[i].charAt(0)) ? tags[i] : "SYM";
    		if ( penn.equals(",")) penn = "NN"; //fix
    		Token token = tokens.get(i);
  				
        	char penn1 = penn.charAt(0); int posNum;
			Lemma lemma = new Lemma();
        	
        	if ( (posNum = posChars.indexOf(penn1)) >= 0 ) {
        		
        		List<String> lemmas = stemmer.findStems(token.getSurface(), posTable[posNum]);
        		
        		if ( lemmas.size() > 0 && (dict.getIndexWord(lemmas.get(lemmas.size() - 1), posTable[posNum]) != null) ) {
        			
        			lemma.setLemmaForm(lemmas.get(lemmas.size() - 1));
        			lemma.setPennTag(penn);
         		}
        		else {
        			
        			// make unknown words nouns
        			lemma.setLemmaForm(token.getSurface().toLowerCase());
        			lemma.setPennTag("NN");
         		}
        	}    		
        	else {
        		
        		//if ( probs[i] < 0.5 && (penn.equals("IN") || penn.equals("DT")) ) {
            	if ( probs[i] < 0.5 && !(penn.equals("SYM")) ) {
        			
        			lemma.setLemmaForm(token.getSurface().toLowerCase());
        			lemma.setPennTag("NN");
        		}
            	else if ( probs[i] < 0.65 && penn.equals("IN") ) {
        			
        			lemma.setPennTag("NN");
        		}
    			lemma.setPennTag(penn);
        	}
			
        	lemma.setType(pennMap.get(lemma.getPennTag()));
        	if ( lemma.getLemmaForm() == null )  lemma.setLemmaForm(token.getSurface().toLowerCase());
			token.getLemmas().add(lemma);
        	
    		//System.out.println(tokensArray[i] + " = " + tags[i] + " ... " + tokens.get(i) + " ... " + probs[i]);
    	}
	}
	
	
	public static POSTagger getInstance() {

		if ( instance == null )  instance = new POSTagger(); 
		return instance;
	}

	
	private static Hashtable<String, String> initializePennMap() {
		
		Hashtable<String, String> map = new Hashtable<String, String>();
		
		map.put("NN", "Noun");
		map.put("NNS", "Noun");
		map.put("NNP", "Noun");
		map.put("NNPS", "Noun");
		map.put("FW", "Noun");
		
		map.put("JJ", "Adjective");
		map.put("JJR", "Adjective");
		map.put("JJS", "Adjective");
		map.put("CD", "Adjective");

		map.put("VB", "Verb");
		map.put("VBD", "Verb");
		map.put("VBN", "Verb");
		map.put("VBZ", "Verb");
		map.put("VBG", "Verb");
		map.put("VBP", "Verb");

		map.put("WDT", "Pronoun");
		map.put("WP", "Pronoun");
		map.put("WRB", "Adverb");

		map.put("RBS", "Adjective");
		map.put("RBR", "Adjective");

		map.put("RB", "Adverb");
		map.put("MD", "Adverb");
		map.put("RP", "Adverb");
		map.put("EX", "Adverb");

		map.put("PRP", "Pronoun");
		map.put("PRP$", "Pronoun");

		map.put("DT", "Determiner");

		map.put("CC", "Conjunction");

		map.put("IN", "Adposition");
		map.put("TO", "Adposition");

		map.put("SYM", "Punctuation");

		map.put("UH", "Noun");
		map.put("LS", "Noun");

		return map;
	}

}
