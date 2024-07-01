package tech.zmario.everything.bukkit.minigames.adapters;

import org.bukkit.entity.Player;

import java.util.Set;

public interface PartyAdapter {

    boolean isPartyLeader(Player player);

    Set<Player> getPartyMembers(Player player);

    boolean isInParty(Player player);

}