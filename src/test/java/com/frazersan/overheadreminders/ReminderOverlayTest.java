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
        store.show("Low Prayer", "LOW PRAYER", Color.RED, 5);
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

    @Test
    public void horizontalOffsetShiftsTheSameDefaultFontWithoutChangingSpacing()
    {
        Client client = mock(Client.class);
        Player player = mock(Player.class);
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        when(client.getLocalPlayer()).thenReturn(player);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(player.getCanvasTextLocation(any(Graphics2D.class), anyString(), anyInt()))
            .thenReturn(new Point(150, 150));
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("Low Prayer", "Low Prayer", Color.RED, 5);
        ReminderOverlay overlay = new ReminderOverlay(client, store, config);
        BufferedImage original = new BufferedImage(350, 200, BufferedImage.TYPE_INT_ARGB);
        BufferedImage shifted = new BufferedImage(350, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D first = original.createGraphics();
        Graphics2D second = shifted.createGraphics();
        try
        {
            overlay.render(first);
            assertEquals(net.runelite.client.ui.FontManager.getRunescapeBoldFont(), first.getFont());
            assertEquals(2, config.messageSpacing());
            assertEquals(40, config.verticalOffset());
            when(config.horizontalOffset()).thenReturn(25);
            overlay.render(second);
            for (int y = 0; y < 200; y++)
            {
                for (int x = 0; x < 325; x++)
                {
                    assertEquals(original.getRGB(x, y), shifted.getRGB(x + 25, y));
                }
            }
        }
        finally { first.dispose(); second.dispose(); }
    }

    @Test
    public void customTextSizeSpacingAndHeightApplyToSeparateRows()
    {
        Client client = mock(Client.class);
        Player player = mock(Player.class);
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        when(client.getLocalPlayer()).thenReturn(player);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(config.textSize()).thenReturn(24);
        when(config.messageSpacing()).thenReturn(10);
        when(config.verticalOffset()).thenReturn(70);
        when(player.getLogicalHeight()).thenReturn(100);
        when(player.getCanvasTextLocation(any(Graphics2D.class), anyString(), anyInt()))
            .thenReturn(new Point(180, 150));
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("Low Prayer", "Low Prayer", Color.RED, 5);
        store.show("Thrall", "Cast Thrall", Color.CYAN, 5);
        BufferedImage image = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try
        {
            new ReminderOverlay(client, store, config).render(graphics);
            assertEquals(24, graphics.getFont().getSize());
            int lineHeight = graphics.getFontMetrics().getHeight() + 10;
            assertTrue(hasColor(image, Color.RED, 150 - lineHeight + 10, 151));
            assertTrue(hasColor(image, Color.CYAN, 150 - 2 * lineHeight + 10, 150 - lineHeight + 1));
            verify(player).getCanvasTextLocation(any(Graphics2D.class), eq(""), eq(170));
        }
        finally { graphics.dispose(); }
    }

    @Test
    public void previewBypassesInventoryAndHidesRealRowsWithoutDeletingThem()
    {
        Client client = mock(Client.class);
        Player player = mock(Player.class);
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        when(client.getLocalPlayer()).thenReturn(player);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(player.getCanvasTextLocation(any(Graphics2D.class), anyString(), anyInt()))
            .thenReturn(new Point(250, 350));
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("Low Prayer", "Actual alert", Color.WHITE, 0);
        ReminderPreview preview = new ReminderPreview();
        InventorySupplyChecker supplies = new InventorySupplyChecker(client);
        ReminderOverlay overlay = new ReminderOverlay(client, store, config, preview, supplies);
        preview.start(config);
        BufferedImage image = new BufferedImage(500, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try
        {
            overlay.render(graphics);
            assertTrue(hasColor(image, Color.GREEN, 0, 400));
            assertFalse(hasColor(image, Color.WHITE, 0, 400));
            assertEquals(1, store.snapshot().size());
            preview.stop();
            overlay.render(graphics);
            assertTrue(hasColor(image, Color.WHITE, 0, 400));
        }
        finally { graphics.dispose(); }
    }

    @Test
    public void inventoryModeReplacesPromptWithOutOfTextAndRestockingRestoresOriginalRow()
    {
        Client client = mock(Client.class);
        Player player = mock(Player.class);
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        when(client.getLocalPlayer()).thenReturn(player);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(player.getCanvasTextLocation(any(Graphics2D.class), anyString(), anyInt()))
            .thenReturn(new Point(250, 150));
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("Liquid Adrenaline", "Drink", Color.YELLOW, 0);
        InventorySupplyChecker supplies = new InventorySupplyChecker(client);
        ReminderOverlay overlay = new ReminderOverlay(client, store, config, new ReminderPreview(), supplies);
        assertTrue(yellowTextWidth(overlay) > 70); // Full Out of Liquid Adrenaline fallback.
        when(config.showOutOfSupplies()).thenReturn(false);
        int normal = yellowTextWidth(overlay);
        assertTrue(normal > 0 && normal < 50); // One switch off restores the saved Drink prompt.
        net.runelite.api.ItemComposition dose = mock(net.runelite.api.ItemComposition.class);
        when(dose.getName()).thenReturn("Liquid adrenaline(2)");
        when(dose.getNote()).thenReturn(-1);
        when(dose.getInventoryActions()).thenReturn(new String[] {"Drink"});
        when(client.getItemDefinition(500001)).thenReturn(dose);
        net.runelite.api.ItemContainer inventory = mock(net.runelite.api.ItemContainer.class);
        when(inventory.getItems()).thenReturn(new net.runelite.api.Item[] {new net.runelite.api.Item(500001, 1)});
        when(client.getItemContainer(net.runelite.api.gameval.InventoryID.INV)).thenReturn(inventory);
        supplies.update();
        int restored = yellowTextWidth(overlay);
        assertTrue(restored > 0 && restored < 50); // The saved short Drink prompt is back.
        assertEquals("Drink", store.snapshot().get(0).message);
        assertEquals(Long.MAX_VALUE, store.snapshot().get(0).expiresAt);
    }

    private int yellowTextWidth(ReminderOverlay overlay)
    {
        BufferedImage image = new BufferedImage(500, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try { overlay.render(graphics); }
        finally { graphics.dispose(); }
        int left = 500, right = -1;
        for (int y = 0; y < 200; y++) for (int x = 0; x < 500; x++)
        {
            if (image.getRGB(x, y) == Color.YELLOW.getRGB())
            {
                left = Math.min(left, x);
                right = Math.max(right, x);
            }
        }
        return right < left ? 0 : right - left + 1;
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
