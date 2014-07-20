/**
 * 
 */
package br.odb.littlehelper3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import br.odb.gameapp.GameAudioManager;
import br.odb.gameworld.Actor;
import br.odb.gameworld.Direction;
import br.odb.libscene.Actor3D;
import br.odb.libscene.ActorConstants;
import br.odb.libscene.Constants;
import br.odb.libscene.Sector;
import br.odb.utils.FileServerDelegate;

/**
 * @author monty
 * 
 * 
 * Concerns:
 * - Manages Sectors and Players in those sectors
 * - Game events management
 */
public class GameEngine implements Runnable {

	private GameWorld world;
	private int timeStep;
	private GameEngineListener listener;
	private GameDelegate delegate;
	private GameAudioManager audioManager;
	/**
	 * 
	 */
	private volatile boolean running = true;
	private boolean updating;
	/**
	 * 
	 */
	public static boolean loaded = false;

	/**
	 * 
	 */
	public GameEngine(GameWorld world, GameEngineListener listener) {

		this.world = world;
		this.listener = listener;
		
		audioManager = GameAudioManager.getInstance();
	}

	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param actor
	 * @param sectorId
	 */
	public void placeActor( GameActor actor, int sectorId) {

		GameSector sector = (GameSector) world.getSector(sectorId);
		actor.moveTo(sector.getCenter());
		actor.setCurrentSector(sectorId);
		sector.onSectorEnteredBy(actor);
	}

	public void setTimeStep(int timeStep) {

		this.timeStep = timeStep;
	}

	// ------------------------------------------------------------------------------------------------------------
	private void doGravity( GameActor actor) {
		GameSector sector = (GameSector) world.getSector(actor
				.getCurrentSector());
		GameSector candidate = sector;
		GameSector groundSector = sector;

		while (groundSector != null
					&& (groundSector.cachedNeighBours[ Constants.FACE_FLOOR ] != null )) {

				candidate = groundSector.cachedNeighBours[ Constants.FACE_FLOOR ];

				if (candidate.isMaster()) {
					continue;
				}

				groundSector = candidate;

			}

			while ((groundSector.getY0() + actor.getDY()) > (actor
					.getPosition().y)) {
				actor.consume(ActorConstants.MOVE_UP);
			}

			while ((groundSector.getY0() + actor.getDY()) < (actor
					.getPosition().y)) {

				actor.consume(ActorConstants.MOVE_DOWN);
			}

	}

