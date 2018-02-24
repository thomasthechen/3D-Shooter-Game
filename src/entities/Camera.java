package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Camera {
	
	private Vector3f position = new Vector3f(0,30,0);
	private float yaw;
	private float roll;
	private float pitch = 0;

	public float magnification = 0;
	
	public float initrotY = 0;
	public float initrotZ = 0;
	
	public boolean thirdperson = false;
	public boolean scoped = false;
	public int crouched = 0;
	public static boolean firing = false;
	
	private float currentTurnSpeed = 0;
	private float turnSpeed = 0;
	
	private float distanceFromPlayer = 35;
	private float angleAroundPlayer = 0;
	
	private long scopeTimer = System.nanoTime();
	private long switchTimer = System.nanoTime();
	public long grenadeTimer = System.nanoTime();
	
	private Player player;

	public Camera(Player player){
		this.player = player;
		player.thirdperson = thirdperson;
		if(thirdperson){
			pitch = 10;
		}else{
			pitch = 0;
			
		}
	}
	
	public void move(){
		
		player.scoped = this.scoped;
		
		if(thirdperson){
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			calculateCameraPosition(horizontalDistance, verticalDistance);
			this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		}else{
			if(!scoped){
				position.x = player.getPosition().x + player.dx;
				position.y = player.getPosition().y + 6.6f - crouched * 2.5f;
				position.z = player.getPosition().z + player.dz;
				yaw =  180 - player.getRotY();
			}else{
				position.x = player.getPosition().x + player.dx + (float)(magnification * Math.cos(Math.toRadians(yaw - 90)) * Math.cos(Math.toRadians(-pitch)));
				position.y = player.getPosition().y + (float)(magnification * Math.sin(Math.toRadians(-pitch))) + 6.6f - crouched * 2.5f;
				position.z = player.getPosition().z + player.dz + (float)(magnification * Math.sin(Math.toRadians(yaw - 90)) * Math.cos(Math.toRadians(-pitch)));
				yaw =  180 - player.getRotY();
			}
		}
		//reset
		if(Mouse.isButtonDown(2)){
			if(thirdperson){
				distanceFromPlayer = 15;
				angleAroundPlayer = 0;
				pitch = 10;
			}
		}
		/*
		//crouch
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			crouched = 1;
		}else{
			crouched = 0;
		}
		*/
		if(thirdperson) crouched = 0;
		
		if(!thirdperson){
			if(true){
				if(Mouse.isButtonDown(1)){
					if(scoped){
						if(((System.nanoTime() - scopeTimer)/1000000) >= 400){
							scoped = false;
							scopeTimer = System.nanoTime();
						}
				
					}else{
						if(((System.nanoTime() - scopeTimer)/1000000) >= 400){
							scoped = true;
							scopeTimer = System.nanoTime();
						}
					}
				}
			}
		}else{
			scoped = false;
		}
		if(player.activeslot != 1) scoped = false;
		
		if(!thirdperson){
		
			turnSpeed = player.sensitivity * Mouse.getDY();
			currentTurnSpeed = turnSpeed;
			
			pitch -= currentTurnSpeed * DisplayManager.getFrameTimeSeconds();
			if(pitch % 360 > 90) pitch = 90;
			if (pitch % 360 < -90) pitch = -90;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_V)){
			if(((System.nanoTime() - switchTimer)/1000000) >= 400){
				if(thirdperson){
					thirdperson = false;
				}else{
					thirdperson = true;
					distanceFromPlayer = 15;
					angleAroundPlayer = 0;
					pitch = 0;
				}
				switchTimer = System.nanoTime();
			}
		}
		//throw grenade
		if(Mouse.isButtonDown(0)){
			if(!player.thrown){
				initrotY = this.yaw;
				initrotZ = this.pitch;
				grenadeTimer = System.nanoTime();
			}
			player.thrown = true;
			
		}
		
	}
	
	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * 0.03f;
		distanceFromPlayer -= zoomLevel;
		
		if(distanceFromPlayer < 3) distanceFromPlayer = 3;
		if(distanceFromPlayer > 100) distanceFromPlayer = 100;
		
	}
	
	private void calculatePitch(){
		float pitchChange = Mouse.getDY() * 2f;
		if(Mouse.isButtonDown(1)){
			pitch -= pitchChange * DisplayManager.getFrameTimeSeconds();
		}
	}
	
	private void calculateAngleAroundPlayer(){
		
		if(Mouse.isButtonDown(1)){
			float angleChange = Mouse.getDX() * 2f;
			angleAroundPlayer -= angleChange * DisplayManager.getFrameTimeSeconds();
		}
	}
	
	private float calculateHorizontalDistance(){
		return (float)(distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float)(distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float)(horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float)(horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		
		position.y = player.getPosition().y + verticDistance + 5.5f;
		
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	
}
