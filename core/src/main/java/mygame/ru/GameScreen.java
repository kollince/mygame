package mygame.ru;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
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

    private Label countdownLabel;
    private TextButton startButton;

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
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = TextureManager.getInstance().getBackground();
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);

        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        countdownLabel = new Label("", labelStyle);
        countdownLabel.setFontScale(3);
        countdownLabel.setPosition(stage.getViewport().getWorldWidth() / 2, stage.getViewport().getWorldHeight() / 2, Align.center);
        stage.addActor(countdownLabel);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;

        startButton = new TextButton("Start", buttonStyle);
        startButton.getLabel().setFontScale(2);
        startButton.setColor(Color.GRAY);
        startButton.setPosition(stage.getViewport().getWorldWidth() / 2 - startButton.getWidth() / 2, stage.getViewport().getWorldHeight() / 4);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetData();
                GameData.setIsGameStarted(true);
                startButton.remove();
            }
        });
        stage.addActor(startButton);

        initInput();
    }
    private void initInput(){
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                Vector3 coords = new Vector3(screenX, screenY, 0);
                stage.getViewport().unproject(coords);
                GameData.setBootsX(coords.x - GameData.getBoots().getWidth()/2f);
                //executeGame.setBootsX(coords.x - TextureManager.getInstance().getBoots().getWidth() / 2f);
                //executeGame.setBootsY(coords.y - TextureManager.getInstance().getBoots().getHeight() / 2f);
                GameData.setBootsY(coords.y - TextureManager.getInstance().getBoots().getHeight() / 2f);
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        executeGame.execute(delta);
        stage.act(delta);
        stage.getViewport().apply();
        stage.draw();

        if (handleCountdown(delta)) return; // Обратный отсчет

        if (GameData.getIsGameTheEnd()) {
            handleGameEnd();
            return;
        }

        if (!GameData.getIsGameStarted()) {
            GameData.setIsGameStarted(true);// Игра началась
        }
        //checkGameTheEnd();
        renderGame();
    }

    private boolean handleCountdown(float delta) {
        if (GameData.getIsPause()) {
            countdownLabel.setText("Pause");
            renderGame();
            return true;
        }

        if (GameData.getCountdown() > 0) {
            GameData.setCountdown(GameData.getCountdown() - delta);
            countdownLabel.setText(String.valueOf((int) Math.ceil(GameData.getCountdown())));
            renderGame();
            return true;
        }

        countdownLabel.setText("");
        return false;
    }
    private void handleGameEnd() {
        if (!GameData.getIsGameTheEnd()) return;

        GameData.setBallX(10000);
        GameData.setBootsX(-2000);
        //executeGame.setBootsX(-2000);

        if (startButton.getStage() == null) {
            startButton.setPosition(
                (stage.getViewport().getWorldWidth() - startButton.getWidth()) / 2,
                (stage.getViewport().getWorldHeight() - startButton.getHeight()) / 2
            );
            stage.addActor(startButton);
        }
    }
    private void updateRoundButton(String text) {
        countdownLabel.setText(text);
        countdownLabel.setPosition(
            (stage.getViewport().getWorldWidth() - countdownLabel.getWidth()) / 2,
            (stage.getViewport().getWorldHeight() - countdownLabel.getHeight()) / 2
        );
        if (countdownLabel.getStage() == null) {
            stage.addActor(countdownLabel);
        }
    }
    private void renderGame(){
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.draw(new TextureRegion(GameData.getBall()),
            GameData.getBallX(), GameData.getBallY(),
            GameData.BALL_WIDTH / 2, GameData.getBallHeight() / 2,
            GameData.BALL_WIDTH, GameData.getBallHeight(),
            1, 1, GameData.getBallRotation());

        batch.draw(GameData.getBoots(), GameData.getBootsX(), GameData.getBootsY());
        batch.end();
    }
    private void resetData(){
        GameData.setIsGameTheEnd(false);
        GameData.setIsGameStarted(false);
        GameData.setCountdown(GameData.getDefaultCountdown());
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
        stage.clear();
        resetData();
    }

    @Override
    public void dispose() {
        stage.dispose(); // Освобождаем ресурсы
    }

}
