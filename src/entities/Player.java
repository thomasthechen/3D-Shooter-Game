package entities;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import engineTest.MainGameLoop;
import renderEngine.DisplayManager;
import terrains.Terrain;
import textures.ModelTexture;

public class Player extends Entity{
	
	private float RUN_SPEED = 0;
	private static float GRAVITY = -150;
	private static final float JUMP_POWER = 36;

	public boolean firing = false;
	private boolean isInAir = false;
	public boolean onBlock = false;
	public boolean switchingGuns = false;
	public boolean firstShot = true;
	public boolean reloading = false;
	public boolean thirdperson = true;
	public boolean scoped = false;
	public boolean hit = false;
	public boolean thrown = false;
	public long hittimer = System.nanoTime();
	public boolean openBuy = false;
	private long closetimer = System.nanoTime();
	public boolean aimbot = false;
	
	private float currentSpeed = 0;
	private float currentStrafeSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	public float dx = 0;
	public float dz = 0;
	public float dy = 0;
	public float sensitivity = 3;
	public float terrainHeight;
	public int activeslot = 1;
	
	private Hitbox hitbox;
	//0 = M4A1
	//1 = M4A4
	//2 = AK47
	//3 = AWP
	//4 = Deagle
	//5 = Shotgun
	private int gunId = 0;
	private int health = 100;
	private int damage = 8;

	public Clip Gunsound;
	public Clip Gunsound1;
	public Clip Gunsound2;
	public Clip Gunsound3;
	public Clip Gunsound4;
	public Clip Gunsound5;
	public Clip Gunsound6;
	public Clip Gunsound7;
	public Clip Gunsound8;
	public Clip Gunsound9;
	public Clip Gunsound10;
	public Clip Gunsound11;
	public Clip Gunsound12;
	public Clip Gunsound13;
	public Clip Gunsound14;
	
	public static Clip hitsound;
	public Clip reloadingSound;
	public Clip deathSound;
	public static Clip headshotSound;
	public Clip Explosion;
	public Clip Grenade;
	
	public Clip runningsound;
	
	public Clip loadClip(String filename){
		Clip in = null;

		try {
			
			File file = new File(filename);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
			in = AudioSystem.getClip();
			in.open(audioStream);
			
		}catch( Exception e ){
			e.printStackTrace();
		}

		return in;
	}
	
	public int getGunId() {
		return gunId;
	}

	public void setGunId(int gunId) {
		this.gunId = gunId;
	}
	
	
	
