package com.neowise.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.neowise.game.main.ScorchedEarthGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Neo Wise";

		config.width = 500;
		config.height = 700;
		config.resizable = false;
		config.backgroundFPS = 30;
		config.foregroundFPS = 30;
		new LwjglApplication(new ScorchedEarthGame(), config);
	}
}
