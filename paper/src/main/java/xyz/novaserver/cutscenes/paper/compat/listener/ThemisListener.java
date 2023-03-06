package xyz.novaserver.cutscenes.paper.compat.listener;

import com.gmail.olexorus.themis.api.ActionEvent;
import com.gmail.olexorus.themis.api.ViolationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.UUID;

public class ThemisListener implements CompatListener {
    private final UUID player;

    public ThemisListener(UUID player) {
        this.player = player;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onViolationEvent(ViolationEvent event) {
        if (event.getPlayer().getUniqueId() == player) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onActionEvent(ActionEvent event) {
        if (event.getPlayer().getUniqueId() == player) {
            event.setCancelled(true);
        }
    }

    @Override
    public void unregister() {
        ViolationEvent.getHandlerList().unregister(this);
        ActionEvent.getHandlerList().unregister(this);
    }
}
