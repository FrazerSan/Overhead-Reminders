package com.shuddie.overhead;

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

    @Inject
    ReminderOverlay(Client client, NotificationStore notifications, OverheadNotificationsConfig config)
    {
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
        graphics.setFont(FontManager.getRunescapeBoldFont());
        FontMetrics metrics = graphics.getFontMetrics();
        Point anchor = player.getCanvasTextLocation(graphics, "", player.getLogicalHeight() + config.verticalOffset());
        if (anchor == null)
        {
            return null;
        }
        int row = 0;
        for (NotificationStore.Entry entry : notifications.snapshot())
        {
            String text = entry.displayText(notifications.now(), config.showCountdown());
            Point location = new Point(anchor.getX() - metrics.stringWidth(text) / 2,
                anchor.getY() - row++ * (metrics.getHeight() + 2));
            OverlayUtil.renderTextLocation(graphics, location, text, entry.color);
        }
        return null;
    }
}
