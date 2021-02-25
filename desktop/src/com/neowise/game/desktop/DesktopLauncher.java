package com.neowise.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.neowise.game.main.NeoWiseGame;
import com.neowise.game.util.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Neo Wise";

		config.width = Constants.w;
		config.height = Constants.h;
		config.resizable = false;
		//config.backgroundFPS = 30;
		//config.foregroundFPS = 30;
		config.forceExit = false;
		new LwjglApplication(new NeoWiseGame(), config);
	}
}
