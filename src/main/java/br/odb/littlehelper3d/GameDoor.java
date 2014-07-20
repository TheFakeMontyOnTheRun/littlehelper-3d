package br.odb.littlehelper3d;

import java.util.ArrayList;

import br.odb.gameapp.GameAssetManager;
import br.odb.gameapp.GameAudioManager;
import br.odb.gameapp.PositionalMediaPlayer;
import br.odb.libscene.Door;
import br.odb.utils.math.Vec3;

/**
 * 
 * @author monty
 * 
 */
public class GameDoor extends Door {

	public class CloseDoorRunnable implements Runnable {

		private GameDoor gameDoor;
		private long delay;

		public CloseDoorRunnable(GameDoor gameDoor, long delay) {
			this.gameDoor = gameDoor;
			this.delay = delay;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(delay);
				gameDoor.closeAllSons();
				
				if ( delegate != null )
					delegate.refreshView();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public class OpenDoorRunnable implements Runnable {
		
		private GameDoor gameDoor;
		private long delay;
		
		public OpenDoorRunnable(GameDoor gameDoor, long delay) {
			this.gameDoor = gameDoor;
			this.delay = delay;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(delay);
				gameDoor.openAllSons();
				
				if ( delegate != null )
					delegate.refreshView();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private PositionalMediaPlayer closeDoorSound;
	/**
	 * 
	 */
	private PositionalMediaPlayer openDoorSound;

	private GameDoor master;
	private ArrayList<GameDoor> sons = new ArrayList<GameDoor>();
	private GameDelegate delegate;

	// ------------------------------------------------------------------------------------------------------------

	public void setMaster(GameDoor door) {
		master = door;

		if (getMesh() == null)
			setMesh(master.getMesh());
	}

	public void addSon(GameDoor door) {
		sons.add(door);
		door.master = this;
		door.setMesh(getMesh());
	}

	/**
	 * 
	 * @param sector
	 * @param pos
	 */
	public GameDoor(int sector, Vec3 pos, PositionalMediaPlayer openSound, PositionalMediaPlayer closeSound, GameDelegate delegate, GameAssetManager gam ) {
		super(sector);

		this.delegate = delegate;
		
		
		//TODO: erro de pertinencia de terefas - o registro do som não deveria estar acontecendo aqui.
			openDoorSound = openSound;
			GameAudioManager.getInstance().registerPlayer(openDoorSound);
			closeDoorSound = closeSound;
			GameAudioManager.getInstance().registerPlayer(closeDoorSound);
		}

	
	public void setDelegate( GameDelegate delegate ) {
		this.delegate = delegate;
	}
	
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void close() {
		closeAfter(0);
	}

	public void justOpen() {
		super.open();
	}

	public void justClose() {
		super.close();
	}

	// ------------------------------------------------------------------------------------------------------------
	/***
	 * 
	 */
	@Override
	public void open() {

		openAfter(0);
	}

	public void silentlyClose() {

		if (master != null)
			master.silentlyClose();
		else if (sons != null) {
			for (GameDoor son : sons) {
				son.justClose();
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------
	@Override
	public void destroy() {

		super.destroy();

		if (closeDoorSound != null)
			closeDoorSound.destroy();

		if (openDoorSound != null)
			openDoorSound.destroy();

		closeDoorSound = null;
		openDoorSound = null;
		master = null;
		sons.clear();
		sons = null;
	}

	public void closeAfter(int delay) {

		if (master != null)
			master.closeAfter(delay);
		else if (sons != null) {
			new Thread(new CloseDoorRunnable(this, delay)).start();
		} else {
			super.close();
		}
	}

	private void closeAllSons() {
		for (GameDoor son : sons) {
			son.justClose();
		}

		super.close();

		if (closeDoorSound != null)
			closeDoorSound.playUnique();
	}

	public void openAfter(int delay) {

		if (master != null) {
			master.openAfter(delay);
		} else if (sons != null) {
			if ( ( delegate != null && delegate.shouldOpenDoor( this ) ) || delegate == null )
				new Thread( new OpenDoorRunnable( this, delay ) ).start();
		} else {
			super.open();
		}
	}

	private void openAllSons() {

		for (int c = 0; c < sons.size(); ++c) {
			sons.get(c).justOpen();
		}

		super.open();
		if (openDoorSound != null)
			openDoorSound.playUnique();
	}

	public Door getMaster() {
		return master;
	}
}
