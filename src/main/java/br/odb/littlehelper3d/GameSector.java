package br.odb.littlehelper3d;

import sun.rmi.runtime.Log;
import br.odb.gameapp.PositionalMediaPlayer;
import br.odb.gameworld.Actor;
import br.odb.gameworld.exceptions.InvalidSlotException;
import br.odb.libscene.Constants;
import br.odb.libscene.Door;
import br.odb.libscene.InvalidSectorQuery;
import br.odb.libscene.Sector;
import br.odb.libstrip.AbstractSquare;
import br.odb.libstrip.AbstractTriangle;
import br.odb.libstrip.AbstractTriangleFactory;
import br.odb.libstrip.Mesh;
import br.odb.libstrip.MeshFactory;
import br.odb.utils.FileServerDelegate;
import br.odb.utils.math.Vec3;

/**
 * 
 * @author Daniel "Monty" Monteiro
 */
public class GameSector extends Sector {
	public int dist;
	public AbstractTriangle[][] decals = new AbstractTriangle[6][];
	public AbstractSquare[] face;
	public Mesh meshWalls[];
	public long frame;
	public Vec3 relPos = new Vec3();
	public int visibleFaces;
	public int incidingDirection;
	public int doorCount = 0;
	public boolean geometryLoaded = false;
	public GameSector[] cachedNeighBours = new GameSector[6];
	private int light;
	public GameSector cachedParent;

	// ------------------------------------------------------------------------------------------------------------
	public GameSector(float i, float i0, float i1, float i2, float i3, float i4) {
		super(i, i0, i1, i2, i3, i4);

		visibleFaces = 0;
		face = new AbstractSquare[6];
		meshWalls = new Mesh[6];
	}

	public void closeAllDoors() {
		Door door;

		for (int c = 0; c < 6; ++c) {

			door = getDoor(c);

			if (door != null) {
				door.close();
			}
		}

	}

	// ------------------------------------------------------------------------------------------------------------

	public void lit() {

		int candelas = this.getEmissiveLightningIntensity();
		for (int c = 0; c < 6; c++)
			if (this.face[c] != null)
				this.face[c].setColorWithOffset(getColor(c), candelas);
	}
	
//	@Override
//	public boolean isOpenAt(int slot) {
//		
//		
//			if ( cachedNeighBours[ slot ] != null ) {
//				
//				if ( cachedParent.getDoor( slot ) != null ) {
//					
//					return cachedParent.getDoor( slot ).isOpen();
//				} else {
//					
//					return true;
//				}
//			} else {
//				return true;
//			}
//	}


	int getEmissiveLightningIntensity() {
		
		return light;
	}

	// ------------------------------------------------------------------------------------------------------------
	public int locateEscapePlane(Vec3 vec) throws InvalidSectorQuery {

		if ((vec.y >= getY0() && vec.y <= getY1())
				&& (vec.z >= getZ0() && vec.z <= getZ1())) {

			if (vec.x >= getX1())
				return Constants.FACE_E;

			if (vec.x <= getX0())
				return Constants.FACE_W;

			return Constants.INSIDE;
		}

		if ((vec.x >= getX0() && vec.x <= getX1())
				&& (vec.z >= getZ0() && vec.z <= getZ1())) {

			if (vec.y >= getY1())
				return Constants.FACE_CEILING;

			if (vec.y <= getY0())
				return Constants.FACE_FLOOR;

			return Constants.INSIDE;
		}

		if ((vec.y >= getY0() && vec.y <= getY1())
				&& (vec.x >= getX0() && vec.x <= getX1())) {

			if (vec.z >= getZ1())
				return Constants.FACE_S;

			if (vec.z <= getZ0())
				return Constants.FACE_N;

			return Constants.INSIDE;
		}

		throw new InvalidSectorQuery();
	}

	@Override
	public void onSectorEnteredBy(GameActor actor) {
		super.onSectorEnteredBy(actor);
		
//		openAllDoors();
	}
	
	@Override
	public boolean blockedByDoor(Vec3 vec) {
		boolean toReturn = super.blockedByDoor(vec);
		
		if ( toReturn ) {
			openAllDoorsAfter( 500 );
		}
		
		return toReturn;
	}

	private void openAllDoorsAfter( int delay ) {
		
		GameDoor door;

		for (int c = 0; c < 6; ++c) {

			door = getDoor(c);

			if (door != null) {				
				door.openAfter( delay );
			}
		}		
	}

	// ------------------------------------------------------------------------------------------------------------
	public void onSectorLeftBy(GameActor actor) {
		super.onSectorLeftBy(actor);
		
		closeAllDoorsAfter( 10000 );
	}

