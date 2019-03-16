package com.nk.m3.game;

import java.util.HashMap;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.Vbo;
import com.ovl.game.GameObject;
import com.ovl.graphics.Color;
import com.ovl.graphics.Sprite;
import com.ovl.utils.Vector2;

public class Gem extends GameObject {
	protected static final Renderer renderer;
	public static final String outlineShaderName;
	public static final Shader outlineShader;
	protected static final Vbo outlineVbo;
	
	private static final float selectedColorIntensity = 0.8f;
	
	private static final float normalScale = 1.16f;
	private static final float selectedScale = 1.5f;
	
	private static final float normalOutline = 0.002f;
	private static final float selectedOutline = 0.008f;
	
	private GemColor gemColor;
	private float selectedTime;
	private boolean isSelected;
	private float shrinkingScale;
	private float shrinkingSpeed;
	private boolean isShrinking;
	private Vector2 outlineSize = new Vector2();
	
	static {
		renderer = OverloadEngine.getInstance().renderer;
		outlineShaderName = "Outline";
		outlineShader = renderer.createShader("Outline");
		outlineVbo = renderer.createVbo("Outline", 4);
	}
	
	public void setGemColor(GemColor gemColor){
		this.gemColor = gemColor;
		
		Sprite newSprite = gemColor.getSprite();
		
		if (sprite == null){
			sprite = new Sprite(newSprite.getTexture());
			
			HashMap<String, ParamSetter> outlineParams = new HashMap<String, ParamSetter>();
			outlineParams.put(Shader.U_COLOR, ParamSetterFactory.build(outlineShader, Shader.U_COLOR, sprite.getColor()));
			outlineParams.put(Shader.U_TEXTURE, ParamSetterFactory.build(outlineShader, Shader.U_TEXTURE, newSprite.getTexture()));
			outlineParams.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(outlineShader, Shader.U_MVPMATRIX));
			outlineParams.put("u_Size", ParamSetterFactory.build(outlineShader, "u_Size", outlineSize));
			sprite.useShader(outlineVbo, outlineParams);
		}

		Vector2 uv[] = newSprite.getUV();
		((Sprite)sprite).setUV(uv);
		((Sprite)sprite).setTextureSize_Normal(newSprite.getTextureSize());
		unshrink();
	}
	
	public GemColor getGemColor(){
		return gemColor;
	}
	
	public void select(){
		selectedTime = 0.0f;
		isSelected = true;
		setScale(selectedScale, selectedScale);
		outlineSize.set(selectedOutline, selectedOutline);
	}
	
	public void unselect(){
		isSelected = false;
		setColor(Color.WHITE);
		setScale(normalScale, normalScale);
		outlineSize.set(normalOutline, normalOutline);
	}
	
	public void shrink(float duration){
		isShrinking = true;
		shrinkingScale = normalScale;
		shrinkingSpeed = 1.0f / duration;
	}
	
	public void unshrink(){
		isShrinking = false;
		setScale(normalScale, normalScale);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		/*if (isSelected){
			selectedTime += deltaTime * 5.0f;
			
			Color color = getColor();
			float value = selectedColorIntensity + (float)Math.cos(selectedTime) * (1.0f - selectedColorIntensity);
			for (int i = 0; i < color.rgba.length - 1; ++i){
				color.rgba[i] = value;
			}
		}*/
		
		if (isShrinking){
			shrinkingScale -= deltaTime * shrinkingSpeed;
			setScale(shrinkingScale, shrinkingScale);
			if (shrinkingScale <= 0.0f){
				isShrinking = false;
			}
		}
	}
	
	public void reset(){
		unselect();
		unshrink();
	}
}
