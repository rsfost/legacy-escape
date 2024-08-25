/*
 * Copyright (c) 2024, rsfost <https://github.com/rsfost>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