	private void closeAllDoorsAfter(int delay) {
		GameDoor door;

		for (int c = 0; c < 6; ++c) {

			door = getDoor(c);

			if (door != null) {
				door.closeAfter( delay );
			}
		}		
	}

	public void openAllDoors() {
		openAllDoorsAfter( 0 );
	}

	public void reset() {
//		super.setEmissiveLightningIntensity(0);
//		relPos.set(0.0f, 0.0f, 0.0f);
		relPos.x = 0.0f;
		relPos.y = 0.0f;
		relPos.z = 0.0f;
			
//		for (int c = 0; c < 6; ++c) {

			// if ( meshWalls[ c ] != null )
			// meshWalls[ c ].setVisibility( false );
			//
			// if ( getDoor( c ) != null && getDoor( c ).getMesh() != null )
			// getDoor( c ).getMesh().setVisibility( false );
//		}
	}

	public void setDoorAt(int slot, int sector, FileServerDelegate delegate, PositionalMediaPlayer openSound, PositionalMediaPlayer closeSound ) {
		
		doorCount++;
		doors[slot] = new GameDoor(sector, getCenterForEdge( slot ), openSound, closeSound, null );
	}

	public void setDoorAt(FileServerDelegate fileServer, int slot, int sector,
			String decalName, GameDelegate delegate, PositionalMediaPlayer openSound, PositionalMediaPlayer closeSound ) {
		
		doorCount++;
		doors[slot] = new GameDoor(sector, getCenterForEdge( slot ), openSound, closeSound, delegate );
		setDecalAt(fileServer, slot, decalName);
	}

	
	public int getLink(int i) throws InvalidSlotException {
		return super.getLink( i );
	}
	
	@Override
	public GameDoor getDoor(int d) {
	
		return (GameDoor) super.getDoor( d );
	}
	
	// ------------------------------------------------------------------------------------------------------------
	public void setLink(int s, int i0, String token) {
		super.setLink(s, i0);

		if (i0 == Constants.NO_LINK)
			visibleFaces++;
	}

	// ------------------------------------------------------------------------------------------------------------
	@Override
	public void setLinks(int l0, int l1, int l2, int l3, int l4, int l5) {
		super.setLinks(l0, l1, l2, l3, l4, l5);
		visibleFaces = 0;
		for (int c = 0; c < 6; ++c)
			try {
				if (getLink(c) == Constants.NO_LINK)
					++visibleFaces;
			} catch (InvalidSlotException e) {
				e.printStackTrace();
			}

	}

	public void setVisible(boolean v) {

		if (visibleFaces != 0) {

			for (int c = 0; c < 6; c++) {
				if (face[c] != null) {
					face[c].setVisibility(v);
				}
			}
		}
	}

	public void setDecalAt(FileServerDelegate fileServer, int face,
			String decalName) {

		String decalFilename = decalName;
		
		Decal decal = new Decal(fileServer, decalName, decalFilename, 800.0f, 480.0f );
		
		if (doors[face] != null) {			
			doors[face].setMesh( applyToFace(face, decal, 4.0f ) );
			doors[face].getMesh().addFacesFrom( applyToFace(face, decal, -4.0f ) );
		} else
			this.meshWalls[face] = applyToFace(face, decal, 1.0f );
	}

	private Mesh applyToFace(int face, Decal decal, float scale ) {

		Decal toReturn = new Decal();

		for (int c = 0; c < decal.faces.size(); ++c) {
			toReturn.addFace(this.applyToFace(face,
					(AbstractTriangle) decal.faces.get(c), scale ) );
		}

		return toReturn;
	}
	


	public AbstractTriangle applyToFace(int faceNum, AbstractTriangle face, float scale ) {
		return applyToFace(faceNum, face, 0.0f, scale );
	}