	// ------------------------------------------------------------------------------------------------------------
	/**
		 * 
		 */
	@Override
	public void run() {

		ArrayList< GameActor> toBeAdded = new ArrayList< GameActor>();
		Actor3D sons;
		boolean actorHasChangedSector;
		boolean needsToRefreshLightning = false;
		GameSector originalSector;
		GameActor actor;

		actorHasChangedSector = true;

		while (running && loaded ) {
			
			updating = true;
			needsToRefreshLightning = false;
			
			try {

				Thread.sleep(timeStep);

				
				audioManager.update();
				
				world.checkpointActors();
				
				if ( listener != null )
					listener.beforeTick();

				if (delegate != null)
					delegate.update(world);

				toBeAdded.clear();
				int count = world.getTotalActors();
				
				for ( Actor3D baseActor: world.getActorList() ) {

					actor = ( GameActor) baseActor;

					if (!actor.isAlive())
						continue;

					actor.tick();

					originalSector = (GameSector) world.getSector(actor.currentSector);
					
					if (actor.isPlayable()) {
						
						doGravity(actor);
					}


					findBestFitForActorFromSector(actor, originalSector);

					if (originalSector != world.getSector(actor.currentSector)) {

						actorHasChangedSector = true;

						if ( actor.getEmissiveLightningIntensity() > 0 )
							needsToRefreshLightning = true;
					}

					sons = actor.checkGenerated();

					if (sons != null) {
						toBeAdded.add( ( GameActor)sons);
					}
				}
				
				int newId = 0;
				
				for ( GameActor spawn : toBeAdded ) {

					spawn.setId(count + newId++ );

					try {
						spawn.getPosition().index = (-2);
					} catch (Exception e) {

					}

					actorHasChangedSector = true;
					world.addActor( spawn );
					listener.onActorAdded( spawn );
				}

				if (actorHasChangedSector) {
					listener.needsToRefreshWindow( false );
				}
				
				if ( needsToRefreshLightning ) {					
					listener.needsToRefreshLightning();
				}
				
				actorHasChangedSector = false;

			} catch (Exception e) {

			}
			
			updating = false;
		}
		
		if ( world != null ) {
			
			for ( Actor3D toBeKilled : world.getActorList() ) {
				
				if (!toBeKilled.isAlive())
					continue;
				
				toBeKilled.setAlive( false );
			}
			
		}
	}

	
	//this would belong to World, wasn't for the dynamics of collision between entities
	private void findBestFitForActorFromSector( GameActor actor,
			GameSector originalSector) {

		int[] neighbours;
		GameSector sector = originalSector;

		if (!originalSector.contains(actor.getPosition())) {

			neighbours = originalSector.getNeighbours();

			for (int d = 0; d < neighbours.length; ++d) {

				sector = (GameSector) world.getSector(neighbours[d]);

				if (sector.isMaster()) {
					continue;
				}

				if (neighbours[d] != 0 && sector.contains(actor.getPosition()))
					break;

				sector = null;
			}

			if (sector == null) {

				int size = world.getTotalSectors();
				for (int d = 1; d < size; ++d) {

					sector = (GameSector) world.getSector(d);

					if (sector.isMaster())
						continue;

					if (sector.contains(actor.getPosition()))
						break;

					sector = null;
				}

			}

			if (sector == null || sector.isMaster()) {

				originalSector.onSectorLeftBy(actor);
				actor.setCurrentSector(originalSector.getId());
				actor.hit( originalSector );

			} else {

				originalSector.onSectorLeftBy(actor);
				sector.onSectorEnteredBy(actor);
				
				if ( sector.getDoorCount() > 0 ) {
					listener.needsToRefreshWindow( false );
				}
				
				if ( delegate != null && sector != originalSector ) {					
					delegate.onSectorEntered(world, actor, sector);
				}
				
				actor.setCurrentSector(sector.getId());
			}
		}
	}

	public void stop() {
		this.running = false;
		loaded = false;
	}
	
	public void pause() {
		loaded = false;
	}
	
	public void resume() {
		loaded = true;
	}

	public void setDelegate(GameDelegate delegate) {

		this.delegate = delegate;
		delegate.setGameEngine( this );
		
		for ( Actor3D actor : world.getActorList() ) {
			( ( GameActor ) actor ).setGameDelegate( delegate );
		}
		
		for ( Sector sector : world ) {
			GameSector s = ( ( GameSector) sector );
			for ( Direction c : Direction.values() ) {

				if ( s.getDoor( c ) != null ) {
					s.getDoor( c ).setDelegate( delegate );
				}
			}
		}
	}

	public void requestMapChange(String mapName) {
		
		listener.requestMapChange(mapName);
	}

	public void start() {
		running = true;
	}

	public void showStory(int index ) {
		
		listener.showHistory( index );
	}

	public void destroy() {
		
		while ( updating ) {
			
			try {
				Thread.sleep( 10 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		world.destroy();
	}

	public void restoreState( FileServerDelegate delegate ) throws FileNotFoundException, IOException {
		world.loadSnapshotAt( delegate.openAsInputStream( "state" ) );		
	}

	public GameWorld getWorld() {

		return world;
	}

	public GameEngineListener getListener() {

		return listener;
	}

	public void showInformation(String information ) {
		listener.needsToDisplayMessage( information );		
	}

	public void needRefreshVisibity() {
		
		listener.needsToRefreshWindow( false );
	}

	public void showScreen( String screenClass ) {
		listener.showScreen( screenClass );		
	}
}
