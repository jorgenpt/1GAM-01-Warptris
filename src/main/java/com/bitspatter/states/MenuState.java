package com.bitspatter.states;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class MenuState extends CommonState {
    interface MenuAction {
        void onClick();
    }

    class MenuItem {
        public Image image, selectedImage;
        public MenuAction action;

        public MenuItem(String file, MenuAction action) throws SlickException {
            this.image = new Image("menu_" + file + "_normal.png");
            this.selectedImage = new Image("menu_" + file + "_selected.png");
            this.action = action;
        }
    }

    public final static int STATE_ID = 1;

    final int MENU_ITEM_SPACING = 15;

    Image title, splash1GAM;
    MenuItem[] menuItems;
    int selectedMenuItem = 0;

    @Override
    public void init(final GameContainer container, final StateBasedGame game) throws SlickException {
        title = new Image("menu_title.png");
        splash1GAM = new Image("menu_1gam_logo.png");

        menuItems = new MenuItem[] { new MenuItem("start", new MenuAction() {
            public void onClick() {
                game.enterState(PlayingState.STATE_ID, new FadeOutTransition(), new FadeInTransition());
            }
        }), new MenuItem("quit", new MenuAction() {
            public void onClick() {
                container.exit();
            }
        }) };
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        int y = MENU_ITEM_SPACING * 2;

        g.drawImage(title, (container.getWidth() - title.getWidth()) / 2, y);
        y += title.getHeight() + MENU_ITEM_SPACING * 3;

        for (int i = 0; i < menuItems.length; ++i) {
            Image image = menuItems[i].image;
            if (i == selectedMenuItem) {
                image = menuItems[i].selectedImage;
            }

            g.drawImage(image, (container.getWidth() - image.getWidth()) / 2, y);
            y += image.getHeight() + MENU_ITEM_SPACING;
        }

        g.drawImage(splash1GAM, (container.getWidth() - splash1GAM.getWidth()) / 2,
                        container.getHeight() - splash1GAM.getHeight() - MENU_ITEM_SPACING);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        super.update(container, game, delta);

        Input input = container.getInput();

        if (input.isKeyPressed(Input.KEY_DOWN)) {
            selectedMenuItem = (selectedMenuItem + 1) % menuItems.length;
        }

        if (input.isKeyPressed(Input.KEY_UP)) {
            selectedMenuItem = (selectedMenuItem - 1) % menuItems.length;
            if (selectedMenuItem < 0) {
                selectedMenuItem += menuItems.length;
            }
        }

        if (input.isKeyPressed(Input.KEY_ENTER)) {
            menuItems[selectedMenuItem].action.onClick();
        }
    }

    @Override
    public int getID() {
        return STATE_ID;
    }
}
