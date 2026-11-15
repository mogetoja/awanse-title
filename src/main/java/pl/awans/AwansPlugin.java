package pl.awans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.track.UserDemoteEvent;
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
        lp.getEventBus().subscribe(this, UserDemoteEvent.class, this::onDemote);

        getServer().getPluginManager().registerEvents(new ListaListener(), this);

        AwansujCommand awansuj = new AwansujCommand("promote", "awans");
        getCommand("awansuj").setExecutor(awansuj);
        getCommand("awansuj").setTabCompleter(awansuj);

        AwansujCommand degraduj = new AwansujCommand("demote", "degradację");
        getCommand("degraduj").setExecutor(degraduj);
        getCommand("degraduj").setTabCompleter(degraduj);
    }

    private void onPromote(UserPromoteEvent event) {
        String groupName = event.getGroupTo().orElse(null);
        if (groupName == null) return;

        Component mainTitle = Component.text("AWANS", NamedTextColor.GREEN, TextDecoration.BOLD);
        Component subTitle = Component.text(String.valueOf(event.getUser().getUsername()), NamedTextColor.YELLOW)
                .append(Component.text(" awansował na rangę ", NamedTextColor.GRAY))
                .append(rankComponent(groupName));
        show(mainTitle, subTitle);
    }

    private void onDemote(UserDemoteEvent event) {
        String nick = String.valueOf(event.getUser().getUsername());
        String groupTo = event.getGroupTo().orElse(null);

        Component mainTitle = Component.text("DEGRAD", NamedTextColor.RED, TextDecoration.BOLD);
        Component subTitle;
        if (groupTo != null) {
            subTitle = Component.text(nick, NamedTextColor.YELLOW)
                    .append(Component.text(" został zdegradowany na rangę ", NamedTextColor.GRAY))
                    .append(rankComponent(groupTo));
        } else {
            // Zdegradowany z najnizszej rangi ze sciezki, czyli nie ma juz zadnej
            String groupFrom = event.getGroupFrom().orElse(null);
            if (groupFrom == null) return;
            subTitle = Component.text(nick, NamedTextColor.YELLOW)
                    .append(Component.text(" został zdegradowany i stracił rangę ", NamedTextColor.GRAY))
                    .append(rankComponent(groupFrom));
        }
        show(mainTitle, subTitle);
    }

    // Prefiks grupy (z kolorami); jak go nie ma, pokazujemy nazwe grupy
    private Component rankComponent(String groupName) {
        Group group = LuckPermsProvider.get().getGroupManager().getGroup(groupName);
        String prefix = null;
        if (group != null) {
            prefix = group.getCachedData().getMetaData().getPrefix();
        }
        return (prefix == null || prefix.isBlank())
                ? Component.text(groupName, NamedTextColor.WHITE)
                : LEGACY.deserialize(prefix.trim());
    }

    private void show(Component mainTitle, Component subTitle) {
        Title title = Title.title(mainTitle, subTitle,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4), Duration.ofMillis(800)));

        // Zdarzenie LuckPerms leci w watku asynchronicznym, a Bukkit chce glownego watku
        Bukkit.getScheduler().runTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(title);
            }
        });
    }
}
