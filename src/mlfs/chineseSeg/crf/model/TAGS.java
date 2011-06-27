package mlfs.chineseSeg.crf.model;

public enum TAGS {
	B(0), M(1), E(2), S(3);
    
    private final int m_value;
    
    private TAGS(int value) {
        this.m_value = value;
    }
    
    public int getValue() {
        return m_value;
    }
}