package game;

final class GameEnd {

    private final String gameId;
    private final String result;
    private final String winnerId;

    private GameEnd(final String gameId, final String result, final String winnerId) {
        this.gameId = gameId;
        this.result = result;
        this.winnerId = winnerId;
    }

    public final String getGameId() {
        return gameId;
    }

    public String getResult() {
        return result;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public static GameEnd win(final String gameId, final String winnerId) {
        return new GameEnd(gameId, "win", winnerId);
    }

    public static GameEnd draw(final String gameId) {
        return new GameEnd(gameId, "draw", null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GameEnd gameEnd = (GameEnd) o;

        if (!gameId.equals(gameEnd.gameId)) return false;
        if (!result.equals(gameEnd.result)) return false;
        if (winnerId != null ? !winnerId.equals(gameEnd.winnerId) : gameEnd.winnerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = gameId.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + (winnerId != null ? winnerId.hashCode() : 0);
        return result1;
    }

    @Override public String toString() {
        return result.equals("win")
                ? String.format("Win (game: %s, player: %s)", gameId, winnerId)
                : String.format("Draw (game: %s", gameId);
    }
}
