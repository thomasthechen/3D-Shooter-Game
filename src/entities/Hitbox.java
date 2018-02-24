package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import engineTest.MainGameLoop;

public class Hitbox extends Entity{
	
	private float lengthX;
	private float lengthY;
	private float lengthZ;
	private Vector3f position;
	
	public Vector3f intersectionspot = new Vector3f(0,0,0);
	
	private int hits = 0;
	public int id;
	

	public Hitbox(TexturedModel model, Vector3f position, float lengthX, float lengthY, float lengthZ, float rotX,
			float rotY, float rotZ, float scale) {
		
		super(model, position, rotX, rotY, rotZ, scale);
		
		this.lengthX = lengthX;
		this.lengthY = lengthY;
		this.lengthZ = lengthZ;
		this.position = position;
	
	}
	
	public float getLengthX() {
		return lengthX;
	}

	public float getLengthY() {
		return lengthY;
	}

	public float getLengthZ() {
		return lengthZ;
	}
	
	public Matrix3f createMatrix(Vector3f row1, Vector3f row2, Vector3f row3){
		
		Matrix3f mat = new Matrix3f();
		mat.m00 = row1.x;
		mat.m01 = row1.y;
		mat.m02 = row1.z;
		
		mat.m10 = row2.x;
		mat.m11 = row2.y;
		mat.m12 = row2.z;
		
		mat.m20 = row3.x;
		mat.m21 = row3.y;
		mat.m22 = row3.z;
		
		
		return mat;
		
		
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
		super.setPosition(position);
	}
	
	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}
	
	
	//add collision detection
	public void collisionDetection(){

		for(Entity bullet: MainGameLoop.allBullets){
			
			float speed = 1200 * DisplayManager.getFrameTimeSeconds();
			Vector3f pastPosition = new Vector3f(bullet.getPosition().x - (float)(speed * Math.cos(Math.toRadians(bullet.getRotZ())) * Math.cos(Math.toRadians(-bullet.getRotY()))), bullet.getPosition().y -(float)(speed * Math.sin(Math.toRadians(bullet.getRotZ()))), bullet.getPosition().z - (float)(speed * Math.cos(Math.toRadians(bullet.getRotZ())) * Math.sin(Math.toRadians(-bullet.getRotY()))));
			
			double distance = Math.sqrt((bullet.getPosition().x - super.getPosition().x) * (bullet.getPosition().x - super.getPosition().x)  + (bullet.getPosition().z - super.getPosition().z) * (bullet.getPosition().z - super.getPosition().z) );
			double pastdistance = Math.sqrt((pastPosition.x - super.getPosition().x) * (pastPosition.x - super.getPosition().x)  + (pastPosition.z - super.getPosition().z) * (pastPosition.z - super.getPosition().z) );
			
			intersectionspot = new Vector3f(pastPosition.x + (float)(pastdistance * (bullet.getPosition().x - pastPosition.x)/(pastdistance + distance)), pastPosition.y + (float)(pastdistance * (bullet.getPosition().y - pastPosition.y)/(pastdistance + distance)), pastPosition.z + (float)(pastdistance * (bullet.getPosition().z - pastPosition.z)/(pastdistance + distance)));
			                     
			if(intersectionspot.x <= position.x + lengthX && intersectionspot.x >= position.x - lengthX){

				if(intersectionspot.y <= position.y + lengthY && intersectionspot.y >= position.y - lengthY){

					if(intersectionspot.z <= position.z + lengthZ && intersectionspot.z >= position.z - lengthZ){
						if(id != bullet.id){
							if(intersectionspot.y >= position.y + lengthY - 1f){
								//headshot
								hits += 3;
								if(!Player.headshotSound.isRunning()){
									Player.headshotSound.setFramePosition(0);
									Player.headshotSound.start();
								}
								bullet.setRemove(true);
							}else{
								hits += 1;
								bullet.setRemove(true);
								if(Player.hitsound.isRunning()){
									Player.hitsound.stop();
									
								}	
								Player.hitsound.setFramePosition(0);
								Player.hitsound.start();
								
								
							}			
						}
					}
				}
			}
		}	
	}
}