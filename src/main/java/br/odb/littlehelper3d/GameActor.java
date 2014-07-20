package br.odb.littlehelper3d;

import br.odb.gameapp.GameAudioListener;
import br.odb.gameapp.PositionalMediaPlayer;
import br.odb.gameworld.Actor;
import br.odb.liboldfart.sceneobjects.ObjMesh;
import br.odb.libscene.ActorConstants;
import br.odb.utils.math.Vec3;


/**
 * 
 * @author daniel
 */
public class GameActor extends Actor implements GameAudioListener {
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public PositionalMediaPlayer walkSound;	
	public PositionalMediaPlayer shotSound;	
//	public PositionalMediaPlayer breatheSound;	
//	public PositionalMediaPlayer beatSound;
	public PositionalMediaPlayer playerSound;

	/**
	 * 
	 */
	private ObjMesh mesh;
	/**
	 * 
	 */
	public int actorClass;
	private GameDelegate delegate;

	public int getActorClass() {
		return actorClass;
	}
	
	public void setActorClass(int actorClass) {
		this.actorClass = actorClass;
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public GameActor() {
		super();
		mesh = new ObjMesh();
		actorClass = 1;
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param actor
	 */
	public GameActor(GameActor actor) {
		super(actor);
		
		mesh = new ObjMesh(actor.getMeshes());
		actorClass = actor.actorClass;
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void consume( ActorConstants cmdCode) {

		
		if ( delegate == null || delegate.onActionAboutToBePerformed( this, cmdCode.ordinal() ) ) {
				
				super.consume(cmdCode);
				
//				Log.d( "derelict", "pos:" + getPosition() );
				
				if ( ( actorClass == 0 ) && ( cmdCode == ActorConstants.MOVE_N || cmdCode == ActorConstants.MOVE_S ) )
					walkSound.playUnique();
				
//				if ( cmdCode == ActorConstants.FIRE )
//					shotSound.playUnique();
		}
		
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void fire() {
//		GameActor ga = new Projectile( this );
//		setGenerated( ga );
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public Actor generateCopy() {
		return new GameActor(this);
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public int getDirection() {

//		Log.d( "derelict", "angle: " + getAngleXZ()  );
		
		int dir = Math.round( ( getAngleXZ() ) / 90.0f);

		while (dir < 0)
			dir += 4;

		while (dir >= 4)
			dir -= 4;

		return dir;

	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public int getDiscreetAngle() {
		
		int toReturn = (int) (getAngleXZ() / Utils.transformTableIncrements);
		
		while ( toReturn < 0 )
			toReturn += 12;
		
		return toReturn;
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public ArrayList<ObjMesh> getMesh() {
		ArrayList<ObjMesh> vector = new ArrayList<ObjMesh>();
		vector.add(mesh);
		return vector;
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param object
	 */
	public void hit(Sector sector) {
		haltAcceleration();
		undo();
	}
	/**
	 * 
	 * @param object
	 */
	public void hit(SceneObject3D object) {
		haltAcceleration();
		undo();
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param obj
	 */
	public void setMesh(ObjMesh obj) {
		mesh = obj;
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void tick() {
		super.tick();
		
	}
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void undo() {

		super.undo();
		
		if ( actorClass == 1 ) {
			consume(ActorConstants.TURN_L);
			consume(ActorConstants.TURN_L);
		}
	}
	// ------------------------------------------------------------------------------------------------------------
	public float getDY() {
		return 9.5f;
	}
	public void setPlayable(boolean b) {
		
		if ( b ) {		
			this.actorClass = 0;
		}
	}
	
	@Override
	public void setAlive(boolean b) {
	
		super.setAlive(b);
		
		if ( !isAlive() ) {
			
//			if ( beatSound != null )
//				beatSound.stop();
//			
//			if ( breatheSound != null )
//				breatheSound.stop();
			
			if ( playerSound != null )
				playerSound.stop();
			
			if ( shotSound != null )
				shotSound.stop();
			
			if ( walkSound != null )
				walkSound.stop();
		}
	}
	
	public boolean isPlayable() {
		
		return actorClass == 0;
	}

	public void writeSnapshot(OutputStream os) throws IOException {
		
		DataOutputStream dos = new DataOutputStream( os );
		dos.writeInt( getCurrentSector() );
		dos.writeFloat( getPosition().x );
		dos.writeFloat( getPosition().y );
		dos.writeFloat( getPosition().z );
		dos.writeFloat( getAngleXZ() );
		dos.writeInt( this.actorClass );
		dos.writeInt( this.candelas );
		dos.writeBoolean( this.isAlive() );
		dos.writeBoolean( this.isPlayable() );
		dos.writeBoolean( this.isVisible() );
	}

	public void loadSnapshot(InputStream is) throws IOException {
		
		DataInputStream dis = new DataInputStream( is );
		Vec3 pos = getPosition();
		setCurrentSector( dis.readInt() );
		pos.x = ( dis.readFloat() );
		pos.y = ( dis.readFloat() );
		pos.z = ( dis.readFloat() );
		setAngleXZ( dis.readFloat() );
		actorClass = dis.readInt();
		candelas = dis.readInt();
		setAlive( dis.readBoolean() );
		setPlayable( dis.readBoolean() );
		setVisible( dis.readBoolean() );
	}
	
	@Override
	public void destroy() {
	
		super.destroy();
		
		if ( walkSound != null )
			walkSound.destroy();
		
		if ( shotSound != null )
			shotSound.destroy();
		
//		if ( breatheSound != null )
//			breatheSound.destroy();
//		
//		if ( beatSound != null )
//			beatSound.destroy();

		
		if ( playerSound != null )
			playerSound.destroy();
		
		mesh.destroy();
		
		walkSound = null;
		shotSound = null;
//		breatheSound = null;
//		beatSound = null;	
		playerSound = null;	
		
		mesh = null;


	}

	public ObjMesh getMainMesh() {

		return mesh;
	}

	public void setGameDelegate(GameDelegate delegate) {
		this.delegate = delegate;		
	}

	@Override
	public Vec3 getDirectionVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3 getPosition() {
		// TODO Auto-generated method stub
		return null;
	}
}
