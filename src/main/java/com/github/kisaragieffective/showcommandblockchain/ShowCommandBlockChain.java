package com.github.kisaragieffective.showcommandblockchain;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.management.PlatformLoggingMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ShowCommandBlockChain extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Please execute this command from player.");
            return true;
        }

        if (!command.getName().toLowerCase().equals("checkchain")) {
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage("please enter [x] [y] [z]");
            return true;
        }

        final Player me = (Player) sender;
        final Location start = parseIntOrRelative(args, me.getLocation());
        if (!isCommandBlock(start.getBlock().getType())) {
            sender.sendMessage("" + start + " is not CommandBlock!");
        }

        final Block target = start.getBlock();
        sender.sendMessage("Starting analysing chain...");
        final Set<Block> set = new HashSet<>();
        while (isCommandBlock(target.getType())) {
            final int meta = target.getData();
            final int face = meta & 7;
            final boolean isConditional = (meta & 8) == 8;
            final ChatColor cc;
            switch (target.getType()) {
                case COMMAND_BLOCK:
                    cc = ChatColor.GOLD;
                    break;
                case CHAIN_COMMAND_BLOCK:
                    cc = ChatColor.GREEN;
                    break;
                case REPEATING_COMMAND_BLOCK:
                    cc = ChatColor.DARK_AQUA;
                    break;
                default:
                    throw new AssertionError("Must not be reached");
            }
            final StringBuilder sb = new StringBuilder(100);
            sb
                    .append(cc)
                    .append('(')
                    .append(target.getX())
                    .append(',')
                    .append(target.getY())
                    .append(',')
                    .append(target.getZ())
                    .append(") [")
                    .append("UDNSWE!!".charAt(face))
                    .append(']');
            if (isConditional) {
                sb.append(" (C)");
            }

            sender.sendMessage(sb.toString());
            if (set.contains(target)) {
                break;
            } else {
                set.add(target);
            }
        }

        final int size = set.size();
        sender.sendMessage("" + size + " block(s) were checked.");
        int gm = me.getWorld().getGameRuleValue(GameRule.MAX_COMMAND_CHAIN_LENGTH);
        if (size > gm) {
            sender.sendMessage("" + ChatColor.YELLOW + "Warning! " + size + " (actual) > " + gm + "(GameRule) !");
        }
        return true;
    }

    private static boolean isCommandBlock(Material type) {
        switch (type) {
            case COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case REPEATING_COMMAND_BLOCK:
                return true;
            default:
                return false;
        }
    }

    private final Location parseIntOrRelative(String[] v, Location base) {
        int x;
        try {
            x = Integer.parseInt(v[0]);
        } catch (NumberFormatException e) {
            // can't be parsed
            try {
                final int offset = getOffset(v[0]);
                x = base.getBlockX() + offset;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        int y;
        try {
            y = Integer.parseInt(v[0]);
        } catch (NumberFormatException e) {
            // can't be parsed
            try {
                final int offset = getOffset(v[0]);
                y = base.getBlockX() + offset;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        int z;
        try {
            z = Integer.parseInt(v[0]);
        } catch (NumberFormatException e) {
            // can't be parsed
            try {
                final int offset = getOffset(v[0]);
                z = base.getBlockX() + offset;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        return base.clone().add(x, y, z);
    }

    private static int getOffset(String relative) {
        Pattern p = Pattern.compile("^~(-?\\d+)?$");
        Matcher m = p.matcher(relative);
        if (!m.matches()) throw new AssertionError("match failed");
        String ofs = m.toMatchResult().group(1);
        return ofs.isEmpty() ? 0 : Integer.parseInt(ofs);
    }
}
