package com.saviour.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background,dizzy;
	Texture[] man;
	Rectangle manRectangle;
	int manState = 0,pause = 0,manY,coinCount,bombCount,score = 0,gameState = 0;
	float gravity = 0.2f,velocity = 0;
	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin,bomb;
	BitmapFont font;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();
	Random random;

	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		manY = Gdx.graphics.getHeight()/2;
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");
		random = new Random(Gdx.graphics.getHeight() - coin.getHeight()-150);

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1){
//			GAME IS LIVE
			//FOR BOMBS
			if(bombCount < 250){
				bombCount++;
			}else{
				bombCount = 0;
				makeBomb();
			}

			bombRectangle.clear();
			for(int i=0; i<bombXs.size(); i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i) - 8);
				bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			//FOR COINS
			if(coinCount < 100){
				coinCount++;
			}else{
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for(int i=0; i<coinXs.size(); i++){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i) - 4);
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}
			if(Gdx.input.justTouched()){
				velocity = -10;
			}

			if(pause < 8){
				pause++;
			}else {
				pause = 0;
				if (manState < 3){
					manState++;
				}else{
					manState = 0;
				}
			}
			velocity += gravity;
			manY -=velocity;
			if(manY <= 0){
				manY = 0 ;
			}

		}else if(gameState == 0){
//			WAITING TO START
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}else if(gameState == 2){
//			GAME OVER
			if(Gdx.input.justTouched()){
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;

				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;

				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount = 0;
			}
		}


		if (gameState == 2){
			batch.draw(dizzy,Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY);
		}else {
			batch.draw(man[manState],Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight());

		for(int i=0;i<coinRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
				score++;

				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for(int i=0;i<bombRectangle.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangle.get(i))){
				gameState = 2;
				break;
			}
		}

		font.draw(batch,String.valueOf(score),100,200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
