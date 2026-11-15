package pl.awans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public class AwansPlugin extends JavaPlugin {

    // &#RRGGBB oraz &l, &r itd. jak w prefiksach LuckPerms
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    @Override
    public void onEnable() {
        LuckPerms lp = LuckPermsProvider.get();
        lp.getEventBus().subscribe(this, UserPromoteEvent.class, this::onPromote);
    }

    private void onPromote(UserPromoteEvent event) {
        String nick = event.getUser().getUsername();
        String groupName = event.getGroupTo().orElse(null);
        if (groupName == null) return;

        LuckPerms lp = LuckPermsProvider.get();
        Group group = lp.getGroupManager().getGroup(groupName);

        // Prefiks grupy (z kolorami); jak go nie ma, pokazujemy nazwe grupy
        String prefix = null;
        if (group != null) {
            prefix = group.getCachedData().getMetaData().getPrefix();
        }
        Component rank = (prefix == null || prefix.isBlank())
                ? Component.text(groupName, NamedTextColor.WHITE)
                : LEGACY.deserialize(prefix.trim());

        Component mainTitle = Component.text("AWANS", NamedTextColor.GOLD, TextDecoration.BOLD);
        Component subTitle = Component.text(String.valueOf(nick), NamedTextColor.YELLOW)
                .append(Component.text(" awansował na rangę ", NamedTextColor.GRAY))
                .append(rank);

        Title title = Title.title(mainTitle, subTitle,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4), Duration.ofMillis(800)));

        // Zdarzenie LuckPerms leci w watku asynchronicznym, a Bukkit chce glownego watku
        Bukkit.getScheduler().runTask(this, () -> {
            // Wszyscy gracze online. Zeby pokazac tylko awansowanemu:
            // Player p = Bukkit.getPlayer(event.getUser().getUniqueId()); if (p != null) p.showTitle(title);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(title);
            }
        });
    }
}
