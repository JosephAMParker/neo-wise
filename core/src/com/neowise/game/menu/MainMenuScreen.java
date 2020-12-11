package com.neowise.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.neowise.game.main.ScorchedEarthGame;


public class MainMenuScreen implements Screen {

    final ScorchedEarthGame game;

    Stage stage;
    //	OrthographicCamera camera;
    Skin skin;
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();
    SpriteBatch batch;

    public MainMenuScreen(final ScorchedEarthGame game) {
        this.game = game;

//		camera = new OrthographicCamera();
//		camera.setToOrtho(false, 480, 800);

        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        Pixmap pixmap = new Pixmap(100,100, Pixmap.Format.RGB888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        BitmapFont bfont = new BitmapFont();
        skin.add("default",bfont);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white",Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);
        float width = w*0.7f;
        float height = h*0.1f;

        final TextButton newGameButton=new TextButton("New Game", textButtonStyle);
        newGameButton.setPosition(w / 2 - width / 2, h * 0.6f);
        newGameButton.setSize(width, height);

        final TextButton optionsButton = new TextButton("Options", textButtonStyle);
        optionsButton.setPosition(w / 2 - width / 2, h * 0.45f);
        optionsButton.setSize(width, height);

        stage.addActor(newGameButton);
        stage.addActor(optionsButton);
//		stage.addActor(textButton);

        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                newGameButton.setText("Starting new game");
                game.createNewHomeBase();
                game.createNewPlayer();
                game.createNewStarMap();
                game.setScreen(game.getStarMap());
            }
        });

        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                newGameButton.setText("Starting new game");
                game.setScreen(new OptionsScreen(game));
            }
        });

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

    		/*
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.font.draw(game.batch, "!!! ", w/2, h/2);
		game.font.draw(game.batch, "Tap anywhere to begin!", w/2 - 60, h/2 + 200);
		game.batch.end();

		if (Gdx.input.isTouched()) {
			game.setScreen(new GameScreen(game));
			dispose();
		}
		*/
    }



    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        stage.dispose();
        skin.dispose();

    }

}
