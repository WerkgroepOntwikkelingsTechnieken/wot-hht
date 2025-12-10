package dev.codewizz.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends ApplicationAdapter {
    private Stage stage;

    private String prevInput = "";

    @Override
    public void create () {
        Database.connect();

        VisUI.setSkipGdxVersionCheck(true);
        VisUI.load(SkinScale.X2);
        stage = new Stage(new ScreenViewport());
        setupInput();

        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        VisTextButton insertTool =  new VisTextButton("Insert Tool");
        insertTool.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prevInput = "";

                VisWindow visWindow = new VisWindow("Insert Tool");

                VisTextField name = new VisTextField();
                VisTextField description = new VisTextField();
                VisTextField code = new VisTextField();

                visWindow.add(new VisLabel("Tool Name: "), name).left().row();
                visWindow.add(new VisLabel("Description: "), description).left().row();
                visWindow.add(new VisLabel("Barcode: "), code).left().row();

                VisTextButton submit = new VisTextButton("Submit");
                submit.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Database.execute("INSERT INTO tool (id, name, description) VALUES (" + code.getText() + ",'" + name.getText() + "','" + description.getText() + "')");
                        visWindow.fadeOut();
                    }
                });
                visWindow.add(submit).expandX().fillX().right().row();

                visWindow.addCloseButton();
                visWindow.pack();
                visWindow.centerWindow();

                stage.addActor(visWindow.fadeIn());
            }
        });
        root.add(insertTool).center();

        VisTextButton insertTeam =  new VisTextButton("Insert Team");
        insertTeam.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prevInput = "";

                VisWindow visWindow = new VisWindow("Insert Team");

                VisTextField name = new VisTextField();
                VisTextField code = new VisTextField();

                visWindow.add(new VisLabel("Team Name: "), name).left().row();
                visWindow.add(new VisLabel("Barcode: "), code).left().row();

                VisTextButton submit = new VisTextButton("Submit");
                submit.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Database.execute("INSERT INTO team (id, name, active, warnings) VALUES (" + code.getText() + ",'" + name.getText() + "', TRUE, 0)");
                        visWindow.fadeOut();
                    }
                });
                visWindow.add(submit).expandX().fillX().right().row();

                visWindow.addCloseButton();
                visWindow.pack();
                visWindow.centerWindow();

                stage.addActor(visWindow.fadeIn());
            }
        });
        root.add(insertTeam).center();


    }

    @Override
    public void resize (int width, int height) {
        if(width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render () {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose () {
        try {
            Database.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        VisUI.dispose();
        stage.dispose();
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new KeyInput(this));
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void scan(String input) {
        System.out.println(input);

        if (prevInput.isEmpty()) { prevInput = input; return; }

        ResultSet team = Database.query("SELECT * FROM team WHERE id = '" + prevInput + "'");
        ResultSet tool = Database.query("SELECT * FROM tool WHERE id = '" + input + "'");
        ResultSet ret = Database.query("SELECT COUNT(*) FROM has WHERE team_id = '" + prevInput + "' AND tool_id = '" + input + "'");
        ResultSet size = Database.query("SELECT COUNT(*) FROM has WHERE team_id = '" + prevInput + "'");

        try {

            if (ret.next() && ret.getInt(1) == 1)  {
                Dialogs.showOKDialog(stage, "Returned Tool", "");
                Database.execute("DELETE FROM has WHERE team_id = '" + prevInput + "' AND tool_id = '" + input + "'");
                prevInput = "";
                return;
            }

            if (team.next() && tool.next() && size.next()) {
                VisWindow window = new VisWindow("Are you sure?");
                window.add(new VisLabel("Do-Group already has " + size.getInt("COUNT(*)") + " tool(s)")).row();

                VisTextButton continueButton = new VisTextButton("Continue");
                continueButton.addListener(new ChangeListener() {
                    @Override
                    public void changed (ChangeEvent event, Actor actor) {
                        window.fadeOut();
                        Database.execute("INSERT INTO has (team_id, tool_id) VALUES (" + prevInput + "," + input + ");");
                        prevInput = "";
                    }
                });

                VisTextButton cancelButton = new VisTextButton("Cancel");
                cancelButton.addListener(new ChangeListener() {
                    @Override
                    public void changed (ChangeEvent event, Actor actor) {
                        window.fadeOut();
                        prevInput = "";
                    }
                });

                VisTable buttons = new VisTable();
                buttons.add(continueButton).expand().left();
                buttons.add(cancelButton).expand().right();
                window.add(buttons).expand().fill();

                window.pack();
                window.centerWindow();
                stage.addActor(window.fadeIn());
            } else {
                prevInput = "";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
