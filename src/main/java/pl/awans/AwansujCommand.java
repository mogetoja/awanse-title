package pl.awans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.track.Track;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** /awansuj i /degraduj <nick> <sciezka> - to samo co /lp user <nick> promote|demote <sciezka> */
public class AwansujCommand implements CommandExecutor, TabCompleter {

    private final String lpVerb;   // "promote" albo "demote"
    private final String doneText; // "awans" albo "degradacja"

    public AwansujCommand(String lpVerb, String doneText) {
        this.lpVerb = lpVerb;
        this.doneText = doneText;
    }

    private static final Pattern NICK = Pattern.compile("[A-Za-z0-9_.]{1,16}");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Component.text("Użycie: /" + label + " <nick> <ścieżka>", NamedTextColor.RED));
            return true;
        }
        String nick = args[0];
        String trackName = args[1].toLowerCase();

        if (!NICK.matcher(nick).matches()) {
            sender.sendMessage(Component.text("Niepoprawny nick.", NamedTextColor.RED));
            return true;
        }
        Track track = LuckPermsProvider.get().getTrackManager().getTrack(trackName);
        if (track == null) {
            sender.sendMessage(Component.text("Nie ma takiej ścieżki: " + trackName
                    + ". Dostępne: " + String.join(", ", trackNames()), NamedTextColor.RED));
            return true;
        }

        // Przechodzi przez normalna komende LuckPerms, wiec tytul "AWANS" odpala sie tak samo
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + nick + " " + lpVerb + " " + track.getName());
        sender.sendMessage(Component.text("Wysłano " + doneText + ": " + nick + " (ścieżka " + track.getName()
                + "). Jeśli się nie udał, powód jest w konsoli serwera.", NamedTextColor.GRAY));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) out.add(p.getName());
            }
        } else if (args.length == 2) {
            for (String t : trackNames()) {
                if (t.startsWith(args[1].toLowerCase())) out.add(t);
            }
        }
        return out;
    }

    private List<String> trackNames() {
        LuckPerms lp = LuckPermsProvider.get();
        List<String> names = new ArrayList<>();
        for (Track t : lp.getTrackManager().getLoadedTracks()) names.add(t.getName());
        return names;
    }
}
