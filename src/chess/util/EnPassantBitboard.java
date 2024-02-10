package chess.util;

public class EnPassantBitboard {

	
	private short bitboard;
	private short previous;
	
	public EnPassantBitboard() {
		bitboard = 0;
		previous = 0;
	}
	
	public short get() {
		return bitboard;
	}
	
	public short getPrevious() {
		return previous;
	}
	
	public void setBitboard(short bitboard) {
		this.previous = this.bitboard;
		this.bitboard = bitboard;
	}
	
}
