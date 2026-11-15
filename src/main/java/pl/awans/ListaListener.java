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
        event.setVersion("§7" + event.getNumPlayers() + " ɢʀᴀᴄᴢʏ");
        event.setProtocolVersion(-1);
    }
}
