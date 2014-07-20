package br.odb.littlehelper3d;
 
import br.odb.gameapp.GameAssetManager;
import br.odb.gameapp.GameAudioManager;
import br.odb.gameapp.PositionalMediaPlayer;
import br.odb.liboldfart.sceneobjects.ObjMesh;
import br.odb.libscene.ActorConstants;
import br.odb.libscene.SceneObject3D;
import br.odb.utils.math.Vec3;

public class Projectile extends AndroidGameActor {

	public PositionalMediaPlayer hitSound;

	
	//R.raw.spark
	public Projectile(GameActor gameActor, int resId ) {
		super();
		
		GameAssetManager gam = ( (PlayGameActivity) PlayGameActivity.getInstance() ).gameAssetManager;
		
		this.candelas = 64;
		this.speed = 5.0f;
		
		hitSound = PositionalMediaPlayer.getFor( new Vec3(), resId, gam );
		
		
		
		ObjMesh obj = gam.meshForName( "torpedo.obj" );
		setMesh(obj);
		obj.moveTo(getPosition());
		actorClass = 2;
		
		GameAudioManager.getInstance().registerPlayer( hitSound );
	}

	@Override
	public void hit(SceneObject3D object) {
		
		hitSound.setPosition( this.getPosition() );
		hitSound.playUnique();
		setAlive( false );
	
		super.hit( object );
	}

	@Override
	public void destroy() {
	
		super.destroy();
		
		hitSound.destroy();
		hitSound = null;
	}

	@Override
	public void tick() {
		
		super.tick();

		if (isAlive()) {
			
			consume(ActorConstants.MOVE_N);
		}
	}

	@Override
	public void undo() {
		super.undo();
	}
}
