package world.player.skill.woodcutting.cutTree

import api.predef.*
import api.predef.ext.*
import io.luna.game.action.impl.ItemContainerAction.AnimatedInventoryAction
import io.luna.game.action.impl.ItemContainerAction.InventoryAction
import io.luna.game.model.EntityState
import io.luna.game.model.item.Item
import io.luna.game.model.mob.Player
import io.luna.game.model.mob.block.Animation
import io.luna.game.model.`object`.GameObject
import world.player.Sounds
import world.player.skill.woodcutting.searchNest.Nest

/**
 * An [InventoryAction] that will enable the cutting of trees.
 */
class CutTreeAction(plr: Player, val axe: Axe, val tree: Tree, val treeObj: GameObject) :
    AnimatedInventoryAction(plr, 1, 6, rand(RANDOM_FAIL_RATE)) {

    companion object {

        /**
         * How often the player will randomly stop cutting, in actions (<x> logs cut).
         */
        val RANDOM_FAIL_RATE = 5..50

        /**
         * The base cut time, the lower this number the faster logs will be cut overall.
         */
        val BASE_CUT_RATE = 25
    }

    /**
     * Sound delay for woodcutting.
     */
    private var soundDelay = -1

    override fun executeIf(start: Boolean) = when {
        mob.inventory.isFull -> {
            // No inventory space.
            mob.sendMessage(onInventoryFull())
            false
        }

        mob.woodcutting.level < tree.level -> {
            // Check if we have required level.
            mob.sendMessage("You need a Woodcutting level of ${tree.level} to cut this.")
            false
        }

        // Check if tree isn't already cut.
        treeObj.state == EntityState.INACTIVE -> false

        !Axe.hasAxe(mob, axe) -> {
            mob.sendMessage("You need an axe to chop this tree.")
            mob.sendMessage("You do not have an axe that you have the woodcutting level to use.")
            false
        }

        else -> {
            if (start) {
                mob.playSound(Sounds.CUT_TREE_1, 40)
                mob.sendMessage("You swing your axe at the tree.")
                delay = getWoodcuttingDelay()
            }
            true
        }
    }

    override fun execute() {
        if (rand().nextInt(256) == 0) {
            val nest = Nest.VALUES.random()
            mob.sendMessage("A bird's nest drops to the floor!")
            world.addItem(nest.id, 1, mob.position, mob)
        } else {
            val name = itemName(tree.logsId)
            mob.sendMessage("You get some ${name.toLowerCase()}.")
            mob.woodcutting.addExperience(tree.exp)

            if (mob.inventory.isFull) {
                interrupt()
            }
        }

        if (tree.depletionChance == 1 || rand().nextInt(tree.depletionChance) == 0) {
            deleteAndRespawnTree()
            complete()
        } else {
            delay = getWoodcuttingDelay()
        }
    }

    override fun animation(): Animation {
        mob.playSound(Sounds.CUT_TREE_2)
        soundDelay = 2
        return axe.animation
    }

    override fun add(): List<Item?> = listOf(Item(
        tree.logsId))

    override fun onFinished() {
        mob.animation(Animation.CANCEL)
    }

    override fun onProcessAnimation() {
        if (soundDelay-- == 0) {
            mob.playSound(Sounds.CUT_TREE_2)
        }
    }

    /**
     * Replaces [treeObj] with a stump and respawns it at a later time.
     */
    private fun deleteAndRespawnTree() {
        if (world.removeObject(treeObj)) {
            mob.playSound(Sounds.TREE_FALLEN)
            val stumpId = TreeStump.TREE_ID_MAP[treeObj.id]?.stumpId
            if (stumpId != null) {
                world.addObject(
                    stumpId,
                    treeObj.position,
                    treeObj.objectType,
                    treeObj.direction)
            }
            world.scheduleOnce(tree.respawnTicks) {
                world.addObject(
                    treeObj.id,
                    treeObj.position,
                    treeObj.objectType,
                    treeObj.direction)
            }
        }
    }

    /**
     * Determines how many ticks it will take to get the next log.
     */
    private fun getWoodcuttingDelay(): Int {
        var baseTime = rand(BASE_CUT_RATE / 2, BASE_CUT_RATE)
        baseTime -= (axe.strength - tree.resistance)
        if (mob.woodcutting.level > tree.level) {
            val treeLvlFactor = tree.level / 9
            val wcLvlFactor = mob.woodcutting.level / 8
            baseTime -= (wcLvlFactor - treeLvlFactor)
        }
        if (baseTime < 1) {
            baseTime = 1
        }
        if (baseTime > BASE_CUT_RATE) {
            baseTime = BASE_CUT_RATE
        }
        return baseTime
    }
}