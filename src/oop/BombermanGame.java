package oop;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;

import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
//import java.awt.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import oop.entities.character.Bomber;
import oop.entities.Entity;
import oop.entities.character.bomb.Bomb;
import oop.entities.character.bomb.Flame;
import oop.entities.character.enemy.Enemy;
import oop.entities.stillobjects.item.BombItem;
import oop.entities.stillobjects.item.FlameItem;
import oop.entities.stillobjects.item.Item;
import oop.entities.stillobjects.item.SpeedItem;
import oop.entities.stillobjects.mapblock.Portal;
import oop.graphics.CreateMap;
// import oop.entities.Grass;
// import oop.entities.Wall;
import oop.graphics.Sprite;
import oop.sound.Sound;
// import oop.graphics.CreateMap;
import oop.menucontrol.Menu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static oop.sound.Sound.bgMusicIsPlaying;
import static oop.sound.Sound.menuMusicIsPlaying;

public class BombermanGame extends Application {

    public static int WIDTH = 31;
    public static int HEIGHT = 21;
    public static int HEIGHT_MENU = Sprite.SCALED_SIZE;

    public static int w = 15;
    public static int h = 10;

    public static int chooseScene = -1;

    private GraphicsContext gc;
    public static Canvas canvas;
    // private final List<Entity> entities = new ArrayList<>();
    public static final List<Enemy> enemy = new ArrayList<>();// list enemy
    public static final List<Entity> stillObjects = new ArrayList<>();// thuc the trong game
    public static final List<Flame> flameList = new ArrayList<>();
    public static List<Bomb> bombs = new ArrayList<>();

    public static String[][] IdMap;
    public static Scene scene;

    public static Text textLevel;
    public static Text textLife;
    public static Text textPoint;

    public static Bomber bomberman;

    public CreateMap map;

    public static Stage gameStage;

    public static int level = 1;
    public static int playerPoint;

    @Override
    public void start(Stage stage) {
        gameStage = stage;
        map = new CreateMap(level);

        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT + HEIGHT_MENU);
        gc = canvas.getGraphicsContext2D();

        Rectangle rect = new Rectangle(0, Sprite.SCALED_SIZE * h, Sprite.SCALED_SIZE * w, HEIGHT_MENU);
        rect.setFill(Color.rgb(0, 0, 0, 1.0));

        textLevel = new Text();
        textLevel.setLayoutX(Sprite.SCALE * 10);
        textLevel.setLayoutY(Sprite.SCALED_SIZE * h + HEIGHT_MENU / 4 * 3 + Sprite.SCALE);
        textLevel.setFill(Color.WHITE);
        textLevel.setFont(Menu.font);

        textLife = new Text();
        textLife.setLayoutX(Sprite.SCALE * 90);
        textLife.setLayoutY(Sprite.SCALED_SIZE * h + HEIGHT_MENU / 4 * 3 + Sprite.SCALE);
        textLife.setFill(Color.WHITE);
        textLife.setFont(Menu.font);

        textPoint = new Text();
        textPoint.setLayoutX(Sprite.SCALE * 160);
        textPoint.setLayoutY(Sprite.SCALED_SIZE * h + HEIGHT_MENU / 4 * 3 + Sprite.SCALE);
        textPoint.setFill(Color.WHITE);
        textPoint.setFont(Menu.font);

        Image icon = new Image(new File("res/icon.png").toURI().toString());

        // Tao root container
        Group root = new Group();
        root.setClip(new Rectangle(Sprite.SCALED_SIZE * w,
                Sprite.SCALED_SIZE * h + HEIGHT_MENU));
        root.getChildren().addAll(canvas, rect, textLevel, textLife, textPoint);

        // Tao scene
        scene = new Scene(root);

        Scene pauseScene = Menu.pauseScene();

        Scene mainmenu = Menu.startScene();
        Scene level = Menu.levelScene();

        // Them scene vao stage
        gameStage.getIcons().add(icon);
        gameStage.setResizable(false);
        gameStage.setScene(Menu.startScene());

