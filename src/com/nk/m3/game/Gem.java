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
import com.ovl.utils.MutableFloat;
import com.ovl.utils.Vector2;

public class Gem extends GameObject {
	protected static final Renderer renderer;
	protected static final String highlightShaderName;
	protected static final Shader highlightShader;
	protected static final Vbo highlightVbo;
	protected static final String u_K = "u_K";
	protected static final String u_B = "u_B";
	protected static final String u_Dist = "u_Dist";
	protected static final String u_Offset = "u_Offset";
	
	private static final float normalScale = 1.16f;
	private static final float selectedScale = 1.5f;
	
	private static final float normal_b = 0.25f;
	private static final float selected_b = 0.33f;
	private static final float selected_b_change = 0.0075f;
	
	private GemColor gemColor;
	private boolean isSelected;
	private float shrinkingScale;
	private float shrinkingSpeed;
	private boolean isShrinking;
	
	private MutableFloat highlight_k = new MutableFloat(1.6f);
	private MutableFloat highlight_b = new MutableFloat(0.0f);
	private MutableFloat highlight_dist = new MutableFloat(0.028f);
	
	static {
		renderer = OverloadEngine.getInstance().renderer;
		highlightShaderName = "Highlight";
		highlightShader = renderer.createShader(highlightShaderName);
		highlightVbo = renderer.createVbo(highlightShaderName, 4);
	}
	
	public void setGemColor(GemColor gemColor){
		this.gemColor = gemColor;
		
		Sprite newSprite = gemColor.getSprite();
		
		if (sprite == null){
			sprite = new Sprite(newSprite.getTexture());
			
			HashMap<String, ParamSetter> shaderParams = new HashMap<String, ParamSetter>();
			shaderParams.put(Shader.U_COLOR, ParamSetterFactory.build(highlightShader, Shader.U_COLOR, sprite.getColor()));
			shaderParams.put(Shader.U_TEXTURE, ParamSetterFactory.build(highlightShader, Shader.U_TEXTURE, newSprite.getTexture()));
			shaderParams.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(highlightShader, Shader.U_MVPMATRIX));
			shaderParams.put(u_K, ParamSetterFactory.build(highlightShader, u_K, highlight_k));
			shaderParams.put(u_B, ParamSetterFactory.build(highlightShader, u_B, highlight_b));
			shaderParams.put(u_Dist, ParamSetterFactory.build(highlightShader, u_Dist, highlight_dist));
			shaderParams.put(u_Offset, ParamSetterFactory.build(highlightShader, u_Offset, getPosition()));
			sprite.useShader(highlightVbo, shaderParams);
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
		isSelected = true;
		setScale(selectedScale, selectedScale);

		highlight_b.value = selected_b;
	}
	
	public void unselect(){
		isSelected = false;
		setColor(Color.WHITE);
		setScale(normalScale, normalScale);

		highlight_b.value = normal_b;
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
		
		if (isSelected){
			highlight_b.value -= selected_b_change;
			if (highlight_b.value <= -selected_b) {
				highlight_b.value = selected_b;
			}
		}
		
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
