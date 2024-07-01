package tech.zmario.everything.bukkit.minigames.adapters.impl;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import org.bukkit.entity.Player;
import tech.zmario.everything.bukkit.minigames.adapters.PartyAdapter;
import tech.zmario.everything.bukkit.utils.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PartiesAdapter implements PartyAdapter {

    private final PartiesAPI parties = Parties.getApi();

    @Override
    public boolean isPartyLeader(Player player) {
        Party party = parties.getPartyOfPlayer(player.getUniqueId());

        if (party != null && party.getLeader() != null) {
            return party.getLeader().equals(player.getUniqueId());
        }

        return false;
    }

    @Override
    public Set<Player> getPartyMembers(Player player) {
        Party party = parties.getPartyOfPlayer(player.getUniqueId());

        return party != null ?
                party.getMembers().stream().map(Utils.SERVER::getPlayer).collect(Collectors.toSet()) :
                new HashSet<>();
    }

    @Override
    public boolean isInParty(Player player) {
        return parties.isPlayerInParty(player.getUniqueId());
    }
}
