package pl.awans;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Na liscie serwerow w miejscu "2/100" pokazuje np. "2 ɢʀᴀᴄᴢʏ".
 * Dziala tak, ze podmieniamy tekst wersji i ustawiamy niezgodny numer protokolu,
 * wtedy klient pokazuje ten tekst zamiast liczby graczy.
 */
public class ListaListener implements Listener {

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        int n = event.getNumPlayers();
        event.setVersion("§7" + n + " " + forma(n));
        event.setProtocolVersion(-1);
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
