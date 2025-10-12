package cakes.nlp.dictionary;

public class Match {

		private int begin, end;
		private String dictionaryEntry;

		public int getBegin() {
			return begin;
		}
		
		void setBegin(int begin) {
			this.begin = begin;
		}
		
		public int getEnd() {
			return end;
		}
		
		void setEnd(int end) {
			this.end = end;
		}

		public String getDictionaryEntry() {
			return dictionaryEntry;
		}
		
		void setDictionaryEntry(String dictionaryEntry) {
			this.dictionaryEntry = dictionaryEntry;
		}
}
