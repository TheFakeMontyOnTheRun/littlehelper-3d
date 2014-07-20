package br.odb.littlehelper3d;

import br.odb.utils.FileServerDelegate;

public interface GameEngineListener {
	
	public void onActorAdded( GameActor actor );

	public void needsToRefreshWindow(  boolean fastRefresh );

	public void loadMeshForActor( GameActor actor, String mesh, FileServerDelegate server );
	
	public void beforeTick();
	
	public void requestMapChange( String mapName );

	public void showHistory(int index);

	public void needsToRefreshLightning();

	public void needsToDisplayMessage(String information);

	public void showScreen(String screenClass);
}
