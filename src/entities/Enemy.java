package entities;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import engineTest.MainGameLoop;
import renderEngine.DisplayManager;
import terrains.Terrain;
import textures.ModelTexture;

public class Enemy extends Entity{
	
	private float RUN_SPEED = 40;
	private static float GRAVITY = -150;
	private static final float JUMP_POWER = 50;
	
	public long firingTimer = System.nanoTime();
	public long deathTimer = 0;
	private long reloadTimer = System.nanoTime();
	private long hittimer = System.nanoTime();
	
	public boolean dead = false;
	private boolean isInAir = false;


	private boolean reloading = false;
	public boolean lockedon = false;
	public boolean deathScream = false;
	public boolean isHit = false;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	public float dx = 0;
	public float dz = 0;
	
	private int health = 100;
	public int damageTaken;
	public int clipAmmo = 10;
	public int MaxclipAmmo = 10;
	public int id = 1;
	public int reloadtime = 2000;
	
	public Hitbox hitbox;
	float[][] datatable;
	
	//create hitboxes
	public float[][] createHitboxData(float lengthX, float lengthY, float lengthZ){
		Vector3f position = new Vector3f(0,0,0);
		//create model data
		float[] vertices = {
			position.x - lengthX, position.y-lengthY, position.z-lengthZ,          //first vertice
				
			position.x + lengthX, position.y-lengthY, position.z-lengthZ, //1 over in x direction
			position.x + lengthX, position.y + lengthY, position.z-lengthZ, //1 x, 1 y
				
			position.x -lengthX, position.y + lengthY, position.z-lengthZ, //1 over in y direction
			position.x -lengthX, position.y-lengthY, position.z + lengthZ, //1 over in z direction
			position.x + lengthX, position.y-lengthY, position.z + lengthZ,//1 x, 1 z
				
			position.x + lengthX, position.y + lengthY, position.z + lengthZ, //opposite to original
			position.x-lengthX, position.y + lengthY, position.z + lengthZ,//1 y, 1 z
				
		};
		// don't care for hitbox texture
		float[] textureCoords = new float[16];
		for(float f: textureCoords){
			f = 0;
		}
		float[] normals = {
				-1, 0, 0,
				0, 0, -1,
				1, 0, 0,
				0, 0, 1,
				0, -1, 0,
				0, 1, 0,	
		};
			
			
		float[][] table = {
			vertices, textureCoords, normals
		};
		return table;
	}
	
	
	public Enemy(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		
		super(model, position, rotX, rotY, rotZ, scale);

		//universal indices	
		int[] indices = { 
						0, 2, 1,
		                0, 3, 2,

		                1,2,6,
		                6,5,1,

		                4,5,6,
		                6,7,4,

		                2,3,6,
		                6,3,7,

		                0,7,3,
		                0,4,7,

		                0,1,5,
		                0,5,4
		};
		//create hitbox		
		datatable = createHitboxData(0.8f, 3.5f, 0.8f);
		float[] vertices = datatable[0];
			
		float[] textureCoords = datatable[1];
		float[] normals = datatable[2];
				
		RawModel hitboxmodel = MainGameLoop.loader.loadToVAO(vertices, textureCoords, normals, indices);
		TexturedModel texturedHitBox = new TexturedModel(hitboxmodel, new ModelTexture(MainGameLoop.loader.loadTexture("gold")));
		hitbox = new Hitbox(texturedHitBox, new Vector3f(position.x, position.y + 3.5f, position.z), 0.8f, 3.5f, 0.8f, 0, 0, 0, 1f);
		hitbox.id = 1;
		
	}
	
	public Hitbox getHitbox() {
		return hitbox;
	}

	public float[][] getDatatable() {
		return datatable;
	}
	
	public boolean isInAir() {
		return isInAir;
	}


	public void setInAir(boolean isInAir) {
		this.isInAir = isInAir;
	}


	public void move(Terrain terrain, Terrain terrain2, Terrain terrain3, Terrain terrain4){
		
		if(id == 1){
			reloadtime = 2100;
			RUN_SPEED = 15;
		}else if(id == 2){
			reloadtime = 1600;
			RUN_SPEED = 16;
		}else if(id == 3){
			reloadtime = 1600;
			RUN_SPEED = 18;
		}
		
		if(health <= 0){
			super.setRotX(-90);
			super.setRotY(super.getRotY());
			super.setRotZ(0);
			super.setPosition(new Vector3f(super.getPosition().x, 0, super.getPosition().z));
			if(deathTimer == 0){
				deathTimer = System.nanoTime();
			}
			deathScream = true;
			return;
		}
		
		if(clipAmmo <= 0){
			if(!reloading){
				reloadTimer = System.nanoTime();
			}
			reloading = true;
		}
		
		if(reloading){
			if(((System.nanoTime() - reloadTimer)/1000000) >= reloadtime){
				clipAmmo = MaxclipAmmo;
				reloading = false;
			}
		}
		
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		 dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		 dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);	
		upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		float terrainHeight = 0;
		
		float terraincoordX = (float) Math.floor(super.getPosition().x/800);
		float terraincoordZ = (float) Math.floor(super.getPosition().z/800);
		
		
		if(terraincoordX == 0 && terraincoordZ == -1) terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(terraincoordX == -1 && terraincoordZ == -1) terrainHeight = terrain2.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(terraincoordX == 0 && terraincoordZ == 0) terrainHeight = terrain3.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(terraincoordX == -1 && terraincoordZ == 0) terrainHeight = terrain4.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		
		if(super.getPosition().x < -798){
			super.setPosition(new Vector3f(-798, super.getPosition().y, super.getPosition().z));
		}
		if(super.getPosition().x > 798){
			super.setPosition(new Vector3f(798, super.getPosition().y, super.getPosition().z));
		}
		if(super.getPosition().z < -798){
			super.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y, -798));
		}
		if(super.getPosition().z > 798){
			super.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y, 798));
		}
		
		if(super.getPosition().y < terrainHeight){
			upwardSpeed = 0;
			super.getPosition().y = terrainHeight;
			isInAir = false;
		}
		hitbox.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y + 3.5f, super.getPosition().z));
		hitbox.collisionDetection();
		if(hitbox.getHits() > 0){
			health -= damageTaken * hitbox.getHits();
			hitbox.setHits(0);
			isHit = true;
			hittimer = System.nanoTime();
		}
		if(((System.nanoTime() - hittimer)/1000000) >= 1000){
			isHit = false;
		}
	}
	
	private void jump(){
		if(!isInAir){
			this.upwardSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	private void hop(){
		if(!isInAir){
			this.upwardSpeed = (float)(1 * JUMP_POWER/2);
			isInAir = true;
		}
	}
	
	public void sprint(){
		currentSpeed = RUN_SPEED;	
	}
	
	public void walk(){
		currentSpeed = 2 * RUN_SPEED/3;	
	}
	
	public void stop(){
		currentSpeed = 0;	
	}


	public int getHealth() {
		return health;
	}


	public void setHealth(int health) {
		this.health = health;
	}
}
