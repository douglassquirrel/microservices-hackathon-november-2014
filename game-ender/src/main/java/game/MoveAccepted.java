package game;

final class MoveAccepted {

    private final String game;
    private final String player;
    private final Position position;

    MoveAccepted(final String game, final String player, final Position position) {
        this.game = game;
        this.player = player;
        this.position = position;
    }

    public String getGame() {
        return game;
    }

    public String getPlayer() {
        return player;
    }

    public Position getPosition() {
        return position;
    }

    @Override public String toString() {
        return "MoveAccepted{" +
                "game='" + game + '\'' +
                ", player='" + player + '\'' +
                ", position=" + position +
                '}';
    }
}
