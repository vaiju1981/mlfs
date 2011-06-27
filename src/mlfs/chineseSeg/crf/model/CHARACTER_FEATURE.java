package mlfs.chineseSeg.crf.model;

public enum CHARACTER_FEATURE{
	DIGIT(1), CHINESE_DIGIT(2), LETTER(3), PUNCTUATION(4), SINGLE(5), PREFIX(6), SUFFIX(7), LONGEST(8), OTHERS(9);
	private int m_value;
	private CHARACTER_FEATURE(int v){
		m_value = v;
	}
	public int getValue(){
		return m_value;
	}
}
