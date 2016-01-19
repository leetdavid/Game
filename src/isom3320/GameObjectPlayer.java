package isom3320;

import processing.core.PConstants;


public class GameObjectPlayer extends GameObject {
	
	private boolean isShooting;
	private GameObjectGun gun;
	private long cantShootFor;
	private double randIncreaser;

	@Override
	public void init(){
		setPos(Main.WIDTH/2, Main.HEIGHT/2);
		isShooting = false;
		motionMult = 0.8D; 
		gun = new GameObjectGun("ak47");
		cantShootFor = 0;
		randIncreaser = 4;
	}
	
	@Override
	public void update(){
		super.update();
		
		//faces the mouse
		setAngle((float)(
				Vector.create(R.mouseX, R.mouseY)
				//.sub(getPos())
				.sub(Vector.create(Main.WIDTH/2, Main.HEIGHT/2))
				.getAngle()
				));
		gun.setAngle(angle);
		
		isShooting = R.mousePressed;
		if(cantShootFor > System.currentTimeMillis()){
			isShooting = false;
		}
		
		if(isShooting && cantShootFor <= System.currentTimeMillis()){
			cantShootFor(getGun().getDelay());
			Main.playSound(getGun().getName());

			Vector spawnPoint = getPos().add(Vector.createFromAngle(getAngle(), 5));
			Vector velocity = Vector.createFromAngle(getAngle(), 15);
			double randomness = !isMoving()? randIncreaser * MathHelper.toRad : 0;
			double damage = getGun().getDamage();
			state.spawn(new GameObjectBullet(spawnPoint, velocity, randomness, damage));
			
			randIncreaser = Math.min(randIncreaser + 4D, 32D);
		} else {
			randIncreaser = 4;
		}

		if(isShooting && !gun.getName().equals("awp"))
			motion.scalar(0.8D);
		
		Vector tempMotion = Vector.ZERO.clone();
		if(Main.isPressed('w')) tempMotion = tempMotion.addY(-0.5);
		if(Main.isPressed('s')) tempMotion = tempMotion.addY(+0.5);
		if(Main.isPressed('a')) tempMotion = tempMotion.addX(-0.5);
		if(Main.isPressed('d')) tempMotion = tempMotion.addX(+0.5);
		if(isShooting){
			tempMotion.scalar(0.1);
		}
		motion = motion.add(tempMotion);
		
	}

	@Override
	public void render(double framestep) {
		// TODO Auto-generated method stub
		
		
		R.pushMatrix();
		{
			R.translate(getXF(), getYF());
			R.rotate((float) getAngle());
			R.rectMode(PConstants.CENTER);
			
			gun.render(framestep);
			
			R.noStroke();
			R.fill(100, 0, 255);
			R.rect(0, 0, 10, 25);
		}
		R.popMatrix();
	}

	public boolean isShooting() {
		return isShooting;
	}
	
	public GameObjectGun getGun() {
		return gun;
	}

	public void setGun(GameObjectGun gun) {
		this.gun = gun;
		Main.playSound(gun.getName() + "_draw");
	}
	public void setGun(String gun) {
		setGun(new GameObjectGun(gun));
	}
	
	public void cantShootFor(long time){
		cantShootFor = time + System.currentTimeMillis();
	}
	
}