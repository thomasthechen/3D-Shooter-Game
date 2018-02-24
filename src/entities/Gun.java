package entities;

import javax.sound.sampled.Clip;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Gun extends Entity{
	
	private int RoF;
	private double recoil;
	private double spread;
	private int gunId;
	private Clip sound;
	private Clip reloadSound;
	
	public double getSpread() {
		return spread;
	}

	public void setSpread(double spread) {
		this.spread = spread;
	}

	public int getRoF() {
		return RoF;
	}

	public double getRecoil() {
		return recoil;
	}

	public int getGunId() {
		return gunId;
	}

	public Clip getSound() {
		return sound;
	}

	public void setRoF(int roF) {
		RoF = roF;
	}

	public void setRecoil(int recoil) {
		this.recoil = recoil;
	}

	public void setGunId(int gunId) {
		this.gunId = gunId;
	}

	public void setSound(Clip sound) {
		this.sound = sound;
	}
	
	
	//guns created in maingameloop; sound is set by player class
	public Gun(TexturedModel model, Vector3f position1, float rotX, float rotY,
			float rotZ, float scale, int gunId) {
		super(model, position1, rotX, rotY, rotZ, scale);
		
		//this.gunId = 2;
		if(gunId == 0){
			//M4A1
			RoF = 667;
			recoil = 0.25;
			spread = 0.1;
		}else if(gunId == 1){
			//M4A4
			RoF = 700;
			recoil = 0.35;
			spread = 0.3;
		}else if(gunId == 2){
			//AK47
			RoF = 600;
			recoil = 0.4;
			spread = 0.35;
		}else if(gunId == 3){
			//AWP
			RoF = 40;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 4){
			//Deagle .50 semi auto
			RoF = 100;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 5){
			//Shotgun
			RoF = 75;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 6){
			//MP5
			RoF = 850;
			recoil = 0.225;
			spread = 0.15;
		}else if(gunId == 7){
			//ACWR
			RoF = 900;
			recoil = 1;
			spread = 1;
		}else if(gunId == 8){
			//Hunting Rifle
			RoF = 55;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 9){
			//Mac 10
			RoF = 1000;
			recoil = 0.25;
			spread = 0.4;
		}else if(gunId == 10){
			//Galil
			RoF = 800;
			recoil = 0.35;
			spread = 0.45;
		}else if(gunId == 13){
			//G36C
			RoF = 800;
			recoil = 0.125;
			spread = 0.125;
		}else if(gunId == 15){
			//M249
			RoF = 800;
			recoil = 0.6;
			spread = 0.5;
		}
		
		
	}
	
	public void update(){
		
		if(gunId == 0){
			//M4A1
			RoF = 667;
			recoil = 0.2;
			spread = 0.1;
		}else if(gunId == 1){
			//M4A4
			RoF = 700;
			recoil = 0.3;
			spread = 0.3;
		}else if(gunId == 2){
			//AK47
			RoF = 600;
			recoil = 0.35;
			spread = 0.35;
		}else if(gunId == 3){
			//AWP
			RoF = 40;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 4){
			//Deagle .50 semi auto
			RoF = 100;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 5){
			//Shotgun
			RoF = 75;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 6){
			//MP5
			RoF = 850;
			recoil = 0.175;
			spread = 0.15;
		}else if(gunId == 7){
			//ACWR
			RoF = 900;
			recoil = 1;
			spread = 1;
		}else if(gunId == 8){
			//Hunting Rifle
			RoF = 55;
			recoil = 0;
			spread = 0.0;
		}else if(gunId == 9){
			//Mac 10
			RoF = 1000;
			recoil = 0.2;
			spread = 0.4;
		}else if(gunId == 10){
			//Galil
			RoF = 800;
			recoil = 0.3;
			spread = 0.45;	
		}else if(gunId == 13){
			//G36C
			RoF = 800;
			recoil = 0.1;
			spread = 0.125;
		}else if(gunId == 15){
			//M249
			RoF = 800;
			recoil = 0.4;
			spread = 0.4;
		}
	}

	public Clip getReloadSound() {
		return reloadSound;
	}

	public void setReloadSound(Clip reloadSound) {
		this.reloadSound = reloadSound;
	}
	
	
	
}
