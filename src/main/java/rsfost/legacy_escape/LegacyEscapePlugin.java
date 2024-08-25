package rsfost.legacy_escape;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.event.KeyEvent;

@Slf4j
@PluginDescriptor(
	name = "Legacy Escape",
	description = "Restores the old functionality of closing interfaces with the escape button"
)
public class LegacyEscapePlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;

	@Inject
	private LegacyEscapeConfig config;

	@Inject
	private KeyManager keyManager;

	private volatile boolean interfaceOpen;

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(this);
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(this);
	}

	@Provides
	LegacyEscapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LegacyEscapeConfig.class);
	}

	@Override
	public void keyPressed(KeyEvent keyEvent)
	{
		handleKeyEvent(keyEvent);
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{

	}

	@Override
	public void keyTyped(KeyEvent keyEvent)
	{

	}

	public synchronized void handleKeyEvent(KeyEvent e)
	{
		if (e.getKeyCode() != KeyEvent.VK_ESCAPE)
		{
			return;
		}

		if (!interfaceOpen)
		{
			e.consume();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		checkWidget(event.getGroupId());
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		interfaceOpen = false;
	}

	private void checkWidget(int groupId)
	{
		final int resizableId = 10551312;
		final int fixedId = 35913768;

		Widget resizableContainer = client.getWidget(resizableId);
		Widget fixedContainer = client.getWidget(fixedId);

		Widget[] children = null;
		if (resizableContainer != null)
		{
			children = resizableContainer.getNestedChildren();
		}
		if (fixedContainer != null && (children == null || children.length == 0))
		{
			children = fixedContainer.getNestedChildren();
		}

		interfaceOpen = children != null && children.length > 0;
	}
}
