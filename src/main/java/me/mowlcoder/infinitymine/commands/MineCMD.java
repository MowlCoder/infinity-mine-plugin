package me.mowlcoder.infinitymine.commands;

import me.mowlcoder.infinitymine.InfinityMine;
import me.mowlcoder.infinitymine.mine.Mine;
import me.mowlcoder.infinitymine.mine.MineManager;
import me.mowlcoder.infinitymine.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MineCMD implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    InfinityMine.getInstance().getConfig().getString("messages.commandUseOnlyForPlayers")
            );
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        String mineAction = args[0];

        if (!player.isOp() && !mineAction.equals("tp")) {
            ChatUtil.sendMessage(
                    player,
                    InfinityMine.getInstance().getConfig()
                            .getString("messages.notEnoughRights")
            );
            return true;
        }


        switch (mineAction) {
            case "create" -> {
                if (args.length != 5) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];

                if (MineManager.getInstance().getMineById(mineId) != null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdAlreadyExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                int mineWidth = Integer.parseInt(args[2]);
                int mineHeight = Integer.parseInt(args[3]);
                int mineDepth = Integer.parseInt(args[4]);

                Mine mine = new Mine(mineId, mineWidth, mineHeight, mineDepth, player.getLocation().add(0, -1, 0));
                mine.generateMine();
                MineManager.getInstance().addMine(mine);

                ChatUtil.sendMessage(player, "Шахта успешно сгенерирована");
            }
            case "open" -> {
                if (args.length != 2) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                mine.openMine();
            }
            case "close" -> {
                if (args.length != 2) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                mine.closeMine();
            }
            case "set-name" -> {
                if (args.length != 3) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                mine.setName(args[2]);
                ChatUtil.sendMessage(
                        player,
                        InfinityMine.getInstance().getConfig().getString("messages.mineSuccessRename")
                );
                return true;
            }
            case "delete" -> {
                if (args.length != 2) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                mine.prepareForDelete();
                MineManager.getInstance().deleteMine(mine);
                ChatUtil.sendMessage(
                        player,
                        InfinityMine.getInstance().getConfig().getString("messages.mineSuccessDelete")
                );
                return true;
            }
            case "set-delay" -> {
                if (args.length != 3) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                mine.setMineResetDelay(Long.parseLong(args[2]));
                ChatUtil.sendMessage(
                        player,
                        InfinityMine.getInstance().getConfig().getString("messages.mineSuccessSetInterval")
                );
                return true;
            }
            case "set-spawn" -> {
                if (args.length != 2) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                mine.setPlayerSpawnLocation(player.getLocation());
                ChatUtil.sendMessage(
                        player,
                        InfinityMine.getInstance().getConfig().getString("messages.mineSuccessSetSpawn")
                );
                return true;
            }
            case "generate" -> {
                if (args.length != 2) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                mine.generateMine();
                ChatUtil.sendMessage(
                        player,
                        InfinityMine.getInstance().getConfig().getString("messages.mineSuccessGenerate")
                );
            }
            case "tp" -> {
                if (args.length != 2) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig().getString("messages.notEnoughArgs")
                    );
                    return true;
                }

                String mineId = args[1];
                Mine mine = MineManager.getInstance().getMineById(mineId);

                if (mine == null) {
                    ChatUtil.sendMessage(
                            player,
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineWithIdNotExist")
                                    .replace("%mine_id%", mineId)
                    );
                    return true;
                }

                player.teleport(mine.getPlayerSpawnLocation());
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                return Arrays.asList(
                        "create",
                        "open",
                        "close",
                        "set-name",
                        "set-delay",
                        "set-spawn",
                        "delete",
                        "generate",
                        "tp"
                );
            }

            return List.of(
                    "tp"
            );
        } else if (args.length == 2) {
            List<String> mineIds = MineManager.getInstance().getAllMineIds();

            if (!args[0].equals("create")) {
                return mineIds;
            }

        }

        return null;
    }
}
