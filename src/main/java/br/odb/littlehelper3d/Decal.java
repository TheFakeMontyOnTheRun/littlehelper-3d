/**
 * 
 */
package br.odb.littlehelper3d;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import br.odb.libstrip.AbstractTriangle;
import br.odb.libstrip.AbstractTriangleFactory;
import br.odb.libstrip.Mesh;
import br.odb.libstrip.MeshFactory;
import br.odb.utils.FileServerDelegate;
import br.odb.utils.math.Vec3;

/**
 * @author monty
 *
 */
public class Decal extends Mesh {

	/**
	 * 
	 */
	public Decal() {
		super();
	}

	/**
	 * @param mesh
	 */
	public Decal(Mesh mesh) {
		super(mesh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public Decal(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static final float normalizeZ( byte z, HashMap<Byte, Float> zRoundings ) {
		
		if (! zRoundings.containsKey( z ) ) {
			zRoundings.put( z, ( (float) - ( ( z + ( -Byte.MIN_VALUE ) ) / 50.0f ) ) );
		}
		
		return zRoundings.get( z );
	}

	public static final float normalizeX( byte x, HashMap<Byte, Float> xRoundings, float screenWidth, float screenHeight ) {

		float rounded;
		if ( !xRoundings.containsKey( x ) ) {
			rounded = ( -( 0.5f ) + ( ( ( x + ( -Byte.MIN_VALUE ) ) * 800.0f ) / ( Byte.MAX_VALUE - Byte.MIN_VALUE ) ) / ( 800.0f ) );
			xRoundings.put( x, rounded );
		}

		rounded = xRoundings.get( x );
		return rounded;
	}
	
	public static final float normalizeY( byte y, HashMap<Byte, Float> yRoundings, float screenWidth, float screenHeight ) {

		
		if ( !yRoundings.containsKey( y ) ) {
			
			yRoundings.put( y, ( 0.5f ) - ( ( ( y + (-Byte.MIN_VALUE)) * 480.f ) / ( Byte.MAX_VALUE - Byte.MIN_VALUE ) ) / 480.0f );
		}

		return yRoundings.get( y );
	}
	
	
	
	public static final AbstractTriangle[] loadGraphic(InputStream is, float screenWidth, float screenHeight, AbstractTriangleFactory factory )
			throws IOException {
		
		return loadFrom( is, -1.2f, screenWidth, screenHeight, factory );
		
	}

	
	private static AbstractTriangle[] loadFrom( InputStream is, float offset, float screenWidth, float screenHeight, AbstractTriangleFactory factory ) throws IOException {
		
		DataInputStream dis;
		dis = new DataInputStream( is );
		
		HashMap< Byte, Float > xRoundings = new HashMap< Byte, Float >();
		HashMap< Byte, Float > yRoundings = new HashMap< Byte, Float >();
		HashMap< Byte, Float > zRoundings = new HashMap< Byte, Float >();
		
		ArrayList< AbstractTriangle> scratch = new ArrayList< AbstractTriangle>();
		AbstractTriangle[] toReturn = null;
		int entries = 0;
		byte[] bytes = new byte[11];
		byte[] edge = new byte[9];
		AbstractTriangle t;
		int header;
		int majorPolys;
		
		header = -1;
		
		if ( header == -1 ) {
			
			majorPolys = dis.readInt();
			
			for ( int d = 0; d < majorPolys; ++d ) {
				
				entries = dis.readInt();
				
				for (int c = 0; c < entries; ++c) {
					
					is.read(edge);
					
					if ( edge[ 8 ] == Byte.MIN_VALUE )
						continue;
					
					t = factory.makeTrig(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)  
							
					t.a = (edge[0] + (-Byte.MIN_VALUE));
					t.r = (edge[1] + (-Byte.MIN_VALUE));
					t.g = (edge[2] + (-Byte.MIN_VALUE));
					t.b = (edge[3] + (-Byte.MIN_VALUE));
				
					
					t.x0 = normalizeX( edge[4], xRoundings, screenWidth, screenHeight );
					t.y0 = normalizeY( edge[5], yRoundings, screenWidth, screenHeight );
					
					t.x1 = normalizeX( edge[6], xRoundings, screenWidth, screenHeight );
					t.y1 = normalizeY( edge[7], yRoundings, screenWidth, screenHeight );
					
					t.x2 = normalizeX( edge[4], xRoundings, screenWidth, screenHeight );
					t.y2 = normalizeY( edge[5], yRoundings, screenWidth, screenHeight );
					
					t.z0 = normalizeZ( Byte.MIN_VALUE, zRoundings );
					t.z1 = normalizeZ( Byte.MIN_VALUE, zRoundings );
					t.z2 = normalizeZ( edge[ 8 ], zRoundings );
					
					float lightFactor;
					Vec3 normal;
					
						
					normal = t.makeNormal().normalized();
					lightFactor = 0.8f + ( normal.dotProduct( MeshFactory.DEFAULT_LIGHT_VECTOR.normalized() ) * 0.2f );
					
					
					t.r *= lightFactor;
					t.g *= lightFactor;
					t.b *= lightFactor;
					t.a *= lightFactor;						
					
					t.flushToGLES();					
					scratch.add( t );
					
					t = new GLES1Triangle();
					t.a = (edge[0] + (-Byte.MIN_VALUE));
					t.r = (edge[1] + (-Byte.MIN_VALUE));
					t.g = (edge[2] + (-Byte.MIN_VALUE));
					t.b = (edge[3] + (-Byte.MIN_VALUE));
					
					t.x0 = normalizeX( edge[6], xRoundings, screenWidth, screenHeight );
					t.y0 = normalizeY( edge[7], yRoundings, screenWidth, screenHeight );
					
					t.x1 = normalizeX( edge[6], xRoundings, screenWidth, screenHeight );
					t.y1 = normalizeY( edge[7], yRoundings, screenWidth, screenHeight );
					
					t.x2 = normalizeX( edge[4], xRoundings, screenWidth, screenHeight );
					t.y2 = normalizeY( edge[5], yRoundings, screenWidth, screenHeight );
					
					t.z0 = normalizeZ( Byte.MIN_VALUE, zRoundings );
					t.z1 = normalizeZ( edge[ 8 ], zRoundings );
					t.z2 = normalizeZ( edge[ 8 ], zRoundings );
					
					
					normal = t.makeNormal().normalized();
					lightFactor = 0.8f + ( normal.dotProduct( MeshFactory.DEFAULT_LIGHT_VECTOR.normalized() ) * 0.2f );
					
					
					t.r *= lightFactor;
					t.g *= lightFactor;
					t.b *= lightFactor;
					t.a *= lightFactor;							
					
					t.flushToGLES();	
					scratch.add( t );
				}
			}
		}
		
		header = -2;
		if ( header == -2 ) {
			
			entries = dis.readInt();
			
			for (int c = 0; c < entries; ++c) {
				
				is.read(bytes);
				
				t = new GLES1Triangle();
				
				t.a = (bytes[0] + (-Byte.MIN_VALUE));
				t.r = (bytes[1] + (-Byte.MIN_VALUE));
				t.g = (bytes[2] + (-Byte.MIN_VALUE));
				t.b = (bytes[3] + (-Byte.MIN_VALUE));
				
				t.x0 = normalizeX( bytes[4], xRoundings, screenWidth, screenHeight );
				t.y0 = normalizeY( bytes[5], yRoundings, screenWidth, screenHeight );
				
				t.x1 = normalizeX( bytes[6], xRoundings, screenWidth, screenHeight );
				t.y1 = normalizeY( bytes[7], yRoundings, screenWidth, screenHeight );
				
				t.x2 = normalizeX( bytes[8], xRoundings, screenWidth, screenHeight );
				t.y2 = normalizeY( bytes[9], yRoundings, screenWidth, screenHeight );
				
//				if ( bytes[ 10 ] > Byte.MIN_VALUE ) {
					
				t.z0 = normalizeZ( bytes[ 10 ], zRoundings );
				t.z1 = normalizeZ( bytes[ 10 ], zRoundings );
				t.z2 = normalizeZ( bytes[ 10 ], zRoundings );
//				} else {
//					t.flatten( offset );
//				}
				
				t.flushToGLES();
				
				
				scratch.add( t );
			}
		}
		
		
		System.out.println( "got " + entries + " polygons" );
		
		toReturn = new GLES1Triangle[ scratch.size() ];
		toReturn = scratch.toArray( toReturn );
		
		return toReturn;
	}
	
	public Decal( FileServerDelegate fileServer, String decalName, String decalFilename, float screenWidth, float screenHeight ) {
		
		super( decalFilename );
		
		try {			
			InputStream is = fileServer.openAsInputStream( decalFilename );
			
			AbstractTriangle[] trigs = loadFrom( is, 0.0f, screenWidth, screenHeight );
			
			for ( int c = 0; c < trigs.length; ++c )
				addFace( trigs[ c ] );
			
			
			setVisibility( true );
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
