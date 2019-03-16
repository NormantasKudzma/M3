package com.nk.m3.game;

//import java.awt.event.KeyEvent;

/*import com.ovl.controls.Controller.Type;
import com.ovl.controls.ControllerEventListener;
import com.ovl.controls.ControllerManager;
import com.ovl.controls.arm.KeyboardController;*/
import com.ovl.graphics.SimpleFont;
import com.ovl.utils.Vector2;

public class EditableText {
	public interface FocusListener {
		public void onFocusChanged(boolean isFocused);
	}
	
	//private static KeyboardController keyboard;
	private static boolean keyboardChecked;
	private static final float markerDelay = 0.6f;
	private static final char markerChar = '-';
	
	private static final int VK_ENTER_WIN10 = 13;
	
	private SimpleFont textObj;
	//private ControllerEventListener keyListener;
	private StringBuilder buf;
	private boolean unselect;
	private boolean selected;
	private boolean markerVisible;
	private float markerTimer;
	private int maxLength;
	private FocusListener listener;
	
	public EditableText(SimpleFont obj, int max){
		if (!keyboardChecked){
			//keyboard = (KeyboardController)ControllerManager.getInstance().getController(Type.TYPE_KEYBOARD);
			keyboardChecked = true;
		}

		textObj = obj;
		maxLength = max;
		buf = new StringBuilder();
		/*keyListener = new ControllerEventListener(){
			@Override
			public void handleEvent(long key, Vector2 pos, int... params) {
				if (params[0] == 1 || !selected){
					return;
				}
				
				System.out.println(" key " + key);
				
				switch ((int)key){
					case VK_ENTER_WIN10:
					case KeyEvent.VK_ENTER:{
						unselect = true;
						break;
					}
					case KeyEvent.VK_DELETE:
					case KeyEvent.VK_BACK_SPACE:{
						toggleMarker(false);
						if (buf.length() > 0){
							buf.setLength(buf.length() - 1);
							refreshText();
						}
						break;
					}
					default:{
						char c = keyboard.getLastChar();
						toggleMarker(false);
						if (c >= 97 && c <= 122 && buf.length() < maxLength){
							buf.append(c);
							refreshText();
						}
						break;
					}
				}
			}
		};*/
	}
	
	public void setListener(FocusListener listener){
		this.listener = listener;
	}
	
	public void select(){
		/*if (keyboard == null){
			return;
		}
		
		keyboard.setUnmaskedCallback(keyListener);*/
		toggleMarker(true);
		selected = true;
		markerTimer = markerDelay;
		refreshText();
		
		if (listener != null){
			listener.onFocusChanged(true);
		}
	}
	
	public void unselect(){
		/*if (keyboard == null){
			return;
		}
		
		keyboard.setUnmaskedCallback(null);*/
		toggleMarker(false);
		selected = false;
		refreshText();

		if (listener != null){
			listener.onFocusChanged(false);
		}
	}
	
	private void refreshText(){
		textObj.setText(buf.toString());
	}
	
	public void update(float deltaTime){
		if (unselect){
			unselect = false;
			unselect();
		}
		
		if (selected){
			markerTimer -= deltaTime;
			if (markerTimer <= 0.0f){
				markerTimer = markerDelay;
				markerVisible = !markerVisible;
				toggleMarker(markerVisible);
			}
		}
	}
	
	private void toggleMarker(boolean visible){
		markerVisible = visible;
		if (visible){
			if (buf.length() <= 0 || buf.charAt(buf.length() - 1) != markerChar){
				buf.append(markerChar);
				refreshText();
			}
		}
		else {
			if (buf.length() > 0 && buf.charAt(buf.length() - 1) == markerChar){
				buf.setLength(buf.length() - 1);
				refreshText();
			}
		}
	}
	
	public String getText(){
		return buf.toString();
	}
}
