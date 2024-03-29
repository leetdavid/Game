package isom3320;

import processing.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Main extends PApplet{

	public static int WIDTH, HEIGHT;

	public static final double MS_PER_UPDATE = 7;

	private static double previous = System.currentTimeMillis();
	private static double lag = 0.0;
	private State currentState;

	public static Map<Character, Boolean> inputs;

	public static PFont font;
	
	public static Clip clip;
	public static boolean musicPlaying;
	
	public static void main(String[] args) {
		WIDTH = 1280;
		HEIGHT = 800;
		MathHelper.rand = new Random();

		PApplet.main(new String[] {"isom3320.Main"});
	}

	@Override
	public void settings(){
		size(WIDTH, HEIGHT);
	}

	@Override
	public void setup(){
		smooth();
		changeState(new StateMenu());
		GameObject.R = this;
		initInputs();
		State.rand = MathHelper.rand;
		State.R = this;
		State.WIDTH = WIDTH;
		State.HEIGHT = HEIGHT;
		font = createFont("dimitri.ttf", 300);
	}

	@Override
	public void draw(){
		double current = System.currentTimeMillis();
		double elapsed = current - previous;
		previous = current;
		lag += elapsed;

		while (lag >= MS_PER_UPDATE) {
			update();
			lag -= MS_PER_UPDATE;
		}
		render(lag/MS_PER_UPDATE);
	}

	public void update(){
		currentState.update();
	}
	public void render(double framestep){
		background(0);
		currentState.render(framestep);
	}

	public void changeState(State state){
		if(currentState != null)
			currentState.deinit();
		currentState = state;
		currentState.init();
	}

	public void initInputs(){

		inputs = new HashMap<Character, Boolean>();

		inputs.put('w', false);
		inputs.put('W', false);
		inputs.put('a', false);
		inputs.put('A', false);
		inputs.put('s', false);
		inputs.put('S', false);
		inputs.put('d', false);
		inputs.put('D', false);

		inputs.put('r', false);
		inputs.put('R', false);
		
		inputs.put('m', false);
		inputs.put('M', false);
	}

	@Override
	public void keyPressed(){
		for(char c : inputs.keySet()){
			if(key == c || keyCode == c){
				inputs.put(c, true);
			}
		}
	}

	@Override
	public void keyReleased(){
		for(char c : inputs.keySet()){
			if(key == c || keyCode == c)
				inputs.put(c, false);
		}
	}

	public static boolean isPressed(char c){
		return inputs.get(c) || inputs.get(Character.toUpperCase(c));
	}

	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(
							Main.class.getResource("/res/" + url + ".wav"));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.out.println("Can't find " + url + "!");
				}
			}
		}).start();
	}
	
	public static synchronized void music(final String url) {
		new Thread(new Runnable() {
			public void run() {
				try {
					clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(
							Main.class.getResource("/res/" + url));
					clip.open(inputStream);
					clip.start();
					clip.loop(Clip.LOOP_CONTINUOUSLY);
				} catch (Exception e) {
					System.out.println("Can't find " + url + "!");
					System.out.println("Full URL: " + Main.class.getResource("/res/" + url));
				}
			}
		}).start();
		musicPlaying = true;
	}
	
	public static synchronized void stopMusic(){
		clip.stop();
		musicPlaying = false;
	}
}