	private AbstractTriangle applyToFace(int faceNum, AbstractTriangle face,
			float delta, float scale, AbstractTriangleFactory factory ) {

		AbstractTriangle toReturn = null;

		float x;
		float y;
		float z;
		float x0 = 0.0f;
		float y0 = 0.0f;
		float z0 = 0.0f;
		float x1 = 0.0f;
		float y1 = 0.0f;
		float z1 = 0.0f;
		float x2 = 0.0f;
		float y2 = 0.0f;
		float z2 = 0.0f;
		float dx = getDX();
		float dy = getDY();
		float dz = getDZ();

		x = this.getX0() + dx / 2;
		y = this.getY0();
		z = this.getZ0() + dz / 2;

		switch (faceNum) {

		case Constants.FACE_S: {

			x0 = x + (face.x0) * dx;
			y0 = (dy / 2.0f) + y + (face.y0) * dy;
			z0 = z + (face.z0 * scale ) + delta + (dz / 2);
			x1 = x + (face.x1) * dx;
			y1 = (dy / 2.0f) + y + (face.y1) * dy;
			z1 = z + (face.z1 * scale ) + delta + (dz / 2);
			x2 = x + (face.x2) * dx;
			y2 = (dy / 2.0f) + y + (face.y2) * dy;
			z2 = z + (face.z2 * scale ) + delta + (dz / 2);
		}
			break;

		case Constants.FACE_N: {

			x0 = x + (face.x0) * dx;
			y0 = (dy / 2.0f) + y + (face.y0) * dy;
			z0 = z - (face.z0  * scale / 2.0f ) - delta - (dz / 2);
			x1 = x + (face.x1) * dx;
			y1 = (dy / 2.0f) + y + (face.y1) * dy;
			z1 = z - (face.z1  * scale / 2.0f ) - delta - (dz / 2);
			x2 = x + (face.x2) * dx;
			y2 = (dy / 2.0f) + y + (face.y2) * dy;
			z2 = z - (face.z2  * scale / 2.0f ) - delta - (dz / 2);
		}
			break;

		case Constants.FACE_E: {

			x0 = x + (face.z0 * scale ) - delta + (dx / 2);
			y0 = (dy / 2.0f) + y + (face.y0 * dy);
			z0 = z + (face.x0 * dz);
			x1 = x + (face.z1 * scale ) - delta + (dx / 2);
			y1 = (dy / 2.0f) + y + (face.y1 * dy);
			z1 = z + (face.x1 * dz);
			x2 = x + (face.z2 * scale ) - delta + (dx / 2);
			y2 = (dy / 2.0f) + y + (face.y2 * dy);
			z2 = z + (face.x2 * dz);
		}
			break;

		case Constants.FACE_W: {

			x0 = x - (face.z0 * scale ) + delta - (dx / 2);
			y0 = (dy / 2.0f) + y + (face.y0 * dy);
			z0 = z + (face.x0 * dz);
			x1 = x - (face.z1 * scale ) + delta - (dx / 2);
			y1 = (dy / 2.0f) + y + (face.y1 * dy);
			z1 = z + (face.x1 * dz);
			x2 = x - (face.z2 * scale ) + delta - (dx / 2);
			y2 = (dy / 2.0f) + y + (face.y2 * dy);
			z2 = z + (face.x2 * dz);
		}
			break;

		case Constants.FACE_FLOOR: {

			x0 = x + (face.x0) * dx;
			y0 = y - (face.z0 * scale ) + delta;// + ( face.z0 );
			z0 = z + (face.y0) * dz;
			x1 = x + (face.x1) * dx;
			y1 = y - (face.z1 * scale ) + delta;// + ( face.z1 );
			z1 = z + (face.y1) * dz;
			x2 = x + (face.x2) * dx;
			y2 = y - (face.z2 * scale ) + delta;// + ( face.z2 );
			z2 = z + (face.y2) * dz;
		}
			break;

		case Constants.FACE_CEILING: {
			x0 = x + (face.x0) * dx;
			y0 = y + (face.z0 * scale ) + dy + delta;
			z0 = z + (face.y0) * dz;
			x1 = x + (face.x1) * dx;
			y1 = y + (face.z1 * scale ) + dy + delta;
			z1 = z + (face.y1) * dz;
			x2 = x + (face.x2) * dx;
			y2 = y + (face.z2 * scale ) + dy + delta;
			z2 = z + (face.y2) * dz;
		}
			break;
		}

		long argb = face.getColor().getARGBColor();
		argb = argb & 0x00FFFFFF;
		int clipped = (int) argb;
		
		toReturn = factory.makeTrig(x0, y0, z0, x1,
				y1, z1, x2, y2, z2, clipped, MeshFactory.DEFAULT_LIGHT_VECTOR);

		return toReturn;
	}

	public void setEmissiveLightningIntensity(int i) {
		light = i;
		
	}

	public void addCandelas(int candelas) {
		
		if ( candelas >= 255 ) {
			light = 255;
			return;
		}

		if ( candelas < 0 ) {
			return;
		}
		
		
		if ( ( light + candelas )  > 255 )
			light = 255;
		else
			light += candelas;		
	}

	public void silentlyCloseAllDoors() {
		for ( int c = 0; c < 6; ++ c) {
			if ( doors[ c ] != null )
				( ( GameDoor ) doors[ c ] ).silentlyClose();
		}		
	}

	public int getDoorCount() {
		
		return doorCount;
	}
}
