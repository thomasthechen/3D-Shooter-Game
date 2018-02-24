package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import engineTest.MainGameLoop;

public class Grenade extends Entity{
	
	public float speed;
	public boolean exploded = false;
	public long grenadeTimer = System.nanoTime();
	public float dy = 0;
	
	public Grenade(TexturedModel model, Vector3f position1, float rotX,
			float rotY, float rotZ, float scale, float speed) {
		super(model, position1, rotX, rotY, rotZ, scale);
		this.speed = speed;
		
	}
	
	
	public void explode(){
		if(((System.nanoTime() - grenadeTimer)/1000000) >= 1500){		
			exploded = true;		
		}
		
	}
	
	

}
