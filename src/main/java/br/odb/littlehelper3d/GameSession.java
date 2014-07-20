package br.odb.littlehelper3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import br.odb.utils.FileServerDelegate;

public class GameSession implements GameEngineListener {
	
	GameEngine engine;
	GameEngineViewer engineView;
	private FileServerDelegate fileServer;
	GameDelegate delegate;
		
	

	public GameSession( GameDelegate delegate ) {
		
		this.delegate = delegate;
	}

	public void setView( GameEngineViewer view) {
		this.engineView = view;
	}

	public void clearSavedStates( FileServerDelegate fsd ) {

//		try {
//			OutputStream fos = fsd.openAsOutputStream( "state" );
//			fos.write( 0 );
//			fos.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public void startNewLevel( String  path ) {
		
		engineView.requestMapChange( path );
		engine = engineView.getGameEngine();
		engine.setDelegate( delegate );
	}

	public void restoreState() {

		
	}

	public void saveState() {

		
	}
	
	public void stop() {
		engine.stop();		
	}
	
	public void pause() {
		
	}
	
	public void start() {
		
	}

	public void destroy() {
		
		stop();
		engine.destroy();
		engine = null;
	}

	@Override
	public void onActorAdded( GameActor actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void needsToRefreshWindow(  boolean fastRefresh  ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadMeshForActor( GameActor actor, String mesh,
			FileServerDelegate server) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestMapChange(String mapName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHistory(int index) {
		// TODO Auto-generated method stub
		
	}

	public void tryRestoreState() {
//		try {
//			engine.restoreState();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void needsToRefreshLightning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void needsToDisplayMessage(String information) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showScreen(String screenClass) {
		// TODO Auto-generated method stub
		
	}
}
