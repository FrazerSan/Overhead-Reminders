package com.frazersan.overheadreminders;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ReminderOverlay extends Overlay
{
    private final Client client;
    private final NotificationStore notifications;
    private final OverheadNotificationsConfig config;

    private final ReminderPreview preview;
    private final InventorySupplyChecker supplies;

    ReminderOverlay(Client client, NotificationStore notifications, OverheadNotificationsConfig config)
    {
        this(client, notifications, config, new ReminderPreview(), new InventorySupplyChecker(client));
    }

    ReminderOverlay(Client client, NotificationStore notifications, OverheadNotificationsConfig config,
        ReminderPreview preview, InventorySupplyChecker supplies)
    {
        this(client, notifications, config, preview, supplies, null);
    }

    @Inject
    ReminderOverlay(Client client, NotificationStore notifications, OverheadNotificationsConfig config,
        ReminderPreview preview, InventorySupplyChecker supplies, OverheadNotificationsPlugin plugin)
    {
        super(plugin);
        this.preview = preview;
        this.supplies = supplies;
        this.client = client;
        this.notifications = notifications;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Player player = client.getLocalPlayer();
        if (client.getGameState() != GameState.LOGGED_IN || player == null)
        {
            return null;
        }
        java.awt.Font font = FontManager.getRunescapeBoldFont();
        graphics.setFont(config.textSize() == 0 ? font : font.deriveFont((float) Math.max(1, config.textSize())));
        FontMetrics metrics = graphics.getFontMetrics();
        Point anchor = player.getCanvasTextLocation(graphics, "", player.getLogicalHeight() + config.verticalOffset());
        if (anchor == null)
        {
            return null;
        }
        int row = 0;
        boolean showingPreview = preview.active();
        for (NotificationStore.Entry entry : showingPreview ? preview.snapshot() : notifications.snapshot())
        {
            String message = entry.message;
            if (!showingPreview && config.showOutOfSupplies() && !supplies.visible(entry.key))
            {
                message = supplies.outOfMessage(entry.key);
            }
            String text = entry.displayText(notifications.now(), config.showCountdown(), message);
            Point location = new Point(anchor.getX() + config.horizontalOffset() - metrics.stringWidth(text) / 2,
                anchor.getY() - row++ * (metrics.getHeight() + config.messageSpacing()));
            OverlayUtil.renderTextLocation(graphics, location, text, entry.color);
        }
        return null;
    }
}
