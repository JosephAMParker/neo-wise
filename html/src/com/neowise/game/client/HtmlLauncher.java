package com.neowise.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.neowise.game.main.NeoWiseGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () { 
                // Fixed size application:
                return new GwtApplicationConfiguration(500, 700);
                //return new GwtApplicationConfiguration();
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new NeoWiseGame();
        }
}