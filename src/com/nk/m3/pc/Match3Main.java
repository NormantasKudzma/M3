package com.nk.m3.pc;

import java.io.File;

import com.jogamp.newt.event.KeyEvent;
import com.nk.m3.game.HighScoreManager;
import com.nk.m3.main.Game;
import com.ovl.controls.Controller.Type;
import com.ovl.controls.ControllerEventListener;
import com.ovl.controls.ControllerKeybind;
import com.ovl.controls.ControllerManager;
import com.ovl.controls.arm.KeyboardController;
import com.ovl.controls.arm.MouseController;
import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.arm.OverloadEngineArm;
import com.ovl.utils.Vector2;

public class Match3Main {
	public static void main(String[] args){
		final Game game = new Game();
		game.platform = new Game.Platform() {
			float mouseFix;
			
			@Override
			public void init() {
				mouseFix = OverloadEngine.getInstance().aspectRatio * 0.5f;
				
				ControllerEventListener mouseClickListener = new ControllerEventListener(){
					@Override
					public void handleEvent(long eventArg, Vector2 pos, int... params) {
						pos.x *= mouseFix;
						
						if (params[0] == 1){
							game.state.onClick(pos);
						}
					}
				};
				
				MouseController mouse = (MouseController)ControllerManager.getInstance().getController(Type.TYPE_MOUSE);
				mouse.addKeybind(new ControllerKeybind(0, mouseClickListener));
				mouse.addKeybind(new ControllerKeybind(1, mouseClickListener));
				mouse.startController();
				
				KeyboardController keyboard = (KeyboardController)ControllerManager.getInstance().getController(Type.TYPE_KEYBOARD);
				keyboard.addKeybind(new ControllerKeybind(KeyEvent.VK_ESCAPE, new ControllerEventListener(){
					@Override
					public void handleEvent(long eventArg, Vector2 pos, int... params) {
						OverloadEngine.getInstance().requestClose();
					}
				}));
				keyboard.startController();
			}
		
			public String getVersion() {
				return "1.1";
			}
			
			public int getBuild() {
				return 8;
			}
		};
		
		HighScoreManager.saver = new HighScoreSaverPc();
		
		EngineConfig cfg = new EngineConfig();
		cfg.game = game;
		cfg.title = "Match 3 : super adventure";
		cfg.configPath = (new File("./resources/res/config.cfg")).getAbsolutePath();
		OverloadEngineArm engine = new OverloadEngineArm(cfg);
		engine.run();
	}
}
