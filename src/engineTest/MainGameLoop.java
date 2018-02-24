package engineTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Enemy;
import entities.Entity;
import entities.Grenade;
import entities.Gun;
import entities.Light;
import entities.Player;
import entities.Wall;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.CollisionDetector;

public class MainGameLoop {
	
	 /*
	 * TO DO:
	 * 1. fix box collision detection
	 * 2. melee
	 * 3. game modes: zombies coop with bots, siege/defense, ctf;
	 */
	
	//bullets
	public static List<Entity> allBullets = new ArrayList<Entity>();
	//grenades
	public static List<Grenade> allGrenades = new ArrayList<Grenade>();
	//explosions
	//public static List<Entity> allExplosions = new ArrayList<Entity>();
	//enemies
	public static List<Enemy> allTerrorists = new ArrayList<Enemy>();
	//guns
	public static List<Gun> allGuns = new ArrayList<Gun>();
	
	public static Loader loader;
	
	public static boolean scoped = false;
	
	public static void main(String[] args) {
		
		loader = new Loader();
		DisplayManager.createDisplay();
		TextMaster.init(loader);
		
		FontType font = new FontType(loader.loadTexture("arial"), new File("res/arial.fnt"));
		
		boolean openBuyMenu = false;
		boolean assaultrifles = false;
		boolean sniperrifles = false;
		boolean shotguns = false;
		boolean pistols = false;
		boolean smgs = false;
		boolean gameOver = false;
		
		long openTimer = System.nanoTime();
		long chooseTimer = System.nanoTime();
		long spawnTimer = System.nanoTime();
		long healTimer = System.nanoTime();
		long reloadTimer = System.nanoTime();
		long buildTimer = System.nanoTime();
		long burstresettimer = System.nanoTime();
		long recoiltimer = System.nanoTime();
		long animtimer = System.nanoTime();
		long grenadeCooldown = System.nanoTime();
		int ClipAmmo = 20;
		int MaxClipAmmo = 20;
		float counter = 0;
		int roundCounter = 0;
		float deviation = 0;
		float recoil = 0;
		int kills = 0;
		int oldkills = 0;
		//id 1 = green hills
		//id 2 = airstrip
		int mapid = 1;
		boolean added = false;
		float[] houseXs = {700, 700, -700};
		float[] houseZs = {700, -700, -700};
		
		long burstTimer = System.nanoTime();
		long counterTimer = System.nanoTime();
		float magnification = 0;
		
		//Texture Pack
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("sand"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		String st1 = "blendMap1";
		String st2 = "blendMap2";
		String st3 = "blendMap3";
		String st4 = "blendMap4";
		
		if(mapid == 1){
			 st1 = "blendMap1";
			 st2 = "blendMap2";
			 st3 = "blendMap3";
			 st4 = "blendMap4";
		}else if(mapid == 2){
			 st1 = "grassmap";
			 st2 = "grassmap";
			 st3 = "grassmap";
			 st4 = "grassmap";
		}
		
		TerrainTexture blendMap1 = new TerrainTexture(loader.loadTexture(st1));
		TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture(st2));
		TerrainTexture blendMap3 = new TerrainTexture(loader.loadTexture(st3));
		TerrainTexture blendMap4 = new TerrainTexture(loader.loadTexture(st4));
		
		RawModel wallModel = OBJLoader.loadObjModel("Wall", loader);
		Vector3f wallMax = new Vector3f(OBJLoader.maxX, OBJLoader.maxY, OBJLoader.maxZ);
		Vector3f wallMin = new Vector3f(OBJLoader.minX, OBJLoader.minY, OBJLoader.minZ);
	
		
		RawModel wallModel1 = OBJLoader.loadObjModel("Wall1", loader);
		Vector3f wallMax1 = new Vector3f(OBJLoader.maxX, OBJLoader.maxY, OBJLoader.maxZ);
		Vector3f wallMin1 = new Vector3f(OBJLoader.minX, OBJLoader.minY, OBJLoader.minZ);
		
		RawModel wallModel2 = OBJLoader.loadObjModel("Wall2", loader);
		Vector3f wallMax2 = new Vector3f(OBJLoader.maxX, OBJLoader.maxY, OBJLoader.maxZ);
		Vector3f wallMin2 = new Vector3f(OBJLoader.minX, OBJLoader.minY, OBJLoader.minZ);
	
		float buffer = 0.2f;

		//trees
		RawModel pinetreemodel = OBJLoader.loadObjModel("pine", loader);
		Vector3f pinetreeMax = new Vector3f(buffer *OBJLoader.maxX, buffer *OBJLoader.maxY, buffer *OBJLoader.maxZ);
		Vector3f pinetreeMin = new Vector3f(buffer *OBJLoader.minX, buffer *OBJLoader.minY, buffer *OBJLoader.minZ);
		buffer = 0.1f;
		RawModel treemodel = OBJLoader.loadObjModel("tree", loader);
		Vector3f treeMax = new Vector3f(buffer *OBJLoader.maxX, buffer *OBJLoader.maxY, buffer *OBJLoader.maxZ);
		Vector3f treeMin = new Vector3f(buffer *OBJLoader.minX, buffer *OBJLoader.minY, buffer *OBJLoader.minZ);
		
		//plants
		RawModel fernmodel = OBJLoader.loadObjModel("fern", loader);
		RawModel grassmodel = OBJLoader.loadObjModel("grassModel", loader);
		
		//weaponry
		RawModel BulletModel = OBJLoader.loadObjModel("Bullet", loader);
		Vector3f bulletMax = new Vector3f(OBJLoader.maxX, OBJLoader.maxY, OBJLoader.maxZ);
		Vector3f bulletMin = new Vector3f(OBJLoader.minX, OBJLoader.minY, OBJLoader.minZ);

		RawModel GrenadeModel = OBJLoader.loadObjModel("grenade1", loader);
		RawModel FireballModel = OBJLoader.loadObjModel("Fireball", loader);
		RawModel BombModel = OBJLoader.loadObjModel("Bomb", loader);

		RawModel M4Model = OBJLoader.loadObjModel("M4A1", loader);
		RawModel M4A4Model = OBJLoader.loadObjModel("M4A4", loader);
		RawModel AKModel = OBJLoader.loadObjModel("AK47", loader);
		RawModel AWPModel = OBJLoader.loadObjModel("AWP", loader);
		RawModel DeagleModel = OBJLoader.loadObjModel("Deagle", loader);
		RawModel ShotgunModel = OBJLoader.loadObjModel("Shotgun", loader);
		RawModel MP5Model = OBJLoader.loadObjModel("MP5", loader);
		RawModel ACWRModel = OBJLoader.loadObjModel("ACWR", loader);
		RawModel RifleModel = OBJLoader.loadObjModel("HuntingRifle", loader);
		RawModel MacModel = OBJLoader.loadObjModel("mac10", loader);
		RawModel GalilModel = OBJLoader.loadObjModel("Galil", loader);
		RawModel Autoshottymodel = OBJLoader.loadObjModel("Autoshotty", loader); 
		RawModel DoubleBarrel = OBJLoader.loadObjModel("DoubleBarrel", loader); 
		RawModel G36Model = OBJLoader.loadObjModel("G36", loader); 
		RawModel FiveSevenModel = OBJLoader.loadObjModel("FiveSeven", loader); 
		RawModel M249Model = OBJLoader.loadObjModel("M249", loader); 
		RawModel KnifeModel = OBJLoader.loadObjModel("Bayonet", loader); 
		//Characters
		RawModel SoldierModel = OBJLoader.loadObjModel("Soldier", loader);
		RawModel TerroristModel = OBJLoader.loadObjModel("Terrorist", loader);
		RawModel TerroristModel2 = OBJLoader.loadObjModel("Terrorist2", loader);
		RawModel TerroristModel3 = OBJLoader.loadObjModel("Terrorist3", loader);
		
		//textured weps
		TexturedModel texturedBullet = new TexturedModel(BulletModel, new ModelTexture(loader.loadTexture("gold")));
		TexturedModel texturedFireball = new TexturedModel(FireballModel, new ModelTexture(loader.loadTexture("Fire")));
		TexturedModel texturedBomb = new TexturedModel(BombModel, new ModelTexture(loader.loadTexture("Bomb")));
		TexturedModel texturedM4 = new TexturedModel(M4Model, new ModelTexture(loader.loadTexture("hot-rod")));
		TexturedModel texturedM4A4 = new TexturedModel(M4A4Model, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedAK = new TexturedModel(AKModel, new ModelTexture(loader.loadTexture("ak")));
		TexturedModel texturedAWP = new TexturedModel(AWPModel, new ModelTexture(loader.loadTexture("awp")));
		TexturedModel texturedDeagle = new TexturedModel(DeagleModel, new ModelTexture(loader.loadTexture("gold")));
		TexturedModel texturedShotgun = new TexturedModel(ShotgunModel, new ModelTexture(loader.loadTexture("shotgun")));
		TexturedModel texturedMP5 = new TexturedModel(MP5Model, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedACWR = new TexturedModel(ACWRModel, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedRifle = new TexturedModel(RifleModel, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedMac = new TexturedModel(MacModel, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedGalil = new TexturedModel(GalilModel, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedAutoshotty = new TexturedModel(Autoshottymodel, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedDoubleBarrel = new TexturedModel(DoubleBarrel, new ModelTexture(loader.loadTexture("DoubleBarrel")));
		TexturedModel texturedG36 = new TexturedModel(G36Model, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedFiveSeven = new TexturedModel(FiveSevenModel, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedM249 = new TexturedModel(M249Model, new ModelTexture(loader.loadTexture("gunmetal")));
		
		TexturedModel texturedKnife = new TexturedModel(KnifeModel, new ModelTexture(loader.loadTexture("gold")));
		
		//textured Characters
		TexturedModel texturedSoldier = new TexturedModel(SoldierModel, new ModelTexture(loader.loadTexture("Swat")));
		TexturedModel texturedTerrorist = new TexturedModel(TerroristModel, new ModelTexture(loader.loadTexture("Terrorist1")));
		TexturedModel texturedTerrorist2 = new TexturedModel(TerroristModel2, new ModelTexture(loader.loadTexture("Terrorist2")));
		TexturedModel texturedTerrorist3 = new TexturedModel(TerroristModel3, new ModelTexture(loader.loadTexture("Terrorist3")));
		
		TexturedModel texturedGrenade = new TexturedModel(GrenadeModel, new ModelTexture(loader.loadTexture("grenade1")));	
		
		//textured world
		TexturedModel texturedTree = new TexturedModel(treemodel, new ModelTexture(loader.loadTexture("treez")));
		TexturedModel texturedPineTree = new TexturedModel(pinetreemodel, new ModelTexture(loader.loadTexture("pine")));
		
		TexturedModel texturedFern = new TexturedModel(fernmodel, new ModelTexture(loader.loadTexture("fern")));
		TexturedModel texturedGrass = new TexturedModel(grassmodel, new ModelTexture(loader.loadTexture("tallGrass")));
		
		//textured walls
		TexturedModel texturedWallBlock = new TexturedModel(wallModel, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedWallPanel = new TexturedModel(wallModel1, new ModelTexture(loader.loadTexture("gunmetal")));
		TexturedModel texturedWallFlatPanel = new TexturedModel(wallModel2, new ModelTexture(loader.loadTexture("gunmetal")));
		
		
		
		//transparency
		texturedGrass.getTexture().setHasTransparency(true);
		texturedFern.getTexture().setHasTransparency(true);
		texturedGrass.getTexture().setUseFakeLighting(true);
		
		//shininess and reflectivity
		ModelTexture M4Tex = texturedM4.getTexture();
		M4Tex.setShineDamper(7);
		M4Tex.setReflectivity(1);
		
		ModelTexture M249Tex = texturedM249.getTexture();
		M249Tex.setShineDamper(5);
		M249Tex.setReflectivity(1);

		ModelTexture knifetex = texturedGrenade.getTexture();
		knifetex.setShineDamper(1);
		knifetex.setReflectivity(1);
		
		ModelTexture wall1 = texturedWallBlock.getTexture();
		wall1.setShineDamper(10);
		wall1.setReflectivity(1f);
		
		ModelTexture wall2 = texturedWallPanel.getTexture();
		wall2.setShineDamper(10);
		wall2.setReflectivity(1f);
		
		ModelTexture wall3 = texturedWallFlatPanel.getTexture();
		wall3.setShineDamper(10);
		wall3.setReflectivity(1f);
		
		ModelTexture grenadetex = texturedGrenade.getTexture();
		grenadetex.setShineDamper(10);
		grenadetex.setReflectivity(1);
		
		ModelTexture MacTex = texturedMac.getTexture();
		MacTex.setShineDamper(10);
		MacTex.setReflectivity(1);
		
		ModelTexture GalilTex = texturedGalil.getTexture();
		GalilTex.setShineDamper(10);
		GalilTex.setReflectivity(1);
		
		ModelTexture AutoshotTex = texturedAutoshotty.getTexture();
		AutoshotTex.setShineDamper(10);
		AutoshotTex.setReflectivity(1);
		
		ModelTexture DoubleBarrelTex = texturedDoubleBarrel.getTexture();
		DoubleBarrelTex.setShineDamper(10);
		DoubleBarrelTex.setReflectivity(1);
		
		ModelTexture G36Tex = texturedG36.getTexture();
		G36Tex.setShineDamper(5);
		G36Tex.setReflectivity(1);
		
		ModelTexture FiveSevenTex = texturedFiveSeven.getTexture();
		FiveSevenTex.setShineDamper(5);
		FiveSevenTex.setReflectivity(1);

		ModelTexture MP5Tex = texturedMP5.getTexture();
		MP5Tex.setShineDamper(5);
		MP5Tex.setReflectivity(1);
	
		ModelTexture M4A4Tex = texturedM4A4.getTexture();
		M4A4Tex.setShineDamper(5);
		M4A4Tex.setReflectivity(1);
		
		ModelTexture AKTex = texturedAK.getTexture();
		AKTex.setShineDamper(3);
		AKTex.setReflectivity(1);
		
		ModelTexture TerroristTex = texturedTerrorist.getTexture();
		TerroristTex.setShineDamper(10);
		TerroristTex.setReflectivity(1);
		
		ModelTexture TerroristTex2 = texturedTerrorist2.getTexture();
		TerroristTex2.setShineDamper(10);
		TerroristTex2.setReflectivity(1);
		
		ModelTexture TerroristTex3 = texturedTerrorist3.getTexture();
		TerroristTex3.setShineDamper(10);
		TerroristTex3.setReflectivity(1);
		
		ModelTexture SoldierTex = texturedSoldier.getTexture();
		SoldierTex.setShineDamper(10);
		SoldierTex.setReflectivity(1);
		
		ModelTexture AWPTex = texturedAWP.getTexture();
		AWPTex.setShineDamper(10);
		AWPTex.setReflectivity(1);
		
		ModelTexture DeagleTex = texturedDeagle.getTexture();
		DeagleTex.setShineDamper(5);
		DeagleTex.setReflectivity(1);
		
		ModelTexture ShotgunTex = texturedShotgun.getTexture();
		ShotgunTex.setShineDamper(10);
		ShotgunTex.setReflectivity(1);
		
		ModelTexture ACWRTex = texturedACWR.getTexture();
		ACWRTex.setShineDamper(10);
		ACWRTex.setReflectivity(1);
		
		ModelTexture RifleTex = texturedRifle.getTexture();
		RifleTex.setShineDamper(10);
		RifleTex.setReflectivity(1);
							
		ModelTexture BulletTex = texturedBullet.getTexture();
		BulletTex.setShineDamper(0.1f);
		BulletTex.setReflectivity(1);
		
		ModelTexture BombTex = texturedBomb.getTexture();
		BombTex.setShineDamper(10);
		BombTex.setReflectivity(1);
		
		ModelTexture explosiontexture = texturedFireball.getTexture();
		explosiontexture.setShineDamper(10);
		explosiontexture.setReflectivity(1);
		
		ModelTexture treetexture = texturedTree.getTexture();
		treetexture.setShineDamper(10);
		treetexture.setReflectivity(0.5f);
		
		ModelTexture pinetreetexture = texturedPineTree.getTexture();
		pinetreetexture.setShineDamper(10);
		pinetreetexture.setReflectivity(0.5f);
	
		ModelTexture ferntexture = texturedFern.getTexture();
		ferntexture.setShineDamper(10);
		ferntexture.setReflectivity(0.5f);
		
		ModelTexture tallgrasstexture = texturedGrass.getTexture();
		tallgrasstexture.setShineDamper(10);
		tallgrasstexture.setReflectivity(0.5f);
		
		Light light = new Light(new Vector3f(0,300,0), new Vector3f(1,1,1));
		//terrains
		String he1 = "Heightmap2";
		String he2 = "Heightmap1";
		String he3 = "Heightmap4";
		String he4 = "Heightmap3";
		
		if(mapid == 1){
			 he1 = "Heightmap2";
			 he2 = "Heightmap1";
			 he3 = "Heightmap4";
			 he4 = "Heightmap3";
			
		}else if(mapid == 2){
			 he1 = "heightmapflat";
			 he2 = "heightmapflat";
			 he3 = "heightmapflat";
			 he4 = "heightmapflat";
		}
		
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap2, he1);
		Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap1, he2);
		Terrain terrain3 = new Terrain(0, 0, loader, texturePack, blendMap4, he3);
		Terrain terrain4 = new Terrain(-1, 0, loader, texturePack, blendMap3, he4);
		
		//world
		List<Entity> allTrees = new ArrayList<Entity>();
		List<Entity> allPineTrees = new ArrayList<Entity>();
		
		List<Entity> allFerns = new ArrayList<Entity>();
		List<Entity> allTallGrass = new ArrayList<Entity>();
		
		List<Wall> Walls = new ArrayList<Wall>();
		
		
		Random random = new Random();
		long firingTimer = System.nanoTime();
		
		if(mapid == 1){
			
			
			for(int i = 0; i < 3; i++){
				
				float x = houseXs[i];
				float z = houseZs[i];
				float y = 0;
				
				float terraincoordX = (float) Math.floor(x/800);
				float terraincoordZ = (float) Math.floor(z/800);
				
				if(terraincoordX == 0 && terraincoordZ == 0) y = terrain3.getHeightOfTerrain(x, z) - 10;
				if(terraincoordX == 0 && terraincoordZ == -1) y = terrain.getHeightOfTerrain(x, z) - 10;
				if(terraincoordX == -1 && terraincoordZ == -1) y = terrain2.getHeightOfTerrain(x, z) - 10;
				if(terraincoordX == -1 && terraincoordZ == 0) y = terrain4.getHeightOfTerrain(x, z) - 10;
				
			}	
			
			
		for(int i = 0; i < 30; i++){
				float x = random.nextFloat() * 1000 - 500;
				float z = random.nextFloat() * 1000 - 500;
				float y = 0;
				
				float terraincoordX = (float) Math.floor(x/800);
				float terraincoordZ = (float) Math.floor(z/800);
				
				if(terraincoordX == 0 && terraincoordZ == 0) y = terrain3.getHeightOfTerrain(x, z);
				if(terraincoordX == 0 && terraincoordZ == -1) y = terrain.getHeightOfTerrain(x, z);
				if(terraincoordX == -1 && terraincoordZ == -1) y = terrain2.getHeightOfTerrain(x, z);
				if(terraincoordX == -1 && terraincoordZ == 0) y = terrain4.getHeightOfTerrain(x, z);
				
				allTrees.add(new Entity(texturedTree, new Vector3f(x,y,z), 0, 0, 0, 1.6f));
		}
			
		float[] forestXs = {400, -400, 0 };
		float[] forestZs = {400, 500, -300 };
		
		for(int i = 0; i < 3; i++){
			//3 pine forests
			float forestX = forestXs[i];
			float forestZ = forestZs[i];
			
			for(int j = 0; j < 70; j++){
				float dx = random.nextFloat() * 450 - 225;
			
				float dz = random.nextFloat() * 450 - 225;

				float y = 0;
			
				float terraincoordX = (float) Math.floor((forestX + dx)/800);
				float terraincoordZ = (float) Math.floor((forestZ + dz)/800);
			
				if(terraincoordX == 0 && terraincoordZ == 0) y = terrain3.getHeightOfTerrain(forestX + dx, forestZ + dz);
				if(terraincoordX == 0 && terraincoordZ == -1) y = terrain.getHeightOfTerrain(forestX + dx, forestZ + dz);
				if(terraincoordX == -1 && terraincoordZ == -1) y = terrain2.getHeightOfTerrain(forestX + dx, forestZ + dz);
				if(terraincoordX == -1 && terraincoordZ == 0) y = terrain4.getHeightOfTerrain(forestX + dx, forestZ + dz);
			
				allPineTrees.add(new Entity(texturedPineTree, new Vector3f(forestX + dx,y,forestZ + dz), 0, 0, 0, 2f));
		
			}
			
			for(int j = 0; j < 20; j++){
				float x = forestX + random.nextFloat() * 400 - 200;	
				float z = forestZ + random.nextFloat() * 400 - 200;
				float y = 0;
				
				float terraincoordX = (float) Math.floor(x/800);
				float terraincoordZ = (float) Math.floor(z/800);
				
				if(terraincoordX == 0 && terraincoordZ == 0) y = terrain3.getHeightOfTerrain(x, z);
				if(terraincoordX == 0 && terraincoordZ == -1) y = terrain.getHeightOfTerrain(x, z);
				if(terraincoordX == -1 && terraincoordZ == -1) y = terrain2.getHeightOfTerrain(x, z);
				if(terraincoordX == -1 && terraincoordZ == 0) y = terrain4.getHeightOfTerrain(x, z);
				
				allFerns.add(new Entity(texturedFern, new Vector3f(x,y,z), 0, 0, 0, 1.5f));
			}
			
		}
		
		}else if(mapid == 2){
		
		
		
		
		}
		
		Walls.add(new Wall(texturedWallFlatPanel, new Vector3f(0, 30, 0), 0, 0, 0, 1, wallMin2, wallMax2));
		
		
		if(mapid == 1){
		for(int i = 0; i < 0; i++){
			float x = random.nextFloat() * 400 - 200 + 300;
			
			float z = random.nextFloat() * 400 - 200 + 300;
			float y = 0;
			
			float terraincoordX = (float) Math.floor(x/800);
			float terraincoordZ = (float) Math.floor(z/800);
			
			if(terraincoordX == 0 && terraincoordZ == 0) y = terrain3.getHeightOfTerrain(x, z);
			if(terraincoordX == 0 && terraincoordZ == -1) y = terrain.getHeightOfTerrain(x, z);
			if(terraincoordX == -1 && terraincoordZ == -1) y = terrain2.getHeightOfTerrain(x, z);
			if(terraincoordX == -1 && terraincoordZ == 0) y = terrain4.getHeightOfTerrain(x, z);
			
			allTallGrass.add(new Entity(texturedGrass, new Vector3f(x,y,z), 0, 0, 0, 4));
		}
		}else if(mapid == 2){
			
		}
		//player's gun
		Gun playerGun = new Gun(texturedM4, new Vector3f(0, 7, 0), 0, 0, 0, 0.225f, 3);
		Entity playerKnife = new Entity(texturedKnife, new Vector3f(0, 7, 0), 0, 0, 0, 0.15f);
		
		//player
		Player player = new Player(texturedSoldier, new Vector3f(0, 45, 0), 0, 180, 0, 1.5f); 
		//0 = M4A1, 1 = M4A4, 2 = AK47, 3 = AWP, 4 = D-eagle 5 = shotgun, 6 = mp5, 7 = acw-r
		player.setGunId(0);
		
		Grenade grenadeViewmodel = new Grenade(texturedGrenade, new Vector3f(player.getPosition().x,player.getPosition().y + 6,player.getPosition().z), 0f, 0f, 0f, 0.05f, 0);
		
		for(int i = 0; i < 16; i++){
			float x = 0;
			float z = 0;
			
			float x1 = random.nextFloat() * 800 - 400;
			if(x1 >= 0){
				x = x1 + 200;
			}else{
				x = x1 - 200;
			}
			
			float z1 = random.nextFloat() * 800 - 400;
			if(z1 >= 0){
				z = z1 + 200;
			}else{
				z = z1 - 200;
			}
			float y = 0;
			Enemy e = new Enemy(texturedTerrorist, new Vector3f(x, y, z), 0, 180, 0, 0.61f);
			//id 1 = ak weilding terrorist
			e.id = 1;
			allTerrorists.add(e);
			
			if(player.getGunId() == 0) e.damageTaken = 28;
			if(player.getGunId() == 1) e.damageTaken = 30;
			if(player.getGunId() == 2) e.damageTaken = 35;
			if(player.getGunId() == 3) e.damageTaken = 100;
			if(player.getGunId() == 4) e.damageTaken = 50;
			if(player.getGunId() == 5) e.damageTaken = 20;
			if(player.getGunId() == 6) e.damageTaken = 20;
			if(player.getGunId() == 7) e.damageTaken = 35;
			if(player.getGunId() == 8) e.damageTaken = 50;
			if(player.getGunId() == 9) e.damageTaken = 20;
			if(player.getGunId() == 10) e.damageTaken = 25;
			if(player.getGunId() == 11) e.damageTaken = 25;
			if(player.getGunId() == 12) e.damageTaken = 25;
			if(player.getGunId() == 13) e.damageTaken = 20;
			if(player.getGunId() == 14) e.damageTaken = 25;
			if(player.getGunId() == 15) e.damageTaken = 25;
			
		}
		//deagle terrorist
		for(int i = 0; i < 8; i++){
			float x = 0;
			float z = 0;
			
			float x1 = random.nextFloat() * 800 - 400;
			if(x1 >= 0){
				x = x1 + 200;
			}else{
				x = x1 - 200;
			}
			
			float z1 = random.nextFloat() * 800 - 400;
			if(z1 >= 0){
				z = z1 + 200;
			}else{
				z = z1 - 200;
			}
			
			float y = 0;
			Enemy e = new Enemy(texturedTerrorist2, new Vector3f(x, y, z), 0, 180, 0, 0.89f);
			//id 2 = deagle weilding terrorist
			e.id = 2;
			e.clipAmmo = 7;
			e.MaxclipAmmo = 7;
			allTerrorists.add(e);
			
			if(player.getGunId() == 0) e.damageTaken = 28;
			if(player.getGunId() == 1) e.damageTaken = 30;
			if(player.getGunId() == 2) e.damageTaken = 35;
			if(player.getGunId() == 3) e.damageTaken = 100;
			if(player.getGunId() == 4) e.damageTaken = 50;
			if(player.getGunId() == 5) e.damageTaken = 20;
			if(player.getGunId() == 6) e.damageTaken = 20;
			if(player.getGunId() == 7) e.damageTaken = 35;
			if(player.getGunId() == 8) e.damageTaken = 50;
			if(player.getGunId() == 9) e.damageTaken = 20;
			if(player.getGunId() == 10) e.damageTaken = 25;
			if(player.getGunId() == 11) e.damageTaken = 25;
			if(player.getGunId() == 12) e.damageTaken = 25;
			if(player.getGunId() == 13) e.damageTaken = 20;
			if(player.getGunId() == 14) e.damageTaken = 25;
			if(player.getGunId() == 15) e.damageTaken = 25;
			
		}
		//6
		//suicide bomber terrorist
		for(int i = 0; i < 6; i++){
					float x = 0;
					float z = 0;
					
					float x1 = random.nextFloat() * 800 - 400;
					if(x1 >= 0){
						x = x1 + 200;
					}else{
						x = x1 - 200;
					}
					
					float z1 = random.nextFloat() * 800 - 400;
					if(z1 >= 0){
						z = z1 + 200;
					}else{
						z = z1 - 200;
					}
					
					float y = 0;
					
					Enemy e = new Enemy(texturedTerrorist3, new Vector3f(x, y, z), 0, 180, 0, 0.3f);
					//id 3 = suicide bomber terrorist
					e.id = 3;
					e.clipAmmo = 7;
					e.MaxclipAmmo = 7;
					allTerrorists.add(e);
					
					if(player.getGunId() == 0) e.damageTaken = 28;
					if(player.getGunId() == 1) e.damageTaken = 30;
					if(player.getGunId() == 2) e.damageTaken = 35;
					if(player.getGunId() == 3) e.damageTaken = 100;
					if(player.getGunId() == 4) e.damageTaken = 50;
					if(player.getGunId() == 5) e.damageTaken = 20;
					if(player.getGunId() == 6) e.damageTaken = 20;
					if(player.getGunId() == 7) e.damageTaken = 35;
					if(player.getGunId() == 8) e.damageTaken = 50;
					if(player.getGunId() == 9) e.damageTaken = 20;
					if(player.getGunId() == 10) e.damageTaken = 25;
					if(player.getGunId() == 11) e.damageTaken = 25;
					if(player.getGunId() == 12) e.damageTaken = 25;
					if(player.getGunId() == 13) e.damageTaken = 20;
					if(player.getGunId() == 14) e.damageTaken = 25;
					if(player.getGunId() == 15) e.damageTaken = 25;
					
		}
		
		for(int i = 0; i < 16; i++){
			//AK47
			Gun g = new Gun(texturedAK, new Vector3f(0,20,0), 0, 0, 0, 0.3f, 0);
			g.setGunId(2);
			allGuns.add(g);
		}
		
		for(int i = 0; i < 8; i++){
			//Deagle
			Gun g = new Gun(texturedDeagle, new Vector3f(0,20,0), 0, 0, 0, 1, 0);
			g.setGunId(4);
			allGuns.add(g);
		}
		
		for(int i = 0; i < 6; i++){
			//bomb
			Gun g = new Gun(texturedBomb, new Vector3f(0,20,0), 0, 0, 0, 1, 0);
			g.setGunId(3);
			allGuns.add(g);
		}
		

		MasterRenderer renderer = new MasterRenderer();
		//camera player
		Camera camera = new Camera(player);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture bar = new GuiTexture(loader.loadTexture("bar"), new Vector2f(-1f, -0.6f), new Vector2f(0.3f, 0.04f));
		GuiTexture backgroundbar = new GuiTexture(loader.loadTexture("backgroundbar"), new Vector2f(-1f, -0.6f), new Vector2f(0.31f, 0.06f));
		GuiTexture heart = new GuiTexture(loader.loadTexture("heart"), new Vector2f(-0.63f, -0.59f), new Vector2f(0.05f, 0.08f));
		GuiTexture bar2 = new GuiTexture(loader.loadTexture("gold"), new Vector2f(-1f, -0.92f), new Vector2f(0.3f, 0.04f));
		GuiTexture backgroundbar2 = new GuiTexture(loader.loadTexture("backgroundbar"), new Vector2f(-1f, -0.92f), new Vector2f(0.31f, 0.06f));
		GuiTexture ammo = new GuiTexture(loader.loadTexture("Ammo"), new Vector2f(-0.63f, -0.91f), new Vector2f(0.05f, 0.08f));
		GuiTexture bar3 = new GuiTexture(loader.loadTexture("blue"), new Vector2f(-1f, -0.76f), new Vector2f(0.3f, 0.04f));
		GuiTexture backgroundbar3 = new GuiTexture(loader.loadTexture("backgroundbar"), new Vector2f(-1f, -0.76f), new Vector2f(0.31f, 0.06f));
		GuiTexture block = new GuiTexture(loader.loadTexture("Wall"), new Vector2f(-0.63f, -0.75f), new Vector2f(0.05f, 0.08f));
		GuiTexture crosshair = new GuiTexture(loader.loadTexture("Crosshair"), new Vector2f(0f, 0f), new Vector2f(0.05f, 0.08f));
		GuiTexture sniperCrosshair = new GuiTexture(loader.loadTexture("sniperCross"), new Vector2f(0f, 2f), new Vector2f(1, 1f));
		GuiTexture hitmarker = new GuiTexture(loader.loadTexture("Crosshair"), new Vector2f(0f, 3f), new Vector2f(0.2f, 0.2f));
		GuiTexture GameOver = new GuiTexture(loader.loadTexture("gameOver"), new Vector2f(0, 0.4f), new Vector2f(0.5f, 0.5f));
		//buymenu stuff
		GuiTexture BuyMenu = new GuiTexture(loader.loadTexture("BuyMenu"), new Vector2f(0, -2f), new Vector2f(0.28125f, 0.5f));
		GuiTexture assaultrifle = new GuiTexture(loader.loadTexture("AssaultRiflesMenu"), new Vector2f(0, -2f), new Vector2f(0.28125f, 0.5f));
		GuiTexture smg = new GuiTexture(loader.loadTexture("SMGsMenu"), new Vector2f(0, -2f), new Vector2f(0.28125f, 0.5f));
		GuiTexture shotgun = new GuiTexture(loader.loadTexture("ShotgunsMenu"), new Vector2f(0, -2f), new Vector2f(0.28125f, 0.5f));
		GuiTexture sniper = new GuiTexture(loader.loadTexture("SniperMenu"), new Vector2f(0, -2f), new Vector2f(0.28125f, 0.5f));
		GuiTexture pistol = new GuiTexture(loader.loadTexture("PistolsMenu"), new Vector2f(0, -2f), new Vector2f(0.28125f, 0.5f));
		GuiTexture damageUp = new GuiTexture(loader.loadTexture("Damage"), new Vector2f(0, -2f), new Vector2f(0.5f, 0.5f));
		
		GuiTexture reloadingSymbol = new GuiTexture(loader.loadTexture("reloadingSymbol"), new Vector2f(-0.53f, -0.91f), new Vector2f(0.03515625f, 0.0625f));
		guis.add(backgroundbar);
		guis.add(bar);
		guis.add(heart);
		guis.add(backgroundbar2);
		guis.add(bar2);
		guis.add(ammo);
		guis.add(backgroundbar3);
		guis.add(bar3);
		guis.add(block);
		
		guis.add(crosshair);
		guis.add(sniperCrosshair);
		guis.add(damageUp);
		guis.add(BuyMenu);
		guis.add(hitmarker);
		guis.add(assaultrifle);
		guis.add(sniper);
		guis.add(shotgun);
		guis.add(smg);
		guis.add(pistol);
		
		
		//TEXT
		GUIText killmessage = new GUIText("KILL!", 1.5f, font, new Vector2f(0,-1), 1f, true);
		killmessage.setColour(1, 0, 0);
		GUIText spawnmessage = new GUIText("Fresh Spawn! 30 new Terrorists!", 1.5f, font, new Vector2f(0,-1), 1f, true);
		spawnmessage.setColour(0, 1, 0);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		if(player.getGunId() == 0){
			//M4A1
			playerGun.setGunId(0);
			playerGun.setModel(texturedM4);
			playerGun.setScale(0.1f);
			playerGun.setSound(player.Gunsound);
			ClipAmmo = 20;
			MaxClipAmmo = 20;
		}else if(player.getGunId() == 1){
			//M4A4
			playerGun.setGunId(1);
			playerGun.setModel(texturedM4A4);
			playerGun.setScale(0.1f);
			playerGun.setSound(player.Gunsound1);
			ClipAmmo = 30;
			MaxClipAmmo = 30;
		}else if(player.getGunId() == 2){
			//AK47
			playerGun.setGunId(2);
			playerGun.setModel(texturedAK);
			playerGun.setScale(0.3f);
			playerGun.setSound(player.Gunsound2);
			ClipAmmo = 30;
			MaxClipAmmo = 30;
		}else if(player.getGunId() == 3){
			//AWP
			playerGun.setGunId(3);
			playerGun.setModel(texturedAWP);
			playerGun.setScale(3.7f);
			playerGun.setSound(player.Gunsound3);
			ClipAmmo = 6;
			MaxClipAmmo = 6;
		}else if(player.getGunId() == 4){
			//Deagle
			playerGun.setGunId(4);
			playerGun.setModel(texturedDeagle);
			playerGun.setScale(1);
			playerGun.setSound(player.Gunsound4);
			ClipAmmo = 7;
			MaxClipAmmo = 7;
		}else if(player.getGunId() == 5){
			//Shotgun
			playerGun.setGunId(5);
			playerGun.setModel(texturedShotgun);
			playerGun.setScale(0.11f);
			playerGun.setSound(player.Gunsound5);
			ClipAmmo = 8;
			MaxClipAmmo = 8;
		}else if(player.getGunId() == 6){
			//mp5
			playerGun.setGunId(6);
			playerGun.setModel(texturedMP5);
			playerGun.setScale(0.3f);
			playerGun.setSound(player.Gunsound10);
			ClipAmmo = 35;
			MaxClipAmmo = 35;
		}else if(player.getGunId() == 7){
			//ACWR Burst fire
			playerGun.setGunId(7);
			playerGun.setModel(texturedACWR);
			playerGun.setScale(0.4f);
			playerGun.setSound(player.Gunsound6);
			ClipAmmo = 30;
			MaxClipAmmo = 30;
		}else if(player.getGunId() == 8){
			//Hunting Rifle
			playerGun.setGunId(8);
			playerGun.setModel(texturedRifle);
			playerGun.setScale(0.555f);
			playerGun.setSound(player.Gunsound7);
			ClipAmmo = 10;
			MaxClipAmmo = 10;
		}else if(player.getGunId() == 9){
			//mac 10
			playerGun.setGunId(9);
			playerGun.setModel(texturedMac);
			playerGun.setScale(0.08f);
			playerGun.setSound(player.Gunsound11);
			ClipAmmo = 30;
			MaxClipAmmo = 30;
		}else if(player.getGunId() == 10){
			//galil
			playerGun.setGunId(10);
			playerGun.setModel(texturedGalil);
			playerGun.setScale(0.95f);
			playerGun.setSound(player.Gunsound13);
			ClipAmmo = 35;
			MaxClipAmmo = 35;
		}else if(player.getGunId() == 11){
			//autoshotty
			playerGun.setGunId(11);
			playerGun.setModel(texturedAutoshotty);
			playerGun.setScale(1.55f);
			playerGun.setSound(player.Gunsound9);
			ClipAmmo = 5;
			MaxClipAmmo = 5;
		}else if(player.getGunId() == 12){
			//double barrel
			playerGun.setGunId(12);
			playerGun.setModel(texturedDoubleBarrel);
			playerGun.setScale(0.12f);
			playerGun.setSound(player.Gunsound14);
			ClipAmmo = 2;
			MaxClipAmmo = 2;
		}else if(player.getGunId() == 13){
			//g36c
			playerGun.setGunId(13);
			playerGun.setModel(texturedG36);
			playerGun.setScale(0.25f);
			playerGun.setSound(player.Gunsound12);
			ClipAmmo = 25;
			MaxClipAmmo = 25;
		}else if(player.getGunId() == 14){
			//five seven
			playerGun.setGunId(14);
			playerGun.setModel(texturedFiveSeven);
			playerGun.setScale(0.1f);
			playerGun.setSound(player.Gunsound8);
			ClipAmmo = 20;
			MaxClipAmmo = 20;
		}else if(player.getGunId() == 15){
			//M249
			playerGun.setGunId(15);
			playerGun.setModel(texturedM249);
			playerGun.setScale(0.25f);
			playerGun.setSound(player.Gunsound1);
			ClipAmmo = 100;
			MaxClipAmmo = 100;
		}
		CollisionDetector cd = new CollisionDetector(player);

		while(!Display.isCloseRequested()){
			player.openBuy = openBuyMenu;
			
			
			//count fps
			counter ++;
			if(((System.nanoTime() - counterTimer)/1000000) >= 1000){
				//System.out.println(counter + " fps");
				counter = 0;
				counterTimer = System.nanoTime();
			}

			if(camera.scoped){
				scoped = true;
			}else{
				scoped = false;
			}
			
			//check gameOver
			if(player.getHealth() <= 0 && gameOver == false){
				guis.add(GameOver);
				gameOver = true;
			}
			//no more than 40 terrorists
			if(allTerrorists.size() <= 10){
				//spawn 30 enemies every 5 minutes
				if(((System.nanoTime() - spawnTimer)/1000000) >= 300000){
				
					for(int i = 0; i < 20; i++){
						float x = random.nextFloat() * 1000 - 500;
						float y = 0;
						float z = random.nextFloat() * 1000 - 500;
						Enemy e = new Enemy(texturedTerrorist, new Vector3f(x, y, z), 0, 180, 0, 0.61f);
						e.id = 1;
						allTerrorists.add(e);
					
						if(player.getGunId() == 0) e.damageTaken = 28;
						if(player.getGunId() == 1) e.damageTaken = 30;
						if(player.getGunId() == 2) e.damageTaken = 35;
						if(player.getGunId() == 3) e.damageTaken = 100;
						if(player.getGunId() == 4) e.damageTaken = 50;
						if(player.getGunId() == 5) e.damageTaken = 20;
						if(player.getGunId() == 6) e.damageTaken = 20;
						if(player.getGunId() == 7) e.damageTaken = 35;
						if(player.getGunId() == 8) e.damageTaken = 50;
						if(player.getGunId() == 9) e.damageTaken = 20;
						if(player.getGunId() == 10) e.damageTaken = 25;
						if(player.getGunId() == 11) e.damageTaken = 25;
						if(player.getGunId() == 12) e.damageTaken = 25;
						if(player.getGunId() == 13) e.damageTaken = 20;
						if(player.getGunId() == 14) e.damageTaken = 25;
						if(player.getGunId() == 15) e.damageTaken = 25;
					}
			
				
					for(int i = 0; i < 10; i++){
						float x = random.nextFloat() * 1000 - 500;
						float y = 0;
						float z = random.nextFloat() * 1000 - 500;
						Enemy e = new Enemy(texturedTerrorist2, new Vector3f(x, y, z), 0, 180, 0, 0.89f);
						e.id = 2;
						e.clipAmmo = 7;
						e.MaxclipAmmo = 7;
						allTerrorists.add(e);
					
						if(player.getGunId() == 0) e.damageTaken = 28;
						if(player.getGunId() == 1) e.damageTaken = 30;
						if(player.getGunId() == 2) e.damageTaken = 35;
						if(player.getGunId() == 3) e.damageTaken = 100;
						if(player.getGunId() == 4) e.damageTaken = 50;
						if(player.getGunId() == 5) e.damageTaken = 20;
						if(player.getGunId() == 6) e.damageTaken = 20;
						if(player.getGunId() == 7) e.damageTaken = 35;
						if(player.getGunId() == 8) e.damageTaken = 50;
						if(player.getGunId() == 9) e.damageTaken = 20;
						if(player.getGunId() == 10) e.damageTaken = 25;
						if(player.getGunId() == 11) e.damageTaken = 25;
						if(player.getGunId() == 12) e.damageTaken = 25;
						if(player.getGunId() == 13) e.damageTaken = 20;
						if(player.getGunId() == 14) e.damageTaken = 25;
						if(player.getGunId() == 15) e.damageTaken = 25;
					}
				
					for(int i = 0; i < 20; i++){
						//AK47
						Gun g = new Gun(texturedAK, new Vector3f(0,20,0), 0, 0, 0, 0.3f, 0);
						g.setGunId(2);
						allGuns.add(g);
					}
				
					for(int i = 0; i < 10; i++){
						//deagle
						Gun g = new Gun(texturedDeagle, new Vector3f(0,20,0), 0, 0, 0, 1f, 0);
						g.setGunId(4);
						allGuns.add(g);
					}
				
					spawnmessage.setPosition(new Vector2f(0, 0.3f));
					System.out.println("Spawned 30 new Terrorists!");
					System.out.println(allTerrorists.size() + "Terrorists remain!");
					spawnTimer = System.nanoTime();
				}
			}
			
			if(((System.nanoTime() - spawnTimer)/1000000) >= 2500){
				spawnmessage.setPosition(new Vector2f(0, -1));
			}
			
			//end game
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				System.exit(0);
			}
			//Every 1/4 second, player can place a block
			if(Keyboard.isKeyDown(Keyboard.KEY_E)){
				if(((System.nanoTime() - buildTimer)/1000000) >= 250){
					Walls.add(new Wall(texturedWallPanel, new Vector3f(player.getPosition().x + (float)(Math.cos(Math.toRadians(camera.getYaw() - 90)) * Math.cos(Math.toRadians(camera.getPitch()))) * 15, player.getPosition().y + (float) (15 * Math.sin(Math.toRadians(-camera.getPitch()))) + 5, player.getPosition().z + (float) (15 * Math.sin(Math.toRadians(camera.getYaw() - 90)) * Math.cos(Math.toRadians(camera.getPitch())))), 0, 0, 0, 1f, wallMin1, wallMax1));
					buildTimer = System.nanoTime();
				}
			}
			
			float shootheight = 5.3f;
			if(camera.thirdperson){
				shootheight = 5.3f;
			}else{
				if(camera.crouched == 0){
					shootheight = 6.6f;
				}else{
					shootheight = 4.1f;
				}
				
			}
			
			if(!camera.scoped){
				crosshair.setPosition(new Vector2f(0,0));
				sniperCrosshair.setPosition(new Vector2f(0,2));
			}else{
				if(playerGun.getGunId() == 3 || playerGun.getGunId() == 7 || playerGun.getGunId() == 8){
					crosshair.setPosition(new Vector2f(0,2));
					sniperCrosshair.setPosition(new Vector2f(0,0));
				}
			}
			
			//shooting
			if(player.firing){
				if(ClipAmmo > 0){
					if(player.getGunId() != 4 && player.getGunId() != 7 && player.getGunId() != 11 && player.getGunId() != 12 && player.getGunId() != 14){
						
						
						
						//full auto
						if(((System.nanoTime() - firingTimer)/1000000) >= (60000/(playerGun.getRoF()))){
							
							float RotX = player.getRotX();
							float RotY = player.getRotY() - 90;
							float RotZ = player.getRotZ();
							
							animtimer = System.nanoTime();
							
							float x = player.getPosition().x + (float) (2 * Math.cos(Math.toRadians(-RotY)));
							float y = player.getPosition().y + shootheight;
							float z = player.getPosition().z + (float) (2 * Math.sin(Math.toRadians(-RotY)));
							
							if(!camera.thirdperson){
								RotZ = -camera.getPitch();
								
								 x = player.getPosition().x + (float)(magnification * Math.cos(Math.toRadians(RotZ)) * Math.cos(Math.toRadians(-RotY)));
								 y = player.getPosition().y + (float)(magnification * Math.sin(Math.toRadians(RotZ))) + shootheight;
								 z = player.getPosition().z + (float)(magnification * Math.cos(Math.toRadians(RotZ)) * Math.sin(Math.toRadians(-RotY)));
								
							}
							
							if(player.getGunId() == 5){
								//multi shot
								double spread = 3 * random.nextDouble() - 1.5f;
								Entity b1 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - (float)spread, RotZ - (float)spread, 0.12f);
								Entity b2 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + (float)spread, RotZ - (float)spread, 0.12f);
								Entity b3 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - (float)spread, RotZ + (float)spread, 0.12f);
								Entity b4 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + (float)spread, RotZ + (float)spread, 0.12f);
								Entity b5 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ, 0.12f);
								Entity b6 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ + 2 *  (float)spread, 0.12f);
								Entity b7 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - 2 *  (float)spread, RotZ, 0.12f);
								Entity b8 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + 2 *  (float)spread, RotZ, 0.12f);
								Entity b9 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ - 2 *  (float)spread, 0.12f);
								b1.id = 0;
								b2.id = 0;
								b3.id = 0;
								b4.id = 0;
								b5.id = 0;
								b6.id = 0;
								b7.id = 0;
								b8.id = 0;
								b9.id = 0;
								
								allBullets.add(b1);
								allBullets.add(b2);
								allBullets.add(b3);
								allBullets.add(b4);
								allBullets.add(b5);
								allBullets.add(b6);
								allBullets.add(b7);
								allBullets.add(b8);
								allBullets.add(b9);
								
							}else if(player.getGunId() == 8){
								double spread = 0.5 * random.nextDouble() - 0.25;
								double direction = 0.5 * random.nextDouble() - 0.25;
								Entity b1 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + (float) spread, RotZ + (float) direction, 0.12f);
								Entity b2 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - (float) spread, RotZ - (float) direction, 0.12f);
								b1.id = 0;
								b2.id = 0;
								
								allBullets.add(b1);
								allBullets.add(b2);
							}else{
								RotZ += (float) (deviation*Math.random() - deviation/2);
								RotY += (float) (deviation*Math.random() - deviation/2);
								float sidekick = 0;
								
								//single shot
								float recoiltrue = 0;
								if(camera.thirdperson){
									recoiltrue = 1;
								}else{
									recoiltrue = 0;
								}
								Entity bullet = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - sidekick, RotZ + recoiltrue * recoil, 0.12f);
								bullet.id = 0;
								allBullets.add(bullet);
							
							}
							
							if(!player.aimbot){
								if(camera.scoped){
									deviation += 0.7 * playerGun.getSpread();
								}else{
									deviation += playerGun.getSpread();
								}
								if(deviation > 4) deviation = 4f;
								recoil += 1.5 * playerGun.getRecoil();
							}
							
							if(playerGun.getSound().isRunning()) playerGun.getSound().stop();
							playerGun.getSound().setFramePosition(0);
							playerGun.getSound().start();
						
							ClipAmmo--;
							if(ClipAmmo == 0){ reloadTimer = System.nanoTime(); }
						
							firingTimer = System.nanoTime();
							//gunFire.setScale(0.15f);
							player.firstShot = false;
						}
					}else if(player.getGunId() == 4 || player.getGunId() == 11 || player.getGunId() == 12 || player.getGunId() == 14){
						//semi auto
						if(player.firstShot){
							float RotX = player.getRotX();
							float RotY = player.getRotY() - 90;
							float RotZ = player.getRotZ();
				
							float x = player.getPosition().x + (float) (2 * Math.cos(Math.toRadians(-RotY)));
							float y = player.getPosition().y + shootheight;
							float z = player.getPosition().z + (float) (2 * Math.sin(Math.toRadians(-RotY)));
							
							if(!camera.thirdperson){
								RotZ = -camera.getPitch();
								
								 x = player.getPosition().x + (float)(magnification * Math.cos(Math.toRadians(RotZ)) * Math.cos(Math.toRadians(-RotY)));
								 y = player.getPosition().y + (float)(magnification * Math.sin(Math.toRadians(RotZ))) + shootheight;
								 z = player.getPosition().z + (float)(magnification * Math.cos(Math.toRadians(RotZ)) * Math.sin(Math.toRadians(-RotY)));
								
							}
							
							if(player.getGunId() == 4 || player.getGunId() == 14){
								
								Entity bullet = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ, 0.12f);
								bullet.id = 0;
								allBullets.add(bullet);
								if(playerGun.getSound().isRunning()) playerGun.getSound().stop();
								playerGun.getSound().setFramePosition(0);
								playerGun.getSound().start();
						
								ClipAmmo--;
								if(ClipAmmo == 0){ reloadTimer = System.nanoTime(); }
							
								player.firstShot = false;
								
							}else if(player.getGunId() == 11){
								if(((System.nanoTime() - firingTimer)/1000000) >= 180){
								Entity bullet = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + 0.8f, RotZ + 0.8f, 0.12f);
								bullet.id = 0;
								Entity bullet2 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + 0.8f, RotZ - 0.8f, 0.12f);
								bullet2.id = 0;
								Entity bullet3 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - 0.8f, RotZ + 0.8f, 0.12f);
								bullet3.id = 0;
								Entity bullet4 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - 0.8f, RotZ - 0.8f, 0.12f);
								bullet4.id = 0;
								Entity bullet5 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ, 0.12f);
								bullet5.id = 0;
								allBullets.add(bullet);
								allBullets.add(bullet2);
								allBullets.add(bullet3);
								allBullets.add(bullet4);
								allBullets.add(bullet5);
								
								if(playerGun.getSound().isRunning()) playerGun.getSound().stop();
								playerGun.getSound().setFramePosition(0);
								playerGun.getSound().start();
						
								ClipAmmo--;
								if(ClipAmmo == 0){ reloadTimer = System.nanoTime(); }
							
								player.firstShot = false;
								firingTimer = System.nanoTime();
							}
							}else if(player.getGunId() == 12){
								
								Entity bullet = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + 0.8f, RotZ + 0.8f, 0.12f);
								bullet.id = 0;
								Entity bullet2 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + 0.8f, RotZ - 0.8f, 0.12f);
								bullet2.id = 0;
								Entity bullet3 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - 0.8f, RotZ + 0.8f, 0.12f);
								bullet3.id = 0;
								Entity bullet4 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - 0.8f, RotZ - 0.8f, 0.12f);
								bullet4.id = 0;
								Entity bullet5 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ, 0.12f);
								bullet5.id = 0;
							
								Entity bullet7 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY - 1, RotZ, 0.12f);
								bullet7.id = 0;
								Entity bullet8 = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY + 1, RotZ, 0.12f);
								bullet8.id = 0;
								allBullets.add(bullet);
								allBullets.add(bullet2);
								allBullets.add(bullet3);
								allBullets.add(bullet4);
								allBullets.add(bullet5);
								allBullets.add(bullet7);
								allBullets.add(bullet8);
								
								if(playerGun.getSound().isRunning()) playerGun.getSound().stop();
								playerGun.getSound().setFramePosition(0);
								playerGun.getSound().start();
						
								ClipAmmo--;
								if(ClipAmmo == 0){ reloadTimer = System.nanoTime(); }
							
								player.firstShot = false;
								
							}
						}
					}else if(player.getGunId() == 7){
						//burst fire
						if(roundCounter < 3){
							if(((System.nanoTime() - firingTimer)/1000000) >= (60000/(playerGun.getRoF()))){
								float RotX = player.getRotX();
								float RotY = player.getRotY() - 90;
								float RotZ = player.getRotZ();
			
								float x = player.getPosition().x + (float) (2f * Math.cos(Math.toRadians(-RotY)));
								float y = player.getPosition().y + shootheight;
								float z = player.getPosition().z + (float) (2f * Math.sin(Math.toRadians(-RotY)));
								
								if(!camera.thirdperson){
									RotZ = -camera.getPitch();
									
									 x = player.getPosition().x + (float)(magnification * Math.cos(Math.toRadians(RotZ)) * Math.cos(Math.toRadians(-RotY)));
									 y = player.getPosition().y + (float)(magnification * Math.sin(Math.toRadians(RotZ))) + shootheight;
									 z = player.getPosition().z + (float)(magnification * Math.cos(Math.toRadians(RotZ)) * Math.sin(Math.toRadians(-RotY)));
									
								}
								
								if(roundCounter == 2){
									deviation += playerGun.getSpread();
								}
								
								burstresettimer = System.nanoTime();
							
								RotZ += (float) (deviation*Math.random() - deviation/2);
								RotY += (float) (deviation*Math.random() - deviation/2);
								Entity bullet = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ, 0.12f);
								bullet.id = 0;
								allBullets.add(bullet);
								if(playerGun.getSound().isRunning()) playerGun.getSound().stop();
								playerGun.getSound().setFramePosition(0);
								playerGun.getSound().start();
								roundCounter++;
					
								ClipAmmo--;
								if(ClipAmmo == 0){ reloadTimer = System.nanoTime(); }
								burstTimer = System.nanoTime();
								firingTimer = System.nanoTime();
							}
						}else{
							if(((System.nanoTime() - burstTimer)/1000000) >= 220){
								roundCounter = 0;
								deviation = 0;
							}
						}
					}
				}
				
				if(!camera.thirdperson && ClipAmmo > 0){
					
					if(camera.scoped){
						if(((System.nanoTime() - recoiltimer)/1000000) >= 16){
								camera.setPitch(camera.getPitch() - 0.21f * (float) playerGun.getRecoil());
						}
					}else{
						if(((System.nanoTime() - recoiltimer)/1000000) >= 16){
								camera.setPitch(camera.getPitch() - 0.3f * (float) playerGun.getRecoil());
						
						}
					}
				}
				
			}else{
				deviation = 0;
				recoil = 0;
				player.firstShot = true;
				recoiltimer = System.nanoTime();
				animtimer = System.nanoTime();
			}
			
			if(((System.nanoTime() - burstresettimer)/1000000) >= 220){
				if(player.getGunId() == 7){
					roundCounter = 0;
					deviation = 0;
				}
			}
			
			//reload
			if(!player.firing){
				//auto
				if(ClipAmmo == 0){
					//2.5 second reload
					if(!player.reloadingSound.isRunning()){
						player.reloadingSound.setFramePosition(0);
						player.reloadingSound.start();
					}
					if(!added){
						guis.add(reloadingSymbol);
						added = true;
					}
					if(((System.nanoTime() - reloadTimer)/1000000) >= 2500){
						ClipAmmo = MaxClipAmmo;
						reloadTimer = System.nanoTime();
						guis.remove(reloadingSymbol);
						player.reloading = false;
						added = false;
					}
				
				}
				//manual
				else if(player.reloading && ClipAmmo != MaxClipAmmo){
					if(!player.reloadingSound.isRunning()){
						player.reloadingSound.setFramePosition(0);
						player.reloadingSound.start();
					}
					if(!added){
						guis.add(reloadingSymbol);
						added = true;
					}
					if(((System.nanoTime() - reloadTimer)/1000000) >= 2400){
						ClipAmmo = MaxClipAmmo;
						player.reloading = false;
						reloadTimer = System.nanoTime();
						guis.remove(reloadingSymbol);
						added = false;
					}
				
				}
			}else{
				reloadTimer = System.nanoTime();
				player.reloadingSound.stop();
			}
			if(ClipAmmo == MaxClipAmmo){
				player.reloading = false;
			}
			
			//movement
			player.calculateDxz();	
			camera.move();
			
			//knife positioning
			playerKnife.setPosition(new Vector3f(player.getPosition().x + player.dx + (float)(0.25 * Math.cos(Math.toRadians(-player.getRotY()))) + (float)(1.2 * Math.cos(Math.toRadians(90-player.getRotY()))), player.getPosition().y + 6.15f, player.getPosition().z + player.dz + (float)(0.25 * Math.sin(Math.toRadians(-player.getRotY()))) + (float)(1.2 * Math.sin(Math.toRadians(90-player.getRotY())))));
			playerKnife.setRotY(player.getRotY() + 25);
			playerKnife.setRotZ(player.getRotZ() + 180 - 5);
			
			grenadeViewmodel.setPosition(new Vector3f(player.getPosition().x + (float)Math.sin(Math.toRadians(-90 + player.getRotY())) + 2 * (float)Math.sin(Math.toRadians(player.getRotY())),player.getPosition().y + 5, player.getPosition().z + (float)Math.cos(Math.toRadians(-90 + player.getRotY())) + 2 * (float)Math.cos(Math.toRadians(player.getRotY()))));
			//gun positioning
			//move up controls displacement away from player
			double moveup = 0.5;
			if(playerGun.getGunId() == 0) moveup = -0.22;                                    
			if(playerGun.getGunId() == 1) moveup = -0.5;
			if(playerGun.getGunId() == 2) moveup = -0.52;
			if(playerGun.getGunId() == 3) moveup = 1.1;
			if(playerGun.getGunId() == 4) moveup = 1;
			if(playerGun.getGunId() == 5) moveup = -0.0;
			if(playerGun.getGunId() == 6) moveup = -0.4;
			if(playerGun.getGunId() == 7) moveup = 1.2;
			if(playerGun.getGunId() == 8) moveup = 0.6;
			if(playerGun.getGunId() == 9) moveup = 0.55;
			if(playerGun.getGunId() == 10) moveup = 1.2;
			if(playerGun.getGunId() == 11) moveup = 1.4;
			if(playerGun.getGunId() == 12) moveup = 0.95;
			if(playerGun.getGunId() == 13) moveup = 0.9;
			if(playerGun.getGunId() == 14) moveup = 0.77;
			if(playerGun.getGunId() == 15) moveup = 0.65;
			
			//moveside = 0 for ironsights, controls side to side, 0.7 sweet spot
			double moveside = 0.7;
			
			if(!camera.thirdperson){
				moveup += 0.35;
				moveside += 0.05;
				if(camera.scoped){
					if(playerGun.getGunId() == 3 || playerGun.getGunId() == 7 || playerGun.getGunId() == 8){
						moveup += 0.4;
						moveside -= 5.05;
					}else{
						moveup = 0.5;
						moveside = 0;
					}
				}else{	
					moveup += 0.4;
					moveside -= 0.12;
				}
				float ms = 60000/playerGun.getRoF();
				float scalar = 180/ms;
				if(player.firing && ClipAmmo > 0 && player.getGunId() != 4 && player.getGunId() != 7 && player.getGunId() != 11 && player.getGunId() != 14){
					moveup -= 0.3 *  Math.sin(Math.toRadians(scalar * ((System.nanoTime() - animtimer)/1000000)));
				}
				
			}
			
			playerGun.setPosition(new Vector3f(player.getPosition().x + player.dx - (float)((moveside) * Math.cos(Math.toRadians(-player.getRotY()))) + (float)((moveup) * Math.cos(Math.toRadians(90-player.getRotY()))), player.getPosition().y + 11, player.getPosition().z + player.dz - (float)((moveside) * Math.sin(Math.toRadians(-player.getRotY()))) + (float)((moveup) * Math.sin(Math.toRadians(90-player.getRotY())))));
			playerGun.setRotY(player.getRotY());
			//playerGun.setRotX(camera.getPitch() * (float)Math.cos(Math.toRadians(player.getRotY())));
			//playerGun.setRotZ(-camera.getPitch());
			
			float offY = 0;
			if(playerGun.getGunId() == 0){
				offY = 5.57f;	
			}else if(playerGun.getGunId() == 1){
				offY = 7;
			}else if(playerGun.getGunId() == 2){
				offY = 6.75f;
			}else if(playerGun.getGunId() == 3){
				offY = 6.55f;
			}else if(playerGun.getGunId() == 4){
				offY = 6f;
			}else if(playerGun.getGunId() == 5){
				offY = 6.15f;
			}else if(playerGun.getGunId() == 6){
				offY = 5.78f;
			}else if(playerGun.getGunId() == 7){
				offY = 5.9f;
			}else if(playerGun.getGunId() == 8){
				offY = 6.6f;
			}else if(playerGun.getGunId() == 9){
				offY = 6.2f;
			}else if(playerGun.getGunId() == 10){
				offY = 6.65f;
			}else if(playerGun.getGunId() == 11){
				offY = 5.9f;
			}else if(playerGun.getGunId() == 12){
				offY = 5.9f;
			}else if(playerGun.getGunId() == 13){
				offY = 5.9f;
			}else if(playerGun.getGunId() == 14){
				offY = 6.4f;
			}else if(playerGun.getGunId() == 15){
				offY = 5.65f;
			}
			if(camera.thirdperson){
				playerGun.setPosition(new Vector3f(playerGun.getPosition().x, player.getPosition().y + 11 - offY, playerGun.getPosition().z));	
			}else{
				playerGun.setPosition(new Vector3f(playerGun.getPosition().x, player.getPosition().y + 11 - offY + 0.96f, playerGun.getPosition().z));	
				
			}
			
			player.move(terrain, terrain2, terrain3, terrain4);
	
			//player tree collision
			for(Entity pineTree: allPineTrees){
				if(cd.EntityPlayerCollision(pineTree, pinetreeMin, pinetreeMax)){
					double angle = 180 + Math.toDegrees(Math.atan2((player.getPosition().x - player.dx - pineTree.getPosition().x), (player.getPosition().z - player.dz - pineTree.getPosition().z)));
					
					if(player.getPosition().y <= pineTree.getPosition().y + pineTree.getScale() * 15 && player.getPosition().y > pineTree.getPosition().y + pineTree.getScale() * 10){
						player.increasePosition(0, -player.dy, 0);
						player.setInAir(false);
						
						player.setPosition(new Vector3f(player.getPosition().x, pineTree.getPosition().y + 15 * pineTree.getScale(), player.getPosition().z));
						
					}else{
						
					
						
						if(angle % 360 > 45 && angle % 360 < 135){
							player.increasePosition(-player.dx, 0, 0);
							
						}else if(angle % 360 > 135 && angle % 360 < 225){
							player.increasePosition(0, 0, -player.dz);
							
						}else if(angle % 360 > 225 && angle % 360 < 315){
							player.increasePosition(-player.dx, 0, 0);
							
						}else if(angle % 360 > 315 || angle % 360 < 45){
							player.increasePosition(0, 0, -player.dz);
							
						}
					}
				}
			}
			
			for(Entity tree: allTrees){
				if(cd.EntityPlayerCollision(tree, treeMin, treeMax)){
					double angle = 180 + Math.toDegrees(Math.atan2((player.getPosition().x - player.dx - tree.getPosition().x), (player.getPosition().z - player.dz - tree.getPosition().z)));
					
					if(player.getPosition().y <= tree.getPosition().y + tree.getScale() * 15 && player.getPosition().y > tree.getPosition().y + tree.getScale() * 10){
						player.increasePosition(0, -player.dy, 0);
						player.setInAir(false);
						
						player.setPosition(new Vector3f(player.getPosition().x, tree.getPosition().y + 15 * tree.getScale(), player.getPosition().z));
						
					}else{
						
						
						if(angle % 360 > 45 && angle % 360 < 135){
							player.increasePosition(-player.dx, 0, 0);
							
						}else if(angle % 360 > 135 && angle % 360 < 225){
							player.increasePosition(0, 0, -player.dz);
							
						}else if(angle % 360 > 225 && angle % 360 < 315){
							player.increasePosition(-player.dx, 0, 0);
							
						}else if(angle % 360 > 315 || angle % 360 < 45){
							player.increasePosition(0, 0, -player.dz);
							
						}
					}
					break;
				}
			}
			
			for(Wall wall: Walls){
				if(cd.EntityPlayerCollision(wall, wall.getMin(), wall.getMax())){
					Vector3f boxCoords = wall.getPosition();
					float length = (wall.getMax().y - wall.getMin().y) * wall.getScale()/2;
					
					double angle = 180 + Math.toDegrees(Math.atan2((player.getPosition().x - player.dx - boxCoords.x), (player.getPosition().z - player.dz - boxCoords.z)));
					
					if(player.getPosition().y <= wall.getPosition().y + length && player.getPosition().y > wall.getPosition().y + length - 4){
						//player.increasePosition(0, -player.dy, 0);
						player.setInAir(false);
						
						player.setPosition(new Vector3f(player.getPosition().x, wall.getPosition().y + length, player.getPosition().z));
						
					}else{
						
						float theta = (float)Math.toDegrees(Math.atan((wall.getMax().x - wall.getMin().x)/(wall.getMax().z - wall.getMin().z)));
						
						if(wall.getRotY() % 180 == 0){	
						
						}else if(wall.getRotY() % 180 == 90){				
							theta = 90 - theta;
						}
						if(angle % 360 >= theta && angle % 360 < 180 - theta){
							player.increasePosition(-player.dx, 0, 0);
							
						}else if(angle % 360 >= 180 - theta && angle % 360 < 180 + theta){
							player.increasePosition(0, 0, -player.dz);
							
						}else if(angle % 360 >= 180 + theta && angle % 360 < 360 - theta){
							player.increasePosition(-player.dx, 0, 0);
						
						}else if(angle % 360 >= 360 - theta || angle % 360 < theta){
							player.increasePosition(0, 0, -player.dz);
						
						}
					}
				}
			}	
			
			if(camera.thirdperson){
				playerGun.setPosition(new Vector3f(playerGun.getPosition().x, player.getPosition().y + 11 - offY, playerGun.getPosition().z));	
			}
			
			
				float zz1 = houseZs[0];
				float xx1 = houseXs[0];
				
				float zz2 = houseZs[1];
				float xx2 = houseZs[1];
				
				float zz3 = houseZs[2];
				float xx3 = houseZs[2];
				
				
				if(player.getPosition().x > xx1 - 40 && player.getPosition().x < xx1 &&
				player.getPosition().z > zz1 - 70 && player.getPosition().z < zz1){
					
					if(player.getHealth() <= 100 - 5){
					//if inside one of buildings, heal
						if(((System.nanoTime() - healTimer)/1000000) >= 400){
							player.setHealth(player.getHealth() + 5);
							healTimer = System.nanoTime();
						}	
					}else{
						player.setHealth(100);
					}
					//open buy menu
					if(((System.nanoTime() - openTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_B)){
							if(!openBuyMenu){
								openBuyMenu = true;
								BuyMenu.setPosition(new Vector2f(0,0));
							}else{
								openBuyMenu = false;
								assaultrifles = false;
								smgs = false;
								shotguns = false;
								sniperrifles = false;
								pistols = false;
								BuyMenu.setPosition(new Vector2f(0,-2));
								assaultrifle.setPosition(new Vector2f(0,-2));
								smg.setPosition(new Vector2f(0,-2));
								shotgun.setPosition(new Vector2f(0,-2));
								sniper.setPosition(new Vector2f(0,-2));
								pistol.setPosition(new Vector2f(0,-2));
							}
						}
						openTimer = System.nanoTime();
					}
				//spawn platform
				}else if(player.getPosition().x > xx2 - 40 && player.getPosition().x < xx2 &&
				player.getPosition().z > zz2 - 70 && player.getPosition().z < zz2){
					
					if(player.getHealth() <= 100 - 5){
					//if inside one of buildings, heal
						if(((System.nanoTime() - healTimer)/1000000) >= 400){
							player.setHealth(player.getHealth() + 5);
							healTimer = System.nanoTime();
						}	
					}else{
						player.setHealth(100);
					}
					//open buy menu
					if(((System.nanoTime() - openTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_B)){
							if(!openBuyMenu){
								openBuyMenu = true;
								BuyMenu.setPosition(new Vector2f(0,0));
							}else{
								openBuyMenu = false;
								assaultrifles = false;
								smgs = false;
								shotguns = false;
								sniperrifles = false;
								pistols = false;
								BuyMenu.setPosition(new Vector2f(0,-2));
								assaultrifle.setPosition(new Vector2f(0,-2));
								smg.setPosition(new Vector2f(0,-2));
								shotgun.setPosition(new Vector2f(0,-2));
								sniper.setPosition(new Vector2f(0,-2));
								pistol.setPosition(new Vector2f(0,-2));
							}
						}
						openTimer = System.nanoTime();
					}
				}else if(player.getPosition().x > xx3 - 40 && player.getPosition().x < xx3 &&
				player.getPosition().z > zz3 - 70 && player.getPosition().z < zz3){
					
					if(player.getHealth() <= 100 - 5){
					//if inside one of buildings, heal
						if(((System.nanoTime() - healTimer)/1000000) >= 400){
							player.setHealth(player.getHealth() + 5);
							healTimer = System.nanoTime();
						}	
					}else{
						player.setHealth(100);
					}
					//open buy menu
					if(((System.nanoTime() - openTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_B)){
							if(!openBuyMenu){
								openBuyMenu = true;
								BuyMenu.setPosition(new Vector2f(0,0));
							}else{
								openBuyMenu = false;
								assaultrifles = false;
								smgs = false;
								shotguns = false;
								sniperrifles = false;
								pistols = false;
								BuyMenu.setPosition(new Vector2f(0,-2));
								assaultrifle.setPosition(new Vector2f(0,-2));
								smg.setPosition(new Vector2f(0,-2));
								shotgun.setPosition(new Vector2f(0,-2));
								sniper.setPosition(new Vector2f(0,-2));
								pistol.setPosition(new Vector2f(0,-2));
							}
						}
						openTimer = System.nanoTime();
					}
				}else if(player.getPosition().x > -15 && player.getPosition().x < 15 &&
				player.getPosition().z > -15 && player.getPosition().z < 15 && player.getPosition().y > 20){
					//open buy menu
					if(((System.nanoTime() - openTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_B)){
							if(!openBuyMenu){
								openBuyMenu = true;
								BuyMenu.setPosition(new Vector2f(0,0));
							}else{
								openBuyMenu = false;
								assaultrifles = false;
								smgs = false;
								shotguns = false;
								sniperrifles = false;
								pistols = false;
								BuyMenu.setPosition(new Vector2f(0,-2));
								assaultrifle.setPosition(new Vector2f(0,-2));
								smg.setPosition(new Vector2f(0,-2));
								shotgun.setPosition(new Vector2f(0,-2));
								sniper.setPosition(new Vector2f(0,-2));
								pistol.setPosition(new Vector2f(0,-2));
							}
						}
						openTimer = System.nanoTime();
					}
				}else{
					openBuyMenu = false;
					assaultrifles = false;
					smgs = false;
					shotguns = false;
					sniperrifles = false;
					pistols = false;
					BuyMenu.setPosition(new Vector2f(0,-2));
					assaultrifle.setPosition(new Vector2f(0,-2));
					smg.setPosition(new Vector2f(0,-2));
					shotgun.setPosition(new Vector2f(0,-2));
					sniper.setPosition(new Vector2f(0,-2));
					pistol.setPosition(new Vector2f(0,-2));
				}
			
			if(openBuyMenu){
				if(!assaultrifles && !smgs && !shotguns && !sniperrifles && !pistols){
				if(Keyboard.isKeyDown(Keyboard.KEY_1)){
					//assault rifles
					if(!assaultrifles){
						assaultrifle.setPosition(new Vector2f(0,0));
						chooseTimer = System.nanoTime();
					}
					assaultrifles = true;
				}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
					//SMGS
					if(!smgs){
						smg.setPosition(new Vector2f(0,0));
						chooseTimer = System.nanoTime();
					}
					smgs = true;
				}else if(Keyboard.isKeyDown(Keyboard.KEY_3)){
					//Shotguns
					if(!shotguns){
						shotgun.setPosition(new Vector2f(0,0));
						chooseTimer = System.nanoTime();
					}
					shotguns = true;
				}else if(Keyboard.isKeyDown(Keyboard.KEY_4)){
					//Sniper Rifles
					if(!sniperrifles){
						sniper.setPosition(new Vector2f(0,0));
						chooseTimer = System.nanoTime();
					}
					sniperrifles = true;
				}else if(Keyboard.isKeyDown(Keyboard.KEY_5)){
					//pistols
					if(!pistols){
						pistol.setPosition(new Vector2f(0,0));
						chooseTimer = System.nanoTime();
					}
					pistols = true;
				}
				}
				
				if(assaultrifles){
					
					if(((System.nanoTime() - chooseTimer)/1000000) >= 150){
					if(Keyboard.isKeyDown(Keyboard.KEY_1)){
						//AK
						player.setGunId(2);
						player.switchingGuns = true;
						openBuyMenu = false;
						assaultrifles = false;
						smgs = false;
						shotguns = false;
						sniperrifles = false;
						pistols = false;
						BuyMenu.setPosition(new Vector2f(0,-2));
						assaultrifle.setPosition(new Vector2f(0,-2));
						smg.setPosition(new Vector2f(0,-2));
						shotgun.setPosition(new Vector2f(0,-2));
						sniper.setPosition(new Vector2f(0,-2));
						pistol.setPosition(new Vector2f(0,-2));
						chooseTimer = System.nanoTime();
						camera.scoped = false;
						player.activeslot = 1;
					}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
						//M4A4
						player.setGunId(1);
						player.switchingGuns = true;
						openBuyMenu = false;
						assaultrifles = false;
						smgs = false;
						shotguns = false;
						sniperrifles = false;
						pistols = false;
						BuyMenu.setPosition(new Vector2f(0,-2));
						assaultrifle.setPosition(new Vector2f(0,-2));
						smg.setPosition(new Vector2f(0,-2));
						shotgun.setPosition(new Vector2f(0,-2));
						sniper.setPosition(new Vector2f(0,-2));
						pistol.setPosition(new Vector2f(0,-2));
						chooseTimer = System.nanoTime();
						camera.scoped = false;
						player.activeslot = 1;
					}else if(Keyboard.isKeyDown(Keyboard.KEY_3)){
						//ACWR
						player.setGunId(7);
						player.switchingGuns = true;
						openBuyMenu = false;
						assaultrifles = false;
						smgs = false;
						shotguns = false;
						sniperrifles = false;
						pistols = false;
						BuyMenu.setPosition(new Vector2f(0,-2));
						assaultrifle.setPosition(new Vector2f(0,-2));
						smg.setPosition(new Vector2f(0,-2));
						shotgun.setPosition(new Vector2f(0,-2));
						sniper.setPosition(new Vector2f(0,-2));
						pistol.setPosition(new Vector2f(0,-2));
						chooseTimer = System.nanoTime();
						camera.scoped = false;
						player.activeslot = 1;
					}else if(Keyboard.isKeyDown(Keyboard.KEY_4)){
						//M4A1
						player.setGunId(0);
						player.switchingGuns = true;
						openBuyMenu = false;
						assaultrifles = false;
						smgs = false;
						shotguns = false;
						sniperrifles = false;
						pistols = false;
						BuyMenu.setPosition(new Vector2f(0,-2));
						assaultrifle.setPosition(new Vector2f(0,-2));
						smg.setPosition(new Vector2f(0,-2));
						shotgun.setPosition(new Vector2f(0,-2));
						sniper.setPosition(new Vector2f(0,-2));
						pistol.setPosition(new Vector2f(0,-2));
						chooseTimer = System.nanoTime();
						camera.scoped = false;
						player.activeslot = 1;
					}else if(Keyboard.isKeyDown(Keyboard.KEY_5)){
						//galil
						player.setGunId(10);
						player.switchingGuns = true;
						openBuyMenu = false;
						assaultrifles = false;
						smgs = false;
						shotguns = false;
						sniperrifles = false;
						pistols = false;
						BuyMenu.setPosition(new Vector2f(0,-2));
						assaultrifle.setPosition(new Vector2f(0,-2));
						smg.setPosition(new Vector2f(0,-2));
						shotgun.setPosition(new Vector2f(0,-2));
						sniper.setPosition(new Vector2f(0,-2));
						pistol.setPosition(new Vector2f(0,-2));
						chooseTimer = System.nanoTime();
						camera.scoped = false;
						player.activeslot = 1;
					}
					}
				}else if(smgs){
					if(((System.nanoTime() - chooseTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_1)){
							//MP5
							player.setGunId(6);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
							//Mac 10
							player.setGunId(9);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}else if(Keyboard.isKeyDown(Keyboard.KEY_3)){
							//G36C
							player.setGunId(13);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}else if(Keyboard.isKeyDown(Keyboard.KEY_4)){
							//M249
							player.setGunId(15);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}
					}
				}else if(shotguns){
					if(((System.nanoTime() - chooseTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_1)){
							//Nova
							player.setGunId(5);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
							//M1014
							player.setGunId(11);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}else if(Keyboard.isKeyDown(Keyboard.KEY_3)){
							//Double Barrel
							player.setGunId(12);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}
					}
				}else if(sniperrifles){
					if(((System.nanoTime() - chooseTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_1)){
							//AWP
							player.setGunId(3);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
							//Hunting Rifle
							player.setGunId(8);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}
					}
				}else if(pistols){
					if(((System.nanoTime() - chooseTimer)/1000000) >= 150){
						if(Keyboard.isKeyDown(Keyboard.KEY_1)){
							//Deagle
							player.setGunId(4);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
							//Five Seven
							player.setGunId(14);
							player.switchingGuns = true;
							openBuyMenu = false;
							assaultrifles = false;
							smgs = false;
							shotguns = false;
							sniperrifles = false;
							pistols = false;
							BuyMenu.setPosition(new Vector2f(0,-2));
							assaultrifle.setPosition(new Vector2f(0,-2));
							smg.setPosition(new Vector2f(0,-2));
							shotgun.setPosition(new Vector2f(0,-2));
							sniper.setPosition(new Vector2f(0,-2));
							pistol.setPosition(new Vector2f(0,-2));
							chooseTimer = System.nanoTime();
							camera.scoped = false;
							player.activeslot = 1;
						}
					}
				}
			}
			
			if(player.thrown){
				if(((System.nanoTime() - grenadeCooldown)/1000000) >= 6000){
					Grenade g = new Grenade(texturedGrenade, new Vector3f(player.getPosition().x,player.getPosition().y + 5,player.getPosition().z), 0f, 0f, 0f, 0.05f, 2);
					allGrenades.add(g);
					player.thrown = false;
					grenadeCooldown = System.nanoTime();	
				}
				player.thrown = false;
			}
			
			for(Grenade g: allGrenades){
				//grenade trajectory
				float speed = g.speed;
				g.increasePosition((float)(speed * Math.cos(Math.toRadians(-camera.initrotZ)) * Math.cos(Math.toRadians(270 + camera.initrotY))), (float)(speed * Math.sin(Math.toRadians(-camera.initrotZ))), (float)(speed * Math.cos(Math.toRadians(-camera.initrotZ)) * Math.sin(Math.toRadians(270 + camera.initrotY))));
				
				g.dy += 150 * DisplayManager.getFrameTimeSeconds();
				g.increasePosition(0,-g.dy * DisplayManager.getFrameTimeSeconds(), 0);
				
				if(((System.nanoTime() - g.grenadeTimer)/1000000) >= 1500){
					
					//explode
					if(!player.Grenade.isRunning()){
						player.Grenade.setFramePosition(0);
						player.Grenade.start();
					}
					
					if(((System.nanoTime() - g.grenadeTimer)/1000000) >= 1700){
						
						for(Enemy e: allTerrorists){
							float difX = e.getPosition().x - g.getPosition().x;
							float difY = e.getPosition().y - g.getPosition().y;
							float difZ = e.getPosition().z - g.getPosition().z;
							float distance = (float) Math.sqrt(difX * difX + difY * difY + difZ * difZ);
							
							if(distance <= 25){
								float damage = 1250/distance;
								e.setHealth(e.getHealth() - (int) damage);
							}
							
						}
						g.explode();
					}
					
				}
				
				float terrainHeight = 0;
				
				float terraincoordX = (float) Math.floor(g.getPosition().x/800);
				float terraincoordZ = (float) Math.floor(g.getPosition().z/800);
				
				
				if(terraincoordX == 0 && terraincoordZ == -1) terrainHeight = terrain.getHeightOfTerrain(g.getPosition().x, g.getPosition().z);
				if(terraincoordX == -1 && terraincoordZ == -1) terrainHeight = terrain2.getHeightOfTerrain(g.getPosition().x, g.getPosition().z);
				if(terraincoordX == 0 && terraincoordZ == 0) terrainHeight = terrain3.getHeightOfTerrain(g.getPosition().x, g.getPosition().z);
				if(terraincoordX == -1 && terraincoordZ == 0) terrainHeight = terrain4.getHeightOfTerrain(g.getPosition().x, g.getPosition().z);
				
				if(g.getPosition().y < terrainHeight){
					g.setPosition(new Vector3f(g.getPosition().x, terrainHeight, g.getPosition().z));
					g.speed = g.speed/2;
					g.dy = -g.dy/2;
				}
				
			}
			
			if(player.hit){
				damageUp.setPosition(new Vector2f(0, 0.25f));
			}else{
				damageUp.setPosition(new Vector2f(0,-2));
				
			}
			
			if(player.switchingGuns){
				if(player.getGunId() == 0){
					//M4A1
					playerGun.setGunId(0);
					playerGun.setModel(texturedM4);
					playerGun.setScale(0.1f);
					playerGun.setSound(player.Gunsound);
					ClipAmmo = 20;
					MaxClipAmmo = 20;
					player.switchingGuns = false;
				}else if(player.getGunId() == 1){
					//M4A4
					playerGun.setGunId(1);
					playerGun.setModel(texturedM4A4);
					playerGun.setScale(0.1f);
					playerGun.setSound(player.Gunsound1);
					ClipAmmo = 30;
					MaxClipAmmo = 30;
					player.switchingGuns = false;
				}else if(player.getGunId() == 2){
					//AK47
					playerGun.setGunId(2);
					playerGun.setModel(texturedAK);
					playerGun.setScale(0.3f);
					playerGun.setSound(player.Gunsound2);
					ClipAmmo = 30;
					MaxClipAmmo = 30;
					player.switchingGuns = false;
				}else if(player.getGunId() == 3){
					//AWP
					playerGun.setGunId(3);
					playerGun.setModel(texturedAWP);
					playerGun.setScale(3.7f);
					playerGun.setSound(player.Gunsound3);
					ClipAmmo = 6;
					MaxClipAmmo = 6;
					player.switchingGuns = false;
				}else if(player.getGunId() == 4){
					//Deagle
					playerGun.setGunId(4);
					playerGun.setModel(texturedDeagle);
					playerGun.setScale(1);
					playerGun.setSound(player.Gunsound4);
					ClipAmmo = 7;
					MaxClipAmmo = 7;
					player.switchingGuns = false;
				}else if(player.getGunId() == 5){
					//Shotgun
					playerGun.setGunId(5);
					playerGun.setModel(texturedShotgun);
					playerGun.setScale(0.11f);
					playerGun.setSound(player.Gunsound5);
					ClipAmmo = 8;
					MaxClipAmmo = 8;
					player.switchingGuns = false;
				}else if(player.getGunId() == 6){
					//MP5
					playerGun.setGunId(6);
					playerGun.setModel(texturedMP5);
					playerGun.setScale(0.3f);
					playerGun.setSound(player.Gunsound10);
					ClipAmmo = 35;
					MaxClipAmmo = 35;
					player.switchingGuns = false;
				}else if(player.getGunId() == 7){
					//ACWR Burst fire
					playerGun.setGunId(7);
					playerGun.setModel(texturedACWR);
					playerGun.setScale(0.4f);
					playerGun.setSound(player.Gunsound6);
					ClipAmmo = 30;
					MaxClipAmmo = 30;
					player.switchingGuns = false;
				}else if(player.getGunId() == 8){
					//Hunting Rifle
					playerGun.setGunId(8);
					playerGun.setModel(texturedRifle);
					playerGun.setScale(0.555f);
					playerGun.setSound(player.Gunsound7);
					ClipAmmo = 10;
					MaxClipAmmo = 10;
					player.switchingGuns = false;
				}else if(player.getGunId() == 9){
					//mac 10
					playerGun.setGunId(9);
					playerGun.setModel(texturedMac);
					playerGun.setScale(0.08f);
					playerGun.setSound(player.Gunsound11);
					ClipAmmo = 30;
					MaxClipAmmo = 30;
					player.switchingGuns = false;
				}else if(player.getGunId() == 10){
					//galil
					playerGun.setGunId(10);
					playerGun.setModel(texturedGalil);
					playerGun.setScale(0.95f);
					playerGun.setSound(player.Gunsound13);
					ClipAmmo = 35;
					MaxClipAmmo = 35;
					player.switchingGuns = false;
				}else if(player.getGunId() == 11){
					//autoshotty
					playerGun.setGunId(11);
					playerGun.setModel(texturedAutoshotty);
					playerGun.setScale(1.55f);
					playerGun.setSound(player.Gunsound9);
					ClipAmmo = 5;
					MaxClipAmmo = 5;
					player.switchingGuns = false;
				}else if(player.getGunId() == 12){
					//double barrel
					playerGun.setGunId(12);
					playerGun.setModel(texturedDoubleBarrel);
					playerGun.setScale(0.12f);
					playerGun.setSound(player.Gunsound14);
					ClipAmmo = 2;
					MaxClipAmmo = 2;
					player.switchingGuns = false;
				}else if(player.getGunId() == 13){
					//g36c
					playerGun.setGunId(13);
					playerGun.setModel(texturedG36);
					playerGun.setScale(0.25f);
					playerGun.setSound(player.Gunsound12);
					ClipAmmo = 25;
					MaxClipAmmo = 25;
					player.switchingGuns = false;
				}else if(player.getGunId() == 14){
					//five seven
					playerGun.setGunId(14);
					playerGun.setModel(texturedFiveSeven);
					playerGun.setScale(0.1f);
					playerGun.setSound(player.Gunsound8);
					ClipAmmo = 20;
					MaxClipAmmo = 20;
					player.switchingGuns = false;
				}else if(player.getGunId() == 15){
					//M249
					playerGun.setGunId(15);
					playerGun.setModel(texturedM249);
					playerGun.setScale(0.25f);
					playerGun.setSound(player.Gunsound1);
					ClipAmmo = 100;
					MaxClipAmmo = 100;
					player.switchingGuns = false;
				}
				
				//reset enemy damage taken
				for(Enemy e: allTerrorists){
					if(player.getGunId() == 0) e.damageTaken = 28;
					if(player.getGunId() == 1) e.damageTaken = 30;
					if(player.getGunId() == 2) e.damageTaken = 35;
					if(player.getGunId() == 3) e.damageTaken = 100;
					if(player.getGunId() == 4) e.damageTaken = 50;
					if(player.getGunId() == 5) e.damageTaken = 20;
					if(player.getGunId() == 6) e.damageTaken = 20;
					if(player.getGunId() == 7) e.damageTaken = 35;
					if(player.getGunId() == 8) e.damageTaken = 50;
					if(player.getGunId() == 9) e.damageTaken = 20;
					if(player.getGunId() == 10) e.damageTaken = 25;
					if(player.getGunId() == 11) e.damageTaken = 25;
					if(player.getGunId() == 12) e.damageTaken = 25;
					if(player.getGunId() == 13) e.damageTaken = 20;
					if(player.getGunId() == 14) e.damageTaken = 25;
					if(player.getGunId() == 15) e.damageTaken = 25;
				}
				
			}
			
			bar.setScale(new Vector2f((float)(0.3f * player.getHealth()/100), 0.04f));
			bar2.setScale(new Vector2f((float)(0.3f * ClipAmmo/MaxClipAmmo), 0.04f));
			bar3.setScale(new Vector2f((float)(0.3f * Math.min((((System.nanoTime() - buildTimer)/1000000)), 1000)/1000), 0.04f));
			
			playerGun.increasePosition(0, -camera.crouched * 2.5f, 0);
			
			//lists of entities
			for(Entity tree: allTrees){
				renderer.processEntity(tree);
			}
			for(Entity pineTree: allPineTrees){
				renderer.processEntity(pineTree);
			}
			
			for(Entity furn: allFerns){
				renderer.processEntity(furn);
			}
			for(Entity tallgrass: allTallGrass){
				renderer.processEntity(tallgrass);
			}
			for(Gun gun: allGuns){
				renderer.processEntity(gun);
				gun.update();
			}
			for(Wall wall: Walls){
				renderer.processEntity(wall);
			}
			playerGun.update();
		
			float dx1 = 0;
			float dy1 = 0;
			float dz1 = 0;
			float RotY1 = 0;
			float RotZ1 = 0;
			int closestEnemyIndex = 0;
			double closestDistance = 10000;
			
			if(player.aimbot){
				
				for(int i = 0; i < allTerrorists.size(); i++){
					double distance = Math.sqrt((player.getPosition().x - allTerrorists.get(i).getPosition().x) * (player.getPosition().x - allTerrorists.get(i).getPosition().x)
					+ (player.getPosition().y - allTerrorists.get(i).getPosition().y) * (player.getPosition().y - allTerrorists.get(i).getPosition().y) + 
					(player.getPosition().z - allTerrorists.get(i).getPosition().z) * (player.getPosition().z - allTerrorists.get(i).getPosition().z));
					if(allTerrorists.get(i).getHealth() > 0){
						if(distance < closestDistance){
							closestDistance = distance;
							closestEnemyIndex = i;
						}
					}
					//System.out.println(closestEnemyIndex);
				}
				if(allTerrorists.size() > 0){
					dx1 = (float)(allTerrorists.get(closestEnemyIndex).getPosition().x - player.getPosition().x);
					dz1 = (float)(allTerrorists.get(closestEnemyIndex).getPosition().z - player.getPosition().z);
					dy1 = (float)(allTerrorists.get(closestEnemyIndex).getPosition().y - player.getPosition().y);
					RotY1 = (float) Math.toDegrees(Math.atan2(dx1, dz1));
					RotZ1 = (float) Math.toDegrees(-Math.atan2(dy1, (float)Math.sqrt(dx1 * dx1 + dz1 * dz1)));
					System.out.println(RotY1);
					camera.setPitch((RotZ1));
					player.setRotY((RotY1));
				}
				if(ClipAmmo > 0){
					player.firing = true;
				}
				
			}
			
			//enemies
			
			for(Enemy enemy: allTerrorists){
				
				renderer.processEntity(enemy);
				//renderer.processEntity(enemy.getHitbox());
				if(enemy.getHealth() > 0){
					float dx = enemy.getPosition().x - player.getPosition().x;
					float dy = enemy.getPosition().y - player.getPosition().y;
					float dz = enemy.getPosition().z - player.getPosition().z;
					//distance from player
					double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
					int range = 0;
					if(player.firing && ClipAmmo > 0){
						if(playerGun.getGunId() == 0){
							range = 1;
						}else if(playerGun.getGunId() == 1){
							range = 3;
						}else if(playerGun.getGunId() == 2){
							range = 3;
						}else if(playerGun.getGunId() == 3){
							range = 3;
						}else if(playerGun.getGunId() == 4){
							range = 1;
						}else if(playerGun.getGunId() == 5){
							range = 2;
						}else if(playerGun.getGunId() == 6){
							range = 2;
						}else if(playerGun.getGunId() == 7){
							range = 2;
						}else if(playerGun.getGunId() == 8){
							range = 2;
						}else if(playerGun.getGunId() == 9){
							range = 2;
						}else if(playerGun.getGunId() == 10){
							range = 3;
						}else if(playerGun.getGunId() == 11){
							range = 2;
						}else if(playerGun.getGunId() == 12){
							range = 2;
						}else if(playerGun.getGunId() == 13){
							range = 2;
						}else if(playerGun.getGunId() == 14){
							range = 1;
						}else if(playerGun.getGunId() == 15){
							range = 3;
						}
						
					}else{
						range = 0;
					}
					//detection range of enemies
					if(dist < 260 + 30 * range){
						enemy.lockedon = true;
					}
					if(dist > 400){
						enemy.lockedon = false;
					}
					//fix
					if(enemy.lockedon){
						float anglediff = enemy.getRotY() - (float)(180 + Math.toDegrees(Math.atan2(dx, dz)));
						
						if(anglediff < 0) anglediff += 360;
						if(anglediff > 360) anglediff -= 360;
						
						if(Math.abs(anglediff) > 8){
							if(anglediff < 180){
								enemy.setRotY(enemy.getRotY() - 7);	
								if(enemy.getRotY() < 0) enemy.setRotY(enemy.getRotY() + 360);
							}else if(anglediff > 180){
								enemy.setRotY(enemy.getRotY() + 7);	
								if(enemy.getRotY() > 360) enemy.setRotY(enemy.getRotY() - 360);
							}
						}else if(Math.abs(anglediff) > 0.5f){
							if(anglediff < 180){
								enemy.setRotY(enemy.getRotY() - 0.25f);
								if(enemy.getRotY() < 0) enemy.setRotY(enemy.getRotY() + 360);
							}else if(anglediff > 180){
								enemy.setRotY(enemy.getRotY() + 0.25f);	
								if(enemy.getRotY() > 360) enemy.setRotY(enemy.getRotY() - 360);
							}
						}
						if(dist > 90){
							enemy.sprint();
						}else{
							if(enemy.id == 3){
								//suicide bomber keeps running
								
								if(dist < 10){
									enemy.stop();
								}else{
									enemy.sprint();
								}
							}else{
								if(dist <= 90 && dist > 40){
									enemy.walk();
								}else if(dist < 40){
									enemy.stop();
								}
							}
							
							float horizdist = (float)(Math.sqrt(dx * dx + dz * dz));
							float dify = enemy.getPosition().y - player.getPosition().y;
							float pitch = (float)(Math.toDegrees(Math.atan2(dify, horizdist)));
							
							//shoot
							if(enemy.id == 1){
								if(((System.nanoTime() - enemy.firingTimer)/1000000) >= 100){
									if(enemy.clipAmmo > 0){
										float RotX = enemy.getRotX();
										float RotY = enemy.getRotY() - 90;
										float RotZ = enemy.getRotZ() - pitch;
									
										float x = enemy.getPosition().x + (float) (10 * Math.cos(Math.toRadians(-RotY)));
										float y = enemy.getPosition().y + 5;
										float z = enemy.getPosition().z + (float) (10 * Math.sin(Math.toRadians(-RotY)));
							
										Entity bullet = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ, 0.12f);
										bullet.earlyRemove = true;
										bullet.gunId = 2;
										bullet.id = 1;
										allBullets.add(bullet);
										if(player.Gunsound2.isRunning()) player.Gunsound2.stop();
										player.Gunsound2.setFramePosition(0);
										player.Gunsound2.start();
								
										enemy.firingTimer = System.nanoTime();
										
										enemy.clipAmmo--;
									}
								}
							}else if(enemy.id == 2){
								if(((System.nanoTime() - enemy.firingTimer)/1000000) >= 400){
									if(enemy.clipAmmo > 0){
										float RotX = enemy.getRotX();
										float RotY = enemy.getRotY() - 90;
										float RotZ = enemy.getRotZ() - pitch;
									
										float x = enemy.getPosition().x + (float) (10 * Math.cos(Math.toRadians(-RotY)));
										float y = enemy.getPosition().y + 5;
										float z = enemy.getPosition().z + (float) (10 * Math.sin(Math.toRadians(-RotY)));
							
									
										Entity bullet = new Entity(texturedBullet, new Vector3f(x,y,z), RotX, RotY, RotZ, 0.12f);
										bullet.earlyRemove = true;
										bullet.gunId = 4;
										bullet.id = 1;
										//2x damage
										allBullets.add(bullet);
										allBullets.add(bullet);
										if(player.Gunsound4.isRunning()) player.Gunsound4.stop();
										player.Gunsound4.setFramePosition(0);
										player.Gunsound4.start();
								
										
										enemy.firingTimer = System.nanoTime();
										
										enemy.clipAmmo--;
									}
								}
							}else if(enemy.id == 3){
								if(dist < 40){
									
									if(!player.Explosion.isRunning()){
										player.Explosion.setFramePosition(0);
										player.Explosion.start();
									}
									if(((System.nanoTime() - enemy.firingTimer)/1000000) >= 2700){
										
										//explode
										enemy.setHealth(0);
										int damage = (int) (2000/dist);
										if(damage < 0){
											damage = 0;
										}

										player.setHealth(player.getHealth() - damage);
										
									}	
								}else{
									enemy.firingTimer = System.nanoTime();
									player.Explosion.stop();
								}
							}	
						}
					}else{
						enemy.stop();
					}
					
					if(Math.sqrt(enemy.dx * enemy.dx + enemy.dz + enemy.dz) >= 1100){
						enemy.stop();
					}
					
					//assign guns
					int size = Math.min(allGuns.size(), allTerrorists.size());
					for(int i = 0; i < size; i++){
						
						if(allGuns.get(i).getGunId() == 2){
							double theta2 = -34;
							double h2 = 0.7f/Math.cos(Math.toRadians(theta2));
							allGuns.get(i).setPosition(new Vector3f(allTerrorists.get(i).getPosition().x - (float)(h2 * Math.cos(Math.toRadians(-theta2-allTerrorists.get(i).getRotY()))), allTerrorists.get(i).getPosition().y + 3.6f, allTerrorists.get(i).getPosition().z  - (float)(h2 * Math.sin(Math.toRadians(-theta2-allTerrorists.get(i).getRotY())))));
							allGuns.get(i).setRotZ(allTerrorists.get(i).getRotZ());
							allGuns.get(i).setRotX(allTerrorists.get(i).getRotX());
							allGuns.get(i).setRotY(allTerrorists.get(i).getRotY());
							
						}else if(allGuns.get(i).getGunId() == 4){
							double theta2 = 73;
							double h2 = 0.7f/Math.cos(Math.toRadians(theta2));
							allGuns.get(i).setPosition(new Vector3f(allTerrorists.get(i).getPosition().x - (float)(h2 * Math.cos(Math.toRadians(-theta2-allTerrorists.get(i).getRotY()))), allTerrorists.get(i).getPosition().y + 5f, allTerrorists.get(i).getPosition().z  - (float)(h2 * Math.sin(Math.toRadians(-theta2-allTerrorists.get(i).getRotY())))));
							allGuns.get(i).setRotZ(allTerrorists.get(i).getRotZ());
							allGuns.get(i).setRotX(allTerrorists.get(i).getRotX());
							allGuns.get(i).setRotY(allTerrorists.get(i).getRotY());
							
						}else if(allGuns.get(i).getGunId() == 3){
							double theta2 = -30;
							double h2 = 2f/Math.cos(Math.toRadians(theta2));
							allGuns.get(i).setPosition(new Vector3f(allTerrorists.get(i).getPosition().x - (float)(h2 * Math.cos(Math.toRadians(-theta2-allTerrorists.get(i).getRotY()))), allTerrorists.get(i).getPosition().y + 2f, allTerrorists.get(i).getPosition().z  - (float)(h2 * Math.sin(Math.toRadians(-theta2-allTerrorists.get(i).getRotY())))));
							allGuns.get(i).setRotZ(allTerrorists.get(i).getRotZ() + 90);
							allGuns.get(i).setRotX(allTerrorists.get(i).getRotX());
							allGuns.get(i).setRotY(allTerrorists.get(i).getRotY() + 90);
							
						}			
					}
				}else{
					if(kills == oldkills){
						kills += 1;
						killmessage.setPosition(new Vector2f(0, 0.1f));
					}
					
					if(!player.deathSound.isRunning()){
						player.deathSound.setFramePosition(0);
						player.deathSound.start();
						
					}
					if(((System.nanoTime() - enemy.deathTimer)/1000000) >= 1000 && enemy.deathTimer != 0){
						enemy.dead = true;
						oldkills += 1;
						killmessage.setPosition(new Vector2f(0, -1));
						
					}
				}
				enemy.move(terrain, terrain2, terrain3, terrain4);
				//collision detection
				for(Wall wall: Walls){
					if(cd.EntityEnemyCollision(wall, wall.getMin(), wall.getMax(), enemy)){
						Vector3f boxCoords = wall.getPosition();
						float length = (wall.getMax().y - wall.getMin().y) * wall.getScale();
						
						double angle = 180 + Math.toDegrees(Math.atan2((enemy.getPosition().x - enemy.dx - boxCoords.x), (enemy.getPosition().z - enemy.dz - boxCoords.z)));
						
						if(enemy.getPosition().y <= wall.getPosition().y + length && enemy.getPosition().y > wall.getPosition().y){
						
							enemy.setInAir(false);
							
							enemy.setPosition(new Vector3f(enemy.getPosition().x, wall.getPosition().y + length, enemy.getPosition().z));
							
						}else{
							
							float theta = (float)Math.toDegrees(Math.atan((wall.getMax().x - wall.getMin().x)/(wall.getMax().z - wall.getMin().z)));
							
							if(angle % 360 >= theta && angle % 360 < 180 - theta){
								enemy.increasePosition(-enemy.dx, 0, 0);
								
							}else if(angle % 360 >= 180 - theta && angle % 360 < 180 + theta){
								enemy.increasePosition(0, 0, -enemy.dz);
								
							}else if(angle % 360 >= 180 + theta && angle % 360 < 360 - theta){
								enemy.increasePosition(-enemy.dx, 0, 0);
								
							}else if(angle % 360 >= 360 - theta || angle % 360 < theta){
								enemy.increasePosition(0, 0, -enemy.dz);
								
							}	
						}
					}
				}	
				//enemy.move(terrain, terrain2, terrain3, terrain4);
				
			}

			for(int i = 0; i < allTerrorists.size(); i++){
				if(allTerrorists.get(i).dead){
					allTerrorists.remove(i);
					allGuns.remove(i);
					
				}
			}
			//processing bullets
			for(Entity bullet: allBullets){
				float speed = 1200 * DisplayManager.getFrameTimeSeconds();
				bullet.increasePosition((float)(speed * Math.cos(Math.toRadians(bullet.getRotZ())) * Math.cos(Math.toRadians(-bullet.getRotY()))), (float)(speed * Math.sin(Math.toRadians(bullet.getRotZ()))), (float)(speed * Math.cos(Math.toRadians(bullet.getRotZ())) * Math.sin(Math.toRadians(-bullet.getRotY()))));
				renderer.processEntity(bullet);
				bullet.collision = true;
			}
			//explosions
			for(Grenade g: allGrenades){
				renderer.processEntity(g);
			}
			
			for(Entity bullet: allBullets){
				for(Wall wall: Walls){
					if(cd.EntityEntityCollision(bullet, bulletMax, bulletMin, wall, wallMax, wallMin)){
						//remove if collision
						bullet.setRemove(true);
						wall.durability -= 2.5;
					}
					if(wall.durability <= 0){
						wall.setRemove(true);
					}
				}
			}
			
			for(int i = 0; i < Walls.size(); i++){
				Wall w = Walls.get(i);
				if(w.isRemove()) Walls.remove(i);
			}
			
			
			//removing bullets
			for(int i = 0; i < allBullets.size(); i++){
				Entity b = allBullets.get(i);
				float dx = b.getPosition().x - player.getPosition().x;
				float dy = b.getPosition().y - player.getPosition().y;
				float dz = b.getPosition().z - player.getPosition().z;
				//distance from player
				double gunRange = 525;
				double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
				//if too far, remove
				if(allBullets.get(i).earlyRemove){
					if(distance > 250){
						allBullets.get(i).setRemove(true);
					}
				}else{
					if(distance > gunRange){
						allBullets.get(i).setRemove(true);
					}
				}
				if(b.isRemove()) allBullets.remove(i);
			
			}
			
			
			//terrains
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processTerrain(terrain3);
			renderer.processTerrain(terrain4);
			
			if(camera.thirdperson){
				renderer.processEntity(player);
				//renderer.processEntity(player.getHitbox());
			}
			
			for(Grenade g: allGrenades){
				renderer.processEntity(g);
				
			}
			for(int i = 0; i < allGrenades.size(); i++){
				if(allGrenades.get(i).exploded){
					allGrenades.remove(i);
				}
			}
			
			if(player.activeslot == 1){
				renderer.processEntity(playerGun);
			}else if(player.activeslot == 2){
				renderer.processEntity(playerKnife);
			}else if(player.activeslot == 3){
				if(!player.thrown){
					if(((System.nanoTime() - grenadeCooldown)/1000000) >= 6000){
						renderer.processEntity(grenadeViewmodel);
					}
				}	
			}
			
			renderer.render(light, camera);
			
			guiRenderer.render(guis);
			
			TextMaster.render();
			
			DisplayManager.updateDisplay();
		}
		
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}	
}