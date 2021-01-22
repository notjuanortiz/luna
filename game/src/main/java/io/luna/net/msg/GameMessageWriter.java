package io.luna.net.msg;

import io.luna.game.model.mob.Player;
import io.luna.net.codec.ByteMessage;

/**
 * An abstraction model that converts raw written byte messages into game messages.
 *
 * @author lare96 <http://github.org/lare96>
 */
public abstract class GameMessageWriter {

    /**
     * Writes data into a buffer.
     *
     * @param player The player.
     * @return The buffer.
     */
    public abstract ByteMessage write(Player player);

    /**
     * Converts the buffer returned by {@link #write(Player)} into a game message.
     *
     * @param player The player.
     * @return The converted game message.
     */
    public final GameMessage toGameMsg(Player player) {
        ByteMessage raw = write(player);
        return new GameMessage(raw.getOpcode(), raw.getType(), raw);
    }
}
