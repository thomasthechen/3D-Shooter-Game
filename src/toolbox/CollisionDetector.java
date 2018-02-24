package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Enemy;
import entities.Entity;
import entities.Player;

public class CollisionDetector {
	
	//player stuff
	private Player player;
	float PminX;
	float PminY;
	float PminZ;
	float PmaxX;
	float PmaxY;
	float PmaxZ;
	
	float EminX;
	float EminY;
	float EminZ;
	float EmaxX;
	float EmaxY;
	float EmaxZ;
	
	float buffer = 1f;
	
	Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(0,0,0), 0, 90, 0, 1);
	Matrix4f transformationMatrix2 = Maths.createTransformationMatrix(new Vector3f(0,0,0), 0, 0, 90, 1);
	
	
	public CollisionDetector(Player player){
		this.player = player;
		
	}
	
	public boolean EntityPlayerCollision(Entity entity, Vector3f min, Vector3f max){
		
		PminX = player.getHitbox().getPosition().x - player.getHitbox().getLengthX();
		PminY = player.getHitbox().getPosition().y - player.getHitbox().getLengthY();
		PminZ = player.getHitbox().getPosition().z - player.getHitbox().getLengthZ();
		
		PmaxX = player.getHitbox().getPosition().x + player.getHitbox().getLengthX();
		PmaxY = player.getHitbox().getPosition().y + player.getHitbox().getLengthY();
		PmaxZ = player.getHitbox().getPosition().z + player.getHitbox().getLengthZ();

			
			//intersect on x, y, z
			if(PminX < entity.getPosition().x + max.x*entity.getScale() * buffer && PmaxX > entity.getPosition().x + min.x*entity.getScale() * buffer &&
			PminY < entity.getPosition().y + max.y*entity.getScale() * buffer && PmaxY > entity.getPosition().y + min.y*entity.getScale() * buffer &&
			PminZ <  entity.getPosition().z + max.z*entity.getScale() * buffer && PmaxZ >  entity.getPosition().z + min.z*entity.getScale() * buffer){
				 
				return true;
			}
			//if no collision, return false
			
			return false;
			
		
	}
	
	public boolean EntityEnemyCollision(Entity entity, Vector3f min, Vector3f max, Enemy enemy){
		
		
			EminX = enemy.getHitbox().getPosition().x - enemy.getHitbox().getLengthX();
			EminY = enemy.getHitbox().getPosition().y - enemy.getHitbox().getLengthY();
			EminZ = enemy.getHitbox().getPosition().z - enemy.getHitbox().getLengthZ();
		
			EmaxX = enemy.getHitbox().getPosition().x + enemy.getHitbox().getLengthX();
			EmaxY = enemy.getHitbox().getPosition().y + enemy.getHitbox().getLengthY();
			EmaxZ = enemy.getHitbox().getPosition().z + enemy.getHitbox().getLengthZ();
			
			//intersect on x, y, z
			if(EminX < entity.getPosition().x + max.x*entity.getScale() && EmaxX > entity.getPosition().x + min.x*entity.getScale() &&
			EminY < entity.getPosition().y + max.y*entity.getScale() && EmaxY > entity.getPosition().y + min.y*entity.getScale() &&
			EminZ <  entity.getPosition().z + max.z*entity.getScale() && EmaxZ >  entity.getPosition().z + min.z*entity.getScale()){
				
				return true;
			}
			
			return false;
	}
	
	public boolean EntityEntityCollision(Entity entity1, Vector3f max1, Vector3f min1, Entity entity2, Vector3f max2, Vector3f min2){
		
		//intersect on x, y, z
		if(entity1.getPosition().x + min1.x * entity1.getScale() < entity2.getPosition().x + max2.x*entity2.getScale() && entity1.getPosition().x + max1.x * entity1.getScale() > entity2.getPosition().x + min2.x*entity2.getScale() &&
		entity1.getPosition().y + min1.y * entity1.getScale() < entity2.getPosition().y + max2.y*entity2.getScale() && entity1.getPosition().y + max1.y * entity1.getScale() > entity2.getPosition().y + min2.y*entity2.getScale() &&
		entity1.getPosition().z + min1.z * entity1.getScale() < entity2.getPosition().z + max2.z*entity2.getScale() && entity1.getPosition().z + max1.z * entity1.getScale() > entity2.getPosition().z + min2.z*entity2.getScale()){
			
			return true;
		}
		
		return false;
	}
	
}
