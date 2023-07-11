package chess.util;

public class ShortBitboard {

	
	private short bitboard;
	
	public ShortBitboard() {
		bitboard = 0;
	}
	
	public short get() {
		return bitboard;
	}
	
	public void setBitboard(short bitboard) {
		this.bitboard = bitboard;
	}
	
}
