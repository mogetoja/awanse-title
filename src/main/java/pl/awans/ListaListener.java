package pl.awans;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Na liscie serwerow w miejscu "2/100" pokazuje np. "2 ɢʀᴀᴄᴢʏ".
 * Dziala tak, ze podmieniamy tekst wersji i ustawiamy niezgodny numer protokolu,
 * wtedy klient pokazuje ten tekst zamiast liczby graczy.
 */
public class ListaListener implements Listener {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private final JavaPlugin plugin;

    public ListaListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        int n = event.getNumPlayers();
        event.setVersion("§7" + n + " " + forma(n));
        event.setProtocolVersion(-1);

        // Napis pod nazwa serwera (dwie linie z config.yml)
        String l1 = plugin.getConfig().getString("motd.linia1", "");
        String l2 = plugin.getConfig().getString("motd.linia2", "");
        event.motd(LEGACY.deserialize(l1).append(Component.newline()).append(LEGACY.deserialize(l2)));
    }

    // 1 gracz, 2-4 gracze, reszta graczy (z wyjatkiem 12-14, 112-114 itd.)
    private static String forma(int n) {
        if (n == 1) return "ɢʀᴀᴄᴢ";
        int last = n % 10;
        int last2 = n % 100;
        if (last >= 2 && last <= 4 && !(last2 >= 12 && last2 <= 14)) return "ɢʀᴀᴄᴢᴇ";
        return "ɢʀᴀᴄᴢʏ";
    }
}
