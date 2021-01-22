package io.luna.game.model.chunk;

import io.luna.game.model.Entity;
import io.luna.game.model.EntityType;
import io.luna.game.model.Position;
import io.luna.game.model.mob.Mob;
import io.luna.game.model.mob.Npc;
import io.luna.game.model.mob.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A model that loads new chunks and manages loaded chunks.
 *
 * @author lare96 <http://github.org/lare96>
 */
public final class ChunkManager implements Iterable<Chunk> {

    /**
     * Determines the local player count at which prioritized updating will start.
     */
    public static final int LOCAL_MOB_THRESHOLD = 50;

    /**
     * How many layers of chunks will be loaded around a player, when looking for viewable mobs.
     */
    public static final int RADIUS = 2;

    /**
     * A map of loaded chunks.
     */
    private final Map<ChunkPosition, Chunk> chunks = new HashMap<>(128); // TODO Proper initial size after cache loading.

    @Override
    public Spliterator<Chunk> spliterator() {
        return Spliterators.spliterator(chunks.values(), Spliterator.NONNULL);
    }

    @Override
    public Iterator<Chunk> iterator() {
        return chunks.values().iterator();
    }

    /**
     * Retrieves a chunk based on the argued chunk position, constructing and loading a new one if needed.
     *
     * @param position The position to construct a new chunk with.
     * @return The existing or newly loaded chunk.
     */
    public Chunk load(ChunkPosition position) {
        return chunks.computeIfAbsent(position, Chunk::new);
    }

    /**
     * Retrieves a chunk based on the argued position, constructing and loading a new one if needed.
     *
     * @param position The position to construct a new chunk with.
     * @return The existing or newly loaded chunk.
     */
    public Chunk load(Position position) {
        return load(position.getChunkPosition());
    }

    /**
     * Shortcut to {@link #getUpdateMobs(Player, EntityType)} for type {@code PLAYER}.
     */
    public Set<Player> getUpdatePlayers(Player player) {
        return getUpdateMobs(player, EntityType.PLAYER);
    }

    /**
     * Shortcut to {@link #getUpdateMobs(Player, EntityType)} for type {@code NPC}.
     */
    public Set<Npc> getUpdateNpcs(Player player) {
        return getUpdateMobs(player, EntityType.NPC);
    }

    /**
     * Returns an update set for {@code type}, potentially sorted by the {@link ChunkMobComparator}.
     *
     * @param player The player.
     * @param type The entity type.
     * @param <T> The type.
     * @return The update set.
     */
    private <T extends Mob> Set<T> getUpdateMobs(Player player, EntityType type) {
        Set<T> updateSet;
        if (player.getLocalPlayers().size() > LOCAL_MOB_THRESHOLD && type == EntityType.PLAYER ||
                player.getLocalNpcs().size() > LOCAL_MOB_THRESHOLD && type == EntityType.NPC) {
            updateSet = new TreeSet<>(new ChunkMobComparator(player));
        } else {
            updateSet = new HashSet<>();
        }

        var chunkPosition = player.getChunkPosition();
        for (int x = -RADIUS; x < RADIUS; x++) {
            for (int y = -RADIUS; y < RADIUS; y++) {
                var currentChunk = load(chunkPosition.translate(x, y));
                Set<T> mobs = currentChunk.getAll(type);
                for (T inside : mobs) {
                    if (inside.isViewableFrom(player)) {
                        updateSet.add(inside);
                    }
                }
            }
        }
        return updateSet;
    }

    /**
     * Returns a set of viewable entities.
     *
     * @param position The relative position.
     * @param type The entity type.
     * @param <T> The type.
     * @return The set.
     */
    public <T extends Entity> Set<T> getViewableEntities(Position position, EntityType type) {
        Set<T> viewable = new HashSet<>();
        ChunkPosition chunkPos = position.getChunkPosition();
        for (int x = -RADIUS; x < RADIUS; x++) {
            for (int y = -RADIUS; y < RADIUS; y++) {
                Chunk chunk = load(chunkPos.translate(x, y));
                Set<T> entities = chunk.getAll(type);
                for (T inside : entities) {
                    if (inside.getPosition().isViewable(position)) {
                        viewable.add(inside);
                    }
                }
            }
        }
        return viewable;
    }

    /**
     * Returns a list of viewable chunks.
     *
     * @param position The relative position.
     * @return The list.
     */
    public List<Chunk> getViewableChunks(Position position) {
        List<Chunk> viewable = new ArrayList<>(16);
        ChunkPosition chunkPos = position.getChunkPosition();
        for (int x = -RADIUS; x < RADIUS; x++) {
            for (int y = -RADIUS; y < RADIUS; y++) {
                Chunk chunk = load(chunkPos.translate(x, y));
                viewable.add(chunk);
            }
        }
        return viewable;
    }

    /**
     * @return A stream over every single chunk.
     */
    public Stream<Chunk> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
