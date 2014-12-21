package game;

final class Position {

    private final int x;
    private final int y;

    Position(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    static Position position(final int x, final int y) {
        return new Position(x, y);
    }

    @Override public String toString() {
        return String.format("(%s, %s)", x, y);
    }
}
