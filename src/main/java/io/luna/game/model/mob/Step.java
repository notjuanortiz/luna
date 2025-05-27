package io.luna.game.model.mob;

import com.google.common.base.MoreObjects;
import io.luna.game.model.Position;

import java.util.Objects;

/**
 * A model representing a step in the walking queue.
 */
public final class Step {

    /**
     * The x coordinate.
     */
    private final int x;

    /**
     * The y coordinate.
     */
    private final int y;

    /**
     * Creates a new {@link Step}.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public Step(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new {@link Step}.
     *
     * @param position The position.
     */
    public Step(Position position) {
        this(position.getX(), position.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Step) {
            Step other = (Step) obj;
            return x == other.x && y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", x)
                .add("y", y)
                .toString();
    }

    /**
     * @return The x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The y coordinate.
     */
    public int getY() {
        return y;
    }
}
