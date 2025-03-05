package mygame.ru;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    private Game game;
    private Stage stage; // Используем переданный Stage
    private SpriteBatch batch; // Используем переданный SpriteBatch
    private OrthographicCamera camera;
    private FitViewport viewport;

    private Image backgroundImage;
    private Texture backgroundTexture;

    private ExecuteGame executeGame;

    public GameScreen(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
        this.batch = ((MyGame) game).getBatch();
        this.executeGame = new ExecuteGame(stage);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = TextureManager.getInstance().getBackground();
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем и отрисовываем объекты игры
        executeGame.execute(delta);

        // Устанавливаем матрицу проекции для рендеринга
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        // Получаем и рисуем мяч
        Texture ballTexture = executeGame.getBall();  // Получаем текстуру мяча
        TextureRegion ballRegion = new TextureRegion(ballTexture);  // Оборачиваем текстуру в TextureRegion

        batch.draw(ballRegion,
            executeGame.getBallX(), executeGame.getBallY(),
            executeGame.getBALL_WIDTH() / 2, executeGame.getBallHeight() / 2,
            executeGame.getBALL_WIDTH(), executeGame.getBallHeight(),
            1, 1, executeGame.getBallRotation());

        // Получаем и рисуем бутсы
        batch.draw(executeGame.getBoots(), executeGame.getBootsX(), executeGame.getBootsY());

        // Закрываем отрисовку
        batch.end();

        // Обновляем и отрисовываем сцену с интерфейсом
        stage.act(delta);
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose(); // Освобождаем ресурсы
    }

}
