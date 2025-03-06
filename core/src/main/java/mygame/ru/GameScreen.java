package mygame.ru;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    private Stage stage;
    private final SpriteBatch batch;
    private FitViewport viewport;
    private Label countdownLabel;
    private TextButton startButton;
    private final ExecuteGame executeGame;

    public GameScreen(Game game, Stage stage) {
        this.stage = stage;
        this.batch = ((MyGame) game).getBatch();
        this.executeGame = new ExecuteGame(stage);
    }
    @Override
    public void show() {
        setupCameraAndStage();
        setupBackground();
        setupCountdownLabel();
        setupStartButton();
        initInput();
    }
    private void setupCameraAndStage() {
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }
    private void setupBackground() {
        Texture backgroundTexture = TextureManager.getInstance().getBackground();
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
    }
    private void setupCountdownLabel() {
        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        countdownLabel = new Label("", labelStyle);
        countdownLabel.setFontScale(3);
        countdownLabel.setPosition(stage.getViewport().getWorldWidth() / 2, stage.getViewport().getWorldHeight() / 2, Align.center);
        stage.addActor(countdownLabel);
    }
    private void setupStartButton() {
        BitmapFont font = new BitmapFont();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = createButtonBackground();
        startButton = new TextButton("Start", buttonStyle);
        startButton.getLabel().setFontScale(2);
        startButton.setColor(Color.CHARTREUSE);
        startButton.setSize(200, 80);
        startButton.setPosition(stage.getViewport().getWorldWidth() / 2 - startButton.getWidth() / 2, stage.getViewport().getWorldHeight() / 4);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetData();
                GameData.setIsGameStarted(true);
                startButton.remove();
            }
        });
        if (GameData.getIsPause()) {
            stage.addActor(startButton);
        }
    }
    private Drawable createButtonBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        pixmap.dispose();
        return drawable;
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
                GameData.setBootsY(coords.y - TextureManager.getInstance().getBoots().getHeight() / 2f);
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        executeGame.execute();
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
        if (startButton.getStage() == null) {
            startButton.setPosition(
                (stage.getViewport().getWorldWidth() - startButton.getWidth()) / 2,
                (stage.getViewport().getWorldHeight() - startButton.getHeight()) / 2
            );
            stage.addActor(startButton);
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
        stage.dispose();
        TextureManager.getInstance().dispose();
    }
}
