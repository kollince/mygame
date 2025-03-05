package mygame.ru;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class MyGame extends Game {
    private SpriteBatch batch;
    private Stage stage;
    private Texture image;

    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage();
        GameScreen gameScreen = new GameScreen(this, stage);
        this.setScreen(gameScreen);

    }

    @Override
    public void dispose() {
        batch.dispose();
        //image.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
