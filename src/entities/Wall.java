package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Wall extends Entity {
	
	private Vector3f min = new Vector3f(0,0,0);
	private Vector3f max = new Vector3f(0,0,0);
	
	public Wall(TexturedModel model, Vector3f position1, float rotX,
			float rotY, float rotZ, float scale, Vector3f min1, Vector3f max1) {
		super(model, position1, rotX, rotY, rotZ, scale);
	
		//this.min.x = this.getScale() * min1.x;
		//this.min.y = this.getScale() * min1.y;
		//this.min.z = this.getScale() * min1.z;
		//this.max.x = this.getScale() * max1.x;
		//this.max.y = this.getScale() * max1.y;
		//this.max.z = this.getScale() * max1.z;
		this.max = max1;
		this.min = min1;
	}
	
	public Vector3f getMin(){
		
		return min;
	}
	
	public Vector3f getMax(){
		
		return max;
	}
}
