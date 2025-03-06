package mygame.ru;

import com.badlogic.gdx.graphics.Texture;

public class TextureManager {
    private static TextureManager instance;  // Единственный экземпляр класса
    private final Texture ball;
    private final Texture background;
    private final Texture boots;

    private TextureManager() {
        this.ball = new Texture("ball.png");
        this.background = new Texture("stadium.png");
        this.boots = new Texture("boots.png");
    }
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }

    public Texture getBall() {
        return ball;
    }

    public Texture getBackground() {
        return background;
    }

    public Texture getBoots() {
        return boots;
    }

    public void dispose() {
        ball.dispose();
        background.dispose();
        boots.dispose();
    }
}

