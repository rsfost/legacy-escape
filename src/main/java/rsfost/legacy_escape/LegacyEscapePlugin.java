package rsfost.legacy_escape;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
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
	public void keyPressed(KeyEvent keyEvent)
	{
		if (keyEvent.getKeyCode() != KeyEvent.VK_ESCAPE)
		{
			return;
		}

		if (!interfaceOpen)
		{
			keyEvent.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{

	}

	@Override
	public void keyTyped(KeyEvent keyEvent)
	{

	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (interfaceOpen)
		{
			return;
		}

		if (inClosableGroup(event.getGroupId()))
		{
			interfaceOpen = true;
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (!interfaceOpen)
		{
			return;
		}

		if (inClosableGroup(event.getGroupId()))
		{
			interfaceOpen = false;
		}
	}

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

	private boolean inClosableGroup(int groupId)
	{
		Widget widget = client.getWidget(groupId, 0);
		for (int i = 0; i < 2 && widget != null; ++i)
		{
			widget = widget.getParent();
		}
		if (widget == null)
		{
			return false;
		}

		return widget.getId() == ComponentID.RESIZABLE_VIEWPORT_RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX;
	}
}
