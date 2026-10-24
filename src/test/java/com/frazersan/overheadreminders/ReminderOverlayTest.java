package com.frazersan.overheadreminders;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Point;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReminderOverlayTest
{
    @Test
    public void simultaneousMessagesRenderOnSeparateRowsWithoutChangingPlayerText()
    {
        Client client = mock(Client.class);
        Player player = mock(Player.class);
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        when(client.getLocalPlayer()).thenReturn(player);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(player.getCanvasTextLocation(any(Graphics2D.class), anyString(), anyInt()))
            .thenReturn(new Point(150, 150));
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("Prayer", "LOW PRAYER", Color.RED, 5);
        store.show("Thrall", "THRALL EXPIRED", Color.CYAN, 5);
        BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try
        {
            new ReminderOverlay(client, store, config).render(graphics);
            int lineHeight = graphics.getFontMetrics().getHeight() + 2;
            assertTrue(hasColor(image, Color.RED, 150 - lineHeight + 2, 151));
            assertTrue(hasColor(image, Color.CYAN, 150 - 2 * lineHeight + 2, 150 - lineHeight + 1));
            verify(player, never()).setOverheadText(any());
            verify(player, never()).setOverheadCycle(anyInt());
        }
        finally
        {
            graphics.dispose();
        }
    }

    private static boolean hasColor(BufferedImage image, Color color, int top, int bottom)
    {
        for (int y = top; y < bottom; y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                if (image.getRGB(x, y) == color.getRGB())
                {
                    return true;
                }
            }
        }
        return false;
    }
}