	//create hitboxes
	public float[][] createHitboxData(float lengthX, float lengthY, float lengthZ){
		Vector3f position = new Vector3f(0,0,0);
		//create model data
		float[] vertices = {
			position.x - lengthX, position.y - lengthY, position.z-lengthZ,          //first vertice
				
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
	
	public Player(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		Gunsound = loadClip("res/Silencedshot.wav");
		Gunsound1 = loadClip("res/Gunshot.wav");
		Gunsound2 = loadClip("res/AKShot.wav");
		Gunsound3 = loadClip("res/AWPShot.wav");
		Gunsound4 = loadClip("res/DeagleShot.wav");
		Gunsound5 = loadClip("res/ShotgunShot.wav");
		Gunsound6 = loadClip("res/ACWRShot.wav");
		Gunsound7 = loadClip("res/RifleShot.wav");
		Gunsound8 = loadClip("res/FiveSevenShot.wav");
		Gunsound9 = loadClip("res/Autoshotty.wav");
		Gunsound10 = loadClip("res/MP5Shot.wav");
		Gunsound11 = loadClip("res/Mac10Shot.wav");
		Gunsound12 = loadClip("res/G36Shot.wav");
		Gunsound13 = loadClip("res/GalilShot.wav");
		Gunsound14 = loadClip("res/DoubleBarrelShot.wav");
		
		hitsound = loadClip("res/Hitsound.wav");
		reloadingSound = loadClip("res/Reload.wav");
		deathSound = loadClip("res/Scream.wav");
		headshotSound = loadClip("res/HeadshotSound.wav");
		Explosion = loadClip("res/Explosion.wav");
		Grenade = loadClip("res/Grenade.wav");
		runningsound = loadClip("res/Running.wav");
		
		for(Gun gun: MainGameLoop.allGuns){
			if(gun.getGunId() == 0){
				gun.setSound(Gunsound);
			}else if(gun.getGunId() == 1){
				gun.setSound(Gunsound1);
			}else if(gun.getGunId() == 2){
				gun.setSound(Gunsound2);
			}else if(gun.getGunId() == 3){
				gun.setSound(Gunsound3);
			}else if(gun.getGunId() == 4){
				gun.setSound(Gunsound4);
			}else if(gun.getGunId() == 5){
				gun.setSound(Gunsound5);
			}
			
		}
		
		//create hitboxes
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
		
		float[][] datatable = createHitboxData(0.8f, 3.5f, 0.8f);
		float[] vertices = datatable[0];
		
		float[] textureCoords = datatable[1];
		float[] normals = datatable[2];
				
		RawModel hitboxmodel = MainGameLoop.loader.loadToVAO(vertices, textureCoords, normals, indices);
		TexturedModel texturedHitBox = new TexturedModel(hitboxmodel, new ModelTexture(MainGameLoop.loader.loadTexture("gold")));
		hitbox = new Hitbox(texturedHitBox, new Vector3f(position.x, position.y + 3.5f, position.z), 0.8f, 3.5f, 0.8f, 0, 0, 0, 1f);	
		hitbox.id = 0;
		Mouse.setGrabbed(true);
		
	}
	
	public Hitbox getHitbox() {
		return hitbox;
	}
	
	public void calculateDxz(){
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float sidedistance = currentStrafeSpeed * DisplayManager.getFrameTimeSeconds();
		dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY()))) + (float) (sidedistance * Math.sin(Math.toRadians(90 + super.getRotY())));
		dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY()))) + (float) (sidedistance * Math.cos(Math.toRadians(90 + super.getRotY())));
		
	}
	public void move(Terrain terrain, Terrain terrain2, Terrain terrain3, Terrain terrain4){
		
		if(scoped){
			RUN_SPEED = 8;
		}else{
			if(gunId == 0 || gunId == 1 || gunId == 2 || gunId == 7 || gunId == 10 || gunId == 8 || gunId == 5 || gunId == 11){
				//assault rifles
				RUN_SPEED = 14;
			}else if(gunId == 6 || gunId == 9 || gunId == 12 || gunId == 13){
				//smgs and shotguns
				RUN_SPEED = 15;
			}else if(gunId == 3 || gunId == 15){
				//heavy: awp, m249
				RUN_SPEED = 13;
			}else if(gunId == 4 || gunId == 14){
				//pistols
				RUN_SPEED = 16;
			}
			
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_D)){
			if(!isInAir){
				if(!runningsound.isRunning()){
					runningsound.setFramePosition(0);
					runningsound.start();
				}
			}
		}
		
		if(activeslot == 2) RUN_SPEED = 17;
		
		checkInputs();
		
		if(health <= 0){
			super.setRotX(-90);
			super.setRotY(super.getRotY());
			super.setRotZ(0);
			super.setPosition(new Vector3f(super.getPosition().x, 0, super.getPosition().z));
			return;
		}
		
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
	
		gravity();
		
		terrainHeight = 0;
		
		float terraincoordX = (float) Math.floor(super.getPosition().x/800);
		float terraincoordZ = (float) Math.floor(super.getPosition().z/800);
		
		if(terraincoordX == 0 && terraincoordZ == -1) terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(terraincoordX == -1 && terraincoordZ == -1) terrainHeight = terrain2.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(terraincoordX == 0 && terraincoordZ == 0) terrainHeight = terrain3.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(terraincoordX == -1 && terraincoordZ == 0) terrainHeight = terrain4.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		
		if(super.getPosition().y < terrainHeight){
			upwardSpeed = 0;
			super.getPosition().y = terrainHeight;
			isInAir = false;
		}
		//hitboxes
		hitbox.collisionDetection();
		hitbox.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y + 3.5f, super.getPosition().z));
		//10 shot kill
		if(hitbox.getHits() > 0){
			health -= damage * hitbox.getHits();
			hitbox.setHits(0);
			hit = true;
			hittimer = System.nanoTime();
		}
		
		if(((System.nanoTime() - hittimer)/1000000) >= 500){
			hit = false;
		}
		
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
		
		if(activeslot != 1){
			firing = false;
			reloading = false;
		}
		if(activeslot != 3){
			thrown = false;
		}
	}
	
	public void gravity(){
		upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		dy = upwardSpeed * DisplayManager.getFrameTimeSeconds();
		if(!isInAir){
			if(dy < -3f) dy = -3f;
		}
		super.increasePosition(dx, dy, dz);
		
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isInAir() {
		return isInAir;
	}

	public void setInAir(boolean isInAir) {
		this.isInAir = isInAir;
	}

	private void jump(){
		if(!isInAir){
			this.upwardSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	boolean turn = false;
	float turnSpeed = 0;
	
	private void checkInputs(){
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed += 14;
			if(this.currentSpeed > RUN_SPEED){
				this.currentSpeed = RUN_SPEED;
			}
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed -= 14;
			if(this.currentSpeed < -RUN_SPEED){
				this.currentSpeed = -RUN_SPEED;
			}
		}else{
			if(this.currentSpeed > 0){
				this.currentSpeed -= 14;
				if(this.currentSpeed < 0){
					this.currentSpeed = 0;
				}
			}else if(this.currentSpeed < 0){
				this.currentSpeed += 14;
				if(this.currentSpeed > 0){
					this.currentSpeed = 0;
				}
			}
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_F)){
			aimbot = true;
		}else{
			aimbot = false;
		}
		
		if(thirdperson){
			if(!Mouse.isButtonDown(1)){
			
				turnSpeed = -Mouse.getDX() * sensitivity;
				this.currentTurnSpeed = turnSpeed;
			}else{
				this.currentTurnSpeed = 0;
			}
			
		}else{
			turnSpeed = -Mouse.getDX() * sensitivity;
			this.currentTurnSpeed = turnSpeed;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentStrafeSpeed -= 10;
			if(this.currentStrafeSpeed < -5*RUN_SPEED/6){
				this.currentStrafeSpeed = -5*RUN_SPEED/6;
			}
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentStrafeSpeed += 10;
			if(this.currentStrafeSpeed > 5*RUN_SPEED/6){
				this.currentStrafeSpeed = 5*RUN_SPEED/6;
			}
		}else{
			if(this.currentStrafeSpeed > 0){
				this.currentStrafeSpeed -= 10;
				if(this.currentStrafeSpeed < 0){
					this.currentStrafeSpeed = 0;
				}
			}else if(this.currentStrafeSpeed < 0){
				this.currentStrafeSpeed += 10;
				if(this.currentStrafeSpeed > 0){
					this.currentStrafeSpeed = 0;
				}
			}
			
		}
		
		if(openBuy){
			closetimer = System.nanoTime();
		}
		
		if(((System.nanoTime() - closetimer)/1000000) >= 500){
		if(Keyboard.isKeyDown(Keyboard.KEY_1)){
			activeslot = 1;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
			activeslot = 2;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_3)){
			activeslot = 3;
		}
		}
		//jump
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
		//fire
		if(Mouse.isButtonDown(0)){
			firing = true;
		}else{
			firing = false;
			firstShot = true;
		}
		//manual reload
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			reloading = true;
		}
	}

}
