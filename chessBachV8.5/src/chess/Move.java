package chess;
public class Move {
    int srcx, srcy, destx, desty;
    boolean capture;  /* true if this was a capture move */

    Move() {}

    Move(int x0, int y0, int x1, int y1, boolean c) {
        srcx = x0;
        srcy = y0;
        destx = x1;
        desty = y1;
        capture = c;
    }

    Move(String s) {
        srcx = s.charAt(0) - 'a';
        srcy = s.charAt(1) - '1';
        destx = s.charAt(2) - 'a';
        desty = s.charAt(3) - '1';
    }

    public void copyMove(Move m) {
        srcx = m.srcx;
        srcy = m.srcy;
        destx = m.destx;
        desty = m.desty;
        capture = m.capture;
    }

    public boolean equals(Move m) {
        return (m.srcx == srcx && m.srcy == srcy && m.destx == destx && m.desty == desty);
    }

    public String toString() {
        return new String (new byte[] {
                (byte) ('a'+srcx), (byte)('1'+srcy), (byte)('a'+destx), (byte)('1'+desty)});
    }
}