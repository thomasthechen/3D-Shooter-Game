package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;
import engineTest.MainGameLoop;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MasterRenderer {
	
	private static final float FOV = 90;
	private static final float FOVSCOPED = 50;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	//clear skies: 0.2f, 1f, 0.6f, 1
	//fog: 0.5f, 0.5f, 0.5f, 1
	//night: 0, 0, 0
	private static final float RED = 0.2f;
	private static final float BLUE = 1f;
	private static final float GREEN = 0.6f;
	
	private Matrix4f projectionMatrix;
	private Matrix4f projectionMatrixScoped;
	
	private StaticShader shader = new StaticShader();
	private StaticShader shader2 = new StaticShader();
	private EntityRenderer renderer;
	private EntityRenderer renderer2;
	boolean scoped = MainGameLoop.scoped;
	private TerrainRenderer terrainRenderer;
	private TerrainRenderer terrainRenderer2;
	private TerrainShader terrainShader = new TerrainShader();
	private TerrainShader terrainShader2 = new TerrainShader();

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	public MasterRenderer(){
		enableCulling();
		createProjectionMatrix();
		createProjectionMatrixScoped();
		renderer = new EntityRenderer(shader, projectionMatrix);
		renderer2 = new EntityRenderer(shader2, projectionMatrixScoped);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		terrainRenderer2 = new TerrainRenderer(terrainShader2, projectionMatrixScoped);
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(Light sun, Camera camera){
		prepare();
		scoped = MainGameLoop.scoped;
		if(scoped){
			shader2.start();
			shader2.loadSkyColor(RED, GREEN, BLUE);
			shader2.loadLight(sun);
			shader2.loadViewMatrix(camera);
		}else{
			shader.start();
			shader.loadSkyColor(RED, GREEN, BLUE);
			shader.loadLight(sun);
			shader.loadViewMatrix(camera);
		}
		
		if(scoped){
			renderer2.render(entities);
		}else{
			renderer.render(entities);
		}

		if(scoped){
			shader2.stop();
		}else{
			shader.stop();
		}
	
		if(scoped){
			terrainShader2.start();
			terrainShader2.loadSkyColor(RED, GREEN, BLUE);
			terrainShader2.loadLight(sun);
			terrainShader2.loadViewMatrix(camera);
		}else{
			terrainShader.start();
			terrainShader.loadSkyColor(RED, GREEN, BLUE);
			terrainShader.loadLight(sun);
			terrainShader.loadViewMatrix(camera);
		}
		
		
		if(scoped){
			terrainRenderer2.render(terrains);
		}else{
			terrainRenderer.render(terrains);
		}
		//terrainRenderer.render(terrains);
		
		if(scoped){
			terrainShader2.stop();
		}else{
			terrainShader.stop();
		}
		terrains.clear();
		entities.clear();
		
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch != null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void cleanUp(){
		shader.cleanUp();
		shader2.cleanUp();
		terrainShader.cleanUp();
		terrainShader2.cleanUp();
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio;
		float x_scale = y_scale / aspectRatio;
		float frustrum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustrum_length);
		projectionMatrix.m33 = 0;
	}
	
	private void createProjectionMatrixScoped(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOVSCOPED / 2f))) * aspectRatio;
		float x_scale = y_scale / aspectRatio;
		float frustrum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrixScoped = new Matrix4f();
		projectionMatrixScoped.m00 = x_scale;
		projectionMatrixScoped.m11 = y_scale;
		projectionMatrixScoped.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrum_length);
		projectionMatrixScoped.m23 = -1;
		projectionMatrixScoped.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustrum_length);
		projectionMatrixScoped.m33 = 0;
	}
	
}