        gameStage.show();
        gameStage.setTitle("Siuuuuuuuuuuuu");
        gameStage.setOnCloseRequest(e -> {
            e.consume();
            logout(gameStage);

        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                levelUP();
                if (chooseScene >= 0) {
                    if (chooseScene % 4 == 0) {
                        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight() + HEIGHT_MENU);
                        // canvas.setLayoutX(0);
                        // canvas.setLayoutY(0);
                        update();
                        render();
                        gameStage.setScene(scene);
                        if (!bgMusicIsPlaying) {
                            Sound.stop("menuMusic");
                            Sound.stop("stageStart");
                            Sound.play("bg");
                            bgMusicIsPlaying = true;
                        }
                    } else if (chooseScene % 4 == 2) {
                        // Sound.stop("menuMusic");
                        if (!menuMusicIsPlaying) {
                            Sound.play("menuMusic");
                            menuMusicIsPlaying = true;
                        }
                        gameStage.setScene(mainmenu);
                        bgMusicIsPlaying = true;
                    } else if (chooseScene % 4 == 1) {
                        Sound.stop("bg");
                        Sound.play("menuMusic");
                        gameStage.setScene(pauseScene);
                    } else {
                        Sound.stop("menuMusic");
                        Sound.play("stageStart");
                        gameStage.setScene(level);
                    }

                }
            }
        };
        timer.start();

        scene.setOnKeyPressed(e -> {
            bomberman.handleKeyPressed(e.getCode());
            if (e.getCode() == KeyCode.P) {
                chooseScene++;
            }
        });
        scene.setOnKeyReleased(e -> bomberman.handleKeyReleased(e.getCode()));
    }

    public static void logout(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CR7");
        alert.setHeaderText("Shiuuuuuuuuuuuuuuuuuuuuuuuu");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }

    public void update() {

        for (int i = 0; i < enemy.size(); i++) {
            enemy.get(i).update();
        }

        for (int i = 0; i < flameList.size(); i++) {
            flameList.get(i).update();
        }
        bomberman.update();

        bombs = bomberman.getBombs();
        for (Bomb bomb : bombs) {
            bomb.update(); // updata cac event bomb
        }
        for (int i = 0; i < stillObjects.size(); i++) {
            stillObjects.get(i).update();
        }

        textLevel.setText("LEVEL: " + level);
        textLife.setText("LIFE: " + bomberman.getLife());
        textPoint.setText("POINT: " + playerPoint);

        updateCanvas();// update camera.
        handleCollisions();
        checkExplode();
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight() + HEIGHT_MENU);
        for (int i = stillObjects.size() - 1; i >= 0; i--) {
            stillObjects.get(i).render(gc);
        }

        enemy.forEach(g -> g.render(gc));

        for (Bomb bomb : bombs) {
            bomb.render(gc);
        }

        bomberman.render(gc);

        flameList.forEach(g -> g.render(gc));
    }

    // camera
    public void updateCanvas() {
        if (-1 * bomberman.getX() + Sprite.SCALED_SIZE * (WIDTH / 4) <= 0 && -1 * bomberman.getX()
                + Sprite.SCALED_SIZE * (WIDTH / 4) >= -1 * Sprite.SCALED_SIZE * (WIDTH / 2 + 1)
                && bomberman.isLive()) {

            canvas.setLayoutX(-bomberman.getX() + Sprite.SCALED_SIZE * (WIDTH / 4));
        }

        if (-1 * bomberman.getY() + Sprite.SCALED_SIZE * (HEIGHT / 4) <= 0 && -1 * bomberman.getY()
                + Sprite.SCALED_SIZE * (HEIGHT / 4) >= -1 * Sprite.SCALED_SIZE * (HEIGHT / 2 + 1)
                && bomberman.isLive()) {

            canvas.setLayoutY(-bomberman.getY() + Sprite.SCALED_SIZE * (HEIGHT / 4));
        }

    }

    public void levelUP() {
        if (bomberman.getLife() <= 0) {
            chooseScene = -1;
            canvas.setLayoutX(0);
            canvas.setLayoutY(0);
            Sound.stop("bg");
            bgMusicIsPlaying = false;
            menuMusicIsPlaying = false;
            gameStage.setScene(Menu.win_loseScene(false));
            level = 1;
            map = new CreateMap(level);
            CreateMap.nextLevel = false;
            return;
        }

        if (CreateMap.nextLevel) {
            chooseScene = -1;
            Sound.stop("bg");
            level++;
            bgMusicIsPlaying = false;
            if (level <= CreateMap.max_level) {
                Sound.play("menuMusic");
            }
            if (level > CreateMap.max_level) {
                level = 1;
                gameStage.setScene(Menu.win_loseScene(true));
                bomberman.setLife(2);
                playerPoint = 0;
                bgMusicIsPlaying = false;
            } else {
                gameStage.setScene(Menu.levelScene());
            }
            bomberman.newLevel();
            map = new CreateMap(level);
            CreateMap.nextLevel = false;
            canvas.setLayoutX(0);
            canvas.setLayoutY(0);
        }

    }

    public void handleCollisions() {
        /**
         * check va cham bomber voi gach, tuong
         */
        Rectangle r1 = bomberman.getBounds(); // hit box cua bomber

        for (Entity stillObject : stillObjects) {
            Rectangle r2 = stillObject.getBounds();
            // bomber vs item.
            if (r1.intersects(r2.getLayoutBounds())) {
                if (stillObject instanceof Item) {
                    Sound.play("itemCollected");
                    if (stillObject instanceof BombItem) {
                        bomberman.setBombRemain(bomberman.getBombRemain() + 1);
                        stillObjects.remove(stillObject);
                    } else if (stillObject instanceof SpeedItem) {
                        // bomberman.setSpeed(bomberman.getSpeed() + 4);
                        bomberman.setAccelration(bomberman.getAccelration() + 1);
                        stillObjects.remove(stillObject);
                    } else if (stillObject instanceof FlameItem) {
                        bomberman.setFlameRadius(bomberman.getFlameRadius() + 1);
                        stillObjects.remove(stillObject);
                    }
                }
                if (stillObject instanceof Portal) {
                    if (enemy.size() == 0) {
                        Sound.play("levelUp");
                        CreateMap.nextLevel = true;
                    }
                    // Sound.play("levelUp");
                    // CreateMap.nextLevel = true;

                }
                if (bomberman.getLayer() >= stillObject.getLayer()) {
                    bomberman.move();
                } else {
                    bomberman.stop();
                }
                break;
            }
        }

        /**
         * check va cham enemy voi tuong, gach.
         */
        for (Enemy enm : enemy) {
            Rectangle r2 = enm.getBounds();
            for (Entity stillObject : stillObjects) {
                Rectangle r3 = stillObject.getBounds();
                if (r2.intersects(r3.getLayoutBounds())) {
                    if (enm.getLayer() >= stillObject.getLayer()) {
                        enm.move();
                    } else {
                        enm.stop();
                    }
                    break;
                }
            }

            // check vao cham enemy va bomb.
            for (Bomb bomb : bomberman.getBombs()) {
                Rectangle r3 = bomb.getBounds();
                if (r2.intersects(r3.getLayoutBounds())) {
                    if (enm.getLayer() >= bomb.getLayer()) {
                        enm.move();
                    } else {
                        enm.stop();
                    }
                    break;
                }
            }
        }

        /**
         * Check va cham giua bomberman va enemy.
         */
        for (Enemy enm : enemy) {
            Rectangle r2 = enm.getBounds();
            if (r1.intersects(r2.getLayoutBounds())) {
                bomberman.setLive(false);
                Sound.play("bomberDie");
            }
        }
    }

    /**
     * bomb no vao cac doi tuong.
     */
    public void checkExplode() {

        for (Flame f : flameList) {
            // bom no vao tuong.
            Rectangle r0 = f.getBounds();
            Rectangle r1 = new Rectangle(r0.getX() + 2, r0.getY() + 2, r0.getWidth() - 4, r0.getHeight() - 4);
            for (Entity stillObject : stillObjects) {
                Rectangle r2 = stillObject.getBounds();
                if (r1.intersects(r2.getLayoutBounds())) {
                    stillObject.setLive(false);
                }
            }
            // bom no vao bomber.
            Rectangle r3 = bomberman.getBounds();
            if (r1.intersects(r3.getLayoutBounds())) {
                bomberman.setLive(false);
                Sound.play("bomberDie");
            }

            // bom no vao enemy.
            for (Enemy enm : enemy) {
                Rectangle r2 = enm.getBounds();
                if (r1.intersects(r2.getLayoutBounds())) {
                    if (enm.isLive()) {
                        playerPoint += enm.getPoint();
                    }
                    enm.setLive(false);
                    Sound.play("enemyDie");
                }
            }
        }
    }
}
