package br.odb.littlehelper3d;

import br.odb.gameapp.GameResource;

public interface GameEngineViewer {

	void refreshView();
	void fadeIn();
	void fadeOut();
	void setOverlay( GameResource res );
	
	//this has to go
	void requestMapChange(String path);	
	GameEngine getGameEngine();
	void setAngle(float angleXZ);
}
