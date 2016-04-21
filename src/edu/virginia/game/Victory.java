package edu.virginia.game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

/**
 * Example game that utilizes our engine. We can create a simple prototype game
 * with just a couple lines of code although, for now, it won't be a very fun
 * game :)
 */
public class Victory extends Game {

	public static int width = 1050;
	public static int height = 1050;

	Map map;
	DisplayObjectContainer game;
	ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	public Victory() {
		super("Ghost Game", width, height);
		getMainFrame().setBounds(0, 0, width, height); // Fixing weird size bug.
		TMXMapReader mapReader = new TMXMapReader();
		try {
			map = mapReader.readMap("resources/victory.tmx");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		game = new DisplayObjectContainer("game");
		getMainFrame().setSize(map.getWidth() * map.getTileWidth(), map.getHeight() * map.getTileHeight());
		TweenJuggler.getInstance();
		for (MapLayer m : map.getLayers()) {
			if (m instanceof TileLayer) {
				TileLayer l = (TileLayer) m;
				for (int i = 0; i < map.getHeight(); i++) {
					for (int j = 0; j < map.getWidth(); j++) {
						Tile t = l.getTileAt(j, i);
						if (t != null) {
							Sprite s = new Sprite("" + t.getId());
							s.setImage((BufferedImage) t.getImage());
							s.setPositionX(j * t.getWidth());
							s.setPositionY(i * t.getHeight());
							sprites.add(s);
						}
					}
				}
			}
		}

		for (Sprite s : sprites) {
			game.addChild(s);
		}
	}

	/**
	 * Engine automatically invokes draw() every frame as well. If we want to
	 * make sure mario gets drawn to the screen, we need to make sure to
	 * override this method and call mario's draw method.
	 */
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if (game != null) {
			game.draw(g);
		}
	}
}
