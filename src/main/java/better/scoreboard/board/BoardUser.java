package better.scoreboard.board;

import better.scoreboard.manager.BoardManager;
import better.scoreboard.util.MessageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.entity.Player;
import sharkbyte.scoreboard.core.Scoreboard;

/**
 * This represents a player. It holds their scoreboard information and allows us to check what scoreboards they can use.
 *
 * @Author: am noah
 * @Since: 1.0.0
 * @Updated: 1.0.0
 */
public class BoardUser {

    private final Player player;
    private final Scoreboard scoreboard;

    private Board activeBoard;

    /**
     * Initialize the BoardUser object.
     */
    public BoardUser(Player player) {
        this.player = player;
        scoreboard = new Scoreboard(PacketEvents.getAPI().getPlayerManager().getUser(player), "BetterScoreboard");
        checkBoards();
    }

    /*
     * Functions.
     */

    /**
     * Check what boards the user is allowed to run, automatically switching to whichever has the highest weight.
     */
    public void checkBoards() {
        if (activeBoard != null && !activeBoard.canRun(player)) switchBoard(null);

        boolean switchedBoard = false;

        for (Board board : BoardManager.getBoards()) {
            if (!board.canRun(player)) continue;

            if (activeBoard == null) {
                switchBoard(board);
                switchedBoard = true;
                continue;
            }

            if (board.getWeight() <= activeBoard.getWeight()) continue;

            switchedBoard = true;
            switchBoard(board);
        }

        if (!switchedBoard) return;

        scoreboard.display();
        scoreboard.update();
    }

    /**
     * Switch the user's current active board to another board.
     */
    public void switchBoard(Board board) {
        if (board == null) {
            if (activeBoard == null) return;
            activeBoard = null;
            scoreboard.destroy();
            return;
        }

        if (activeBoard == null) {
            scoreboard.create();
        }

        activeBoard = board;
        scoreboard.setTitle(MessageUtil.modify(player, activeBoard.getTitle().getLine()));
        scoreboard.setShowNumbers(!activeBoard.shouldHideNumbers());
        // Set active lines.
        for (int i = 0; i < activeBoard.getLineCount(); i++) scoreboard.setLine(i, MessageUtil.modify(player, activeBoard.getLine(i).getLine()));
        // Remove unused lines.
        for (int i = activeBoard.getLineCount(); i < 15; i++) scoreboard.setLine(i, null);
    }

    /**
     * Handle a server tick, updating lines as necessary.
     */
    public void tick() {
        if (activeBoard == null) return;

        if (activeBoard.getTitle().isUpdateTick()) scoreboard.setTitle(MessageUtil.modify(player, activeBoard.getTitle().getLine()));
        for (int i = 0; i < activeBoard.getLineCount(); i++) {
            if (!activeBoard.getLine(i).isUpdateTick()) continue;
            scoreboard.setLine(i, MessageUtil.modify(player, activeBoard.getLine(i).getLine()));
        }
        scoreboard.update();
    }
}
